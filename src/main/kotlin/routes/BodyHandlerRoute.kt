package routes

import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import tasks.MultiHashTask
import tasks.MultiHashTaskResult
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class BodyHandlerRoute(
	router: Router,
	activeTasks: ConcurrentHashMap<UUID, MultiHashTask>,
	finishedTasks: Deque<MultiHashTaskResult>
                      ) : AbstractRoute(router, activeTasks, finishedTasks)
{
	override fun mount()
	{
		router.route().handler(BodyHandler.create())
	}
}
