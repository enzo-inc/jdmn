
import java.util.*
import java.util.stream.Collectors

@javax.annotation.Generated(value = ["decision.ftl", "Approval"])
@com.gs.dmn.runtime.annotation.DRGElement(
    namespace = "",
    name = "Approval",
    label = "",
    elementKind = com.gs.dmn.runtime.annotation.DRGElementKind.DECISION,
    expressionKind = com.gs.dmn.runtime.annotation.ExpressionKind.DECISION_TABLE,
    hitPolicy = com.gs.dmn.runtime.annotation.HitPolicy.UNIQUE,
    rulesCount = 5
)
class Approval() : com.gs.dmn.runtime.JavaTimeDMNBaseDecision() {
    override fun applyMap(input_: MutableMap<String, String>, context_: com.gs.dmn.runtime.ExecutionContext): type.TApproval? {
        try {
            return apply(input_.get("Age")?.let({ number(it) }), input_.get("RiskCategory"), input_.get("isAffordable")?.let({ it.toBoolean() }), context_)
        } catch (e: Exception) {
            logError("Cannot apply decision 'Approval'", e)
            return null
        }
    }

    fun apply(age: java.lang.Number?, riskCategory: String?, isAffordable: Boolean?, context_: com.gs.dmn.runtime.ExecutionContext): type.TApproval? {
        try {
            // Start decision 'Approval'
            var annotationSet_: com.gs.dmn.runtime.annotation.AnnotationSet = context_.getAnnotations()
            var eventListener_: com.gs.dmn.runtime.listener.EventListener = context_.getEventListener()
            var externalExecutor_: com.gs.dmn.runtime.external.ExternalFunctionExecutor = context_.getExternalFunctionExecutor()
            var cache_: com.gs.dmn.runtime.cache.Cache = context_.getCache()
            val approvalStartTime_ = System.currentTimeMillis()
            val approvalArguments_ = com.gs.dmn.runtime.listener.Arguments()
            approvalArguments_.put("Age", age)
            approvalArguments_.put("RiskCategory", riskCategory)
            approvalArguments_.put("isAffordable", isAffordable)
            eventListener_.startDRGElement(DRG_ELEMENT_METADATA, approvalArguments_)

            // Evaluate decision 'Approval'
            val output_: type.TApproval? = evaluate(age, riskCategory, isAffordable, context_)

            // End decision 'Approval'
            eventListener_.endDRGElement(DRG_ELEMENT_METADATA, approvalArguments_, output_, (System.currentTimeMillis() - approvalStartTime_))

            return output_
        } catch (e: Exception) {
            logError("Exception caught in 'Approval' evaluation", e)
            return null
        }
    }

    private inline fun evaluate(age: java.lang.Number?, riskCategory: String?, isAffordable: Boolean?, context_: com.gs.dmn.runtime.ExecutionContext): type.TApproval? {
        var annotationSet_: com.gs.dmn.runtime.annotation.AnnotationSet = context_.getAnnotations()
        var eventListener_: com.gs.dmn.runtime.listener.EventListener = context_.getEventListener()
        var externalExecutor_: com.gs.dmn.runtime.external.ExternalFunctionExecutor = context_.getExternalFunctionExecutor()
        var cache_: com.gs.dmn.runtime.cache.Cache = context_.getCache()
        // Apply rules and collect results
        val ruleOutputList_ = com.gs.dmn.runtime.RuleOutputList()
        ruleOutputList_.add(rule0(age, riskCategory, isAffordable, context_))
        ruleOutputList_.add(rule1(age, riskCategory, isAffordable, context_))
        ruleOutputList_.add(rule2(age, riskCategory, isAffordable, context_))
        ruleOutputList_.add(rule3(age, riskCategory, isAffordable, context_))
        ruleOutputList_.add(rule4(age, riskCategory, isAffordable, context_))

        // Return results based on hit policy
        var output_: type.TApproval?
        if (ruleOutputList_.noMatchedRules()) {
            // Default value
            output_ = type.TApprovalImpl("Standard", "Declined")
        } else {
            val ruleOutput_: com.gs.dmn.runtime.RuleOutput? = ruleOutputList_.applySingle(com.gs.dmn.runtime.annotation.HitPolicy.UNIQUE)
            output_ = toDecisionOutput(ruleOutput_ as ApprovalRuleOutput)
        }

        return output_
    }

