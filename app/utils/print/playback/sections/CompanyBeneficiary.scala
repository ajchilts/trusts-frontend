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

package utils.print.playback.sections

import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.UserAnswers
import pages.register.beneficiaries.company._
import play.api.i18n.Messages
import utils.CheckAnswersFormatters
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

object CompanyBeneficiary {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] =
    if (name(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          name(index, userAnswers),
          shareOfIncomeYesNo(index, userAnswers),
          shareOfIncome(index, userAnswers),
          addressYesNo(index, userAnswers),
          address(index, userAnswers, countryOptions)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.companyBeneficiary.heading"))
      )))
    } else {
      None
    }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(CompanyBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "companyBeneficiaryName.checkYourAnswersLabel",
        CheckAnswersFormatters.escape(x),
        None
      )
  }

  def shareOfIncomeYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CompanyBeneficiaryDiscretionYesNoPage(index)) map {
      x =>
        AnswerRow(
          "companyBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel",
          CheckAnswersFormatters.yesOrNo(x),
          None
        )
    }

  def shareOfIncome(index: Int, userAnswers: UserAnswers): Option[AnswerRow] =
    userAnswers.get(CompanyBeneficiaryShareOfIncomePage(index)) map {
      x =>
        AnswerRow(
          "companyBeneficiaryShareOfIncome.checkYourAnswersLabel",
          CheckAnswersFormatters.escape(x),
          None
        )
    }

  def addressYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CompanyBeneficiaryAddressYesNoPage(index)) map {
      x =>
        AnswerRow(
          "companyBeneficiaryAddressYesNo.checkYourAnswersLabel",
          CheckAnswersFormatters.yesOrNo(x),
          None
        )
    }

  def address(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CompanyBeneficiaryAddressPage(index)) map {
      case address: UKAddress => AnswerRow(
        "companyBeneficiaryAddress.checkYourAnswersLabel",
        CheckAnswersFormatters.ukAddress(address),
        None
      )
      case address: InternationalAddress => AnswerRow(
        "companyBeneficiaryAddress.checkYourAnswersLabel",
        CheckAnswersFormatters.internationalAddress(address, countryOptions),
        None
      )
    }

}
