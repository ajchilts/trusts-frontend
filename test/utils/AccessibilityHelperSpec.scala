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

import base.SpecBase
import org.scalatest.mockito.MockitoSugar

class AccessibilityHelperSpec extends SpecBase with MockitoSugar {

  "AccessibilityHelper" must {

    "build an accessible formated string" in {
      val trn = "XTRN1234567890"
      val response = AccessibilityHelper.formatTRN(trn)
      response mustBe "X T R N 1 2 3 4 5 6 7 8 9 0"
    }

  }
}