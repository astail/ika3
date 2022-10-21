package net.astail

import net.astail.Main.twitter_id
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.joda.time.DateTime
import org.joda.time.Hours
import com.github.nscala_time.time.Imports._
import net.astail.ImageMagickWrapper.{Height, Width, delImage, imageAppend, resize, sizeCheck}

import java.net.URL
import scala.io.Source


object ika {

  val coopUrl = "https://spla3.yuu26.com/api/coop-grouping-regular"


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

  def coopGet(p2: P2): Coop = {
    val time = p2 match {
      case Now => "now"
      case Next => "next"
    }
    val url = s"${coopUrl}/${time}"

    val jsonObj = result(url)
    implicit val formats = DefaultFormats
    (jsonObj \ "results").extract[List[Coop]].head
  }


  def timeDisplay(time: String): String = time.toDateTime.toString("yyyy-MM-dd HH:mm")

  def coopToDiscord(p2: P2): String = {
    val coop = coopGet(p2: P2)
    val timestamp: DateTime = org.joda.time.DateTime.now()
    val kumaSan: String = {
      val endHour: Int = Hours.hoursBetween(timestamp, coop.end_time.toDateTime).getHours()
      s"バイト募集中 @${endHour}時間"
    }

    s"""${kumaSan}
       |時間: ${timeDisplay(coop.start_time)} ~ ${timeDisplay(coop.end_time)}
       |ステージ: ${coop.stage.name}
       |武器: ${coop.weapons.map(_.name).mkString(", ")}
       |""".stripMargin
  }

  def mergeWeaponsAndMaps(stageImageURL: String, weaponsImageURL: List[String]): String = {
    val mapData = sizeCheck(stageImageURL)
    val weaponsImage: String = imageAppend(weaponsImageURL, Width)
    val resizeWeaponsImage: String = resize(weaponsImage, mapData.width, Width)
    val merge = imageAppend(List(stageImageURL, resizeWeaponsImage), Height)
    delImage(resizeWeaponsImage)
    merge
  }

  def coopImage(p2: P2): String = {
    val coop = coopGet(p2)
    val stageImageURL: String = coop.stage.image
    val weaponsImageURL: List[String] = coop.weapons.map(_.image)
    mergeWeaponsAndMaps(stageImageURL, weaponsImageURL)
  }

}
