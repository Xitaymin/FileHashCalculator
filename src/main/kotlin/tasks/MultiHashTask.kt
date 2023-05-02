package tasks

import MultiHashSettings
import java.util.*

data class MultiHashTask(
	val settings: MultiHashSettings
                        )
{
	val id: UUID = UUID.randomUUID()
	@Transient
	var isAborted = false
	private set
	var speed: Double = 0.0
	var progress: Int = 0
	@Transient
	val startTime: Long = System.currentTimeMillis()

	fun abort()
	{
		isAborted = true
	}

}

