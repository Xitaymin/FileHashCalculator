package routes

import MultiHashSettings
import io.vertx.ext.web.Router
import processors.ConcurrentMultiHasher
import tasks.MultiHashTask
import tasks.MultiHashTaskResult
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class StartHashRoute(
	router: Router,
	activeTasks: ConcurrentHashMap<UUID, MultiHashTask>,
	finishedTasks: Deque<MultiHashTaskResult>
                    ) : AbstractRoute(router, activeTasks, finishedTasks)
{
	override fun mount()
	{
		router.post("/api/hash/start")
		.handler { ctx ->

			val requestBody = ctx.body().asString()

			val settings = customGson.fromJson(requestBody, MultiHashSettings::class.java)

			val taskId = ConcurrentMultiHasher(settings).run(activeTasks, finishedTasks)

			ctx.response()
				.end(customGson.toJson(taskId))

		}
	}


}