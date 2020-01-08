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

package mapping.playback.protectors

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractor
import models.core.pages.{FullName, IndividualOrBusiness}
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.joda.time.DateTime
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.protectors.{DoesTrustHaveAProtectorYesNoPage, ProtectorIndividualOrBusinessPage}
import pages.register.protectors.business._
import pages.register.protectors.individual._

class ProtectorExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  val protectorExtractor : PlaybackExtractor[Option[DisplayTrustProtectorsType]] =
    injector.instanceOf[ProtectorExtractor]

  "Protector Extractor" - {

    "when no protectors" - {

      "must return false for doesTrustHaveAProtector given no individual protector and no company protector" in {

        val protector = DisplayTrustProtectorsType(Nil, Nil)

        val ua = UserAnswers("fakeId")

        val extraction = protectorExtractor.extract(ua, Some(protector))

        extraction.right.value.get(DoesTrustHaveAProtectorYesNoPage()).get mustBe false
      }

      "must return false for doesTrustHaveAProtector given no protector" in {

        val ua = UserAnswers("fakeId")

        val extraction = protectorExtractor.extract(ua, None)

        extraction.right.value.get(DoesTrustHaveAProtectorYesNoPage()).get mustBe false
      }
    }

    "when there are individual protectors" - {

      "must return user answers updated with doesTrustHaveAProtector true" in {
        val protectors = DisplayTrustProtectorsType(
          protector = List(
            DisplayTrustProtector(
              lineNo = s"1",
              bpMatchStatus = Some("01"),
              name = NameType(s"First Name", None, s"Last Name"),
              dateOfBirth = Some(DateTime.parse("1970-02-01")),
              identification = Some(
                DisplayTrustIdentificationType(
                  safeId = Some("8947584-94759745-84758745"),
                  nino = Some(s"1234567890"),
                  passport = None,
                  address = None
                )
              ),
              entityStart = "2019-11-26"
            ),
            DisplayTrustProtector(
              lineNo = s"2",
              bpMatchStatus = Some("02"),
              name = NameType(s"First Name", None, s"Last Name"),
              dateOfBirth = Some(DateTime.parse("1980-02-01")),
              identification = Some(
                DisplayTrustIdentificationType(
                  safeId = Some("1234567-12345678-12345678"),
                  nino = Some(s"0987654321"),
                  passport = None,
                  address = None
                )
              ),
              entityStart = "2019-11-27"
            )
          ),
          protectorCompany = Nil
        )

        val ua = UserAnswers("fakeId")

        val extraction = protectorExtractor.extract(ua, Some(protectors))

        extraction.right.value.get(DoesTrustHaveAProtectorYesNoPage()).get mustBe true

        extraction.right.value.get(ProtectorIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Individual
        extraction.right.value.get(IndividualProtectorNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.get(IndividualProtectorDateOfBirthYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualProtectorDateOfBirthPage(0)).get mustBe LocalDate.of(1970, 2, 1)
        extraction.right.value.get(IndividualProtectorNINOYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualProtectorNINOPage(0)).get mustBe "1234567890"
        extraction.right.value.get(IndividualProtectorSafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(IndividualProtectorPassportIDCardYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorPassportIDCardPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")

        extraction.right.value.get(ProtectorIndividualOrBusinessPage(1)).get mustBe IndividualOrBusiness.Individual
        extraction.right.value.get(IndividualProtectorNamePage(1)).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.get(IndividualProtectorDateOfBirthYesNoPage(1)).get mustBe true
        extraction.right.value.get(IndividualProtectorDateOfBirthPage(1)).get mustBe LocalDate.of(1980, 2, 1)
        extraction.right.value.get(IndividualProtectorNINOYesNoPage(1)).get mustBe true
        extraction.right.value.get(IndividualProtectorNINOPage(1)).get mustBe "0987654321"
        extraction.right.value.get(IndividualProtectorSafeIdPage(1)).get mustBe "1234567-12345678-12345678"
        extraction.right.value.get(IndividualProtectorPassportIDCardYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorPassportIDCardPage(1)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressUKYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorMetaData(1)).get mustBe MetaData("2", Some("02"), "2019-11-27")
      }
    }

    "when there are business protectors" - {

      "must return user answers updated with doesTrustHaveAProtector true" in {
        val protectors = DisplayTrustProtectorsType(
          protector = Nil,
          protectorCompany = List(
            DisplayTrustProtectorBusiness(
              lineNo = s"1",
              bpMatchStatus = Some("01"),
              name = s"Business 1",
              identification = Some(
                DisplayTrustIdentificationOrgType(
                  safeId = Some("8947584-94759745-84758745"),
                  utr = Some("1234567890"),
                  address = None
                )
              ),
              entityStart = "2019-11-26"
            ),
            DisplayTrustProtectorBusiness(
              lineNo = s"2",
              bpMatchStatus = Some("02"),
              name = s"Business 2",
              identification = Some(
                DisplayTrustIdentificationOrgType(
                  safeId = Some("1234567-12345678-12345678"),
                  utr = Some("0987654321"),
                  address = None
                )
              ),
              entityStart = "2019-11-27"
            )
          )
        )

        val ua = UserAnswers("fakeId")

        val extraction = protectorExtractor.extract(ua, Some(protectors))

        extraction.right.value.get(DoesTrustHaveAProtectorYesNoPage()).get mustBe true

        extraction.right.value.get(ProtectorIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Business
        extraction.right.value.get(BusinessProtectorNamePage(0)).get mustBe "Business 1"
        extraction.right.value.get(BusinessProtectorSafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(BusinessProtectorUtrYesNoPage(0)).get mustBe true
        extraction.right.value.get(BusinessProtectorUtrPage(0)).get mustBe "1234567890"
        extraction.right.value.get(BusinessProtectorAddressYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(BusinessProtectorAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(BusinessProtectorAddressPage(0)) mustNot be(defined)
        extraction.right.value.get(BusinessProtectorMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")

        extraction.right.value.get(ProtectorIndividualOrBusinessPage(1)).get mustBe IndividualOrBusiness.Business
        extraction.right.value.get(BusinessProtectorNamePage(1)).get mustBe "Business 2"
        extraction.right.value.get(BusinessProtectorSafeIdPage(1)).get mustBe "1234567-12345678-12345678"
        extraction.right.value.get(BusinessProtectorUtrYesNoPage(1)).get mustBe true
        extraction.right.value.get(BusinessProtectorUtrPage(1)).get mustBe "0987654321"
        extraction.right.value.get(BusinessProtectorAddressYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(BusinessProtectorAddressUKYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(BusinessProtectorAddressPage(1)) mustNot be(defined)
        extraction.right.value.get(BusinessProtectorMetaData(1)).get mustBe MetaData("2", Some("02"), "2019-11-27")
      }
    }

    "when there are individual and business protectors" - {

      "must return user answers updated with doesTrustHaveAProtector true" in {
        val protectors = DisplayTrustProtectorsType(
          protector = List(
            DisplayTrustProtector(
              lineNo = s"1",
              bpMatchStatus = Some("01"),
              name = NameType(s"First Name", None, s"Last Name"),
              dateOfBirth = Some(DateTime.parse("1970-02-01")),
              identification = Some(
                DisplayTrustIdentificationType(
                  safeId = Some("8947584-94759745-84758745"),
                  nino = Some(s"1234567890"),
                  passport = None,
                  address = None
                )
              ),
              entityStart = "2019-11-26"
            )
          ),
          protectorCompany = List(
            DisplayTrustProtectorBusiness(
              lineNo = s"1",
              bpMatchStatus = Some("01"),
              name = s"Business 1",
              identification = Some(
                DisplayTrustIdentificationOrgType(
                  safeId = Some("8947584-94759745-84758745"),
                  utr = Some("1234567890"),
                  address = None
                )
              ),
              entityStart = "2019-11-26"
            )
          )
        )

        val ua = UserAnswers("fakeId")

        val extraction = protectorExtractor.extract(ua, Some(protectors))

        extraction.right.value.get(DoesTrustHaveAProtectorYesNoPage()).get mustBe true

        extraction.right.value.get(ProtectorIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Individual
        extraction.right.value.get(IndividualProtectorNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.get(IndividualProtectorNINOYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualProtectorNINOPage(0)).get mustBe "1234567890"
        extraction.right.value.get(IndividualProtectorSafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(IndividualProtectorPassportIDCardYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorPassportIDCardPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")

        extraction.right.value.get(ProtectorIndividualOrBusinessPage(1)).get mustBe IndividualOrBusiness.Business
        extraction.right.value.get(BusinessProtectorNamePage(1)).get mustBe "Business 1"
        extraction.right.value.get(BusinessProtectorSafeIdPage(1)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(BusinessProtectorUtrYesNoPage(1)).get mustBe true
        extraction.right.value.get(BusinessProtectorUtrPage(1)).get mustBe "1234567890"
        extraction.right.value.get(BusinessProtectorAddressYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(BusinessProtectorAddressUKYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(BusinessProtectorAddressPage(1)) mustNot be(defined)
        extraction.right.value.get(BusinessProtectorMetaData(1)).get mustBe MetaData("1", Some("01"), "2019-11-26")
      }
    }
  }
}
