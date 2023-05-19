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

package uk.gov.hmrc.outcomeauditingstub.config

import play.api.Configuration
import uk.gov.hmrc.outcomeauditingstub.model.response.Response

import java.util.{List => jList}

import javax.inject.{Inject, Singleton}

@Singleton
class ConfigurationService @Inject()(config: Configuration) {

  val appName: String = config.get[String]("appName")

  val authToken: String = config.get[String]("microservice.outcome-auditing-stub.basicAuthToken")

  val ninoResponseMap: Map[String, Response] = {
    val c = config.get[Seq[String]]("microservice.outcome-auditing-stub.nino-responses")
      c.map(s => s.split(","))
      .map(sa => sa(0) -> (sa(1), sa(2)))
      .map{case (nino, (code, message)) => nino -> Response(code, message)}
      .toMap
  }

  val bankAccountResponseMap: Map[String, Response] = {
    val c = config.get[Seq[String]]("microservice.outcome-auditing-stub.bank-account-responses")
      c.map(s => s.split(","))
      .map(sa => sa(0) -> (sa(1), sa(2)))
      .map{case (nino, (code, message)) => nino -> Response(code, message)}
      .toMap
  }
}
