import common.HashType
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import routes.BodyHandlerRoute
import routes.CorsRoute
import routes.ErrorHandlerRoute
import routes.ProgressRoute
import routes.StartHashRoute
import routes.StopHashRoute
import routes.TaskResultRoute
import routes.TasksResultsRoute
import tasks.MultiHashTask
import tasks.MultiHashTaskResult
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

class MainVerticle : AbstractVerticle()
{

	private val activeTasks: ConcurrentHashMap<UUID, MultiHashTask> = ConcurrentHashMap()

	private val finishedTasks: Deque<MultiHashTaskResult> = ConcurrentLinkedDeque()

	override fun start()
	{
		val vertx = Vertx.vertx()

		val router = Router.router(vertx)

		val routers = listOf(
			BodyHandlerRoute(router, activeTasks, finishedTasks),
			CorsRoute(router, activeTasks, finishedTasks),
			ErrorHandlerRoute(router, activeTasks, finishedTasks),

			StartHashRoute(router, activeTasks, finishedTasks),
			StopHashRoute(router, activeTasks, finishedTasks),
			TaskResultRoute(router, activeTasks, finishedTasks),
			ProgressRoute(router, activeTasks, finishedTasks),
			TasksResultsRoute(router, activeTasks, finishedTasks),

			)
		routers.forEach { it.mount() }

		vertx.createHttpServer()
			.requestHandler(router)
			.listen(8080)

	}
}

data class MultiHashSettings(val path: String, val hashTypes: List<HashType>)

data class HashResponse(val activeTasks: List<MultiHashTask>, val finishedTasks: Deque<MultiHashTaskResult>)




