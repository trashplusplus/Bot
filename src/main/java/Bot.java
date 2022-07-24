import java.io.IOException;

import java.util.*;

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

public class Bot extends TelegramLongPollingBot
{
	private Map<Long, Player> players = new HashMap<>();

	public static void main(String[] args) throws IOException
	{
		ApiContextInitializer.init(); //инициализация API
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(); //создание объекта в API
		try
		{
			telegramBotsApi.registerBot(new Bot());

		}
		catch (TelegramApiRequestException e)
		{
			e.printStackTrace();
		}
	}

	//что бот будет отвечать
	public void sendMsg(Message message, String text)
	{
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);

		//чат айди, чтобы было понятно кому отвечать
		sendMessage.setChatId(message.getChatId());

		//конкретно, на какое сообщение ответить
		//sendMessage.setReplyToMessageId(message.getMessageId());

		sendMessage.setText(text);
		try
		{
			//добавили кнопку и поместили в нее сообщение
			setButtons(sendMessage);
			sendMessage(sendMessage);
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
	}

	String waitCommand = "";
	//метод для приема сообщений и обновлений
	public void onUpdateReceived(Update update)
	{

		Message message = update.getMessage();

		if (message != null && message.hasText())
		{
			System.out.println("Текстик: " + message.getText());

			Inventory inv;

			if (waitCommand != "/sell"){


				switch (message.getText())
				{
					case "/start":

						long id = message.getChatId();

						//Проверка на ID, чтобы нажав два раза /start не создавался новый пользователь
						if(players.isEmpty()){
							players.put(id, new Player(id));
						}else{
							for(Map.Entry<Long, Player> pair : players.entrySet()){
								if (pair.getKey() != id){
									players.put(id, new Player(id));
								}
							}
						}
						sendMsg(message, "Бот содержит следующие команды: \n /help - помощь \n" + "/inv - посмотреть инвентарь \n" + "/find - искать новый предмет");

						break;

					case "/inv":

						inv = players.get(message.getChatId()).getInventory();

						if (inv.getInvSize() != 0)
						{
							sendMsg(message, "Ваш инвентарь: ");

							//sendMsg(message, "\u26BD");

							sendMsg(message, "\n" + inv.showInventory() + "\n");
							sendMsg(message, "Всего предметов: " + inv.getInvSize());
						}
						else
						{
							sendMsg(message, "Ваш инвентарь пуст ");
						}

						break;

					case "/find":

						inv = players.get(message.getChatId()).getInventory();
						Item i = inv.findItem();
						sendMsg(message, String.format("Вы нашли: %s", i.toString()));
						System.out.println("Текстик: " + message.getText());

						break;

					case "/balance":
						inv = players.get(message.getChatId()).getInventory();
						sendMsg(message, "Ваш баланс: " + inv.getBalance() + "$");
						break;
					case "/stats":
						sendMsg(message, "Всего игроков: " + players.size());
						break;
					case "/sell":
						waitCommand = "/sell";
						sendMsg(message, "Введите ID предмета, который вы хотите продать: ");
						int itemSellIndex = 0;
						inv = players.get(message.getChatId()).getInventory();
						sendMsg(message, "Предметы, доступные для продажи: ");

						for(int j = 0; j < inv.getInvSize(); j++){
							sendMsg(message, "Предмет" + "|"  + itemSellIndex + "|: " + inv.getItem(j) );
							itemSellIndex++;
						}

						break;
					default:
						sendMsg(message, "Неизвестная команда");

						break;
					/*
					 * TODO LIST
					 *
					 *
					 * Нужно сделать для каждого пользователя уникальный экземпляр Inv
					 * чтобы сначала проверялся ID пользователя, а потом если его не существует то инстанцировать для него новый ID
					 * message.getChatId() - возвращает ID вшитый в телеграм аккаунт
					 *
					 * Возможно нужно написать класс User и какой-то контейнер хранить, чтобы перед добавлением нового пользователя пробегаться
					 * по всем юзерам и если такого нет, добавлять его.
					 *
					 * Добавить команду /allplayers чтобы считать всех участников игры
					 *
					 * Ну и самое сложное пока что, это возможность /find ить предметы раз в 20 минут например, проверять дату нужно и время
					 */
				}
			}else{

				switch (message.getText()){
					default:
						inv = players.get(message.getChatId()).getInventory();
						String sellID = message.getText();
						inv.sellItem(Integer.parseInt(sellID));
						sendMsg(message, "Предмет успешно продан");
						waitCommand = "";

						break;
				}
			}
		}
	}

	//кнопки

	public void setButtons(SendMessage sendMessage)
	{
		//инициаллизация клавиатуры 
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		//установка разметки
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
		//вывод клавиатуры (видел или нет)
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		//скрывать или не скрывать после использования
		replyKeyboardMarkup.setOneTimeKeyboard(true);

		List<KeyboardRow> keyboardRowList = new ArrayList<>();
		KeyboardRow keyboardFirstRow = new KeyboardRow();

		//добавили новую кнопку в первый ряд
		keyboardFirstRow.add(new KeyboardButton("/inventory"));
		keyboardFirstRow.add(new KeyboardButton("/find"));
		//добавили в спиок всех кнопок
		keyboardRowList.add(keyboardFirstRow);

	}


	public String getBotUsername()
	{
		return "Needle";
	}


	public String getBotToken()
	{
		return "1286692994:AAFxHRBuJ1FIzQFBizgPHrng37ctoFtzLLY";
		//токен регается через бот самого тг BotFather, там же пишется описание, название и токен
	}
}
