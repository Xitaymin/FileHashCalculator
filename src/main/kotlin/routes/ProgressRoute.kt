package routes

import exceptions.TaskNotFoundException
import io.vertx.ext.web.Router
import tasks.MultiHashTask
import tasks.MultiHashTaskResult
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ProgressRoute(
	router: Router, activeTasks: ConcurrentHashMap<UUID, MultiHashTask>,
	finishedTasks: Deque<MultiHashTaskResult>
                   ) : AbstractRoute(
	router,
	activeTasks, finishedTasks
                                    )
{
	override fun mount()
	{
		router
			.get("/api/hash/progress/:id")
			.handler { ctx ->
				val taskId = UUID.fromString(ctx.pathParam("id"))
				val task = activeTasks[taskId] ?: finishedTasks.find { it.id == taskId }
				?: throw TaskNotFoundException("Task with id $taskId not found")

				ctx.response()
					.end(customGson.toJson(task))
			}
	}
}