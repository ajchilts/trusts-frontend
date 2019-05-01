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

package views

import controllers.routes
import forms.IndividualBeneficiaryIncomeFormProvider
import models.{FullName, NormalMode, UserAnswers}
import pages.IndividualBeneficiaryNamePage
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.IndividualBeneficiaryIncomeView

class IndividualBeneficiaryIncomeViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryIncome"
  val index = 0
  val name = "First Last"
  val fullName = FullName("First", None, "Last")

  val form = new IndividualBeneficiaryIncomeFormProvider()()

  "IndividualBeneficiaryIncomeView view" must {

    val userAnswers = UserAnswers(userAnswersId)
      .set(IndividualBeneficiaryNamePage(index), fullName).success.value

    val view = viewFor[IndividualBeneficiaryIncomeView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fullName, index)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like stringPageWithDynamicTitle(form, applyView, messageKeyPrefix, name.toString, routes.IndividualBeneficiaryIncomeController.onSubmit(NormalMode, index).url)

    behave like pageWithASubmitButton(applyView(form))
  }
}