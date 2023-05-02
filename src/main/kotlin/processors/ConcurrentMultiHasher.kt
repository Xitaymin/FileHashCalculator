package processors

import MultiHashSettings
import common.BufferPool
import common.TaskStatus
import exceptions.FilePathNotFoundException
import exceptions.TaskAlreadyExistsException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import tasks.MultiHashTask
import tasks.MultiHashTaskResult
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.Path
import kotlin.io.path.exists

class ConcurrentMultiHasher(private val settings: MultiHashSettings)
{
	companion object {
		private val bufferSize: Int = 1024 * 1024 * 2
		val buffersPool = BufferPool(bufferSize)
	}

	private val mathContext = MathContext(2, RoundingMode.HALF_UP)

	@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
	fun run(activeTasks: ConcurrentHashMap<UUID, MultiHashTask>, finishedTasks: Deque<MultiHashTaskResult>): UUID
	{
		val path = getPathIfTaskStartPossible(activeTasks)

		val task = MultiHashTask(settings)
		activeTasks[task.id] = task
		val hashResult = MultiHashTaskResult(task.id, task.settings)

		GlobalScope.launch(Dispatchers.IO) {
			val digests = settings.hashTypes.map { MessageDigest.getInstance(it.toDigestName()) }

			val totalBytesSize = Files.size(path)
			var processedBytes = 0L
			try
			{
				BufferedInputStream(FileInputStream(settings.path)).use { inputStream ->
					var bytesRead = 0

					GlobalScope.produce(Dispatchers.IO) {
						while (bytesRead != -1 && !task.isAborted)
						{
							val buffer = buffersPool.allocate()

							if (inputStream.read(buffer).also { bytesRead = it } != -1 && !task.isAborted)
							{
								send(Message(buffer, bytesRead))
							} else break
						}
					}.consumeEach { (buffer, bytesRead) ->
						try
						{
							digests.map { launch { it.update(buffer, 0, bytesRead) } }.joinAll()
						}
						finally
						{
							buffersPool.free(buffer)
						}

						processedBytes += bytesRead
						task.speed = calculateCurrentSpeed(processedBytes, System.currentTimeMillis() - task.startTime)
						task.progress = calculateProgress(processedBytes, totalBytesSize)
					}
				}

				if (task.isAborted)
				{
					hashResult.status = TaskStatus.Stopped
				} else
				{
					digests.forEach {
						hashResult.result[it.algorithm] = it.digest().toHex()
					}
					hashResult.status = TaskStatus.Finished
				}

			} catch (e: Exception)
			{
				hashResult.status = TaskStatus.Failed
			} finally
			{
				hashResult.duration = Duration.ofMillis(System.currentTimeMillis() - task.startTime)
				hashResult.averageSpeed = calculateAverageSpeed(processedBytes, hashResult.duration.toMillis())
				updateTaskRegistries(activeTasks, finishedTasks, hashResult)
			}
		}

		return task.id
	}

	private fun calculateAverageSpeed(processedBytes: Long, durationInMs: Long): BigDecimal
	{
		val processed = BigDecimal(processedBytes, mathContext)
		val duration = BigDecimal(durationInMs, mathContext).divide(BigDecimal(1000, mathContext), mathContext)
		val processedMb = processed.divide(BigDecimal(1024 * 1024, mathContext), mathContext)
		return processedMb.divide(duration, mathContext)
	}

	private fun calculateCurrentSpeed(bytesRead: Long, ms: Long): Double
	{
		val mb = bytesRead / 1024 / 1024.toDouble()
		val seconds = ms.toDouble() / 1000
		return (mb / seconds)
	}

	private fun calculateProgress(processedBytes: Long, totalBytesSize: Long) =
		(processedBytes * 100 / totalBytesSize).toInt()

	private fun updateTaskRegistries(
		activeTasks: ConcurrentHashMap<UUID, MultiHashTask>,
		finishedTasks: Deque<MultiHashTaskResult>,
		task: MultiHashTaskResult
	                                )
	{
		activeTasks.remove(task.id)
		finishedTasks.push(task)
	}

	private fun getPathIfTaskStartPossible(activeTasks: ConcurrentHashMap<UUID, MultiHashTask>): Path
	{
		if (activeTasks.values.any { it.settings.path == settings.path })
		{
			throw TaskAlreadyExistsException("Task already exists")
		}

		val path = Path(settings.path)

		if (!path.exists())
		{
			throw FilePathNotFoundException("Path does not exist")
		}
		return path
	}

	private fun ByteArray.toHex(): String
	{
		return joinToString("") { "%02x".format(it) }
	}

	data class Message(val buffer: ByteArray, val bytesRead: Int)

}