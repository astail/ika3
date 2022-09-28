package net.astail

import org.slf4j.{Logger, LoggerFactory}

object Main {
  def main(args: Array[String]): Unit = {
    val tag = net.astail.Git.tag
    val rev = net.astail.Git.shortHash
    val logger: Logger = LoggerFactory.getLogger(this.getClass)

    s"""
       | ==================== start ika3 ===================
       |
       |    tag : $tag
       |    rev : $rev
       |
       | ===================================================
       """.stripMargin.split('\n').foreach(logger.info)

  }
}
