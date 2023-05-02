package routes

import exceptions.FilePathNotFoundException
import exceptions.TaskAlreadyExistsException
import exceptions.TaskNotFoundException
import io.vertx.ext.web.Router
import server.HttpStatus
import tasks.MultiHashTask
import tasks.MultiHashTaskResult
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ErrorHandlerRoute(
	router: Router, activeTasks: ConcurrentHashMap<UUID, MultiHashTask>,
	finishedTasks: Deque<MultiHashTaskResult>
                   ) : AbstractRoute(
	router,
	activeTasks, finishedTasks
                                    )
{
	override fun mount()
	{
		router.route("/api/*").failureHandler { ctx ->

			when (ctx.failure())
			{
				is FilePathNotFoundException  -> ctx.response().statusCode = HttpStatus.NotFound.Code
				is TaskNotFoundException      -> ctx.response().statusCode = HttpStatus.NotFound.Code
				is TaskAlreadyExistsException -> ctx.response().statusCode = HttpStatus.BadRequest.Code
				else                          -> ctx.response().statusCode = HttpStatus.InternalServerError.Code
			}
			ctx.response().end(ctx.failure().message)
		}
	}
}