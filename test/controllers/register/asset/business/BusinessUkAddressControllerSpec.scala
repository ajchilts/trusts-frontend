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

package controllers.register.asset.business

import base.RegistrationSpecBase
import controllers.IndexValidation
import controllers.register.routes._
import forms.UKAddressFormProvider
import models.NormalMode
import models.core.pages.UKAddress
import org.scalacheck.Arbitrary.arbitrary
import pages.register.asset.business.BusinessNamePage
import pages.register.trustees.individual.TrusteesUkAddressPage
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.asset.buisness.BusinessUkAddressView

class BusinessUkAddressControllerSpec extends RegistrationSpecBase with IndexValidation {

  val formProvider = new UKAddressFormProvider()
  val form: Form[UKAddress] = formProvider()
  val index = 0
  val businessName = "Test"
  val validAnswer = UKAddress("value 1", "value 2", Some("value 3"), Some("value 4"), "AB1 1AB")

  lazy val assetUkAddressRoute: String = routes.BusinessUkAddressController.onPageLoad(NormalMode, index, fakeDraftId).url

  "AssetUkAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(BusinessNamePage(index), "Test").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, assetUkAddressRoute)

      val view = application.injector.instanceOf[BusinessUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, index, businessName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(BusinessNamePage(index), "Test").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, assetUkAddressRoute)

      val view = application.injector.instanceOf[BusinessUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, index, businessName)(fakeRequest, messages).toString

      application.stop()
    }


    "redirect to Asset Name page when AssetName is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, assetUkAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.BusinessNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(BusinessNamePage(index), "Test").success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, assetUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("line3", "value 3"), ("line4", "town"), ("postcode", "AB1 1AB") )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(BusinessNamePage(index), "Test").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, assetUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "invalid value"))

      val boundForm = form.bind(Map("line1" -> "invalid value"))

      val view = application.injector.instanceOf[BusinessUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, index, businessName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, assetUkAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, assetUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.BusinessUkAddressController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[UKAddress],
        TrusteesUkAddressPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.BusinessUkAddressController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("line1", "line1"), ("line2", "line2"), ("line3", "line3"), ("line4", "town or city"), ("postcode", "AB1 1AB"))
      }

      validateIndex(
        arbitrary[UKAddress],
        TrusteesUkAddressPage.apply,
        postForIndex
      )
    }


  }
}