    @com.gs.dmn.runtime.annotation.Rule(index = 0, annotation = "")
    private fun rule0(age: java.lang.Number?, riskCategory: String?, isAffordable: Boolean?, context_: com.gs.dmn.runtime.ExecutionContext): com.gs.dmn.runtime.RuleOutput {
        // Rule metadata
        val drgRuleMetadata: com.gs.dmn.runtime.listener.Rule = com.gs.dmn.runtime.listener.Rule(0, "")

        // Rule start
        var annotationSet_: com.gs.dmn.runtime.annotation.AnnotationSet = context_.getAnnotations()
        var eventListener_: com.gs.dmn.runtime.listener.EventListener = context_.getEventListener()
        var externalExecutor_: com.gs.dmn.runtime.external.ExternalFunctionExecutor = context_.getExternalFunctionExecutor()
        var cache_: com.gs.dmn.runtime.cache.Cache = context_.getCache()
        eventListener_.startRule(DRG_ELEMENT_METADATA, drgRuleMetadata)

        // Apply rule
        var output_: ApprovalRuleOutput = ApprovalRuleOutput(false)
        if (ruleMatches(eventListener_, drgRuleMetadata,
            numericGreaterEqualThan(age, number("18")),
            stringEqual(riskCategory, "Low"),
            booleanEqual(isAffordable, true)
        )) {
            // Rule match
            eventListener_.matchRule(DRG_ELEMENT_METADATA, drgRuleMetadata)

            // Compute output
            output_.setMatched(true)
            output_.status = "Approved"
            output_.rate = "Best"
        }

        // Rule end
        eventListener_.endRule(DRG_ELEMENT_METADATA, drgRuleMetadata, output_)

        return output_
    }

    @com.gs.dmn.runtime.annotation.Rule(index = 1, annotation = "")
    private fun rule1(age: java.lang.Number?, riskCategory: String?, isAffordable: Boolean?, context_: com.gs.dmn.runtime.ExecutionContext): com.gs.dmn.runtime.RuleOutput {
        // Rule metadata
        val drgRuleMetadata: com.gs.dmn.runtime.listener.Rule = com.gs.dmn.runtime.listener.Rule(1, "")

        // Rule start
        var annotationSet_: com.gs.dmn.runtime.annotation.AnnotationSet = context_.getAnnotations()
        var eventListener_: com.gs.dmn.runtime.listener.EventListener = context_.getEventListener()
        var externalExecutor_: com.gs.dmn.runtime.external.ExternalFunctionExecutor = context_.getExternalFunctionExecutor()
        var cache_: com.gs.dmn.runtime.cache.Cache = context_.getCache()
        eventListener_.startRule(DRG_ELEMENT_METADATA, drgRuleMetadata)

        // Apply rule
        var output_: ApprovalRuleOutput = ApprovalRuleOutput(false)
        if (ruleMatches(eventListener_, drgRuleMetadata,
            numericGreaterEqualThan(age, number("18")),
            stringEqual(riskCategory, "Medium"),
            booleanEqual(isAffordable, true)
        )) {
            // Rule match
            eventListener_.matchRule(DRG_ELEMENT_METADATA, drgRuleMetadata)

            // Compute output
            output_.setMatched(true)
            output_.status = "Approved"
            output_.rate = "Standard"
        }

        // Rule end
        eventListener_.endRule(DRG_ELEMENT_METADATA, drgRuleMetadata, output_)

        return output_
    }

    @com.gs.dmn.runtime.annotation.Rule(index = 2, annotation = "")
    private fun rule2(age: java.lang.Number?, riskCategory: String?, isAffordable: Boolean?, context_: com.gs.dmn.runtime.ExecutionContext): com.gs.dmn.runtime.RuleOutput {
        // Rule metadata
        val drgRuleMetadata: com.gs.dmn.runtime.listener.Rule = com.gs.dmn.runtime.listener.Rule(2, "")

        // Rule start
        var annotationSet_: com.gs.dmn.runtime.annotation.AnnotationSet = context_.getAnnotations()
        var eventListener_: com.gs.dmn.runtime.listener.EventListener = context_.getEventListener()
        var externalExecutor_: com.gs.dmn.runtime.external.ExternalFunctionExecutor = context_.getExternalFunctionExecutor()
        var cache_: com.gs.dmn.runtime.cache.Cache = context_.getCache()
        eventListener_.startRule(DRG_ELEMENT_METADATA, drgRuleMetadata)

        // Apply rule
        var output_: ApprovalRuleOutput = ApprovalRuleOutput(false)
        if (ruleMatches(eventListener_, drgRuleMetadata,
            numericLessThan(age, number("18")),
            booleanOr(stringEqual(riskCategory, "Medium"), stringEqual(riskCategory, "Low")),
            booleanEqual(isAffordable, true)
        )) {
            // Rule match
            eventListener_.matchRule(DRG_ELEMENT_METADATA, drgRuleMetadata)

            // Compute output
            output_.setMatched(true)
            output_.status = "Declined"
            output_.rate = "Standard"
        }

        // Rule end
        eventListener_.endRule(DRG_ELEMENT_METADATA, drgRuleMetadata, output_)

        return output_
    }

