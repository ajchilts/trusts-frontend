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

package controllers.register.asset.property_or_land

import base.RegistrationSpecBase
import controllers.register.routes._
import forms.RemoveIndexFormProvider
import models.core.pages.InternationalAddress
import models.registration.pages.Status.Completed
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.entitystatus.AssetStatus
import pages.register.asset.property_or_land.PropertyOrLandInternationalAddressPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.RemoveIndexView

class RemovePropertyOrLandWithAddressInternationalControllerSpec extends RegistrationSpecBase with ScalaCheckPropertyChecks {

  val messagesPrefix = "removePropertyOrLandAsset"

  lazy val formProvider = new RemoveIndexFormProvider()
  lazy val form = formProvider(messagesPrefix)

  lazy val formRoute = routes.RemovePropertyOrLandWithAddressInternationalController.onSubmit(0, fakeDraftId)

  lazy val content : String = "line 1"

  val index = 0

  "RemovePropertyOrLandWithAddressInternational Controller" when {

    "no address added" must {
      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.RemovePropertyOrLandWithAddressInternationalController.onPageLoad(index, fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(messagesPrefix, form, index, fakeDraftId, "the property or land", formRoute)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "address has been provided" must {
      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers
          .set(PropertyOrLandInternationalAddressPage(0), InternationalAddress(
            line1 = "line 1",
            line2 = "line 2",
            country = "GB"
          )).success.value
          .set(AssetStatus(0), Completed).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.RemovePropertyOrLandWithAddressInternationalController.onPageLoad(index, fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(messagesPrefix, form, index, fakeDraftId, content, formRoute)(fakeRequest, messages).toString

        application.stop()
      }
    }



    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(PropertyOrLandInternationalAddressPage(0), InternationalAddress(
          line1 = "line 1",
          line2 = "line 2",
          country = "GB"
        )).success.value
        .set(AssetStatus(0), Completed).success.value

      forAll(arbitrary[Boolean]) {
        value =>
        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .build()

        val request =
          FakeRequest(POST, routes.RemovePropertyOrLandWithAddressInternationalController.onSubmit(index, fakeDraftId).url)
            .withFormUrlEncodedBody(("value", value.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.register.asset.routes.AddAssetsController.onPageLoad(fakeDraftId).url

        application.stop()
      }

    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(PropertyOrLandInternationalAddressPage(0), InternationalAddress(
          line1 = "line 1",
          line2 = "line 2",
          country = "GB"
        )).success.value
        .set(AssetStatus(0), Completed).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, routes.RemovePropertyOrLandWithAddressInternationalController.onSubmit(index, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[RemoveIndexView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(messagesPrefix, boundForm, index, fakeDraftId, content, formRoute)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.RemovePropertyOrLandWithAddressInternationalController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, routes.RemovePropertyOrLandWithAddressInternationalController.onSubmit(index, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
