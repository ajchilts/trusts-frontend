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
import controllers.IndexValidation
import controllers.register.routes._
import forms.property_or_land.PropertyOrLandDescriptionFormProvider
import models.NormalMode
import org.jsoup.Jsoup
import org.scalacheck.Arbitrary.arbitrary
import pages.register.asset.property_or_land.PropertyOrLandDescriptionPage
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.asset.property_or_land.PropertyOrLandDescriptionView

class PropertyOrLandDescriptionControllerSpec extends RegistrationSpecBase with IndexValidation {

  val formProvider = new PropertyOrLandDescriptionFormProvider()
  val form: Form[String] = formProvider()
  val index = 0

  lazy val propertyOrLandDescriptionRoute: String = routes.PropertyOrLandDescriptionController.onPageLoad(NormalMode, index, fakeDraftId).url

  "PropertyOrLandDescription Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, propertyOrLandDescriptionRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PropertyOrLandDescriptionView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, index, fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(PropertyOrLandDescriptionPage(index), "answer").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, propertyOrLandDescriptionRoute)

      val view = application.injector.instanceOf[PropertyOrLandDescriptionView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("answer"), NormalMode, index, fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, propertyOrLandDescriptionRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when empty form data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, propertyOrLandDescriptionRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[PropertyOrLandDescriptionView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, index, fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, propertyOrLandDescriptionRoute)
          .withFormUrlEncodedBody(("value", "$$$$$$$$$"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      val document = Jsoup.parse(contentAsString(result))
      document.getElementById("error-message-value-input").text() mustBe
        "Error: The description of the property or land must only include letters a to z, numbers, ampersands (&), apostrophes, hyphens and spaces"

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, propertyOrLandDescriptionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, propertyOrLandDescriptionRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

  "for a GET" must {

    def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
      val route = routes.PropertyOrLandDescriptionController.onPageLoad(NormalMode, index, fakeDraftId).url

      FakeRequest(GET, route)
    }

    validateIndex(
      arbitrary[String],
      PropertyOrLandDescriptionPage.apply,
      getForIndex
    )

  }

  "for a POST" must {
    def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

      val route =
        routes.PropertyOrLandDescriptionController.onPageLoad(NormalMode, index, fakeDraftId).url

      FakeRequest(POST, route)
        .withFormUrlEncodedBody(("value", "true"))
    }

    validateIndex(
      arbitrary[String],
      PropertyOrLandDescriptionPage.apply,
      postForIndex
    )
  }
}
