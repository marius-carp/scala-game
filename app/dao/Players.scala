package dao

import helpers.PostgresSupport
import models.Player
import play.api.db.slick.Config.driver.simple._

object Players extends PostgresSupport {
  lazy val players = TableQuery[PlayerTable]

  class PlayerTable(tag: Tag) extends Table[Player](tag, "players") {
    def idPlayer = column[Int]("id_player", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.NotNull)
    def level = column[Int]("level")

    def * = (idPlayer.?, name, level) <> (Player.tupled, Player.unapply _)
  }

  def findAll = players.list

  def savePlayer(player: Player): Player = {
    player.idPlayer match {
      case None => {
        val id = (players returning players.map(_.idPlayer)) += player
        player.copy(idPlayer = Some(id))
      }
      case Some(id) => {
        val query = for {
          c <- players if (c.idPlayer === id)
        } yield c
        query.update(player)
        player
      }
    }
  }

}
