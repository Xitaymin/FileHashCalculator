package common

import java.util.concurrent.ConcurrentLinkedQueue

abstract class AbstractObjectPool<T>(private val factory: () -> T)
{
	private val pool = ConcurrentLinkedQueue<T>()

	init
	{
		repeat(2) {
			free(factory())
		}
	}

	fun allocate(): T = pool.poll() ?: factory()

	fun free(obj: T) = pool.offer(obj)
}

class BufferPool(private val bufferSize: Int) : AbstractObjectPool<ByteArray>({ ByteArray(bufferSize) })
