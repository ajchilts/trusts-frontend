/*
 * Copyright 2020 HM Revenue & Customs
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

package mapping.reads

import java.time.LocalDate

import generators.{Generators, ModelGenerators}
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsSuccess, Json}

class TrusteeReadsSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with Generators with ModelGenerators {

  "Trustee" - {

    "must deserialise" - {

      "from a trustee individual" in {

        forAll(arbitrary[LocalDate], arbitrary[FullName], arbitrary[String], arbitrary[UKAddress]) {
          (date, fullName, str, address) =>

            val json = Json.obj(
              "name" -> fullName,
              "dateOfBirth" -> date,
              "nino" -> str,
              "address" -> address,
              "isThisLeadTrustee" -> false,
              "individualOrBusiness" -> IndividualOrBusiness.Individual.toString
            )

            json.validate[Trustee] mustEqual JsSuccess(TrusteeIndividual(false, fullName, Some(date), Some(str), Some(address)))
        }
      }

      "from a lead trustee individual" in {

        forAll(
          arbitrary[LocalDate],
          arbitrary[FullName],
          arbitrary[UKAddress],
          Gen.const(IndividualOrBusiness.Individual)
        ){
          (date, fullName, address, individual) =>
            val json = Json.obj(
              "nino" -> "QQ12121212",
              "name" -> fullName,
              "telephoneNumber" -> "+440101010101",
              "dateOfBirth" -> date,
              "isUKCitizen" -> true,
              "isThisLeadTrustee" -> true,
              "address" -> address,
              "addressUKYesNo" -> true,
              "individualOrBusiness" -> individual.toString
            )

            json.validate[Trustee] mustEqual JsSuccess(
              LeadTrusteeIndividual(
                isLead = true,
                name = fullName,
                dateOfBirth = date,
                nino = Some("QQ12121212"),
                isUKCitizen = true,
                liveInUK = true,
                telephoneNumber = "+440101010101",
                passport = None,
                address = address
              )
            )
        }

      }

      "from a trustee organisation" in {

        forAll(arbitrary[String], arbitrary[UKAddress]) {
          (str, address) =>

            val json = Json.obj(
              "name" -> str,
              "utr" -> str,
              "address" -> address,
              "isThisLeadTrustee" -> false,
              "individualOrBusiness" -> IndividualOrBusiness.Business.toString
            )

            json.validate[Trustee] mustEqual JsSuccess(TrusteeOrganisation(false, str, Some(str), Some(address)))
        }
      }

      "from a lead trustee organisation" in {

        forAll(
          arbitrary[String],
          arbitrary[UKAddress],
          Gen.const(IndividualOrBusiness.Business)
        ){
          (str, address, business) =>
            val json = Json.obj(
              "nino" -> "QQ12121212",
              "name" -> str,
              "telephoneNumber" -> "+440101010101",
              "utr" -> str,
              "isUKBusiness" -> true,
              "isThisLeadTrustee" -> true,
              "address" -> address,
              "addressUKYesNo" -> true,
              "individualOrBusiness" -> business.toString
            )

            json.validate[Trustee] mustEqual JsSuccess(
              LeadTrusteeOrganisation(
                isLead = true,
                name = str,
                isUKBusiness = true,
                utr = Some(str),
                liveInUK = true,
                address = address,
                telephoneNumber = "+440101010101"
              )
            )
        }

      }

    }

  }

}
