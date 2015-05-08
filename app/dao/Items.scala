package dao

import helpers.PostgresSupport
import models.Item
import play.api.db.slick.Config.driver.simple._

object Items extends PostgresSupport{
  lazy val items = TableQuery[ItemTable]

  class ItemTable(tag: Tag) extends Table[Item](tag, "items") {
    def idItem = column[Int]("id_item", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.NotNull)
    def damage = column[Int]("damage", O.Nullable, O.Default(0))
    def armor = column[Int]("armor", O.Nullable, O.Default(0))

    def * = (idItem.?, name, damage.?, armor.?) <> (Item.tupled, Item.unapply _)

    def ? = (idItem.?, name.?, damage.?, armor.?)
  }

  def findAll = items.list

  def save(item: Item): Item = {
    item.idItem match {
      case None => {
        val id = (items returning items.map(_.idItem)) += item
        item.copy(idItem = Some(id))
      }
      case Some(id) => {
        val query = for {
          c <- items if (c.idItem === id)
        } yield c
        query.update(item)
        item
      }
    }
  }
}
