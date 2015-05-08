package dao

import helpers.PostgresSupport
import models.{Item, PlayerBag, Inventory}
import play.api.Logger
import play.api.db.slick.Config.driver.simple._

object Inventories extends PostgresSupport {
  val inventories = TableQuery[InventoryTable]

  class InventoryTable(tag: Tag) extends Table[Inventory](tag, "inventories") {
    def idPlayer = column[Int]("id_player", O.NotNull)
    def idItem = column[Int]("id_item", O.NotNull)

    def * = (idPlayer, idItem) <> (Inventory.tupled, Inventory.unapply _)

    def ? = (idPlayer.?, idItem.?)

    def playerFK = foreignKey("inventory_idPlayer", idPlayer, Players.players)(_.idPlayer)
    def itemFK = foreignKey("inventory_idItem", idItem, Items.items)(_.idItem)
  }

  def findAll = inventories.list

  def save(inventory: Inventory): Unit = {
    inventories += inventory
  }

  def save(inventories: List[Inventory]): Unit = {
    inventories.foreach(save)
  }

  def getPlayersWithItems: List[PlayerBag] = {
    val query = for {
      ((p, im), it) <- Players.players leftJoin inventories on (_.idPlayer === _.idPlayer) leftJoin
        Items.items on (_._2.idItem === _.idItem)
    } yield (p, it.?)

    val result = query.list

    Logger.debug("list: " + result)

    result.groupBy(_._1)
      .mapValues(_.map(_._2))
      .toList.map{
        case(player, item) =>
          PlayerBag(
            idPlayer = player.idPlayer.get,
            name = player.name,
            level = player.level,
            items = if (item.head._1.isDefined) item.map {item =>
              Item(
                idItem = item._1,
                name = item._2.getOrElse(""),
                damage = item._3,
                armor = item._4
              )
            }
            else List()
          )
      }
  }

}


