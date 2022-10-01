package net.astail

import net.astail.Main.twitter_id
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse

import java.net.URL
import scala.io.Source

object ika {
  val coopNow = "https://spla3.yuu26.com/api/coop-grouping-regular/now"

  def result(api: String) = {
    val url = api
    val requestProperties = Map(
      "User-Agent" -> s"twitter @${twitter_id}"
    )

    val connection = new URL(url).openConnection
    requestProperties.foreach {
      case (name, value) => connection.setRequestProperty(name, value)
    }
    val str = Source.fromInputStream(connection.getInputStream).mkString
    parse(str)
  }

  def coopGetNow: Coop = {
    val jsonObj = result(coopNow)
    implicit val formats = DefaultFormats
    (jsonObj \ "results").extract[List[Coop]].head
  }

}
