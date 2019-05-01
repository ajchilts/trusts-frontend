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

package forms

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class IndividualBeneficiaryIncomeFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "individualBeneficiaryIncome.error.required"
  val lengthKey = "individualBeneficiaryIncome.error.length"
  val invalid = "individualBeneficiaryIncome.error.invalid"

  val form = new IndividualBeneficiaryIncomeFormProvider()()

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.percentageRegex)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = Validation.numericRegex,
      generator = nonEmptyString,
      error = FormError(fieldName, invalid, Seq(Validation.numericRegex))
    )


    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      Validation.percentageRegex,
      generator =  intsInRange(0,1000),
      error = FormError(fieldName, lengthKey, Seq(Validation.percentageRegex))
    )


  }
}