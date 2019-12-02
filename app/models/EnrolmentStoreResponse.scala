/*
 * Copyright 2019 HM Revenue & Customs
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

package models

import play.api.Logger
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

sealed trait EnrolmentStoreResponse

object EnrolmentStoreResponse {

  implicit val format: Format[EnrolmentStore] = Json.format[EnrolmentStore]

  case class EnrolmentStore(principalUserIds: Seq[String], delegatedUserIds: Seq[String]) extends EnrolmentStoreResponse
  case object NotClaimed extends EnrolmentStoreResponse
  case object NoContent extends EnrolmentStoreResponse
  case object ServiceUnavailable extends EnrolmentStoreResponse
  case object Forbidden extends EnrolmentStoreResponse
  case object BadRequest extends EnrolmentStoreResponse
  case object ServerError extends EnrolmentStoreResponse
  case object AlreadyClaimed extends EnrolmentStoreResponse

  implicit lazy val httpReads: HttpReads[EnrolmentStoreResponse] =
    new HttpReads[EnrolmentStoreResponse] {
      override def read(method: String, url: String, response: HttpResponse): EnrolmentStoreResponse = {
        Logger.info(s"[AgentTrusts] response status received from ES0 api: ${response.status}, body :${response.body}")

        response.status match {
          case OK =>
            response.json.as[EnrolmentStore] match {
                case EnrolmentStore(Seq(), _) => NotClaimed
                case _ => AlreadyClaimed
            }
          case NO_CONTENT =>
            NotClaimed
          case SERVICE_UNAVAILABLE =>
            ServiceUnavailable
          case FORBIDDEN =>
            Forbidden
          case BAD_REQUEST =>
            BadRequest
          case _ =>
            ServerError
        }
      }
    }
}