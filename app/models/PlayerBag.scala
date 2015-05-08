package models

case class PlayerBag (
  idPlayer: Int,
  name: String,
  level: Int,
  items: List[Item]
)

