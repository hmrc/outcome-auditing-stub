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

package uk.gov.hmrc.outcomeauditingstub.service

import akka.util.Helpers.Requiring
import play.api.libs.json.JsObject
import uk.gov.hmrc.outcomeauditingstub.config.ConfigurationService
import uk.gov.hmrc.outcomeauditingstub.model.request
import uk.gov.hmrc.outcomeauditingstub.model.response.Response

import javax.inject.Inject

class BankAccountSubmissionHandler @Inject()(configuration: ConfigurationService) {
  def handle(json: JsObject): Either[Response, Response] = {
    (for {
      sortCode <- json.transform(request.bankAccountSortCodeSubmissionAttribute)
        accountNumber <- json.transform(request.bankAccountNumberSubmissionAttribute)
    } yield (sortCode, accountNumber))
      .fold(
      error =>
        Left(Response.error),
        bdJs => {
        val baDetails = s"${bdJs._1.value}:${bdJs._2.value}"
        val baDetailsResponse = configuration.bankAccountResponseMap.get(baDetails)
        Either.cond(baDetailsResponse.isDefined, baDetailsResponse.get, Response.error)
      }
    )
  }
}
