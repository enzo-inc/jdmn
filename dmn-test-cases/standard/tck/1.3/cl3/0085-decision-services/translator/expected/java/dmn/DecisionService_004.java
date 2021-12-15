
import java.util.*;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = {"bkm.ftl", "decisionService_004"})
@com.gs.dmn.runtime.annotation.DRGElement(
    namespace = "",
    name = "decisionService_004",
    label = "",
    elementKind = com.gs.dmn.runtime.annotation.DRGElementKind.DECISION_SERVICE,
    expressionKind = com.gs.dmn.runtime.annotation.ExpressionKind.OTHER,
    hitPolicy = com.gs.dmn.runtime.annotation.HitPolicy.UNKNOWN,
    rulesCount = -1
)
public class DecisionService_004 extends com.gs.dmn.runtime.DefaultDMNBaseDecision {
    public static final com.gs.dmn.runtime.listener.DRGElement DRG_ELEMENT_METADATA = new com.gs.dmn.runtime.listener.DRGElement(
        "",
        "decisionService_004",
        "",
        com.gs.dmn.runtime.annotation.DRGElementKind.DECISION_SERVICE,
        com.gs.dmn.runtime.annotation.ExpressionKind.OTHER,
        com.gs.dmn.runtime.annotation.HitPolicy.UNKNOWN,
        -1
    );

    public static final DecisionService_004 INSTANCE = new DecisionService_004();

    private final Decision_004_2 decision_004_2;

    public DecisionService_004() {
        this(new Decision_004_2());
    }

    public DecisionService_004(Decision_004_2 decision_004_2) {
        this.decision_004_2 = decision_004_2;
    }

    public static String decisionService_004(com.gs.dmn.runtime.annotation.AnnotationSet annotationSet_, com.gs.dmn.runtime.listener.EventListener eventListener_, com.gs.dmn.runtime.external.ExternalFunctionExecutor externalExecutor_, com.gs.dmn.runtime.cache.Cache cache_) {
        return INSTANCE.apply(annotationSet_, eventListener_, externalExecutor_, cache_);
    }

    private String apply(com.gs.dmn.runtime.annotation.AnnotationSet annotationSet_, com.gs.dmn.runtime.listener.EventListener eventListener_, com.gs.dmn.runtime.external.ExternalFunctionExecutor externalExecutor_, com.gs.dmn.runtime.cache.Cache cache_) {
        try {
            // Start DS 'decisionService_004'
            long decisionService_004StartTime_ = System.currentTimeMillis();
            com.gs.dmn.runtime.listener.Arguments decisionService_004Arguments_ = new com.gs.dmn.runtime.listener.Arguments();
            eventListener_.startDRGElement(DRG_ELEMENT_METADATA, decisionService_004Arguments_);

            // Apply child decisions
            String decision_004_2 = this.decision_004_2.apply(annotationSet_, eventListener_, externalExecutor_, cache_);

            // Evaluate DS 'decisionService_004'
            String output_ = evaluate(decision_004_2, annotationSet_, eventListener_, externalExecutor_, cache_);

            // End DS 'decisionService_004'
            eventListener_.endDRGElement(DRG_ELEMENT_METADATA, decisionService_004Arguments_, output_, (System.currentTimeMillis() - decisionService_004StartTime_));

            return output_;
        } catch (Exception e) {
            logError("Exception caught in 'decisionService_004' evaluation", e);
            return null;
        }
    }

    protected String evaluate(String decision_004_2, com.gs.dmn.runtime.annotation.AnnotationSet annotationSet_, com.gs.dmn.runtime.listener.EventListener eventListener_, com.gs.dmn.runtime.external.ExternalFunctionExecutor externalExecutor_, com.gs.dmn.runtime.cache.Cache cache_) {
        return decision_004_2;
    }
}