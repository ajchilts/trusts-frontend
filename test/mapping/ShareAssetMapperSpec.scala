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

package mapping

import base.SpecBaseHelpers
import generators.Generators
import models.Status.{Completed, InProgress}
import models.{ShareClass, WhatKindOfAsset}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.entitystatus.AssetStatus
import pages.shares.{ShareClassPage, ShareCompanyNamePage, SharePortfolioNamePage, SharePortfolioOnStockExchangePage, SharePortfolioQuantityInTrustPage, SharePortfolioValueInTrustPage, ShareQuantityInTrustPage, ShareValueInTrustPage, SharesInAPortfolioPage, SharesOnStockExchangePage}
import pages.{AssetMoneyValuePage, WhatKindOfAssetPage}

class ShareAssetMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val shareAssetMapper : Mapping[List[SharesType]] = injector.instanceOf[ShareAssetMapper]

  "ShareAssetMapper" - {
    "must not be able to create a share asset when missing values in user answers" in {

      val userAnswers = emptyUserAnswers
        .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value
        .set(SharesInAPortfolioPage(0), true).success.value
        .set(AssetStatus(0), InProgress).success.value

      shareAssetMapper.build(userAnswers) mustNot be(defined)

    }

    "non-portfolio" - {

      "must be able to create a Share Asset" in {
        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(0), false).success.value
          .set(ShareCompanyNamePage(0), "Portfolio").success.value
          .set(ShareQuantityInTrustPage(0), "20").success.value
          .set(ShareValueInTrustPage(0), "300").success.value
          .set(SharesOnStockExchangePage(0), true).success.value
          .set(ShareClassPage(0), ShareClass.Deferred).success.value
          .set(AssetStatus(0), Completed).success.value

        shareAssetMapper.build(userAnswers).value mustBe Assets(
          monetary = None,
          propertyOrLand = None,
          shares = Some(
            List(
              SharesType(
                numberOfShares = "20",
                orgName = "Portfolio",
                shareClass = "Deferred ordinary shares",
                typeOfShare = "Quoted",
                value = 300
              )
            )
          ),
          business = None,
          partnerShip = None,
          other = None
        )
      }

    }

    "portfolio" - {

      "must be able to create a Share Asset" in {
        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(0), true).success.value
          .set(SharePortfolioNamePage(0), "Portfolio").success.value
          .set(SharePortfolioQuantityInTrustPage(0), "30").success.value
          .set(SharePortfolioValueInTrustPage(0), "999999999999").success.value
          .set(SharePortfolioOnStockExchangePage(0), false).success.value
          .set(AssetStatus(0), Completed).success.value

        shareAssetMapper.build(userAnswers).value mustBe Assets(
          monetary = None,
          propertyOrLand = None,
          shares = Some(
            List(
              SharesType(
                numberOfShares = "30",
                orgName = "Portfolio",
                shareClass = "Other",
                typeOfShare = "Unquoted",
                value = 999999999999L
              )
            )
          ),
          business = None,
          partnerShip = None,
          other = None
        )
      }

    }
  }
}
