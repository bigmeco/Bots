package com.bigmeco

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.callbackQuery
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.entities.InlineKeyboardMarkup
import me.ivmg.telegram.entities.KeyboardButton
import me.ivmg.telegram.entities.KeyboardReplyMarkup
import me.ivmg.telegram.entities.InlineKeyboardButton
import me.ivmg.telegram.network.fold


fun main(args: Array<String>) {
        val bot = bot {
            token = "829470729:AAHgoo9woTpGms1vxY6SVPDymLTvUPn_gQA"
            dispatch {
                command("inlineButtons") { bot, update ->
                    val chatId = update.message?.chat?.id ?: return@command

                    val inlineKeyboardMarkup = InlineKeyboardMarkup(generateButtons())
                    bot.sendMessage(chatId = chatId, text = "Hello, inline buttons!", replyMarkup = inlineKeyboardMarkup)
                }
                command("showAlert") { bot, update ->
                    val chatId = update.message?.chat?.id ?: return@command

                    val inlineKeyboardMarkup = InlineKeyboardMarkup(generateButtons())
                    bot.sendMessage(chatId = chatId, text = "Hello, inline buttons!", replyMarkup = inlineKeyboardMarkup)
                }
                command("start") { bot, update->
                    val chatId = update.message?.chat?.id ?: return@command

                    val keyboardMarkup = KeyboardReplyMarkup(keyboard = generateUsersButton(), resizeKeyboard = true)
                    val result = bot.sendMessage(chatId = update.message!!.chat.id, text = "Hello, users buttons!",  replyMarkup = keyboardMarkup)
                    result.fold({
                    },{
                        // do something with the error
                    })
                }
                callbackQuery("testButton") { bot, update ->
                    update.callbackQuery?.let {
                        val chatId = it.message?.chat?.id ?: return@callbackQuery
                        bot.sendMessage(chatId = chatId, text = it.data)
                    }
                }
            }
        }
    bot.startPolling()



    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText(bot.getChat(1).first!!.body().toString(), ContentType.Text.Plain)
            }

            get("/demo") {
                call.respondText("HELLO WORLD!")
            }

        }
    }
    server.start(wait = true)


}
fun generateUsersButton(): List<List<KeyboardButton>> {
    return listOf(
        listOf(KeyboardButton("Request location (not supported on desktop)", requestLocation = true)),
        listOf(KeyboardButton("Request contact", requestContact = true))
    )
}

fun generateButtons(): List<List<InlineKeyboardButton>> {
    return listOf(
        listOf(InlineKeyboardButton(text = "Test Inline Button", callbackData = "testButton")),
        listOf(InlineKeyboardButton(text = "Show alert", callbackData = "showAlert"))
    )
}