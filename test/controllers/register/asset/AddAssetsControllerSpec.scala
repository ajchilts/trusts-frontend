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

package controllers.register.asset

import base.RegistrationSpecBase
import controllers.register.routes._
import forms.{AddAssetsFormProvider, YesNoFormProvider}
import models.NormalMode
import models.core.UserAnswers
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset.{Money, Shares}
import models.registration.pages.{AddAssets, ShareClass}
import pages.entitystatus.AssetStatus
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.shares._
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.AddRow
import views.html.register.asset.{AddAnAssetYesNoView, AddAssetsView}

class AddAssetsControllerSpec extends RegistrationSpecBase {

  lazy val addAssetsRoute: String = routes.AddAssetsController.onPageLoad(fakeDraftId).url
  lazy val addOnePostRoute: String = routes.AddAssetsController.submitOne(fakeDraftId).url
  lazy val addAnotherPostRoute: String = routes.AddAssetsController.submitAnother(fakeDraftId).url

  def changeMoneyAssetRoute(index: Int): String =
    money.routes.AssetMoneyValueController.onPageLoad(NormalMode, index, fakeDraftId).url

  def changeSharesAssetRoute(index: Int): String =
    shares.routes.SharesInAPortfolioController.onPageLoad(NormalMode, index, fakeDraftId).url

  def removeAssetYesNoRoute(index: Int): String =
    routes.RemoveAssetYesNoController.onPageLoad(index, fakeDraftId).url

  val addAssetsForm: Form[AddAssets] = new AddAssetsFormProvider()()
  val yesNoForm: Form[Boolean] = new YesNoFormProvider().withPrefix("addAnAssetYesNo")

  lazy val assets = List(
    AddRow("£4800", typeLabel = "Money", changeMoneyAssetRoute(0), removeAssetYesNoRoute(0)),
    AddRow("Share Company Name", typeLabel = "Shares", changeSharesAssetRoute(1), removeAssetYesNoRoute(1))
  )

  val userAnswersWithAssetsComplete: UserAnswers = emptyUserAnswers
    .set(WhatKindOfAssetPage(0), Money).success.value
    .set(AssetMoneyValuePage(0), "4800").success.value
    .set(AssetStatus(0), Completed).success.value
    .set(WhatKindOfAssetPage(1), Shares).success.value
    .set(SharesInAPortfolioPage(1), false).success.value
    .set(ShareCompanyNamePage(1), "Share Company Name").success.value
    .set(SharesOnStockExchangePage(1), true).success.value
    .set(ShareClassPage(1), ShareClass.Ordinary).success.value
    .set(ShareQuantityInTrustPage(1), "1000").success.value
    .set(ShareValueInTrustPage(1), "10").success.value
    .set(AssetStatus(1), Completed).success.value

  "AddAssets Controller" when {

    "no data" must {
      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, addAssetsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, addAssetsRoute)
            .withFormUrlEncodedBody(("value", AddAssets.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "there are no assets" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, addAssetsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddAnAssetYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(addAssetsForm, NormalMode,fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = yesNoForm.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[AddAnAssetYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "there are existing assets" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithAssetsComplete)).build()

        val request = FakeRequest(GET, addAssetsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddAssetsView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(addAssetsForm, NormalMode,fakeDraftId, Nil, assets, "You have added 2 assets")(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(userAnswersWithAssetsComplete)).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", AddAssets.options.head.value))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithAssetsComplete)).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = addAssetsForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddAssetsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId, Nil, assets, "You have added 2 assets")(fakeRequest, messages).toString

        application.stop()
      }

    }
  }
}
