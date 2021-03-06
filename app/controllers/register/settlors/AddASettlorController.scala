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

package controllers.register.settlors

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.{AddASettlorFormProvider, YesNoFormProvider}
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.register.settlors.living_settlor.trust_type.KindOfTrustPage
import pages.register.settlors.{AddASettlorPage, AddASettlorYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.AddASettlorViewHelper
import utils.annotations.LivingSettlor
import views.html.register.settlors.{AddASettlorView, AddASettlorYesNoView}

import scala.concurrent.{ExecutionContext, Future}

class AddASettlorController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       @LivingSettlor navigator: Navigator,
                                       identify: RegistrationIdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: RegistrationDataRequiredAction,
                                       yesNoFormProvider: YesNoFormProvider,
                                       addAnotherFormProvider: AddASettlorFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       addAnotherView: AddASettlorView,
                                       yesNoView: AddASettlorYesNoView,
                                       requiredAnswer: RequiredAnswerActionProvider
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val addAnotherForm = addAnotherFormProvider()
  val yesNoForm: Form[Boolean] = yesNoFormProvider.withPrefix("addAnSettlorYesNo")

  private def actions(draftId: String) =
    identify andThen getData(draftId) andThen requireData andThen requiredAnswer(RequiredAnswer(KindOfTrustPage))

  private def heading(count: Int)(implicit mp : MessagesProvider) = {
    count match {
      case 0 => Messages("addASettlor.heading")
      case 1 => Messages("addASettlor.singular.heading")
      case size => Messages("addASettlor.count.heading", size)
    }
  }

  private def trustHintText(implicit request: RegistrationDataRequest[AnyContent]): Option[String] = request.userAnswers.get(KindOfTrustPage) map { trust =>
    s"addASettlor.$trust"
  }

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val settlors = new AddASettlorViewHelper(request.userAnswers, draftId).rows

      settlors.count match {
        case 0 =>
          Ok(yesNoView(yesNoForm, mode, draftId, trustHintText))
        case count =>
          Ok(addAnotherView(addAnotherForm, mode, draftId, settlors.inProgress, settlors.complete, heading(count), trustHintText))
      }
  }

  def submitOne(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(yesNoView(formWithErrors, mode, draftId, trustHintText)))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddASettlorYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddASettlorYesNoPage, mode, draftId)(updatedAnswers))
        }
      )
  }

  def submitAnother(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          val settlors = new AddASettlorViewHelper(request.userAnswers, draftId).rows

          Future.successful(BadRequest(
            addAnotherView(
              formWithErrors,
              mode,
              draftId,
              settlors.inProgress,
              settlors.complete,
              heading(settlors.count),
              trustHintText
            )
          ))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddASettlorPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddASettlorPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
