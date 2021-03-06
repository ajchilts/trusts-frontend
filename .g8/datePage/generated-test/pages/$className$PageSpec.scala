package pages

import java.time.LocalDate

class $className$PageSpec extends PageBehaviours {

  "$className$Page" must {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate]($className$Page)

    beSettable[LocalDate]($className$Page)

    beRemovable[LocalDate]($className$Page)
  }
}
