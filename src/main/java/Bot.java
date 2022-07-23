import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

public class Bot extends TelegramLongPollingBot{
		
	Inv inv = new Inv();
	int co = 0;
	
	
	public static void main(String[] args) throws IOException {
		
		ApiContextInitializer.init(); //инициализаци€ API
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(); //создание объекта в API
		try {
			telegramBotsApi.registerBot(new Bot());
			
		}catch (TelegramApiRequestException e) {
			e.printStackTrace(); 
		}
			
		
	}
	
		//что бот будет отвечать
		public void sendMsg(Message message, String text) {
		
			
			
			SendMessage sendMessage = new SendMessage();
			sendMessage.enableMarkdown(true);
			
			//чат айди, чтобы было пон€тно кому отвечать
			sendMessage.setChatId(message.getChatId());
			
			//конкретно, на какое сообщение ответить
			//sendMessage.setReplyToMessageId(message.getMessageId());
			
			sendMessage.setText(text);
			try {
				//добавили кнопку и поместили в нее сообщение
				setButtons(sendMessage);
				sendMessage(sendMessage);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
			
			
		
		}
		
	
	//метод дл€ приема сообщений и обновлений
	public void onUpdateReceived(Update update) {
		
		Message message = update.getMessage();
	
		if (message != null && message.hasText()){
			
			System.out.println("“екстик: " + message.getText());
			switch(message.getText()) {
			
				
			case "/start":
				long id = message.getChatId();

				sendMsg(message, "Ѕот содержит следующие команды: \n /help - помощь \n" + "/inv - посмотреть инвентарь \n" + "/find - искать новый предмет"); 
						
				break;
				
			case "/inv":
				if(inv.getInvSize() != 0) {
					
			
				sendMsg(message, "¬аш инвентарь: ");
	
				
				//sendMsg(message, "\u26BD");

					sendMsg(message, "\n" + inv.showInventory() + "\n");
	
				}else {
					sendMsg(message, "¬аш инвентарь пуст ");
				}
				break;
				
			case "/find":

				Item i = inv.findItem();
				sendMsg(message, "¬ы нашли: " + i.getTitle() + " |" + i.getRarity() + "| " + 
				i.getCost() + "$");
				
				System.out.println("¬зывано /find: " + co + " " + message.getChatId());
				System.out.println("“екстик: " + message.getText());
				co++;
				
				
				
				break;
			case "/balance":
				
				sendMsg(message, "¬аш баланс: " + inv.getBalance() + "$");
				
				break;
			
			default: 
				sendMsg(message, "Ќеизвестна€ команда");
				break;
				
				/*
				 * TODO LIST
				 * 
				 * 
				 * Ќужно сделать дл€ каждого пользовател€ уникальный экземпл€р Inv
				 * чтобы сначала провер€лс€ ID пользовател€, а потом если его не существует то инстанцировать дл€ него новый ID
				 * message.getChatId() - возвращает ID вшитый в телеграм аккаунт
				 * 
				 * ¬озможно нужно написать класс User и какой-то контейнер хранить, чтобы перед добавлением нового пользовател€ пробегатьс€
				 * по всем юзерам и если такого нет, добавл€ть его.
				 * 
				 * ƒобавить команду /allplayers чтобы считать всех участников игры
				 * 
				 * Ќу и самое сложное пока что, это возможность /find ить предметы раз в 20 минут например, провер€ть дату нужно и врем€
				 */
					
					
			}
		}
		
	}
	
	//кнопки
	
	public void setButtons(SendMessage sendMessage) {
		//инициаллизаци€ клавиатуры 
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		//установка разметки
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
		//вывод клавиатуры (видел или нет)
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		//скрывать или не скрывать после использовани€
		replyKeyboardMarkup.setOneTimeKeyboard(true);
		
		List<KeyboardRow> keyboardRowList = new ArrayList<>();
		KeyboardRow keyboardFirstRow = new KeyboardRow();
		
		//добавили новую кнопку в первый р€д
		keyboardFirstRow.add(new KeyboardButton("/inventory"));
		keyboardFirstRow.add(new KeyboardButton("/find"));
		//добавили в спиок всех кнопок
		keyboardRowList.add(keyboardFirstRow);
		
	}
	

	public String getBotUsername() {
		
		return "Needle";
	}

	 
	public String getBotToken() {
	
		return "1286692994:AAFxHRBuJ1FIzQFBizgPHrng37ctoFtzLLY";
		//токен регаетс€ через бот самого тг BotFather, там же пишетс€ описание, название и токен
	}

}
