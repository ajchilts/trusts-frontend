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

import forms.trustees.TrusteeIndividualOrBusinessFormProvider
import models.NormalMode
import models.core.pages.IndividualOrBusiness
import pages.register.trustees.IsThisLeadTrusteePage
import play.api.data.Form
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.register.trustees.TrusteeIndividualOrBusinessView

class TrusteeIndividualOrBusinessViewSpec extends OptionsViewBehaviours {

  val messageKeyPrefix = "trusteeIndividualOrBusiness"

  val leadHeading = Messages("trusteeIndividualOrBusiness.heading", "lead")
  val heading = Messages("trusteeIndividualOrBusiness.heading", "")

  val index = 0

  val form = new TrusteeIndividualOrBusinessFormProvider()(messageKeyPrefix)


  "IndividualOrBusinessView as lead trustee" must {

    val userAnswers = emptyUserAnswers
      .set(IsThisLeadTrusteePage(index), true).success.value

    val view = viewFor[TrusteeIndividualOrBusinessView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, leadHeading)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithOptions(form, applyView, IndividualOrBusiness.options)
  }


  "IndividualOrBusinessView as nonlead trustee" must {

    val userAnswers = emptyUserAnswers
      .set(IsThisLeadTrusteePage(index), false).success.value

    val view = viewFor[TrusteeIndividualOrBusinessView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, heading)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithOptions(form, applyView, IndividualOrBusiness.options)
  }
}
