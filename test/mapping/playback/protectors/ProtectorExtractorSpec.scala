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

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractor
import models.core.pages.FullName
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import models.registration.pages.AddressOrUtr
import org.joda.time.DateTime
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.protectors.DoesTrustHaveAProtectorYesNoPage
import pages.register.protectors.company._
import pages.register.protectors.individual._

class ProtectorExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  val protectorExtractor : PlaybackExtractor[Option[DisplayTrustProtectorsType]] =
    injector.instanceOf[ProtectorExtractor]

  "Protector Extractor" - {

    "when no protectors" - {

      "must return false for doesTrustHaveAProtector given no individual protector and no company protector" in {

        val protector = DisplayTrustProtectorsType(None, None)

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

    "when there are protectors" - {

      "must return user answers updated with doesTrustHaveAProtector true" in {
        val protectors = DisplayTrustProtectorsType(
          protector = Some(
            List(
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
            )
          ),
          protectorCompany = Some(
            List(
              DisplayTrustProtectorCompany(
                lineNo = s"1",
                bpMatchStatus = Some("01"),
                name = s"Company 1",
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
        )

        val ua = UserAnswers("fakeId")

        val extraction = protectorExtractor.extract(ua, Some(protectors))

        extraction.right.value.get(DoesTrustHaveAProtectorYesNoPage()).get mustBe true
        
        extraction.right.value.get(IndividualProtectorNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.get(IndividualProtectorNINOYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualProtectorNINOPage(0)).get mustBe "1234567890"
        extraction.right.value.get(IndividualProtectorSafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(IndividualProtectorPassportIDCardYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorPassportIDCardPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")

        extraction.right.value.get(CompanyProtectorNamePage(0)).get mustBe "Company 1"
        extraction.right.value.get(CompanyProtectorSafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(CompanyProtectorAddressOrUtrPage(0)).get mustBe AddressOrUtr.Utr
        extraction.right.value.get(CompanyProtectorUtrPage(0)).get mustBe "1234567890"
        extraction.right.value.get(CompanyProtectorAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyProtectorAddressPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyProtectorMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
      }
    }
  }
}