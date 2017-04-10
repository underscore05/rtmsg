package drivers

import com.github.tminglei.slickpg.PgDate2Support
import slick.driver.PostgresDriver
import tables.ShortcodeTypes
import tables.ShortcodeTypes.ShortcodeType

/**
  * Created by richardroque on Apr/01/2017.
  */
object DatabaseDriver extends PostgresDriver with PgDate2Support {

  override val api = CustomApi

  object CustomApi extends API with DateTimeImplicits {
    implicit val shortcodeTypeMapper = MappedJdbcType.base[ShortcodeType, String](_.toString, ShortcodeTypes.withName)
  }

}