    @com.gs.dmn.runtime.annotation.Rule(index = 3, annotation = "")
    private fun rule3(age: java.lang.Number?, riskCategory: String?, isAffordable: Boolean?, context_: com.gs.dmn.runtime.ExecutionContext): com.gs.dmn.runtime.RuleOutput {
        // Rule metadata
        val drgRuleMetadata: com.gs.dmn.runtime.listener.Rule = com.gs.dmn.runtime.listener.Rule(3, "")

        // Rule start
        var annotationSet_: com.gs.dmn.runtime.annotation.AnnotationSet = context_.getAnnotations()
        var eventListener_: com.gs.dmn.runtime.listener.EventListener = context_.getEventListener()
        var externalExecutor_: com.gs.dmn.runtime.external.ExternalFunctionExecutor = context_.getExternalFunctionExecutor()
        var cache_: com.gs.dmn.runtime.cache.Cache = context_.getCache()
        eventListener_.startRule(DRG_ELEMENT_METADATA, drgRuleMetadata)

        // Apply rule
        var output_: ApprovalRuleOutput = ApprovalRuleOutput(false)
        if (ruleMatches(eventListener_, drgRuleMetadata,
            true,
            stringEqual(riskCategory, "High"),
            booleanEqual(isAffordable, true)
        )) {
            // Rule match
            eventListener_.matchRule(DRG_ELEMENT_METADATA, drgRuleMetadata)

            // Compute output
            output_.setMatched(true)
            output_.status = "Declined"
            output_.rate = "Standard"
        }

        // Rule end
        eventListener_.endRule(DRG_ELEMENT_METADATA, drgRuleMetadata, output_)

        return output_
    }

    @com.gs.dmn.runtime.annotation.Rule(index = 4, annotation = "")
    private fun rule4(age: java.lang.Number?, riskCategory: String?, isAffordable: Boolean?, context_: com.gs.dmn.runtime.ExecutionContext): com.gs.dmn.runtime.RuleOutput {
        // Rule metadata
        val drgRuleMetadata: com.gs.dmn.runtime.listener.Rule = com.gs.dmn.runtime.listener.Rule(4, "")

        // Rule start
        var annotationSet_: com.gs.dmn.runtime.annotation.AnnotationSet = context_.getAnnotations()
        var eventListener_: com.gs.dmn.runtime.listener.EventListener = context_.getEventListener()
        var externalExecutor_: com.gs.dmn.runtime.external.ExternalFunctionExecutor = context_.getExternalFunctionExecutor()
        var cache_: com.gs.dmn.runtime.cache.Cache = context_.getCache()
        eventListener_.startRule(DRG_ELEMENT_METADATA, drgRuleMetadata)

        // Apply rule
        var output_: ApprovalRuleOutput = ApprovalRuleOutput(false)
        if (ruleMatches(eventListener_, drgRuleMetadata,
            true,
            true,
            booleanEqual(isAffordable, false)
        )) {
            // Rule match
            eventListener_.matchRule(DRG_ELEMENT_METADATA, drgRuleMetadata)

            // Compute output
            output_.setMatched(true)
            output_.status = "Declined"
            output_.rate = "Standard"
        }

        // Rule end
        eventListener_.endRule(DRG_ELEMENT_METADATA, drgRuleMetadata, output_)

        return output_
    }

    fun toDecisionOutput(ruleOutput_: ApprovalRuleOutput): type.TApproval {
        val result_: type.TApprovalImpl = type.TApprovalImpl()
        result_.status = ruleOutput_.let({ it.status })
        result_.rate = ruleOutput_.let({ it.rate })
        return result_
    }

    companion object {
        val DRG_ELEMENT_METADATA : com.gs.dmn.runtime.listener.DRGElement = com.gs.dmn.runtime.listener.DRGElement(
            "",
            "Approval",
            "",
            com.gs.dmn.runtime.annotation.DRGElementKind.DECISION,
            com.gs.dmn.runtime.annotation.ExpressionKind.DECISION_TABLE,
            com.gs.dmn.runtime.annotation.HitPolicy.UNIQUE,
            5
        )
    }
}
