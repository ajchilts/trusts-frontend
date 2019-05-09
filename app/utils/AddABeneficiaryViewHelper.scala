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

import models.entities.IndividualBeneficiary
import models.{FullName, UserAnswers}
import pages.IndividualBeneficiaries
import play.api.i18n.Messages
import viewmodels.{AddRow, AddToRows}

class AddABeneficiaryViewHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  private def parseName(name : Option[FullName]) : String = {
    name match {
      case Some(x) => s"$x"
      case None => ""
    }
  }

  private def parseBeneficiary(individualBeneficiary : IndividualBeneficiary) : AddRow = {
    AddRow(
      parseName(individualBeneficiary.name),
      "Individual Beneficiary",
      "#",
      "#"
    )
  }

  def rows : AddToRows = {
    val beneficiaries = userAnswers.get(IndividualBeneficiaries).toList.flatten

    val complete = beneficiaries.filter(_.isComplete).map(parseBeneficiary)

    val inProgress = beneficiaries.filterNot(_.isComplete).map(parseBeneficiary)

    AddToRows(inProgress, complete)
  }

}