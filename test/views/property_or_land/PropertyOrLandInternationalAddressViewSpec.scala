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

package views.property_or_land

import controllers.routes
import forms.InternationalAddressFormProvider
import models.{InternationalAddress, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.QuestionViewBehaviours
import views.html.property_or_land.PropertyOrLandInternationalAddressView

class PropertyOrLandInternationalAddressViewSpec extends QuestionViewBehaviours[InternationalAddress] {

  val messageKeyPrefix = "site.address.international"
  val entityName = "the property or land"
  val index: Int = 0

  override val form = new InternationalAddressFormProvider()()

  "PropertyOrLandInternationalAddressView" must {

    val view = viewFor[PropertyOrLandInternationalAddressView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, NormalMode, fakeDraftId, index)(fakeRequest, messages)


    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, entityName)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      controllers.property_or_land.routes.PropertyOrLandInternationalAddressController.onSubmit(NormalMode, index, fakeDraftId).url,
      Seq(("line1",None), ("line2",None), ("line3", None), ("line4", None), ("country", None)),
      entityName
    )

    behave like pageWithASubmitButton(applyView(form))

  }
}