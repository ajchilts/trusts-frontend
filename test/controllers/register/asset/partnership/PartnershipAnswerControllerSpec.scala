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

package controllers.register.asset.partnership

import java.time.{LocalDate, ZoneOffset}

import base.RegistrationSpecBase
import controllers.register.routes._
import models.NormalMode
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset.Partnership
import pages.entitystatus.AssetStatus
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.partnership.{PartnershipDescriptionPage, PartnershipStartDatePage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.asset.partnership.PartnershipAnswersView

class PartnershipAnswerControllerSpec extends RegistrationSpecBase {

  def onwardRoute = Call("GET", "/foo")

  val index: Int = 0
  val validDate: LocalDate = LocalDate.now(ZoneOffset.UTC)

  lazy val partnershipAnswerRoute: String = routes.PartnershipAnswerController.onPageLoad(index, fakeDraftId).url

  "PartnershipAnswer Controller" must {

      "return OK and the correct view for a GET" in {

        val userAnswers =
          emptyUserAnswers
            .set(WhatKindOfAssetPage(index), Partnership).success.value
            .set(PartnershipDescriptionPage(index), "Partnership Description").success.value
            .set(PartnershipStartDatePage(index), validDate).success.value
            .set(AssetStatus(index), Completed).success.value

        val countryOptions = injector.instanceOf[CountryOptions]
        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.whatKindOfAsset(index).value,
              checkYourAnswersHelper.partnershipDescription(index).value,
              checkYourAnswersHelper.partnershipStartDate(index).value
            )
          )
        )

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, partnershipAnswerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[PartnershipAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(index, fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }


      "redirect to PartnershipDescription page on a GET if no answer for 'What is the description for the partnership?' at index" in {

        val answers =
          emptyUserAnswers
            .set(WhatKindOfAssetPage(index), Partnership).success.value
            .set(PartnershipStartDatePage(index), validDate).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val request = FakeRequest(GET, partnershipAnswerRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.PartnershipDescriptionController.onPageLoad(NormalMode, index, fakeDraftId).url

        application.stop()

      }

    "redirect to PartnershipStartDate page on a GET if no answer for 'When did the partnership start?' at index" in {

      val answers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(index), Partnership).success.value
          .set(PartnershipDescriptionPage(index), "Partnership Description").success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, partnershipAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.PartnershipStartDateController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()

    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, partnershipAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

  }
}
