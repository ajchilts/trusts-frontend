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

package navigation.registration

import controllers.register.routes
import javax.inject.{Inject, Singleton}
import mapping.reads.{Assets, Trustees}
import models.registration.pages.Status.Completed
import models.NormalMode
import models.core.UserAnswers
import pages._
import pages.entitystatus.{DeceasedSettlorStatus, TrustDetailsStatus}
import pages.register.settlors.deceased_settlor.SetupAfterSettlorDiedYesNoPage
import play.api.mvc.Call
import sections._
import sections.beneficiaries.{Beneficiaries, ClassOfBeneficiaries, IndividualBeneficiaries}

@Singleton
class TaskListNavigator @Inject()() {

  private def trustDetailsRoute(draftId: String)(answers: UserAnswers) = {
    val completed = answers.get(TrustDetailsStatus).contains(Completed)
    if (completed) {
      routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    } else {
      routes.TrustNameController.onPageLoad(NormalMode, draftId)
    }
  }

  private def trusteeRoute(draftId: String)(answers: UserAnswers) = {
    answers.get(sections.Trustees).getOrElse(Nil) match {
      case Nil =>
        controllers.register.trustees.routes.TrusteesInfoController.onPageLoad(draftId)
      case _ :: _ =>
        controllers.register.trustees.routes.AddATrusteeController.onPageLoad(draftId)
    }
  }

  private def settlorRoute(draftId: String)(answers: UserAnswers) = {
    answers.get(DeceasedSettlorStatus) match {
      case Some(value) =>
        if(value.equals(Completed)) {
          controllers.register.settlors.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
        } else {
          controllers.register.settlors.deceased_settlor.routes.SetupAfterSettlorDiedController.onPageLoad(NormalMode,draftId)
        }
      case None =>
        answers.get(SetupAfterSettlorDiedYesNoPage) match {
          case None => controllers.register.settlors.routes.SettlorInfoController.onPageLoad(draftId)
          case _ =>
            answers.get (LivingSettlors).getOrElse (Nil) match {
              case Nil => controllers.register.settlors.deceased_settlor.routes.SetupAfterSettlorDiedController.onPageLoad (NormalMode, draftId)
              case _ => controllers.register.settlors.routes.AddASettlorController.onPageLoad (draftId)
            }
        }
    }
  }

  private def beneficiaryRoute(draftId: String)(answers: UserAnswers) = {
    if(isAnyBeneficiaryAdded(answers)) {
      controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    } else {
      controllers.register.beneficiaries.routes.IndividualBeneficiaryInfoController.onPageLoad(draftId)
    }
  }

  private def isAnyBeneficiaryAdded(answers: UserAnswers) = {

    val individuals = answers.get(IndividualBeneficiaries).getOrElse(Nil)
    val classes = answers.get(ClassOfBeneficiaries).getOrElse(Nil)

    individuals.nonEmpty || classes.nonEmpty
  }

  private def assetRoute(draftId: String)(answers: UserAnswers) = {
    answers.get(sections.Assets).getOrElse(Nil) match {
      case _ :: _ =>
        controllers.register.asset.routes.AddAssetsController.onPageLoad(draftId)
      case Nil =>
        controllers.register.asset.routes.AssetInterruptPageController.onPageLoad(draftId)
    }
  }

  private def taskListRoutes(draftId: String): Page => UserAnswers => Call = {
    case TrustDetails => trustDetailsRoute(draftId)
    case Trustees => trusteeRoute(draftId)
    case Settlors => settlorRoute(draftId)
    case Beneficiaries => beneficiaryRoute(draftId)
    case TaxLiability => _ => routes.TaskListController.onPageLoad(draftId)
    case Assets => assetRoute(draftId)
    case _ => _ => routes.IndexController.onPageLoad()
  }

  def nextPage(page: Page, userAnswers: UserAnswers, draftId: String) : Call = {
    taskListRoutes(draftId)(page)(userAnswers)
  }

}
