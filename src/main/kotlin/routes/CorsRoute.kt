package routes

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler
import tasks.MultiHashTask
import tasks.MultiHashTaskResult
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CorsRoute(
	router: Router, activeTasks: ConcurrentHashMap<UUID, MultiHashTask>,
	finishedTasks: Deque<MultiHashTaskResult>
               ) : AbstractRoute(
	router,
	activeTasks, finishedTasks
                                )
{
	override fun mount()
	{
		router.route()
			.handler(
				CorsHandler.create()
					.addOrigin("http://localhost:3000")
					.allowedMethod(HttpMethod.GET)
					.allowedMethod(HttpMethod.POST)
					.allowCredentials(true)
					.allowedHeader("Access-Control-Allow-Headers")
					.allowedHeader("Authorization")
					.allowedHeader("Access-Control-Allow-Method")
					.allowedHeader("Access-Control-Allow-Origin")
					.allowedHeader("Access-Control-Allow-Credentials")
					.allowedHeader("Content-Type")
			        )
	}
}