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

package uk.gov.hmrc.outcomeauditingstub.model

import play.api.libs.json._

object request {

  val exampleRequestJson = Json.parse(
    """{
      |  "correlationId": "33df37a4-a535-41fe-8032-7ab718b45526",
      |  "submitter": "ipp",
      |  "submission": {
      |    "submissionType": "bank-account",
      |    "submissionAttribute": {
      |      "sortCode": "608580",
      |      "accountNumber": "48835625"
      |    }
      |  },
      |  "outcome": {
      |    "outcomeType": "PAYMENT_ALLOCATION",
      |    "decision": "PAYMENT_ALLOCATED",
      |    "reasons": "ACCOUNT_ALLOCATED_TO_DETAILS",
      |    "evidence": {
      |      "sa_utr": "0123456789",
      |      "paye_ref": "ABC/A1234",
      |      "full_name": "Jane Smith",
      |      "user_id": "0123456789112345"
      |    }
      |  }
      |}""".stripMargin)

  val submitter: Reads[JsString] = (__ \ "submitter").json.pick[JsString]
  val submission: Reads[JsObject] = (__ \ "submission").json.pickBranch
  val submissionType: Reads[JsString] = (__ \ "submissionType").json.pick[JsString]
  val submissionAttribute: Reads[JsObject] = (__ \ "submissionAttribute").json.pickBranch
  val ninoSubmissionAttribute: Reads[JsString] = (__ \ "submission" \ "submissionAttribute" \ "nino").json.pick[JsString]
  val bankAccountSortCodeSubmissionAttribute: Reads[JsString] = (__ \"submission" \ "submissionAttribute" \ "sortCode").json.pick[JsString]
  val bankAccountNumberSubmissionAttribute: Reads[JsString] = (__ \ "submission" \ "submissionAttribute" \ "accountNumber").json.pick[JsString]
  val outcome: Reads[JsObject] = (__ \ "outcome").json.pickBranch
  val outcomeType: Reads[JsString] = (__ \ "outcomeType").json.pick[JsString]

  def submissionAndOutcomeTypes(outcomeRequestJson: JsValue): Either[String, (String, String)] = (
    (for {
      submission <- outcomeRequestJson.transform(submission).map(_.fieldSet.head._2)
      submissionType <- submission.transform(submissionType)
      outcome <- outcomeRequestJson.transform(outcome).map(_.fieldSet.head._2)
      outcomeType <- outcome.transform(outcomeType)
    } yield (submissionType.value, outcomeType.value)).asEither
    ) match {
    case Left(_)  => Left("Invalid request json")
    case Right(r) => Right(r)
  }

  sealed abstract class SubmissionType(val value: String)
  case object NinoSubmissionType extends SubmissionType("nino")
  case object BankAccountSubmissionType extends SubmissionType("bank-account")

  sealed abstract class OutcomeType(val value: String)
  case object InsightsOutcomeType extends OutcomeType("insights")
  case object PaymentAllocationOutcometype extends OutcomeType("payment-allocation")

}
