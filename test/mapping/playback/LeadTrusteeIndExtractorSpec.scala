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

package mapping.playback

import java.time.LocalDate

import org.joda.time.DateTime
import base.SpecBaseHelpers
import generators.Generators
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.trustees._

class LeadTrusteeIndExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  val leadTrusteeIndExtractor : PlaybackExtractor[Option[DisplayTrustLeadTrusteeIndType]] =
    injector.instanceOf[LeadTrusteeIndExtractor]

  "Lead Trustee Individual Extractor" - {

    "when no lead trustee individual" - {

      "must return user answers" in {

        val leadTrustee = None

        val ua = UserAnswers("fakeId")

        val extraction = leadTrusteeIndExtractor.extract(ua, leadTrustee)

        extraction mustBe 'right

      }

    }

    "when there is a lead trustee individual" - {

      "with only nino return user answers updated" in {
        val leadTrustee = DisplayTrustLeadTrusteeIndType(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          name = NameType("First Name", None, "Last Name"),
          dateOfBirth = DateTime.parse("2018-02-01"),
          phoneNumber = "+441234567890",
          email = Some("test@test.com"),
          identification =
            DisplayTrustIdentificationType(
              safeId = Some("8947584-94759745-84758745"),
              nino = Some("NA1111111A"),
              passport = None,
              address = None
            ),
          entityStart = "2019-11-26"
        )

        val ua = UserAnswers("fakeId")

        val extraction = leadTrusteeIndExtractor.extract(ua, Some(leadTrustee))

        extraction.right.value.success.value.get(IsThisLeadTrusteePage(0)).get mustBe true
        extraction.right.value.success.value.get(TrusteeIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Individual
        extraction.right.value.success.value.get(TrusteesNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.success.value.get(TrusteesDateOfBirthPage(0)).get mustBe LocalDate.of(2018,2,1)
        extraction.right.value.success.value.get(TrusteeAUKCitizenPage(0)).get mustBe true
        extraction.right.value.success.value.get(TrusteesNinoPage(0)).get mustBe "NA1111111A"
        extraction.right.value.success.value.get(TrusteeLiveInTheUKPage(0)) mustNot be(defined)
        extraction.right.value.success.value.get(TrusteesUkAddressPage(0)) mustNot be(defined)
        extraction.right.value.success.value.get(TelephoneNumberPage(0)).get mustBe "+441234567890"
        extraction.right.value.success.value.get(EmailPage(0)).get mustBe "test@test.com"
        extraction.right.value.success.value.get(LeadTrusteeMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.success.value.get(TrusteesSafeIdPage(0)) must be(defined)
      }

      "with only address return user answers updated" in {
        val leadTrustee = DisplayTrustLeadTrusteeIndType(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          name = NameType("First Name", None, "Last Name"),
          dateOfBirth = DateTime.parse("2018-02-01"),
          phoneNumber = "+441234567890",
          email = Some("test@test.com"),
          identification =
            DisplayTrustIdentificationType(
              safeId = Some("8947584-94759745-84758745"),
              nino = None,
              passport = None,
              address = Some(AddressType("line 1", "line2", None, None, Some("NE11NE"), "GB"))
            ),
          entityStart = "2019-11-26"
        )

        val ua = UserAnswers("fakeId")

        val extraction = leadTrusteeIndExtractor.extract(ua, Some(leadTrustee))

        extraction.right.value.success.value.get(IsThisLeadTrusteePage(0)).get mustBe true
        extraction.right.value.success.value.get(TrusteeIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Individual
        extraction.right.value.success.value.get(TrusteesNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.success.value.get(TrusteesDateOfBirthPage(0)).get mustBe LocalDate.of(2018,2,1)
        extraction.right.value.success.value.get(TrusteeAUKCitizenPage(0)).get mustBe false
        extraction.right.value.success.value.get(TrusteesNinoPage(0)) mustNot be(defined)
        extraction.right.value.success.value.get(TrusteeLiveInTheUKPage(0)).get mustBe true
        extraction.right.value.success.value.get(TrusteesUkAddressPage(0)) must be(defined)
        extraction.right.value.success.value.get(TelephoneNumberPage(0)).get mustBe "+441234567890"
        extraction.right.value.success.value.get(EmailPage(0)).get mustBe "test@test.com"
        extraction.right.value.success.value.get(LeadTrusteeMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.success.value.get(TrusteesSafeIdPage(0)) must be(defined)
      }

    }

  }

}
