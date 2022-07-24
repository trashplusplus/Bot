import java.io.IOException;

import java.util.*;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

public class Bot extends TelegramLongPollingBot
{
	private Map<Long, Player> players = new HashMap<>();

	public void printCommands(Message message){
		sendMsg(message, "Бот содержит следующие команды: \n" +
				"\uD83D\uDD0D /find - искать предметы \n" +
				"\uD83D\uDCC3 /inv - открыть инвентарь \n" +
				"\uD83C\uDF80 /top - посмотреть рейтинг всех игроков \n" +
				"\uD83D\uDCB9 /stats - онлайн игроков \n" +
				"\uD83D\uDCB3 /balance - проверить баланс  \n" +
				"\uD83D\uDCB0 /sell - продать предмет \n" +
				"\uD83D\uDCE9 /help - список всех команд \n" +
				"ℹ /info - информация об игре"
		);
	}

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

			if (waitCommand != "/sell" && waitCommand != "/start"){
				switch (message.getText())
				{
					case "/start":
						sendMsg(message, "\uD83C\uDF77 Добро пожаловать в Needle");
						sendMsg(message, "Введите ник: ");
						waitCommand = "/start";
						break;

					case "/inv":

						inv = players.get(message.getChatId()).getInventory();

						if (inv.getInvSize() != 0)
						{
							sendMsg(message, "\uD83C\uDF81\t Ваш инвентарь: ");

							//sendMsg(message, "\u26BD");

							sendMsg(message, "\n" + inv.showInventory() + "\n");
							sendMsg(message,  "\uD83C\uDF81\t Всего предметов: " + inv.getInvSize());
						}
						else
						{
							sendMsg(message, "\uD83C\uDF81\t Ваш инвентарь пуст ");
						}

						break;

					case "/find":

						inv = players.get(message.getChatId()).getInventory();
						Item i = inv.findItem();
						sendMsg(message, String.format("\uD83C\uDF81\t Вы нашли: %s", i.toString()));

						break;

					case "/balance":
						inv = players.get(message.getChatId()).getInventory();
						sendMsg(message,  "\uD83D\uDCB2 Ваш баланс: " + "$" + inv.getBalance());
						break;
					case "/stats":
						sendMsg(message, "\uD83D\uDCBB Всего игроков: " + players.size());

						break;
					case "/sell":
						inv = players.get(message.getChatId()).getInventory();
						if(inv.getInvSize() > 0){
							waitCommand = "/sell";
							sendMsg(message, " Введите ID предмета, который вы хотите продать: ");
							int itemSellIndex = 0;

							sendMsg(message, "Предметы, доступные для продажи: ");

							for(int j = 0; j < inv.getInvSize(); j++){
								sendMsg(message, "Предмет " + "|"  + itemSellIndex + "|: " + inv.getItem(j) );
								itemSellIndex++;
							}
						}else{
							sendMsg(message, "⚠\t Ваш инвентарь пуст. Нет доступных вещей для продажи ");
						}
						break;
					case "/top":
						sendMsg(message, "\uD83D\uDCBB Все игроки: ");
						for(Map.Entry<Long, Player> pair : players.entrySet()){
							sendMsg(message, "Игрок: " + pair.getValue().getUsername() + " | " + "$" + pair.getValue().getInventory().getBalance());
						}
						break;
					case "/help":
						printCommands(message);
						break;
					case "/info":
						sendMsg(message, "Needle - это многопользовательская телеграм игра, нацеленная на коллекционирование " +
								"предметов. Вам как игроку предстоит собирать их, открывать ачивки и соревноваться с другими " +
								"игроками. Предметы Вы можете продавать, тем самым увеличивая свой игровой баланс. Внутриигровую валюту " +
								"вы можете тратить на покупку предметов у других игроков, на внутриигровое казино, а также на поиск предметов " +
								"сокращая время ожидания для поиска. Предметы вы можете искать раз в 6 часов. Среди них есть обычные, редкие, коллекционные " +
								"и подарочные. Последняя категория не имеет цены, а это значит, что она может быть продана среди игроков за установленную " +
								"цену. Покупать и выставлять предметы можно на аукционе. Удачи и приятной игры. ");
						break;
					default:
						sendMsg(message, "⚠\t Неизвестная команда");
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
			}else if(waitCommand == "/sell"){

				switch (message.getText()){
					default:


						try{
							inv = players.get(message.getChatId()).getInventory();
							String sellID = message.getText();
							Integer.parseInt(sellID);
							inv.sellItem(Integer.parseInt(sellID));
							sendMsg(message, "✔ Предмет успешно продан");
						}catch(NumberFormatException e) {
							e.printStackTrace();
							sendMsg(message, "⚠\t Пожалуйста, введите целое число");
							waitCommand = "";
						}catch(IndexOutOfBoundsException ee){
							ee.printStackTrace();
							sendMsg(message, "⚠\t Указан неверный ID");
							waitCommand = "";
						}


						waitCommand = "";
						break;
				}
			}else if(waitCommand == "/start") {
				switch (message.getText()) {

					default:
						String username = message.getText();

						long id = message.getChatId();
						//Проверка на ID, чтобы нажав два раза /start не создавался новый пользователь
						if(players.isEmpty()){
							players.put(id, new Player(id, username));
						}else{
							for(Map.Entry<Long, Player> pair : players.entrySet()){
								if (pair.getKey() != id && pair.getValue().getUsername() != username){
									players.put(id, new Player(id, username));
								}else{
									sendMsg(message, "Пользователь с таким именем уже есть ");
								}
							}
						}
						printCommands(message);
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
		keyboardFirstRow.add(new KeyboardButton( "/help"));

		//keyboardFirstRow.add(new KeyboardButton("/find"));
		//добавили в спиок всех кнопок
		keyboardRowList.add(keyboardFirstRow);
		replyKeyboardMarkup.setKeyboard(keyboardRowList);

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
