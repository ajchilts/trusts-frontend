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

package views.register.trustees

import forms.trustees.AddATrusteeFormProvider
import models.NormalMode
import models.core.pages.IndividualOrBusiness
import models.registration.pages.AddATrustee
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.register.trustees.AddATrusteeView

class AddATrusteeViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  val featureUnavalible = "/trusts-registration/feature-not-available"

  val completeTrustees = Seq(
    AddRow("trustee one", IndividualOrBusiness.Individual.toString, featureUnavalible, featureUnavalible),
    AddRow("trustee two", IndividualOrBusiness.Individual.toString, featureUnavalible, featureUnavalible),
    AddRow("trustee three", IndividualOrBusiness.Individual.toString, featureUnavalible, featureUnavalible)
  )

  val inProgressTrustees = Seq(
    AddRow("trustee one", IndividualOrBusiness.Individual.toString, featureUnavalible, featureUnavalible),
    AddRow("trustee two", IndividualOrBusiness.Individual.toString, featureUnavalible, featureUnavalible),
    AddRow("trustee three", IndividualOrBusiness.Individual.toString, featureUnavalible, featureUnavalible)
  )

  val messageKeyPrefix = "addATrustee"

  val form = new AddATrusteeFormProvider()()

  val view = viewFor[AddATrusteeView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, fakeDraftId, Nil, Nil, isLeadTrusteeDefined = false, heading = "Add a trustee")(fakeRequest, messages)

  def applyView(form: Form[_], inProgressTrustees: Seq[AddRow], completeTrustees: Seq[AddRow]): HtmlFormat.Appendable = {
    val count = inProgressTrustees.size + completeTrustees.size
    val title = if (count > 1) s"You have added $count trustees" else "You have added 1 trustee"
    view.apply(form, NormalMode, fakeDraftId, inProgressTrustees, completeTrustees, isLeadTrusteeDefined = true, heading = title)(fakeRequest, messages)
  }

  "AddATrusteeView" when {

    "there is no trustee data" must {

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithNoTabularData(applyView(form))

      behave like pageWithOptions(form, applyView, AddATrustee.options.toSet)
    }


    "there is data in progress" must {

      val viewWithData = applyView(form, inProgressTrustees, Nil)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithInProgressTabularData(viewWithData, inProgressTrustees)

      behave like pageWithOptions(form, applyView, AddATrustee.options.toSet)
    }

    "there is complete data" must {

      val viewWithData = applyView(form, Nil, completeTrustees)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithCompleteTabularData(viewWithData, completeTrustees)

      behave like pageWithOptions(form, applyView, AddATrustee.options.toSet)
    }

    "there is both in progress and complete data" must {

      val viewWithData = applyView(form, inProgressTrustees, completeTrustees)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithTabularData(viewWithData, inProgressTrustees, completeTrustees)

      behave like pageWithOptions(form, applyView, AddATrustee.options.toSet)
    }

  }
}