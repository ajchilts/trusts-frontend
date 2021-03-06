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

package mapping.registration

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping._
import models.core.pages.{FullName, IndividualOrBusiness, InternationalAddress, UKAddress}
import models.registration.pages.PassportOrIdCardDetails
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.settlors.living_settlor._

class IndividualSettlorsMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers  {

  val individualSettlorsMapper: Mapping[List[Settlor]] = injector.instanceOf[IndividualSettlorsMapper]

  val dateOfBirth: LocalDate = LocalDate.of(1944, 10, 10)

  "IndividualSettlorsMapper" - {

    "when user answers is empty" - {

      "must not be able to create a Settlor" in {

        val userAnswers = emptyUserAnswers

        individualSettlorsMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty " - {

      "must be able to create a Settlor with minimal data journey" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(0), FullName("First", None, "Last")).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(0), false).success.value
            .set(SettlorIndividualNINOYesNoPage(0), false).success.value
            .set(SettlorAddressYesNoPage(0), false).success.value

          individualSettlorsMapper.build(userAnswers).value mustBe List(Settlor(
            name = NameType("First", None, "Last"),
            dateOfBirth = None,
            identification = None
          )
        )
      }


      "must be able to create a Settlor with date of birth" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(0), FullName("First", None, "Last")).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(0), true).success.value
            .set(SettlorIndividualDateOfBirthPage(0), dateOfBirth).success.value
            .set(SettlorIndividualNINOYesNoPage(0), false).success.value
            .set(SettlorAddressYesNoPage(0), false).success.value

          individualSettlorsMapper.build(userAnswers).value mustBe List(Settlor(
            name = NameType("First", None, "Last"),
            dateOfBirth = Some(dateOfBirth),
            identification = None
          )
        )
      }

      "must be able to create a Settlor with a NINO and DOB" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(0), FullName("First", None, "Last")).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(0), true).success.value
            .set(SettlorIndividualDateOfBirthPage(0), dateOfBirth).success.value
            .set(SettlorIndividualNINOYesNoPage(0), true).success.value
            .set(SettlorIndividualNINOPage(0), "NH111111A").success.value
            .set(SettlorAddressYesNoPage(0), false).success.value

          individualSettlorsMapper.build(userAnswers).value mustBe List(Settlor(
            name = NameType("First", None, "Last"),
            dateOfBirth = Some(dateOfBirth),
            identification = Some(IdentificationType(Some("NH111111A"), None, None))
          )
        )
      }

      "must be able to create a Settlor with a UK address and DOB" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(0), FullName("First", None, "Last")).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(0), true).success.value
            .set(SettlorIndividualDateOfBirthPage(0), dateOfBirth).success.value
            .set(SettlorIndividualNINOYesNoPage(0), false).success.value
            .set(SettlorAddressYesNoPage(0), true).success.value
            .set(SettlorAddressUKYesNoPage(0), true).success.value
            .set(SettlorAddressUKPage(0), UKAddress("line1", "line2", Some("line3"), Some("Newcastle"), "ab1 1ab")).success.value

          individualSettlorsMapper.build(userAnswers).value mustBe List(Settlor(
            name = NameType("First", None, "Last"),
            dateOfBirth = Some(dateOfBirth),
            identification = Some(IdentificationType(None, None, Some(AddressType("line1", "line2", Some("line3"), Some("Newcastle"), Some("ab1 1ab"), "GB"))))
          )
        )
      }
    }

      "must be able to create a Settlor with an International address and DOB" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(0), FullName("First", None, "Last")).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(0), true).success.value
            .set(SettlorIndividualDateOfBirthPage(0), dateOfBirth).success.value
            .set(SettlorIndividualNINOYesNoPage(0), false).success.value
            .set(SettlorAddressYesNoPage(0), true).success.value
            .set(SettlorAddressUKYesNoPage(0), false).success.value
            .set(SettlorAddressInternationalPage(0), InternationalAddress("line1", "line2", Some("line3"), "FR")).success.value

          individualSettlorsMapper.build(userAnswers).value mustBe List(Settlor(
            name = NameType("First", None, "Last"),
            dateOfBirth = Some(dateOfBirth),
            identification = Some(IdentificationType(None, None,  Some(AddressType("line1", "line2", Some("line3"), None, None, "FR"))))
          )
        )
      }

    "must be able to create a Settlor with a passport and DOB" in {

      val expiryDate = LocalDate.of(2020, 10, 10)

      val userAnswers =
        emptyUserAnswers
          .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
          .set(SettlorIndividualNamePage(0), FullName("First", None, "Last")).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(0), true).success.value
          .set(SettlorIndividualDateOfBirthPage(0), dateOfBirth).success.value
          .set(SettlorAddressYesNoPage(0), true).success.value
          .set(SettlorIndividualNINOYesNoPage(0), false).success.value
          .set(SettlorIndividualPassportYesNoPage(0), true).success.value
          .set(SettlorIndividualPassportPage(0), PassportOrIdCardDetails("UK", "1234567", expiryDate)).success.value

        individualSettlorsMapper.build(userAnswers).value mustBe List(Settlor(
          name = NameType("First", None, "Last"),
          dateOfBirth = Some(dateOfBirth),
          identification = Some(IdentificationType(None, Some(PassportType("1234567", expiryDate,"UK")), None))
        )
      )
    }

    "must be able to create multiple Settlors" in {

      val expiryDate = LocalDate.of(2020, 10, 10)

      val userAnswers =
        emptyUserAnswers
          .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
          .set(SettlorIndividualNamePage(0), FullName("First", None, "Last")).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(0), true).success.value
          .set(SettlorIndividualDateOfBirthPage(0), dateOfBirth).success.value
          .set(SettlorAddressYesNoPage(0), true).success.value
          .set(SettlorIndividualNINOYesNoPage(0), false).success.value
          .set(SettlorIndividualPassportYesNoPage(0), true).success.value
          .set(SettlorIndividualPassportPage(0), PassportOrIdCardDetails("UK", "2345678", expiryDate)).success.value
          .set(SettlorIndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value
          .set(SettlorIndividualNamePage(1), FullName("Another", None, "Name")).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(1), false).success.value
          .set(SettlorAddressYesNoPage(1), true).success.value
          .set(SettlorIndividualNINOYesNoPage(1), false).success.value
          .set(SettlorIndividualPassportYesNoPage(1), true).success.value
          .set(SettlorIndividualPassportPage(1), PassportOrIdCardDetails("UK", "1234567", expiryDate)).success.value

      individualSettlorsMapper.build(userAnswers).value mustBe List(
        Settlor(
          NameType("First", None, "Last"),
          Some(dateOfBirth),
          Some(IdentificationType(None,Some(PassportType("2345678", expiryDate, "UK")), None))
        ),
        Settlor(
          NameType("Another", None, "Name"),
          None,
          Some(IdentificationType(None,Some(PassportType("1234567", expiryDate, "UK")),None))
        )
      )
    }
  }
}
