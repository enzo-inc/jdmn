
import java.util.*;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = {"bkm.ftl", "bkm_012_2"})
@com.gs.dmn.runtime.annotation.DRGElement(
    namespace = "",
    name = "bkm_012_2",
    label = "",
    elementKind = com.gs.dmn.runtime.annotation.DRGElementKind.BUSINESS_KNOWLEDGE_MODEL,
    expressionKind = com.gs.dmn.runtime.annotation.ExpressionKind.LITERAL_EXPRESSION,
    hitPolicy = com.gs.dmn.runtime.annotation.HitPolicy.UNKNOWN,
    rulesCount = -1
)
public class Bkm_012_2 extends com.gs.dmn.runtime.JavaTimeDMNBaseDecision {
    public static final com.gs.dmn.runtime.listener.DRGElement DRG_ELEMENT_METADATA = new com.gs.dmn.runtime.listener.DRGElement(
        "",
        "bkm_012_2",
        "",
        com.gs.dmn.runtime.annotation.DRGElementKind.BUSINESS_KNOWLEDGE_MODEL,
        com.gs.dmn.runtime.annotation.ExpressionKind.LITERAL_EXPRESSION,
        com.gs.dmn.runtime.annotation.HitPolicy.UNKNOWN,
        -1
    );

    private static class Bkm_012_2LazyHolder {
        static final Bkm_012_2 INSTANCE = new Bkm_012_2();
    }
    public static Bkm_012_2 instance() {
        return Bkm_012_2LazyHolder.INSTANCE;
    }

    private Bkm_012_2() {
    }

    @java.lang.Override()
    public java.lang.Number applyMap(java.util.Map<String, String> input_, com.gs.dmn.runtime.ExecutionContext context_) {
        try {
            return apply((input_.get("a") != null ? number(input_.get("a")) : null), context_);
        } catch (Exception e) {
            logError("Cannot apply decision 'Bkm_012_2'", e);
            return null;
        }
    }

    public java.lang.Number apply(java.lang.Number a, com.gs.dmn.runtime.ExecutionContext context_) {
        try {
            // Start BKM 'bkm_012_2'
            com.gs.dmn.runtime.annotation.AnnotationSet annotationSet_ = context_ != null ? context_.getAnnotations() : null;
            com.gs.dmn.runtime.listener.EventListener eventListener_ = context_ != null ? context_.getEventListener() : null;
            com.gs.dmn.runtime.external.ExternalFunctionExecutor externalExecutor_ = context_ != null ? context_.getExternalFunctionExecutor() : null;
            com.gs.dmn.runtime.cache.Cache cache_ = context_ != null ? context_.getCache() : null;
            long bkm_012_2StartTime_ = System.currentTimeMillis();
            com.gs.dmn.runtime.listener.Arguments bkm_012_2Arguments_ = new com.gs.dmn.runtime.listener.Arguments();
            bkm_012_2Arguments_.put("a", a);
            eventListener_.startDRGElement(DRG_ELEMENT_METADATA, bkm_012_2Arguments_);

            // Evaluate BKM 'bkm_012_2'
            java.lang.Number output_ = lambda.apply(a, context_);

            // End BKM 'bkm_012_2'
            eventListener_.endDRGElement(DRG_ELEMENT_METADATA, bkm_012_2Arguments_, output_, (System.currentTimeMillis() - bkm_012_2StartTime_));

            return output_;
        } catch (Exception e) {
            logError("Exception caught in 'bkm_012_2' evaluation", e);
            return null;
        }
    }

    public com.gs.dmn.runtime.LambdaExpression<java.lang.Number> lambda =
        new com.gs.dmn.runtime.LambdaExpression<java.lang.Number>() {
            public java.lang.Number apply(Object... args_) {
                java.lang.Number a = 0 < args_.length ? (java.lang.Number) args_[0] : null;
                com.gs.dmn.runtime.ExecutionContext context_ = 1 < args_.length ? (com.gs.dmn.runtime.ExecutionContext) args_[1] : null;
                com.gs.dmn.runtime.annotation.AnnotationSet annotationSet_ = context_ != null ? context_.getAnnotations() : null;
                com.gs.dmn.runtime.listener.EventListener eventListener_ = context_ != null ? context_.getEventListener() : null;
                com.gs.dmn.runtime.external.ExternalFunctionExecutor externalExecutor_ = context_ != null ? context_.getExternalFunctionExecutor() : null;
                com.gs.dmn.runtime.cache.Cache cache_ = context_ != null ? context_.getCache() : null;

                return numericMultiply(a, number("10"));
            }
        };
}
