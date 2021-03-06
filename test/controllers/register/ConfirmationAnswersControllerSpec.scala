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

package controllers.register

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneOffset}

import base.RegistrationSpecBase
import models.RegistrationSubmission.AllStatus
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.Completed
import models.registration.pages.TrusteesBasedInTheUK.UKBasedTrustees
import models.registration.pages._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import pages.entitystatus._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.shares._
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import pages.register.settlors.deceased_settlor._
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage}
import pages.register.settlors.{AddASettlorPage, SetUpAfterSettlorDiedYesNoPage}
import pages.register.trust_details._
import pages.register.trustees._
import pages.register.trustees.individual._
import pages.register.{RegistrationSubmissionDatePage, RegistrationTRNPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DraftRegistrationService
import utils.countryOptions.CountryOptions
import utils.{CheckYourAnswersHelper, TestUserAnswers}
import viewmodels.{AnswerSection, RegistrationAnswerSections}
import views.html.register.ConfirmationAnswerPageView

import scala.concurrent.Future

class ConfirmationAnswersControllerSpec extends RegistrationSpecBase {

  val index = 0

  "ConfirmationAnswersController" must {

    val beneficiarySections = List(
      AnswerSection(
        Some("headingKey1"),
        List.empty,
        Some("sectionKey1")
      ),
      AnswerSection(
        Some("headingKey2"),
        List.empty,
        Some("sectionKey2")
      )
    )

    val registrationSections = RegistrationAnswerSections(
      beneficiaries = Some(beneficiarySections)
    )

    when(registrationsRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus(Some(Completed))))
    when(mockCreateDraftRegistrationService.getAnswerSections(any())(any())).thenReturn(Future.successful(registrationSections))

    "return OK and the correct view for a GET when tasklist completed" in {

      val userAnswers =
        TestUserAnswers.emptyUserAnswers
          .set(TrustNamePage, "New Trust").success.value
          .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
          .set(GovernedInsideTheUKPage, true).success.value
          .set(AdministrationInsideUKPage, true).success.value
          .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
          .set(EstablishedUnderScotsLawPage, true).success.value
          .set(TrustResidentOffshorePage, false).success.value
          .set(TrustDetailsStatus, Completed).success.value

          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("First", None, "Trustee")).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.now(ZoneOffset.UTC)).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteesNinoPage(index), "AB123456C").success.value
          .set(TelephoneNumberPage(index), "0191 1111111").success.value
          .set(TrusteeAddressInTheUKPage(index), true).success.value
          .set(TrusteesUkAddressPage(index), UKAddress("Line1", "Line2", None, None, "NE62RT")).success.value
          .set(TrusteeStatus(index), Status.Completed).success.value
          .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
          .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
          .set(SettlorDateOfDeathYesNoPage, true).success.value
          .set(SettlorDateOfDeathPage, LocalDate.now).success.value
          .set(SettlorDateOfBirthYesNoPage, true).success.value
          .set(SettlorsDateOfBirthPage, LocalDate.now).success.value
          .set(SettlorsNationalInsuranceYesNoPage, true).success.value
          .set(SettlorNationalInsuranceNumberPage, "AB123456C").success.value
          .set(SettlorsLastKnownAddressYesNoPage, true).success.value
          .set(WasSettlorsAddressUKYesNoPage, true).success.value
          .set(SettlorsUKAddressPage, UKAddress("Line1", "Line2", None, None, "NE62RT")).success.value

          .set(WhatKindOfAssetPage(index), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(index), "100").success.value
          .set(AssetStatus(index), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(1), true).success.value
          .set(SharePortfolioNamePage(1), "Company").success.value
          .set(SharePortfolioOnStockExchangePage(1), true).success.value
          .set(SharePortfolioQuantityInTrustPage(1), "1234").success.value
          .set(SharePortfolioValueInTrustPage(1), "4000").success.value
          .set(AssetStatus(1), Completed).success.value
          .set(AddAssetsPage, NoComplete).success.value

          .set(DeceasedSettlorStatus, Status.Completed).success.value

          .set(RegistrationTRNPage, "XNTRN000000001").success.value
          .set(RegistrationSubmissionDatePage, LocalDateTime.now).success.value


      val countryOptions = injector.instanceOf[CountryOptions]
      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = false)
      val leadTrusteeIndividualOrBusinessMessagePrefix = "leadTrusteeIndividualOrBusiness"
      val leadTrusteeFullNameMessagePrefix = "leadTrusteesName"

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.trustName.value,
            checkYourAnswersHelper.whenTrustSetup.value
          ),
          Some("Trust details")
        ),
        AnswerSection(
          None,
          Seq(checkYourAnswersHelper.setUpAfterSettlorDied.value,
            checkYourAnswersHelper.deceasedSettlorsName.value,
            checkYourAnswersHelper.deceasedSettlorDateOfDeathYesNo.value,
            checkYourAnswersHelper.deceasedSettlorDateOfDeath.value,
            checkYourAnswersHelper.deceasedSettlorDateOfBirthYesNo.value,
            checkYourAnswersHelper.deceasedSettlorsDateOfBirth.value,
            checkYourAnswersHelper.deceasedSettlorsNINoYesNo.value,
            checkYourAnswersHelper.deceasedSettlorNationalInsuranceNumber.value,
            checkYourAnswersHelper.deceasedSettlorsLastKnownAddressYesNo.value,
            checkYourAnswersHelper.wasSettlorsAddressUKYesNo.value,
            checkYourAnswersHelper.deceasedSettlorsUKAddress.value
          ),
          Some("Settlor")
        ),
        AnswerSection(
          Some("Trustee 1"),
          Seq(
            checkYourAnswersHelper.trusteeIndividualOrBusiness(index, leadTrusteeIndividualOrBusinessMessagePrefix).value,
            checkYourAnswersHelper.trusteeFullName(index, leadTrusteeFullNameMessagePrefix).value,
            checkYourAnswersHelper.trusteesDateOfBirth(index).value,
            checkYourAnswersHelper.trusteeAUKCitizen(index).value,
            checkYourAnswersHelper.trusteesNino(index).value,
            checkYourAnswersHelper.trusteeLiveInTheUK(index).value,
            checkYourAnswersHelper.trusteesUkAddress(index).value,
            checkYourAnswersHelper.telephoneNumber(index).value
          ),
          Some("Trustees")
        ),
        beneficiarySections(0),
        beneficiarySections(1)
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .build()

      val request = FakeRequest(GET, routes.ConfirmationAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationAnswerPageView]

      status(result) mustEqual OK

      val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      val trnDateTime = LocalDateTime.now.format(dateFormatter)

      contentAsString(result) mustEqual
        view(expectedSections, "XNTRN000000001", trnDateTime)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to tasklist page when tasklist not completed" in {

      val userAnswers =
        TestUserAnswers.emptyUserAnswers
          .set(TrustNamePage, "New Trust").success.value
          .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
          .set(GovernedInsideTheUKPage, true).success.value
          .set(AdministrationInsideUKPage, true).success.value
          .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
          .set(EstablishedUnderScotsLawPage, true).success.value
          .set(TrustResidentOffshorePage, false).success.value
          .set(TrustDetailsStatus, Completed).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.ConfirmationAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TaskListController.onPageLoad(fakeDraftId).url

      application.stop()

    }

    "return OK and the correct view for a GET when tasklist completed with living settlor" in {

      val userAnswers =
        TestUserAnswers.emptyUserAnswers
          .set(TrustNamePage, "New Trust").success.value
          .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
          .set(GovernedInsideTheUKPage, true).success.value
          .set(AdministrationInsideUKPage, true).success.value
          .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
          .set(EstablishedUnderScotsLawPage, true).success.value
          .set(TrustResidentOffshorePage, false).success.value
          .set(TrustDetailsStatus, Completed).success.value

          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("First", None, "Trustee")).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.now(ZoneOffset.UTC)).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteesNinoPage(index), "AB123456C").success.value
          .set(TelephoneNumberPage(index), "0191 1111111").success.value
          .set(TrusteeAddressInTheUKPage(index), true).success.value
          .set(TrusteesUkAddressPage(index), UKAddress("line1", "line2", Some("line3"), Some("line4"), "AB1 1AB")).success.value
          .set(TrusteeStatus(index), Status.Completed).success.value
          .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
          .set(HoldoverReliefYesNoPage, true).success.value
          .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(SettlorIndividualNamePage(index), FullName("First", None, "Settlor")).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(index), true).success.value
          .set(SettlorIndividualDateOfBirthPage(index), LocalDate.now).success.value
          .set(SettlorIndividualNINOYesNoPage(index), true).success.value
          .set(SettlorIndividualNINOPage(index), "AB123456C").success.value
          .set(LivingSettlorStatus(index), Status.Completed).success.value
          .set(AddASettlorPage, AddASettlor.NoComplete).success.value

          .set(WhatKindOfAssetPage(index), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(index), "100").success.value
          .set(AssetStatus(index), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(1), true).success.value
          .set(SharePortfolioNamePage(1), "Company").success.value
          .set(SharePortfolioOnStockExchangePage(1), true).success.value
          .set(SharePortfolioQuantityInTrustPage(1), "1234").success.value
          .set(SharePortfolioValueInTrustPage(1), "4000").success.value
          .set(AssetStatus(1), Completed).success.value
          .set(AddAssetsPage, NoComplete).success.value

          .set(RegistrationTRNPage, "XNTRN000000001").success.value
          .set(RegistrationSubmissionDatePage, LocalDateTime.now).success.value


      val countryOptions = injector.instanceOf[CountryOptions]
      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = false)
      val leadTrusteeIndividualOrBusinessMessagePrefix = "leadTrusteeIndividualOrBusiness"
      val leadTrusteeFullNameMessagePrefix = "leadTrusteesName"

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.trustName.value,
            checkYourAnswersHelper.whenTrustSetup.value
          ),
          Some("Trust details")
        ),
        AnswerSection(
          headingKey = Some("Settlor 1"),
          Seq(checkYourAnswersHelper.setUpAfterSettlorDied.value,
            checkYourAnswersHelper.kindOfTrust.value,
            checkYourAnswersHelper.holdoverReliefYesNo.value,
            checkYourAnswersHelper.settlorIndividualOrBusiness(index).value,
            checkYourAnswersHelper.settlorIndividualName(index).value,
            checkYourAnswersHelper.settlorIndividualDateOfBirthYesNo(index).value,
            checkYourAnswersHelper.settlorIndividualDateOfBirth(index).value,
            checkYourAnswersHelper.settlorIndividualNINOYesNo(index).value,
            checkYourAnswersHelper.settlorIndividualNINO(index).value
          ),
          Some("Settlors")
        ),
        AnswerSection(
          headingKey = Some("Trustee 1"),
          Seq(
            checkYourAnswersHelper.trusteeIndividualOrBusiness(index, leadTrusteeIndividualOrBusinessMessagePrefix).value,
            checkYourAnswersHelper.trusteeFullName(index, leadTrusteeFullNameMessagePrefix).value,
            checkYourAnswersHelper.trusteesDateOfBirth(index).value,
            checkYourAnswersHelper.trusteeAUKCitizen(index).value,
            checkYourAnswersHelper.trusteesNino(index).value,
            checkYourAnswersHelper.trusteeLiveInTheUK(index).value,
            checkYourAnswersHelper.trusteesUkAddress(index).value,
            checkYourAnswersHelper.telephoneNumber(index).value
          ),
          Some("Trustees")
        ),
        beneficiarySections(0),
        beneficiarySections(1)
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.ConfirmationAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationAnswerPageView]

      status(result) mustEqual OK

      val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      val trnDateTime = LocalDateTime.now.format(dateFormatter)

      contentAsString(result) mustEqual
        view(expectedSections, "XNTRN000000001", trnDateTime)(fakeRequest, messages).toString

      application.stop()
    }

  }
}
