package routes

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import common.DurationJsonAdapter
import io.vertx.ext.web.Router
import tasks.MultiHashTask
import tasks.MultiHashTaskResult
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractRoute(
	protected val router: Router,
	protected val activeTasks: ConcurrentHashMap<UUID, MultiHashTask>,
	protected val finishedTasks: Deque<MultiHashTaskResult>
                            )
{

	companion object
	{
		val customGson: Gson =  GsonBuilder().registerTypeAdapter(Duration::class.java, DurationJsonAdapter()).create()
	}

	abstract fun mount()
}
