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

package connector

import config.FrontendAppConfig
import javax.inject.Inject

import mapping.Registration
import models.TrustResponse
import play.api.libs.json.{JsValue, Json, Writes}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TrustConnector @Inject()(http: HttpClient, config : FrontendAppConfig) {

  val trustsEndpoint = s"${config.trustsUrl}/trusts/register"

  def register(registration: Registration)(implicit  hc:HeaderCarrier ): Future[TrustResponse] = {

    val response = http.POST[JsValue, TrustResponse](trustsEndpoint, Json.toJson(registration))
    (implicitly[Writes[JsValue]], TrustResponse.httpReads, implicitly[HeaderCarrier](hc),global)

    response
  }

}

