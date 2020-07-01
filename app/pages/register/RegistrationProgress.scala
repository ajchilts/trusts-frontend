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

package pages.register

import javax.inject.Inject
import mapping.reads.{Assets, Trustees}
import models.core.UserAnswers
import models.registration.pages.Status._
import models.registration.pages._
import navigation.registration.TaskListNavigator
import pages.entitystatus.{DeceasedSettlorStatus, TrustDetailsStatus}
import pages.register.asset.AddAssetsPage
import pages.register.beneficiaries.AddABeneficiaryPage
import pages.register.settlors.living_settlor.trust_type.SetUpInAdditionToWillTrustYesNoPage
import pages.register.settlors.{AddASettlorPage, SetUpAfterSettlorDiedYesNoPage}
import pages.register.trust_details.WhenTrustSetupPage
import pages.register.trustees.AddATrusteePage
import repositories.RegistrationsRepository
import sections._
import sections.beneficiaries.{Beneficiaries, ClassOfBeneficiaries, IndividualBeneficiaries}
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels._

import scala.concurrent.{ExecutionContext, Future}

class RegistrationProgress @Inject()(navigator: TaskListNavigator, registrationsRepository: RegistrationsRepository)
                                    (implicit ec: ExecutionContext) {

  def items(userAnswers: UserAnswers, draftId: String)(implicit hc: HeaderCarrier): Future[List[Task]] =
    for {
      resolvedAssetsStatus <- assetsStatus(userAnswers)
    } yield {
      List (
        Task(Link(TrustDetails, navigator.trustDetailsJourney(userAnswers, draftId).url), isTrustDetailsComplete(userAnswers)),
        Task(Link(Settlors, navigator.settlorsJourney(userAnswers, draftId).url), isSettlorsComplete(userAnswers)),
        Task(Link(Trustees, navigator.trusteesJourney(userAnswers, draftId).url), isTrusteesComplete(userAnswers)),
        Task(Link(Beneficiaries, navigator.beneficiariesJourney(userAnswers, draftId).url), isBeneficiariesComplete(userAnswers)),
        Task(Link(Assets, navigator.assetsJourneyUrl(draftId)), resolvedAssetsStatus),
        Task(Link(TaxLiability, navigator.taxLiabilityJourney(draftId).url), None)
      )
    }

  private def determineStatus(complete: Boolean): Option[Status] = {
    if (complete) {
      Some(Completed)
    } else {
      Some(InProgress)
    }
  }

  def isTrustDetailsComplete(userAnswers: UserAnswers): Option[Status] = {
    userAnswers.get(WhenTrustSetupPage) match {
      case None => None
      case Some(_) =>
        val completed = userAnswers.get(TrustDetailsStatus).contains(Completed)
        determineStatus(completed)
    }
  }

  def isTrusteesComplete(userAnswers: UserAnswers): Option[Status] = {
    val noMoreToAdd = userAnswers.get(AddATrusteePage).contains(AddATrustee.NoComplete)

    userAnswers.get(_root_.sections.Trustees) match {
      case Some(l) =>

        if (l.isEmpty) {
          None
        } else {
          val hasLeadTrustee = l.exists(_.isLead)
          val isComplete = !l.exists(_.status == InProgress) && noMoreToAdd && hasLeadTrustee

          determineStatus(isComplete)
        }
      case None =>
        None
    }
  }

  def isSettlorsComplete(userAnswers: UserAnswers): Option[Status] = {
    val setUpAfterSettlorDied = userAnswers.get(SetUpAfterSettlorDiedYesNoPage)
    val inAdditionToWillTrust = userAnswers.get(SetUpInAdditionToWillTrustYesNoPage).getOrElse(false)

    def isDeceasedSettlorComplete: Option[Status] = {
      val deceasedCompleted = userAnswers.get(DeceasedSettlorStatus)
      val isComplete = deceasedCompleted.contains(Completed)
      determineStatus(isComplete)
    }

    setUpAfterSettlorDied match {
      case None => None
      case Some(setupAfterDeceased) =>
        if (setupAfterDeceased) {isDeceasedSettlorComplete}
        else {
          userAnswers.get(LivingSettlors).getOrElse(Nil) match {
            case Nil =>
              if (!setupAfterDeceased && !inAdditionToWillTrust) {Some(Status.InProgress)}
              else { determineStatus(true) }
            case living =>
              val noMoreToAdd = userAnswers.get(AddASettlorPage).contains(AddASettlor.NoComplete)
              val isComplete = !living.exists(_.status == InProgress)
              determineStatus(isComplete && noMoreToAdd)
          }
        }
    }
  }

  def isBeneficiariesComplete(userAnswers: UserAnswers): Option[Status] = {

    def individualBeneficiariesComplete: Boolean = {
      val individuals = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)

      if (individuals.isEmpty) {
        false
      } else {
        !individuals.exists(_.status == InProgress)
      }
    }

    def classComplete: Boolean = {
      val classes = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)
      if (classes.isEmpty) {
        false
      } else {
        !classes.exists(_.status == InProgress)
      }
    }

    val noMoreToAdd = userAnswers.get(AddABeneficiaryPage).contains(AddABeneficiary.NoComplete)

    val individuals = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)
    val classes = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)

    (individuals, classes) match {
      case (Nil, Nil) => None
      case (ind, Nil) =>
        determineStatus(individualBeneficiariesComplete && noMoreToAdd)
      case (Nil, c) =>
        determineStatus(classComplete && noMoreToAdd)
      case (ind, c) =>
        determineStatus(individualBeneficiariesComplete && classComplete && noMoreToAdd)
    }
  }

  def assetsStatus(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Option[Status]] = {
    registrationsRepository.getSectionStatus(userAnswers.draftId, "assets")
  }

  def isTaskListComplete(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    if (isTrustDetailsComplete(userAnswers).contains(Completed) &&
      isSettlorsComplete(userAnswers).contains(Completed) &&
      isTrusteesComplete(userAnswers).contains(Completed) &&
      isBeneficiariesComplete(userAnswers).contains(Completed)){
      assetsStatus(userAnswers) map {
        result => result.contains(Completed)
      }
    } else {
      Future.successful(false)
    }
  }
}
