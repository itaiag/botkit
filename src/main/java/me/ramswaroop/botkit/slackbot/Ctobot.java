package me.ramswaroop.botkit.slackbot;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import me.ramswaroop.botkit.slackbot.core.Bot;
import me.ramswaroop.botkit.slackbot.core.Controller;
import me.ramswaroop.botkit.slackbot.core.EventType;
import me.ramswaroop.botkit.slackbot.core.models.Event;
import me.ramswaroop.botkit.slackbot.core.models.Message;

@Component
public class Ctobot extends Bot {

	private static final Logger log = LoggerFactory.getLogger(SlackBot.class);

	/**
	 * Slack token from application.properties file. You can get your slack
	 * token next <a href="https://my.slack.com/services/new/bot">creating a new
	 * bot</a>.
	 */
	@Value("${slackBotToken}")
	private String slackToken;

	@Override
	public String getSlackToken() {
		return slackToken;
	}

	@Override
	public Bot getSlackBot() {
		return this;
	}

	@Controller(events = EventType.MESSAGE, pattern = "[Jj][Ss]ystem version?")
	public void latestJSystemVersion(WebSocketSession session, Event event, Matcher matcher) {
		log.debug("Asked what is the latest JSystem version");
		reply(session, event, new Message("The latest JSystem version is 6.1.04"));
	}

	@Controller(events = EventType.MESSAGE, pattern = "question", next = "canIHelp")
	public void questionsAsked(WebSocketSession session, Event event, Matcher matcher) {
		startConversation(event, "canIHelp");
		reply(session, event, new Message("Can I help with that question?"));
	}

	@Controller(next = "moreDetails")
	public void canIHelp(WebSocketSession session, Event event) {
		if (event.getText().toLowerCase().contains("yes") || event.getText().toLowerCase().contains("sure")) {
			reply(session, event, new Message("Great. Shoot"));
			nextConversation(event);
		} else {
			reply(session, event, new Message("Ok... whaever"));
			stopConversation(event);
		}
	}

	@Controller(next = "isItReproducable")
	public void moreDetails(WebSocketSession session, Event event) {
		reply(session, event, new Message("I need more details. '" + event.getText()
				+ "' is not enough. Think about it like you are asking a question in StackOverflow"));
		nextConversation(event);
	}

	@Controller(next = "haveYouDebugged")
	public void isItReproducable(WebSocketSession session, Event event) {
		reply(session, event, new Message("Is that thing reproducable?"));
		nextConversation(event);
	}

	@Controller(next = "writeInTheForum")
	public void haveYouDebugged(WebSocketSession session, Event event) {
		if (event.getText().toLowerCase().contains("yes") || event.getText().toLowerCase().contains("sure")) {
			reply(session, event, new Message("Have you tried to debug it?"));
			nextConversation(event);
		} else {
			reply(session, event, new Message("Try and get back to me"));
			stopConversation(event);
		}

	}
	
	@Controller()
	public void writeInTheForum(WebSocketSession session, Event event) {
		reply(session, event, new Message("I have an idea, write it in the Google group: https://groups.google.com/forum/#!forum/topqeng"));
		stopConversation(event);
	}


}
