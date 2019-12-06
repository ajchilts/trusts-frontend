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

package controllers.register

import java.time.LocalDate

import base.SpecBase
import models.registration.pages.TrusteesBasedInTheUK.{InternationalAndUKTrustees, NonUkBasedTrustees, UKBasedTrustees}
import models.registration.pages.{NonResidentType, WhenTrustSetupPage}
import pages._
import pages.register.{AdministrationInsideUKPage, CountryAdministeringTrustPage, CountryGoverningTrustPage, EstablishedUnderScotsLawPage, GovernedInsideTheUKPage, InheritanceTaxActPage, NonResidentTypePage, RegisteringTrustFor5APage, TrustNamePage, TrustPreviouslyResidentPage, TrustResidentOffshorePage}
import pages.register.agents.AgentOtherThanBarristerPage
import pages.register.settlors.SettlorsBasedInTheUKPage
import pages.register.trustees.TrusteesBasedInTheUKPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.TrustDetailsAnswerPageView

class TrustDetailsAnswerPageControllerSpec extends SpecBase {

  "TrustDetailsAnswerPageController" when {

    "trust based in the UK" must {

      "return OK and the correct view for a GET" in {

        val answers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
            .set(EstablishedUnderScotsLawPage, true).success.value
            .set(TrustResidentOffshorePage, false).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val countryOptions = application.injector.instanceOf[CountryOptions]

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.trustName.value,
              checkYourAnswersHelper.whenTrustSetup.value,
              checkYourAnswersHelper.governedInsideTheUK.value,
              checkYourAnswersHelper.administrationInsideUK.value,
              checkYourAnswersHelper.trusteesBasedInUK.value,
              checkYourAnswersHelper.establishedUnderScotsLaw.value,
              checkYourAnswersHelper.trustResidentOffshore.value
            )
          )
        )

        val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustDetailsAnswerPageView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }
    }


    "trust contains International and UK based and registered under Scots law" must {

      "return OK and the correct view for a GET" in {

        val answers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
            .set(SettlorsBasedInTheUKPage, true).success.value
            .set(EstablishedUnderScotsLawPage, true).success.value
            .set(TrustResidentOffshorePage, false).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val countryOptions = application.injector.instanceOf[CountryOptions]

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.trustName.value,
              checkYourAnswersHelper.whenTrustSetup.value,
              checkYourAnswersHelper.governedInsideTheUK.value,
              checkYourAnswersHelper.administrationInsideUK.value,
              checkYourAnswersHelper.trusteesBasedInUK.value,
              checkYourAnswersHelper.settlorsBasedInTheUK.value,
              checkYourAnswersHelper.establishedUnderScotsLaw.value,
              checkYourAnswersHelper.trustResidentOffshore.value
            )
          )
        )

        val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustDetailsAnswerPageView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }
    }


    "trust not administered in the UK and registering for Schedule 5A" must {

      "return OK and the correct view for a GET" in {

        val answers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, NonUkBasedTrustees).success.value
            .set(RegisteringTrustFor5APage, true).success.value
            .set(NonResidentTypePage, NonResidentType.NonDomiciled).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val countryOptions = application.injector.instanceOf[CountryOptions]

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.trustName.value,
              checkYourAnswersHelper.whenTrustSetup.value,
              checkYourAnswersHelper.governedInsideTheUK.value,
              checkYourAnswersHelper.administrationInsideUK.value,
              checkYourAnswersHelper.trusteesBasedInUK.value,
              checkYourAnswersHelper.registeringTrustFor5A.value,
              checkYourAnswersHelper.nonresidentType.value
            )
          )
        )

        val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustDetailsAnswerPageView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "trust contains international and UK  settlors and registering for Schedule 5A" must {

      "return OK and the correct view for a GET" in {

        val answers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, InternationalAndUKTrustees).success.value
            .set(SettlorsBasedInTheUKPage, false).success.value
            .set(RegisteringTrustFor5APage, true).success.value
            .set(NonResidentTypePage, NonResidentType.NonDomiciled).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val countryOptions = application.injector.instanceOf[CountryOptions]

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.trustName.value,
              checkYourAnswersHelper.whenTrustSetup.value,
              checkYourAnswersHelper.governedInsideTheUK.value,
              checkYourAnswersHelper.administrationInsideUK.value,
              checkYourAnswersHelper.trusteesBasedInUK.value,
              checkYourAnswersHelper.settlorsBasedInTheUK.value,
              checkYourAnswersHelper.registeringTrustFor5A.value,
              checkYourAnswersHelper.nonresidentType.value
            )
          )
        )

        val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustDetailsAnswerPageView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

  }

    "trust not administered in the UK and registering for inheritance tax" must {

      "return OK and the correct view for a GET" in {

        val answers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrusteesBasedInTheUKPage, NonUkBasedTrustees).success.value
            .set(RegisteringTrustFor5APage, false).success.value
            .set(InheritanceTaxActPage, true).success.value
            .set(AgentOtherThanBarristerPage, true).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val countryOptions = application.injector.instanceOf[CountryOptions]

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.trustName.value,
              checkYourAnswersHelper.whenTrustSetup.value,
              checkYourAnswersHelper.governedInsideTheUK.value,
              checkYourAnswersHelper.administrationInsideUK.value,
              checkYourAnswersHelper.trusteesBasedInUK.value,
              checkYourAnswersHelper.registeringTrustFor5A.value,
              checkYourAnswersHelper.inheritanceTaxAct.value,
              checkYourAnswersHelper.agentOtherThanBarrister.value
            )
          )
        )

        val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustDetailsAnswerPageView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "trust not administered or governed in the UK but trustees resident in UK" must {

      "return OK and the correct view for a GET" in {

        val answers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(GovernedInsideTheUKPage, false).success.value
            .set(CountryGoverningTrustPage, "France").success.value
            .set(AdministrationInsideUKPage, false).success.value
            .set(CountryAdministeringTrustPage, "Spain").success.value
            .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
            .set(EstablishedUnderScotsLawPage, false).success.value
            .set(TrustResidentOffshorePage, true).success.value
            .set(TrustPreviouslyResidentPage, "France").success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val countryOptions = application.injector.instanceOf[CountryOptions]

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.trustName.value,
              checkYourAnswersHelper.whenTrustSetup.value,
              checkYourAnswersHelper.governedInsideTheUK.value,
              checkYourAnswersHelper.countryGoverningTrust.value,
              checkYourAnswersHelper.administrationInsideUK.value,
              checkYourAnswersHelper.countryAdministeringTrust.value,
              checkYourAnswersHelper.trusteesBasedInUK.value,
              checkYourAnswersHelper.establishedUnderScotsLaw.value,
              checkYourAnswersHelper.trustResidentOffshore.value,
              checkYourAnswersHelper.trustPreviouslyResident.value
            )
          )
        )

        val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustDetailsAnswerPageView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "redirect to the next page when valid data is submitted" in {

      val answers =
        emptyUserAnswers

      val application =
        applicationBuilder(userAnswers = Some(answers)).build()

      val request =
        FakeRequest(POST, routes.TrustDetailsAnswerPageController.onSubmit(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, routes.TrustDetailsAnswerPageController.onSubmit(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

  }
}