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

import play.api.mvc._

import java.util.Base64
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class BasicAuthAction[B](username: String, password: String)(val parser: BodyParser[B])(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[Request, B] with ActionFilter[Request] {

  def filter[A](request: Request[A]): Future[Option[Result]] = {
    val result = request.headers.get("Authorization") map { authHeader =>
      Try(decodeBasicAuth(authHeader)) match {
        case Success((user, pass)) if (pass == password) => None
        case _                                           => Some(Results.Unauthorized)
      }
    } getOrElse Some(Results.Unauthorized)

    Future.successful(result)
  }

  private[this] def decodeBasicAuth(authHeader: String): (String, String) = {
    val baStr = authHeader.replaceFirst("Basic ", "")
    val decoded = Base64.getDecoder.decode(baStr)
    val Array(user, password) = new String(decoded).split(":")
    (user, password)
  }
}
