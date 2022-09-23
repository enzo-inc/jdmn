import typing
import decimal
import datetime
import isodate
import time

import jdmn.runtime.Context
import jdmn.runtime.DefaultDMNBaseDecision
import jdmn.runtime.ExecutionContext
import jdmn.runtime.LambdaExpression
import jdmn.runtime.LazyEval
import jdmn.runtime.Pair
import jdmn.runtime.Range
import jdmn.runtime.RuleOutput
import jdmn.runtime.RuleOutputList

import jdmn.runtime.annotation.Annotation
import jdmn.runtime.annotation.AnnotationSet
import jdmn.runtime.annotation.DRGElementKind
import jdmn.runtime.annotation.ExpressionKind
import jdmn.runtime.annotation.HitPolicy

import jdmn.runtime.cache.Cache

import jdmn.runtime.external.ExternalFunctionExecutor

import jdmn.runtime.listener.Arguments
import jdmn.runtime.listener.DRGElement
import jdmn.runtime.listener.EventListener
import jdmn.runtime.listener.Rule

import type_.Monthly
import type_.MonthlyImpl
import type_.TApplicantData
import type_.TApplicantDataImpl
import type_.TBureauData
import type_.TBureauDataImpl

import ApplicationRiskScore

import PostBureauRiskCategoryTable


# Generated(value = ["decision.ftl", "'Post-bureauRiskCategory'"])
class PostBureauRiskCategory(jdmn.runtime.DefaultDMNBaseDecision.DefaultDMNBaseDecision):
    DRG_ELEMENT_METADATA: jdmn.runtime.listener.DRGElement.DRGElement = jdmn.runtime.listener.DRGElement.DRGElement(
        "",
        "'Post-bureauRiskCategory'",
        "",
        jdmn.runtime.annotation.DRGElementKind.DRGElementKind.DECISION,
        jdmn.runtime.annotation.ExpressionKind.ExpressionKind.INVOCATION,
        jdmn.runtime.annotation.HitPolicy.HitPolicy.UNKNOWN,
        -1
    )

    def __init__(self, applicationRiskScore: ApplicationRiskScore.ApplicationRiskScore = None):
        jdmn.runtime.DefaultDMNBaseDecision.DefaultDMNBaseDecision.__init__(self)
        self.applicationRiskScore = ApplicationRiskScore.ApplicationRiskScore() if applicationRiskScore is None else applicationRiskScore

    def apply(self, applicantData: typing.Optional[type_.TApplicantData.TApplicantData], bureauData: typing.Optional[type_.TBureauData.TBureauData], annotationSet_: jdmn.runtime.annotation.AnnotationSet.AnnotationSet, eventListener_: jdmn.runtime.listener.EventListener.EventListener, externalExecutor_: jdmn.runtime.external.ExternalFunctionExecutor.ExternalFunctionExecutor, cache_: jdmn.runtime.cache.Cache.Cache) -> typing.Optional[str]:
        try:
            # Start decision ''Post-bureauRiskCategory''
            postBureauRiskCategoryStartTime_ = int(time.time_ns()/1000)
            postBureauRiskCategoryArguments_ = jdmn.runtime.listener.Arguments.Arguments()
            postBureauRiskCategoryArguments_.put("ApplicantData", applicantData)
            postBureauRiskCategoryArguments_.put("BureauData", bureauData)
            eventListener_.startDRGElement(self.DRG_ELEMENT_METADATA, postBureauRiskCategoryArguments_)

            # Evaluate decision ''Post-bureauRiskCategory''
            output_: typing.Optional[str] = self.evaluate(applicantData, bureauData, annotationSet_, eventListener_, externalExecutor_, cache_)

            # End decision ''Post-bureauRiskCategory''
            eventListener_.endDRGElement(self.DRG_ELEMENT_METADATA, postBureauRiskCategoryArguments_, output_, (int(time.time_ns()/1000) - postBureauRiskCategoryStartTime_))

            return output_
        except Exception as e:
            self.logError("Exception caught in ''Post-bureauRiskCategory'' evaluation", e)
            return None

    def evaluate(self, applicantData: typing.Optional[type_.TApplicantData.TApplicantData], bureauData: typing.Optional[type_.TBureauData.TBureauData], annotationSet_: jdmn.runtime.annotation.AnnotationSet.AnnotationSet, eventListener_: jdmn.runtime.listener.EventListener.EventListener, externalExecutor_: jdmn.runtime.external.ExternalFunctionExecutor.ExternalFunctionExecutor, cache_: jdmn.runtime.cache.Cache.Cache) -> typing.Optional[str]:
        # Apply child decisions
        applicationRiskScore: typing.Optional[decimal.Decimal] = self.applicationRiskScore.apply(applicantData, annotationSet_, eventListener_, externalExecutor_, cache_)

        return PostBureauRiskCategoryTable.PostBureauRiskCategoryTable.instance().apply(None if (applicantData is None) else (applicantData.existingCustomer), applicationRiskScore, None if (bureauData is None) else (bureauData.creditScore), annotationSet_, eventListener_, externalExecutor_, cache_)
