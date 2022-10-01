package net.astail

import net.astail.Main.twitter_id
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.joda.time.DateTime
import org.joda.time.Hours

import com.github.nscala_time.time.Imports._

import java.net.URL
import scala.io.Source


object ika {
  val coopNow = "https://spla3.yuu26.com/api/coop-grouping-regular/now"

  def result(url: String) = {
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


  def timeDisplay(time: String): String = time.toDateTime.toString("yyyy-MM-dd HH:mm")

  def coopToDiscord: String = {
    val coop = coopGetNow
    val timestamp: DateTime = DateTime.now()
    val kumaSan: String = {
      val endHour: Int = Hours.hoursBetween(timestamp, coop.end_time.toDateTime).getHours()
      s"バイト募集中 @${endHour}時間"
    }

    s"""${kumaSan}
       |時間: ${timeDisplay(coop.start_time)} ~ ${timeDisplay(coop.end_time)}
       |ステージ: ${coop.stage.name}
       |武器: ${coop.weapons.map(_.name).mkString(",")}
       |""".stripMargin
  }

}
