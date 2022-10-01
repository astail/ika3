package net.astail

import net.astail.Main.{rev, token}
import net.astail.ika._
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.utils.Compression
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.{Logger, LoggerFactory}

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

  def slash = {
      val jda = JDABuilder.createDefault(token)
        .addEventListeners(new Bot)
        .build
      jda.upsertCommand("coop", "Information on the current coop").queue()
    }

  class Bot extends ListenerAdapter {
    override def onSlashCommandInteraction(event: SlashCommandInteractionEvent): Unit = {
      val message = event.getName
      message match {
        case "coop" => event.reply(coopToDiscord).setEphemeral(true).queue()
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

      def sendMessage(x: String) = event.getChannel.sendMessage(x).queue


      def messageMatch = {
        message.diff(s"@$botName").trim match {
          case "test" => sendMessage(s"userId: ${userId}, botName: ${botName}, userNameGet: ${userNameGet}")
          case "coop" => sendMessage(coopToDiscord)
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
