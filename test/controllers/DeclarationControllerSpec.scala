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

package controllers

import base.SpecBase
import forms.DeclarationFormProvider
import models.{FullName, NormalMode, RegistrationTRNResponse, UnableToRegister, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.DeclarationPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.DeclarationView
import org.mockito.Matchers.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.{when, _}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future


class DeclarationControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")
  def confirmationRoute = Call("GET", "/confirmation")

  val formProvider = new DeclarationFormProvider()
  val form = formProvider()
  val name = "name"

  lazy val declarationRoute = routes.DeclarationController.onPageLoad().url

  before {
    reset(mockSubmissionService)
  }

  "Declaration Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers),AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DeclarationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(DeclarationPage, FullName("First", None, "Last")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val view = application.injector.instanceOf[DeclarationView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(FullName("First",None, "Last")), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the confirmation page when valid data is submitted and registration submitted successfully " in {

      when(mockSubmissionService.submit(any[UserAnswers])(any[HeaderCarrier])).
        thenReturn(Future.successful(RegistrationTRNResponse("xTRN12456")))


      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent)
          .overrides(bind[Navigator].toInstance(new FakeNavigator(confirmationRoute)))
          .build()

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("firstName", "value 1"), ("lastName", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual confirmationRoute.url
      verify(mockSubmissionService, times(1)).submit(any[UserAnswers])(any[HeaderCarrier])
      application.stop()
    }

    "redirect to the task list page when valid data is submitted and submission service can not register successfully" in {

      when(mockSubmissionService.submit(any[UserAnswers])(any[HeaderCarrier])).
        thenReturn(Future.failed(UnableToRegister()))


      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent)
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("firstName", "value 1"), ("lastName", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
      verify(mockSubmissionService, times(1)).submit(any[UserAnswers])(any[HeaderCarrier])
      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers),AffinityGroup.Agent).build()

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[DeclarationView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
