package tasks

import MultiHashSettings
import common.TaskStatus
import java.math.BigDecimal
import java.time.Duration
import java.util.*

data class MultiHashTaskResult(
	val id: UUID,
	val settings: MultiHashSettings
                              )
{
	var status: TaskStatus = TaskStatus.Running
	val result = mutableMapOf<String, String>()
	var averageSpeed: BigDecimal? = null
	var duration: Duration = Duration.ZERO

}