
import java.util.*
import java.util.stream.Collectors

@javax.annotation.Generated(value = ["decision.ftl", "Time"])
@com.gs.dmn.runtime.annotation.DRGElement(
    namespace = "",
    name = "Time",
    label = "",
    elementKind = com.gs.dmn.runtime.annotation.DRGElementKind.DECISION,
    expressionKind = com.gs.dmn.runtime.annotation.ExpressionKind.LITERAL_EXPRESSION,
    hitPolicy = com.gs.dmn.runtime.annotation.HitPolicy.UNKNOWN,
    rulesCount = -1
)
class Time() : com.gs.dmn.runtime.JavaTimeDMNBaseDecision() {
    override fun applyMap(input_: MutableMap<String, String>, context_: com.gs.dmn.runtime.ExecutionContext): java.time.temporal.TemporalAccessor? {
        try {
            return apply(input_.get("InputTime")?.let({ time(it) }), context_)
        } catch (e: Exception) {
            logError("Cannot apply decision 'Time'", e)
            return null
        }
    }

    fun apply(inputTime: java.time.temporal.TemporalAccessor?, context_: com.gs.dmn.runtime.ExecutionContext): java.time.temporal.TemporalAccessor? {
        try {
            // Start decision 'Time'
            var annotationSet_: com.gs.dmn.runtime.annotation.AnnotationSet = context_.getAnnotations()
            var eventListener_: com.gs.dmn.runtime.listener.EventListener = context_.getEventListener()
            var externalExecutor_: com.gs.dmn.runtime.external.ExternalFunctionExecutor = context_.getExternalFunctionExecutor()
            var cache_: com.gs.dmn.runtime.cache.Cache = context_.getCache()
            val timeStartTime_ = System.currentTimeMillis()
            val timeArguments_ = com.gs.dmn.runtime.listener.Arguments()
            timeArguments_.put("InputTime", inputTime)
            eventListener_.startDRGElement(DRG_ELEMENT_METADATA, timeArguments_)

            // Evaluate decision 'Time'
            val output_: java.time.temporal.TemporalAccessor? = evaluate(inputTime, context_)

            // End decision 'Time'
            eventListener_.endDRGElement(DRG_ELEMENT_METADATA, timeArguments_, output_, (System.currentTimeMillis() - timeStartTime_))

            return output_
        } catch (e: Exception) {
            logError("Exception caught in 'Time' evaluation", e)
            return null
        }
    }

    fun applyProto(timeRequest_: proto.TimeRequest, context_: com.gs.dmn.runtime.ExecutionContext): proto.TimeResponse {
        // Create arguments from Request Message
        val inputTime: java.time.temporal.TemporalAccessor? = time(timeRequest_.getInputTime())

        // Invoke apply method
        val output_: java.time.temporal.TemporalAccessor? = apply(inputTime, context_)

        // Convert output to Response Message
        val builder_: proto.TimeResponse.Builder = proto.TimeResponse.newBuilder()
        val outputProto_ = string(output_)
        builder_.setTime(outputProto_)
        return builder_.build()
    }

    private inline fun evaluate(inputTime: java.time.temporal.TemporalAccessor?, context_: com.gs.dmn.runtime.ExecutionContext): java.time.temporal.TemporalAccessor? {
        var annotationSet_: com.gs.dmn.runtime.annotation.AnnotationSet = context_.getAnnotations()
        var eventListener_: com.gs.dmn.runtime.listener.EventListener = context_.getEventListener()
        var externalExecutor_: com.gs.dmn.runtime.external.ExternalFunctionExecutor = context_.getExternalFunctionExecutor()
        var cache_: com.gs.dmn.runtime.cache.Cache = context_.getCache()
        return inputTime as java.time.temporal.TemporalAccessor?
    }

    companion object {
        val DRG_ELEMENT_METADATA : com.gs.dmn.runtime.listener.DRGElement = com.gs.dmn.runtime.listener.DRGElement(
            "",
            "Time",
            "",
            com.gs.dmn.runtime.annotation.DRGElementKind.DECISION,
            com.gs.dmn.runtime.annotation.ExpressionKind.LITERAL_EXPRESSION,
            com.gs.dmn.runtime.annotation.HitPolicy.UNKNOWN,
            -1
        )

        @JvmStatic
        fun requestToMap(timeRequest_: proto.TimeRequest): kotlin.collections.Map<String, Any?> {
            // Create arguments from Request Message
            val inputTime: java.time.temporal.TemporalAccessor? = com.gs.dmn.feel.lib.JavaTimeFEELLib.INSTANCE.time(timeRequest_.getInputTime())

            // Create map
            val map_: kotlin.collections.MutableMap<String, Any?> = mutableMapOf()
            map_.put("InputTime", inputTime)
            return map_
        }

        @JvmStatic
        fun responseToOutput(timeResponse_: proto.TimeResponse): java.time.temporal.TemporalAccessor? {
            // Extract and convert output
            return com.gs.dmn.feel.lib.JavaTimeFEELLib.INSTANCE.time(timeResponse_.getTime())
        }
    }
}
