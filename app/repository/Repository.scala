package repository

import dao.conceptdao.Games
import dao.defaultdao.{Players, Items}
import Items.ItemTable
import Players.PlayerTable
import models.{Game, Item, Player}
import play.api.Logger
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._

object Repository {
  lazy val players = TableQuery[PlayerTable]
  lazy val items = TableQuery[ItemTable]

  def save(): Unit ={
    val player = Player(None, "Frz", 21)
    val item = Item(None, "item", Some(21), Some(21))

    play.api.db.slick.DB("default").withTransaction { implicit defSession =>
      try {
        defSession.conn.setAutoCommit(false)
        val played = Players.savePlayer(player)(defSession)
        Items.save(item)(defSession)

        play.api.db.slick.DB("concept").withTransaction { implicit conSession =>
          try {
            conSession.conn.setAutoCommit(false)
            val game = Game(None, played.name)

            Games.saveGame(game)(conSession)
            throw new Exception("rollback 1")
          }
          catch {
            case e: Exception =>
              Logger.debug("concept: " + e.getMessage)
              conSession.conn.rollback()
              throw new Exception("rollback 2")
          }
        }

        defSession.conn.commit()
      }
      catch {
        case e: Exception =>
          Logger.debug("default: " + e.getMessage)
          defSession.conn.rollback()
      }
    }

  }

}
