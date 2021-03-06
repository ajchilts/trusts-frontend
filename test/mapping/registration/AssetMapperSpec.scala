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

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping._
import models.core.pages.UKAddress
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.entitystatus.AssetStatus
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.business._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.other._
import pages.register.asset.partnership._
import pages.register.asset.property_or_land._
import pages.register.asset.shares._

class AssetMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val assetMapper: Mapping[Assets] = injector.instanceOf[AssetMapper]

  "AssetMapper" - {

    "when user answers is empty" - {

      "must not be able to create Assets" in {

        val userAnswers = emptyUserAnswers

        assetMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty " - {

      "must be able to create Assets for money" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(0), "2000").success.value
          .set(AssetStatus(0), Completed).success.value


        val expected = Some(Assets(Some(List(AssetMonetaryAmount(2000))),None,None,None,None,None))

        assetMapper.build(userAnswers) mustBe expected
      }

      "must be able to create Assets for shares" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(0), true).success.value
          .set(SharePortfolioNamePage(0), "Portfolio").success.value
          .set(SharePortfolioQuantityInTrustPage(0), "30").success.value
          .set(SharePortfolioValueInTrustPage(0), "999999999999").success.value
          .set(SharePortfolioOnStockExchangePage(0), false).success.value
          .set(AssetStatus(0), Completed).success.value


        val expected = Some(Assets(None,None,Some(List(SharesType("30","Portfolio","Other","Unquoted",999999999999L))),None,None,None))

        assetMapper.build(userAnswers) mustBe expected
      }

      "must be able to create Assets for both shares and money" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(0), true).success.value
          .set(SharePortfolioNamePage(0), "Portfolio").success.value
          .set(SharePortfolioQuantityInTrustPage(0), "30").success.value
          .set(SharePortfolioValueInTrustPage(0), "999999999999").success.value
          .set(SharePortfolioOnStockExchangePage(0), false).success.value
          .set(AssetStatus(0), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(1), "2000").success.value
          .set(AssetStatus(1), Completed).success.value


        val expected = Some(Assets(Some(List(AssetMonetaryAmount(2000))),None,Some(List(SharesType("30","Portfolio","Other","Unquoted",999999999999L))),None,None,None))

        assetMapper.build(userAnswers) mustBe expected
      }

      "must be able to create Assets for both shares, money and property or land" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(0), true).success.value
          .set(SharePortfolioNamePage(0), "Portfolio").success.value
          .set(SharePortfolioQuantityInTrustPage(0), "30").success.value
          .set(SharePortfolioValueInTrustPage(0), "999999999999").success.value
          .set(SharePortfolioOnStockExchangePage(0), false).success.value
          .set(AssetStatus(0), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(1), "2000").success.value
          .set(AssetStatus(1), Completed).success.value
          .set(WhatKindOfAssetPage(2), WhatKindOfAsset.PropertyOrLand).success.value
          .set(PropertyOrLandAddressYesNoPage(2), true).success.value
          .set(PropertyOrLandAddressUkYesNoPage(2), true).success.value
          .set(PropertyOrLandUKAddressPage(2), UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")).success.value
          .set(PropertyOrLandTotalValuePage(2), "1000").success.value
          .set(TrustOwnAllThePropertyOrLandPage(2), false).success.value
          .set(PropertyLandValueTrustPage(2), "750").success.value


        val expected = Some(Assets(Some(List(AssetMonetaryAmount(2000))),
          Some(List(PropertyLandType(None, Some(AddressType("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), Some("Z99 2YY"), "GB")), 1000, 750L))),
          Some(List(SharesType("30","Portfolio","Other","Unquoted",999999999999L))),None,None,None))

        assetMapper.build(userAnswers) mustBe expected
      }

      "must be able to create Assets for partnership" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Partnership).success.value
          .set(PartnershipDescriptionPage(0), "Partnership Description").success.value
          .set(PartnershipStartDatePage(0), LocalDate.now).success.value
          .set(AssetStatus(0), Completed).success.value

        val expected = Some(Assets(
          monetary = None,
          propertyOrLand = None,
          shares = None,
          business = None,
          partnerShip = Some(List(PartnershipType("Partnership Description", LocalDate.now))),
          other = None
        ))

        assetMapper.build(userAnswers) mustBe expected
      }

      "must be able to create Assets for other" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Other).success.value
          .set(OtherAssetDescriptionPage(0), "Description").success.value
          .set(OtherAssetValuePage(0), "4000").success.value
          .set(AssetStatus(0), Completed).success.value

        val expected = Some(Assets(
          monetary = None,
          propertyOrLand = None,
          shares = None,
          business = None,
          partnerShip = None,
          other = Some(List(OtherAssetType("Description", 4000)))
        ))

        assetMapper.build(userAnswers) mustBe expected
      }

      "must be able to create Assets for business asset" in {

        val address = UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Business).success.value
          .set(BusinessNamePage(0), "Test").success.value
          .set(BusinessDescriptionPage(0), "Description").success.value
          .set(BusinessAddressUkYesNoPage(0), true).success.value
          .set(BusinessUkAddressPage(0), address).success.value
          .set(BusinessValuePage(0), "123").success.value
          .set(AssetStatus(0), Completed).success.value

        val expected = Some(Assets(
          monetary = None,
          propertyOrLand = None,
          shares = None,
          business = Some(List(BusinessAssetType("Test", "Description", AddressType("26","Grangetown", Some("Tyne and Wear"), Some("Newcastle"), Some("Z99 2YY"), "GB"), 123L))),
          partnerShip = None,
          other = None
        ))

        assetMapper.build(userAnswers) mustBe expected
      }
    }
  }
}