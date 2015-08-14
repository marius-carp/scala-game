package dao.conceptdao

import helpers.ConceptSupport
import models.{Player, Game}
import play.api.db.slick.Config.driver.simple._




object Games extends ConceptSupport{
  lazy val games = TableQuery[GameTable]
  
  class GameTable(tag: Tag) extends Table[Game](tag, "games") {
    def idGame = column[Int]("id_game", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.NotNull)

    def * = (idGame.?, name) <> (Game.tupled, Game.unapply _)
  }

  def findAll = games.list
  
  def saveGame(game: Game)(implicit session: Session): Game = {
    game.idGame match {
      case None => {
        val id = (games returning games.map(_.idGame)) += game
        game.copy(idGame = Some(id))
      }
      case Some(id) => {
        val query = for {
          c <- games if (c.idGame === id)
        } yield c
        query.update(game)
        game
      }
    }
  }


}
