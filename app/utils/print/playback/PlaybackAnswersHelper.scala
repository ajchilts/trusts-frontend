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

package utils.print.playback

import mapping.reads.{Trustee, Trustees}
import models.core.pages.IndividualOrBusiness
import models.playback.UserAnswers
import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage}
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import sections._
import utils.print.playback.sections.protectors.{CompanyProtector, IndividualProtector}
import viewmodels.AnswerSection

class PlaybackAnswersHelper(countryOptions: CountryOptions, userAnswers: UserAnswers)
                           (implicit messages: Messages) {

  def trustee(index: Int): Seq[AnswerSection] = {

    println("index: "+index)
    println("IsThisLeadTrusteePage(index): "+IsThisLeadTrusteePage(index))
    userAnswers.get(IsThisLeadTrusteePage(index)) flatMap { isLeadTrustee =>
      userAnswers.get(TrusteeIndividualOrBusinessPage(index)) flatMap { individualOrBusiness =>
        println("**************")
        println("isLeadTrustee: "+isLeadTrustee)
        if (isLeadTrustee) {
          individualOrBusiness match {
            case IndividualOrBusiness.Individual => LeadTrusteeIndividual(index, userAnswers, countryOptions)
            case IndividualOrBusiness.Business => LeadTrusteeBusiness(index, userAnswers, countryOptions)
          }
        } else {
          individualOrBusiness match {
            case IndividualOrBusiness.Individual => TrusteeIndividual(index, userAnswers, countryOptions)
            case IndividualOrBusiness.Business => TrusteeOrganisation(index, userAnswers, countryOptions)
          }
        }
      }
    }

  }.getOrElse(Nil)

  def allTrustees : Seq[AnswerSection] = {

    val trustees = userAnswers.get(_root_.sections.Trustees)

    println("trustees: "+trustees)


    val size = userAnswers.get(_root_.sections.Trustees).map(_.size).getOrElse(0)
    println("******* allTrustees *******")
    println("size: "+size)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield trustee(index)).flatten
    }
  }


  def charityBeneficiaries : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.beneficiaries.CharityBeneficiaries).map(_.value.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield CharityBeneficiary(index, userAnswers, countryOptions)).flatten
    }
  }

  def individualBeneficiaries : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.beneficiaries.IndividualBeneficiaries).map(_.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield IndividualBeneficiary(index, userAnswers, countryOptions)).flatten
    }
  }

  def protectors : Seq[AnswerSection] = {

    val protectors: Seq[AnswerSection] = individualProtectors ++ companyProtectors

    if (protectors.nonEmpty) {
      Seq(
        Seq(AnswerSection(sectionKey = Some(messages("answerPage.section.protectors.heading")))),
        protectors
      ).flatten
    } else {
      Nil
    }
  }

  private def individualProtectors : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.protectors.IndividualProtectors).map(_.value.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield IndividualProtector(index, userAnswers, countryOptions)).flatten
    }
  }

  private def companyProtectors : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.protectors.CompanyProtectors).map(_.value.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield CompanyProtector(index, userAnswers, countryOptions)).flatten
    }
  }

}
