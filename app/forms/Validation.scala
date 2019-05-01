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

package forms

object Validation {

  val countryRegex = "^[A-Za-z ,.()'-]*$"
  val postcodeRegex = """^[a-zA-Z]{1,2}[0-9][0-9a-zA-Z]?\s?[0-9][a-zA-Z]{2}$"""
  val nameRegex = "^[A-Za-z0-9 ,.()/&'-]*$"
  val utrRegex = "^[0-9]*$"
  val ninoRegex = """^(?i)[ \t]*[A-Z]{1}[ \t]*[ \t]*[A-Z]{1}[ \t]*[0-9]{1}[ \t]*[ \t]*[0-9]{1}[ \t]*""" +
    """[ \t]*[0-9]{1}[ \t]*[ \t]*[0-9]{1}[ \t]*[ \t]*[0-9]{1}[ \t]*[ \t]*[0-9]{1}[ \t]*[A-D]{1}[ \t]*$"""
  val telephoneRegex = """^\+[0-9 ]{1,18}$|^[0-9 ]{1,19}$"""
  val addressLineRegex = "^[A-Za-z0-9 ,.()/&'-]*$"
  val clientRefRegex = "^[A-Za-z0-9&'`-]*$"
  val currencyRegex = "^[0-9]{1,12}$"
  val numericRegex = "^[0-9]*$"
  val percentageRegex = "^([0-9]|[1-9][0-9]|100)$"
  val decimalCheck = "^[^.]*$"

}