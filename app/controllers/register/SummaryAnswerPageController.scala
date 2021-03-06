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

package controllers.register

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import javax.inject.Inject
import pages.register.RegistrationProgress
import pages.register.agents.AgentInternalReferencePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import utils.print.register.PrintUserAnswersHelper
import views.html.register.SummaryAnswerPageView

import scala.concurrent.ExecutionContext


class SummaryAnswerPageController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             identify: RegistrationIdentifierAction,
                                             getData: DraftIdRetrievalActionProvider,
                                             requireData: RegistrationDataRequiredAction,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: SummaryAnswerPageView,
                                             registrationComplete : TaskListCompleteActionRefiner,
                                             printUserAnswersHelper: PrintUserAnswersHelper
                                            )(implicit ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport {

  private def actions(draftId : String) =
    identify andThen getData(draftId) andThen requireData andThen registrationComplete

  def onPageLoad(draftId : String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      printUserAnswersHelper.summary(draftId, request.userAnswers).map {
        sections =>
          Ok(
            view(
              sections,
              request.affinityGroup == Agent,
              request.userAnswers.get(AgentInternalReferencePage).getOrElse("")
            )
          )
      }
  }

}