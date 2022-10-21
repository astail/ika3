package net.astail

case class Stage(id: Int, name: String, image: String)
case class Weapons(name: String, image: String)
case class Coop(start_time: String,
                end_time: String,
                stage: Stage,
                weapons: List[Weapons]
               )


sealed trait P2
case object Now extends P2
case object Next extends P2