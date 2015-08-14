package controllers

import dao.defaultdao.{Players, Items, Inventories}
import models._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick.DBAction
import play.api.libs.json.Json
import play.api.mvc._
import repository.Repository

object Application extends Controller {

  implicit val playerJson = Json.format[Player]
  implicit val itemJson = Json.format[Item]
  implicit val inventoryJson = Json.format[Inventory]
  implicit val playerBagJson = Json.format[PlayerBag]
  implicit val bagPlayerJson = Json.format[BagPlayer]

  val playerForm = Form(
    mapping (
      "idPlayer" -> optional(number),
      "name" -> text,
      "level" -> number
    )(Player.apply)(Player.unapply)
  )

  val itemForm = Form(
    mapping(
      "idItem" -> optional(number),
      "name" -> text,
      "damage" -> optional(number),
      "armor" -> optional(number)
    )(Item.apply)(Item.unapply)
  )

  val inventoryForm = Form(
    mapping(
      "idPlayer" -> number,
      "idItem" -> number
    )(Inventory.apply)(Inventory.unapply)
  )

  val playerBagForm = Form(
    mapping(
      "idPlayer" -> number,
      "name" -> text,
      "level" -> number,
      "items" -> list(mapping(
        "idItem" -> optional(number),
        "name" -> text,
        "damage" -> optional(number),
        "armor" -> optional(number)
      )(Item.apply)(Item.unapply)
      )
    )(PlayerBag.apply)(PlayerBag.unapply)
  )

  def index = Action {
    Ok(views.html.index(Players.findAll))
  }

  def addSinglePlayer = DBAction { implicit rs =>
    val player = playerForm.bindFromRequest.get
    /*Players.savePlayer(player)*/

    Redirect(routes.Application.index)
  }

  def findAll = DBAction{ implicit rs =>
    Ok(Json.toJson(Players.findAll))
  }

  def addMultiPlayer = DBAction(parse.json) { implicit rs =>
    rs.request.body.validate[Player].map { player =>
      /*Players.savePlayer(player)*/

      Ok(Json.toJson(player))
    }.recoverTotal{
      e => BadRequest("Invalid Json")
    }
  }

  def buySingleItem = DBAction(parse.json){ implicit rs =>
    rs.request.body.validate[List[Inventory]].map { inventory =>
      Inventories.save(inventory)

      Ok("Added items")
    }.recoverTotal{
      e => BadRequest("Invalid Json")
    }
  }

  def getSinglePlayerBag = DBAction { implicit rs =>
    val ceva = BagPlayer(Players.findAll, Items.findAll)

    Ok(Json.toJson(ceva))
  }

  def getMultiPlayerBag = DBAction{ implicit rs =>
    Ok(Json.toJson(Inventories.getPlayersWithItems))
  }


  def test = DBAction{ implicit rs =>
    Repository.save()
    Ok("merge")
  }
}