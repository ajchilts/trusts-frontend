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

package controllers.register.settlors.living_settlor.business

import controllers.actions._
import controllers.actions.register.settlors.living_settlor.business.NameRequiredActionProvider
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import forms.YesNoFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.register.settlors.living_settlor.business.SettlorBusinessAddressUKYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.LivingSettlors
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.annotations.LivingSettlor
import views.html.register.settlors.living_settlor.business.SettlorBusinessAddressUKYesNoView

import scala.concurrent.{ExecutionContext, Future}

class SettlorBusinessAddressUKYesNoController @Inject()(
                                                           override val messagesApi: MessagesApi,
                                                           registrationsRepository: RegistrationsRepository,
                                                           @LivingSettlor navigator: Navigator,
                                                           identify: RegistrationIdentifierAction,
                                                           getData: DraftIdRetrievalActionProvider,
                                                           validateIndex: IndexActionFilterProvider,
                                                           requireData: RegistrationDataRequiredAction,
                                                           nameRequired: NameRequiredActionProvider,
                                                           yesNoFormProvider: YesNoFormProvider,
                                                           val controllerComponents: MessagesControllerComponents,
                                                           view: SettlorBusinessAddressUKYesNoView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("settlorBusinessAddressUKYesNo")

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      validateIndex(index, LivingSettlors) andThen
      nameRequired(index, draftId)

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(SettlorBusinessAddressUKYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index, request.businessName))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index, request.businessName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorBusinessAddressUKYesNoPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorBusinessAddressUKYesNoPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
