/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.outcomeauditingstub.controllers

import play.api.libs.json._
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.outcomeauditingstub.config.ConfigurationService
import uk.gov.hmrc.outcomeauditingstub.model.request.{BankAccountSubmissionType, InsightsOutcomeType, NinoSubmissionType, PaymentAllocationOutcometype, submissionAndOutcomeTypes}
import uk.gov.hmrc.outcomeauditingstub.model.response.Response
import uk.gov.hmrc.outcomeauditingstub.service.{BankAccountSubmissionHandler, NinoSubmissionHandler}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class OutcomeAuditingController @Inject()(config: ConfigurationService,
                                          ninoSubmissionHandler: NinoSubmissionHandler,
                                          bankAccountSubmissionHandler: BankAccountSubmissionHandler,
                                          cc: ControllerComponents)
  extends BackendController(cc) {

  private def WithBasicAuth = new BasicAuthAction[JsValue]("outcome-auditing-stub", config.authToken)(parse.json)

  def audit: Action[JsValue] = WithBasicAuth.async(parse.json) { implicit request =>
    import uk.gov.hmrc.outcomeauditingstub.model.response.Response.Implicits._

    val json = request.body.as[JsObject]
    Future.successful(
      submissionAndOutcomeTypes(json) match {
        case Right((NinoSubmissionType.value, InsightsOutcomeType.value))                 => ninoSubmissionHandler.handle(json)
        case Right((BankAccountSubmissionType.value, InsightsOutcomeType.value))          => bankAccountSubmissionHandler.handle(json)
        case Right((BankAccountSubmissionType.value, PaymentAllocationOutcometype.value)) => bankAccountSubmissionHandler.handle(json)
        case Right((st, ot))                                                              => Left[Response, Response](Response.outcomeNotSupported(st, ot))
        case Left(_)                                                                      => Left[Response, Response](Response.error)
      }
    ).map {
      case Right(okResponse)   => Ok(okResponse.toJson)
      case Left(errorResponse) => BadRequest(errorResponse.toJson)
    }
  }
}
