package routes

import io.vertx.ext.web.Router
import server.HttpStatus
import tasks.MultiHashTask
import tasks.MultiHashTaskResult
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class StopHashRoute(
	router: Router, activeTasks: ConcurrentHashMap<UUID, MultiHashTask>,
	finishedTasks: Deque<MultiHashTaskResult>
                   ) : AbstractRoute(
	router,
	activeTasks, finishedTasks
                                    )
{
	override fun mount()
	{
		router.post("/api/hash/stop").handler { ctx ->
			val requestBody = ctx.body().asString()

			val taskId = customGson.fromJson(requestBody, UUID::class.java)

			activeTasks[taskId]?.abort()

			ctx.response()
				.setStatusCode(HttpStatus.Ok.Code)
				.end()
		}
	}
}