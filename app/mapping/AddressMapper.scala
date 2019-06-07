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

package mapping
import models.{Address, InternationalAddress, UKAddress, UserAnswers}
import pages.QuestionPage

class AddressMapper  {

  def build(userAnswers           : UserAnswers,
            isUk                  : QuestionPage[Boolean],
            ukAddress             : QuestionPage[UKAddress],
            internationalAddress  : QuestionPage[InternationalAddress]) : Option[AddressType] = {

    userAnswers.get(isUk) flatMap {
      uk =>
        if(uk) {
          buildUkAddress(userAnswers.get(ukAddress))
        } else {
          buildInternationalAddress(userAnswers.get(internationalAddress))
        }
    }
  }

   private def buildUkAddress(address: Option[models.UKAddress]): Option[AddressType] = {
    address.map{
      x=>
        val line2 = x.line2.getOrElse(x.townOrCity)
        val townOrCity = if(line2==x.townOrCity) None else Some(x.townOrCity)

        AddressType(
          x.line1,
          line2,
          x.line3,
          townOrCity,
          Some(x.postcode),
          "GB")
    }
  }

  private def buildInternationalAddress(address: Option[models.InternationalAddress]): Option[AddressType] = {
    address.map{
      x=>
        AddressType(
          x.line1,
          x.line2,
          x.line3,
          x.line4,
          None,
          x.country
        )
    }
  }

  def build (ukOrInternationalAddress : Option[Address]): Option[AddressType] = {
    ukOrInternationalAddress flatMap {
      case ukAddress : UKAddress => buildUkAddress(Some(ukAddress))
      case international : InternationalAddress => buildInternationalAddress(Some(international))
    }

  }


}