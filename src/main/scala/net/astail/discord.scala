package net.astail

import net.astail.ImageMagickWrapper.delImage
import net.astail.Main.{rev, token}
import net.astail.ika._
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.utils.{Compression, FileUpload}
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.{Logger, LoggerFactory}

import java.io.File
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object discord {
  val setStatus = "rev: " + rev
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def setupBuilder = {
    JDABuilder.createDefault(token)
      .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
      .setBulkDeleteSplittingEnabled(false)
      .setCompression(Compression.NONE)
      .setActivity(Activity.playing(setStatus))
      .addEventListeners(new MessageListener)
      .build
  }

  def activityUpdate(st: String) = {
    val builder = JDABuilder.createDefault(token)
    builder.build.getPresence.setActivity(Activity.playing(st))
  }

  def setupSlashCommand = {
    val jda = JDABuilder.createDefault(token)
      .addEventListeners(new SlashCommand)
      .build
    jda.upsertCommand("coop", "今のサーモンランの情報を返します").queue()
    jda.upsertCommand("coop-n", "次のサーモンランの情報を返します").queue()
  }

  class SlashCommand extends ListenerAdapter {
    override def onSlashCommandInteraction(event: SlashCommandInteractionEvent): Unit = {
      val message = event.getName
      message match {
        case "coop" => event.reply(coopToDiscord(coopGet(Now), Now)).setEphemeral(true).queue()
        case "coop-n" => event.reply(coopToDiscord(coopGet(Next), Next)).setEphemeral(true).queue()
        case _ => None
      }
    }
  }

  class MessageListener extends ListenerAdapter {
    override def onMessageReceived(event: MessageReceivedEvent): Unit = {
      val botName = event.getJDA.getSelfUser.getName
      val userName = event.getMember.getUser.getName
      val userNickname = event.getMember.getNickname
      val userNameGet = {
        userNickname match {
          case null => userName
          case _ => userNickname
        }
      }

      val userId = event.getMember.getUser.getIdLong
      val message = event.getMessage.getContentDisplay

      def sendMessage(x: String) = event.getChannel.sendMessage(x).queue()

      def uploadFile(x: String, filePath: String) = {
        event.getChannel.sendMessage(x).addFiles(FileUpload.fromData(new File(filePath))).queue()
      }

      val helpList = List("help", "--help", "-h", "version", "--version", "-v")

      def help = {
        sendMessage(
          s"""
             |今のサーモンラン:
             |  `/coop`
             |  `@$botName coop`
             |
             |次のサーモンラン:
             |  `/coop-n`
             |  `@$botName coop-n`
             |
             |version: `${rev}`
             | """.stripMargin
        )
      }

      def messageMatch = {
        message.diff(s"@$botName").trim match {
          case s if helpList.contains(s) => help
          case "test" => sendMessage(s"userId: ${userId}, botName: ${botName}, userNameGet: ${userNameGet}")
          case "coop" => {
            sendMessage("今のサーモンラン情報を確認中")
            val coop: Coop = coopGet(Now)
            val imageDir = coopImage(coop)
            uploadFile(coopToDiscord(coop, Now), imageDir)
            delImage(imageDir)
          }
          case "coop-n" => {
            sendMessage("次のサーモンラン情報を確認中")
            val coop: Coop = coopGet(Next)
            val imageDir = coopImage(coop)
            uploadFile(coopToDiscord(coop, Next), imageDir)
            delImage(imageDir)
          }
          case _ => None
        }
      }


      if (!event.getAuthor.isBot) {
        Future {
          messageMatch
        }
      }
    }
  }

}
