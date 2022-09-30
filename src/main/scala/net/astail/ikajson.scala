package net.astail

case class Stage(id: Int, name: String, image: String)
case class Weapons(name: String, image: String)
case class Coop(start_time: String,
                end_time: String,
                stage: Stage,
                weapons: List[Weapons]
               )
