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

package utils

import java.time.format.DateTimeFormatter

import models.core.UserAnswers
import models.core.pages.{Address, FullName, InternationalAddress, UKAddress}
import models.registration.pages.KindOfTrust._
import models.registration.pages.{KindOfTrust, PassportOrIdCardDetails}
import pages.register.agents.AgentNamePage
import pages.register.asset.business.BusinessNamePage
import pages.register.asset.shares.ShareCompanyNamePage
import pages.register.settlors.deceased_settlor.SettlorsNamePage
import pages.register.settlors.living_settlor.SettlorIndividualNamePage
import pages.register.settlors.living_settlor.business.SettlorBusinessNamePage
import pages.register.trustees.individual.TrusteesNamePage
import pages.register.trustees.organisation.TrusteeOrgNamePage
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.domain.Nino
import utils.countryOptions.CountryOptions

object CheckAnswersFormatters {

  val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def utr(answer: String)(implicit messages: Messages): Html = {
    HtmlFormat.escape(answer)
  }

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html = {
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }
  }

  def formatNino(nino: String): String = Nino(nino).formatted

  def country(code: String, countryOptions: CountryOptions): String =
    countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse("")

  def currency(value: String): Html = escape(s"£$value")

  def percentage(value: String): Html = escape(s"$value%")

  def trusteeName(index: Int, userAnswers: UserAnswers): String =
    userAnswers.get(TrusteesNamePage(index)).map(_.toString).getOrElse("")

  def orgName(index: Int, userAnswers: UserAnswers): String =
    userAnswers.get(TrusteeOrgNamePage(index)).getOrElse("")

  def answer[T](key: String, answer: T)(implicit messages: Messages): Html =
    HtmlFormat.escape(messages(s"$key.$answer"))

  def escape(x: String) = HtmlFormat.escape(x)

  def deceasedSettlorName(userAnswers: UserAnswers): String =
    userAnswers.get(SettlorsNamePage).map(_.toString).getOrElse("")

  def shareCompName(index: Int, userAnswers: UserAnswers): String = {
    userAnswers.get(ShareCompanyNamePage(index)).getOrElse("")
  }

  def livingSettlorName(index: Int, userAnswers: UserAnswers): String = {
    userAnswers.get(SettlorIndividualNamePage(index)).map(_.toString).getOrElse("")
  }

  def businessSettlorName(index: Int, userAnswers: UserAnswers): String = {
    userAnswers.get(SettlorBusinessNamePage(index)).getOrElse("")
  }

  def assetName(index: Int, userAnswers: UserAnswers): String = {
    userAnswers.get(BusinessNamePage(index)).getOrElse("")
  }

  def agencyName(userAnswers: UserAnswers): String = {
    userAnswers.get(AgentNamePage).getOrElse("")
  }

  def ukAddress(address: UKAddress): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        Some(HtmlFormat.escape(address.line2)),
        address.line3.map(HtmlFormat.escape),
        address.line4.map(HtmlFormat.escape),
        Some(HtmlFormat.escape(address.postcode))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def internationalAddress(address: InternationalAddress, countryOptions: CountryOptions): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        Some(HtmlFormat.escape(address.line2)),
        address.line3.map(HtmlFormat.escape),
        Some(country(address.country, countryOptions))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def addressFormatter(address: Address, countryOptions: CountryOptions): Html = {
    address match {
      case a:UKAddress => ukAddress(a)
      case a:InternationalAddress => internationalAddress(a, countryOptions)
    }
  }

  def passportOrIDCard(passportOrIdCard: PassportOrIdCardDetails, countryOptions: CountryOptions): Html = {
    val lines =
      Seq(
        Some(country(passportOrIdCard.country, countryOptions)),
        Some(HtmlFormat.escape(passportOrIdCard.cardNumber)),
        Some(HtmlFormat.escape(passportOrIdCard.expiryDate.format(dateFormatter)))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def fullName(fullname: FullName) = {
    val middle = fullname.middleName.map(" " + _ + " ").getOrElse(" ")
    s"${fullname.firstName}${middle}${fullname.lastName}"
  }

  def kindOfTrust(kindOfTrust: KindOfTrust, messages: Messages) = {
    kindOfTrust match {
      case Intervivos =>
        messages("kindOfTrust.Lifetime")
      case Deed =>
        messages("kindOfTrust.Deed")
      case Employees =>
        messages("kindOfTrust.Employees")
      case FlatManagement =>
        messages("kindOfTrust.Building")
      case HeritageMaintenanceFund =>
        messages("kindOfTrust.Repair")
    }
  }

}
