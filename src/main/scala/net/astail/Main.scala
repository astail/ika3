package net.astail

import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}

object Main {
  val tag = net.astail.Git.tag
  val rev = net.astail.Git.shortHash
  val twitter_id = ConfigFactory.load.getString("twitter_id")
  val token = ConfigFactory.load.getString("ika3_discord_token")
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  def main(args: Array[String]): Unit = {

    s"""
       | ==================== start ika3 ===================
       |
       |    tag : $tag
       |    rev : $rev
       |
       | ===================================================
       """.stripMargin.split('\n').foreach(logger.info)

    //discord.activityUpdate("test")
    discord.setupBuilder
    //discord.slash
  }
}
