package models

case class Item (
  idItem: Option[Int],
  name: String,
  damage: Option[Int],
  armor: Option[Int]
)