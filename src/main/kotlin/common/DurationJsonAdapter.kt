package common

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.Duration

class DurationJsonAdapter : JsonSerializer<Duration>
{
	override fun serialize(src: Duration, typeOfSrc: Type, context: JsonSerializationContext?): JsonElement
	{
		return when
		{
			src.toMinutes() > 0 -> prepareDurationJson(src.toMinutes(), "minutes")
			src.toSeconds() > 0 -> prepareDurationJson(src.toSeconds(), "seconds")
			else                -> prepareDurationJson(src.toMillis(), "milliseconds")
		}
	}

	private fun prepareDurationJson(value: Long, timeUnit: String): JsonObject
	{
		val jsonDuration = JsonObject()
		jsonDuration.addProperty("value", value)
		jsonDuration.addProperty("timeUnit", timeUnit)
		return jsonDuration
	}
}
