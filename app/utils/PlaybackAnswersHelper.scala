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

package utils

import javax.inject.Inject
import models.playback.UserAnswers
import pages.register.beneficiaries.charity._
import pages.register.settlors.deceased_settlor._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.CheckYourAnswersHelper._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class PlaybackAnswersHelper @Inject()(countryOptions: CountryOptions)(userAnswers: UserAnswers)(implicit messages: Messages) {

  def deceasedSettlor: Option[Seq[AnswerSection]] = DeceasedSettlorSection(userAnswers, countryOptions)

  def charityBeneficiary(index: Int): Option[Seq[AnswerSection]] = CharityBeneficiary(index, userAnswers, countryOptions)

}

object CharityBeneficiary {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {
    if (charityName(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          charityName(index, userAnswers),
          charityShareOfIncomeYesNo(index, userAnswers),
          charityAddressYesNo(index, userAnswers)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.charityBeneficiary.heading"))
      )))
    } else {
      None
    }
  }

  def charityName(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(CharityBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "charityName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

  def charityShareOfIncomeYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CharityBeneficiaryDiscretionYesNoPage(index)) map {
      x =>
        AnswerRow(
          "charityShareOfIncomeYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def charityAddressYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CharityBeneficiaryAddressYesNoPage(index)) map {
      x =>
        AnswerRow(
          "charityAddressYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

}

object DeceasedSettlorSection {

  def apply(userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {

    val questions = Seq(
      setupAfterSettlorDied(userAnswers),
      deceasedSettlorsName(userAnswers),
      deceasedSettlorDateOfDeathYesNo(userAnswers),
      deceasedSettlorDateOfDeath(userAnswers),
      deceasedSettlorDateOfBirthYesNo(userAnswers),
      deceasedSettlorsDateOfBirth(userAnswers),
      deceasedSettlorsNINoYesNo(userAnswers),
      deceasedSettlorNationalInsuranceNumber(userAnswers),
      deceasedSettlorsLastKnownAddressYesNo(userAnswers),
      wasSettlorsAddressUKYesNo(userAnswers),
      deceasedSettlorsUKAddress(userAnswers),
      deceasedSettlorsInternationalAddress(userAnswers, countryOptions)
    ).flatten

    if (deceasedSettlorsName(userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        questions,
        sectionKey = Some(messages("answerPage.section.deceasedSettlor.heading"))
      )))
    } else {
      None
    }
  }

  def setupAfterSettlorDied(userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SetupAfterSettlorDiedPage) map {
    x =>
      AnswerRow(
        "setupAfterSettlorDied.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def deceasedSettlorsUKAddress(userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorsUKAddressPage) map {
    x =>
      AnswerRow(
        "settlorsUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        None
      )
  }

  def deceasedSettlorsNINoYesNo(userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorsNINoYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsNINoYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def deceasedSettlorsName(userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorsNamePage) map {
    x =>
      AnswerRow(
        "settlorsName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        None
      )
  }

  def deceasedSettlorsLastKnownAddressYesNo(userAnswers: UserAnswers)
                                           (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorsLastKnownAddressYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def deceasedSettlorsInternationalAddress(userAnswers: UserAnswers, countryOptions: CountryOptions): Option[AnswerRow] =
    userAnswers.get(SettlorsInternationalAddressPage) map {
      x =>
        AnswerRow(
          "settlorsInternationalAddress.checkYourAnswersLabel",
          internationalAddress(x, countryOptions),
          None
        )
    }

  def deceasedSettlorsDateOfBirth(userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorsDateOfBirthPage) map {
    x =>
      AnswerRow(
        "settlorsDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        None
      )
  }

  def deceasedSettlorNationalInsuranceNumber(userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorNationalInsuranceNumberPage) map {
    x =>
      AnswerRow(
        "settlorNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        None
      )
  }

  def deceasedSettlorDateOfDeathYesNo(userAnswers: UserAnswers)
                                     (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeathYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def deceasedSettlorDateOfDeath(userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeath.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        None
      )
  }

  def deceasedSettlorDateOfBirthYesNo(userAnswers: UserAnswers)
                                     (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorDateOfBirthYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def wasSettlorsAddressUKYesNo(userAnswers: UserAnswers)
                               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(WasSettlorsAddressUKYesNoPage) map {
    x =>
      AnswerRow(
        "wasSettlorsAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def deceasedSettlorName(userAnswers: UserAnswers): String = userAnswers.get(SettlorsNamePage).map(_.toString).getOrElse("")

}