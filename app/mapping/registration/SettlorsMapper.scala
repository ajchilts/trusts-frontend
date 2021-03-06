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

package mapping.registration

import javax.inject.Inject
import mapping.Mapping
import models.core.UserAnswers

class SettlorsMapper @Inject()(individualSettlorsMapper: IndividualSettlorsMapper, businessSettlorsMapper: BusinessSettlorsMapper) extends Mapping[Settlors] {

   def build(userAnswers: UserAnswers): Option[Settlors] = {
     val individualSettlors = individualSettlorsMapper.build(userAnswers)
     val businessSettlors = businessSettlorsMapper.build(userAnswers)

     val settlors = Settlors(
       settlor = individualSettlors,
       settlorCompany = businessSettlors
     )

     settlors match {
       case Settlors(None, None) => None
       case _ => Some(settlors)
     }
  }
}
