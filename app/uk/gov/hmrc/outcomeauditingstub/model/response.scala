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

import play.api.libs.json.{Format, JsValue, Json}

object response {
  case class Response(code: String, message: String)
  object Response {
    private val okCode: String = "ok"
    private val parseErrorCode = "parse-error"
    private val errorCode = "error"

    def ok(message: String): Response = Response(okCode, message)
    def parseError: Response = Response(parseErrorCode, "could not parse json body")
    def error: Response = Response(errorCode, "could not process outcome")
    def outcomeNotSupported(st: String, ot: String): Response = Response(errorCode, s"outcomes not supported for '$st', '$ot'")

    object Implicits {
      implicit val responseFormat: Format[Response] = Json.format[Response]

      implicit class ResponseWithJson(r: Response) {
        def toJson: JsValue = Json.toJson(r)
      }
    }
  }
}
