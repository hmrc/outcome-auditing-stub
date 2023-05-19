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

import play.api.libs.json.JsObject
import uk.gov.hmrc.outcomeauditingstub.config.ConfigurationService
import uk.gov.hmrc.outcomeauditingstub.model.request.ninoSubmissionAttribute
import uk.gov.hmrc.outcomeauditingstub.model.response.Response

import javax.inject.Inject

class NinoSubmissionHandler @Inject()(configuration: ConfigurationService) {
  def handle(json: JsObject): Either[Response, Response] = {
    json.transform(ninoSubmissionAttribute).fold(
      error =>
        Left(Response.error),
      ninoJs => {
        val nino = ninoJs.value
        val ninoResponse = configuration.ninoResponseMap.get(nino)
        Either.cond(ninoResponse.isDefined, ninoResponse.get, Response.error)
      }
    )
  }
}
