package routes

import HashResponse
import io.vertx.ext.web.Router
import tasks.MultiHashTask
import tasks.MultiHashTaskResult
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class TasksResultsRoute(
	router: Router,
	activeTasks: ConcurrentHashMap<UUID, MultiHashTask>,
	finishedTasks: Deque<MultiHashTaskResult>
                    ) : AbstractRoute(router, activeTasks, finishedTasks)
{
	override fun mount()
	{
		router.get("/api/hash/tasks")
			.handler { ctx ->
				ctx.response()
					.end(customGson.toJson(HashResponse(activeTasks.values.toList(), finishedTasks)))

			}
	}

}