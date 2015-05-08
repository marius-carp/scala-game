import dao.Items
import models.Item
import play.api.GlobalSettings
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    if(Items.findAll.isEmpty) {
      Seq(
        Item(
          None,
          name = "Death Axe",
          damage = Some(11),
          armor = None
        ),
        Item(
          None,
          name = "Shinny Shield",
          damage = Some(2),
          armor = Some(10)
        ),
        Item(
          None,
          name = "Justice Chest",
          damage = None,
          armor = Some(7)
        ),
        Item(
          None,
          name = "Warrior\'s Helmet",
          damage = None,
          armor = Some(5)
        ),
        Item(
          None,
          name = "Plate Pants",
          damage = Some(11),
          armor = None
        )
      ).foreach(Items.save)
    }
  }

}
