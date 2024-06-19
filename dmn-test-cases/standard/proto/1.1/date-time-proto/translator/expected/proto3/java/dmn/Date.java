
import java.util.*;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = {"decision.ftl", "Date"})
@com.gs.dmn.runtime.annotation.DRGElement(
    namespace = "",
    name = "Date",
    label = "",
    elementKind = com.gs.dmn.runtime.annotation.DRGElementKind.DECISION,
    expressionKind = com.gs.dmn.runtime.annotation.ExpressionKind.LITERAL_EXPRESSION,
    hitPolicy = com.gs.dmn.runtime.annotation.HitPolicy.UNKNOWN,
    rulesCount = -1
)
public class Date extends com.gs.dmn.runtime.JavaTimeDMNBaseDecision {
    public static final com.gs.dmn.runtime.listener.DRGElement DRG_ELEMENT_METADATA = new com.gs.dmn.runtime.listener.DRGElement(
        "",
        "Date",
        "",
        com.gs.dmn.runtime.annotation.DRGElementKind.DECISION,
        com.gs.dmn.runtime.annotation.ExpressionKind.LITERAL_EXPRESSION,
        com.gs.dmn.runtime.annotation.HitPolicy.UNKNOWN,
        -1
    );

    public static java.util.Map<String, Object> requestToMap(proto.DateRequest dateRequest_) {
        // Create arguments from Request Message
        java.time.LocalDate inputDate = com.gs.dmn.feel.lib.JavaTimeFEELLib.INSTANCE.date(dateRequest_.getInputDate());

        // Create map
        java.util.Map<String, Object> map_ = new java.util.LinkedHashMap<>();
        map_.put("InputDate", inputDate);
        return map_;
    }

    public static java.time.LocalDate responseToOutput(proto.DateResponse dateResponse_) {
        // Extract and convert output
        return com.gs.dmn.feel.lib.JavaTimeFEELLib.INSTANCE.date(dateResponse_.getDate());
    }

    public Date() {
    }

    @java.lang.Override()
    public java.time.LocalDate applyMap(java.util.Map<String, String> input_, com.gs.dmn.runtime.ExecutionContext context_) {
        try {
            return apply((input_.get("InputDate") != null ? date(input_.get("InputDate")) : null), context_);
        } catch (Exception e) {
            logError("Cannot apply decision 'Date'", e);
            return null;
        }
    }

    public java.time.LocalDate apply(java.time.LocalDate inputDate, com.gs.dmn.runtime.ExecutionContext context_) {
        try {
            // Start decision 'Date'
            com.gs.dmn.runtime.annotation.AnnotationSet annotationSet_ = context_ != null ? context_.getAnnotations() : null;
            com.gs.dmn.runtime.listener.EventListener eventListener_ = context_ != null ? context_.getEventListener() : null;
            com.gs.dmn.runtime.external.ExternalFunctionExecutor externalExecutor_ = context_ != null ? context_.getExternalFunctionExecutor() : null;
            com.gs.dmn.runtime.cache.Cache cache_ = context_ != null ? context_.getCache() : null;
            long dateStartTime_ = System.currentTimeMillis();
            com.gs.dmn.runtime.listener.Arguments dateArguments_ = new com.gs.dmn.runtime.listener.Arguments();
            dateArguments_.put("InputDate", inputDate);
            eventListener_.startDRGElement(DRG_ELEMENT_METADATA, dateArguments_);

            // Evaluate decision 'Date'
            java.time.LocalDate output_ = lambda.apply(inputDate, context_);

            // End decision 'Date'
            eventListener_.endDRGElement(DRG_ELEMENT_METADATA, dateArguments_, output_, (System.currentTimeMillis() - dateStartTime_));

            return output_;
        } catch (Exception e) {
            logError("Exception caught in 'Date' evaluation", e);
            return null;
        }
    }

    public proto.DateResponse applyProto(proto.DateRequest dateRequest_, com.gs.dmn.runtime.ExecutionContext context_) {
        // Create arguments from Request Message
        java.time.LocalDate inputDate = date(dateRequest_.getInputDate());

        // Invoke apply method
        java.time.LocalDate output_ = apply(inputDate, context_);

        // Convert output to Response Message
        proto.DateResponse.Builder builder_ = proto.DateResponse.newBuilder();
        String outputProto_ = string(output_);
        builder_.setDate(outputProto_);
        return builder_.build();
    }

    public com.gs.dmn.runtime.LambdaExpression<java.time.LocalDate> lambda =
        new com.gs.dmn.runtime.LambdaExpression<java.time.LocalDate>() {
            public java.time.LocalDate apply(Object... args_) {
                java.time.LocalDate inputDate = 0 < args_.length ? (java.time.LocalDate) args_[0] : null;
                com.gs.dmn.runtime.ExecutionContext context_ = 1 < args_.length ? (com.gs.dmn.runtime.ExecutionContext) args_[1] : null;
                com.gs.dmn.runtime.annotation.AnnotationSet annotationSet_ = context_ != null ? context_.getAnnotations() : null;
                com.gs.dmn.runtime.listener.EventListener eventListener_ = context_ != null ? context_.getEventListener() : null;
                com.gs.dmn.runtime.external.ExternalFunctionExecutor externalExecutor_ = context_ != null ? context_.getExternalFunctionExecutor() : null;
                com.gs.dmn.runtime.cache.Cache cache_ = context_ != null ? context_.getCache() : null;

                return inputDate;
            }
        };
}
