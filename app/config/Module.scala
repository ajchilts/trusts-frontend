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

package config

import com.google.inject.AbstractModule
import connector.OtacAuthConnectorImpl
import controllers.actions._
import controllers.actions.register._
import navigation.Navigator
import navigation.registration.{LivingSettlorNavigator, PartnershipNavigator, PropertyOrLandNavigator}
import repositories.{DefaultRegistrationsRepository, RegistrationsRepository}
import uk.gov.hmrc.auth.otac.OtacAuthConnector
import utils.annotations.{LivingSettlor, Partnership, PropertyOrLand}
import utils.{DateFormatter, TrustsDateFormatter}

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[RegistrationDataRetrievalAction]).to(classOf[RegistrationDataRetrievalActionImpl]).asEagerSingleton()
    bind(classOf[RegistrationDataRequiredAction]).to(classOf[RegistrationDataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[DraftIdRetrievalActionProvider]).to(classOf[DraftIdDataRetrievalActionProviderImpl]).asEagerSingleton()
    bind(classOf[RequireDraftRegistrationActionRefiner]).to(classOf[RequireDraftRegistrationActionRefinerImpl]).asEagerSingleton()

    bind(classOf[TaskListCompleteActionRefiner]).to(classOf[TaskListCompleteActionRefinerImpl]).asEagerSingleton()

    bind(classOf[RequiredAgentAffinityGroupActionProvider]).to(classOf[RequireStateActionProviderImpl]).asEagerSingleton()

    bind(classOf[RegistrationsRepository]).to(classOf[DefaultRegistrationsRepository]).asEagerSingleton()

    bind(classOf[OtacAuthConnector]).to(classOf[OtacAuthConnectorImpl]).asEagerSingleton()

    bind(classOf[Navigator]).annotatedWith(classOf[PropertyOrLand]).to(classOf[PropertyOrLandNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[LivingSettlor]).to(classOf[LivingSettlorNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[Partnership]).to(classOf[PartnershipNavigator])

    bind(classOf[DateFormatter]).to(classOf[TrustsDateFormatter])
  }
}
