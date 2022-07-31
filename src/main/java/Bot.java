import java.io.IOException;

import java.util.*;
import java.util.concurrent.TimeUnit;

import ability.Ability;
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
	private Map<Long, Player> players = new HashMap<>(); //контейнер игроков
	private List<Integer> activeInputByPlayers = new ArrayList<>();

	public static enum State{
		AwaitingCommands,
		AwaitingArguments;
	}

	private List<State> player_state = new LinkedList<>();

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

	public void command_help(Message message){
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

	public void command_inv(Message message, Inventory inv){
		inv = players.get(message.getChatId()).getInventory();
		if (inv.getInvSize() != 0){

			sendMsg(message, "\uD83C\uDF81\t Ваш инвентарь: ");
			//sendMsg(message, "\u26BD");
			sendMsg(message, "\n" + inv.showInventory() + "\n");
			sendMsg(message,  "\uD83C\uDF81\t Всего предметов: " + inv.getInvSize());
		}
		else
		{
			sendMsg(message, "\uD83C\uDF81\t Ваш инвентарь пуст ");
		}
	}

	public void command_find(Message message, Inventory inv){

		//inv = players.get(message.getChatId()).getInventory();
		//Item i = inv.findItem();
		//sendMsg(message, String.format("\uD83C\uDF81\t Вы нашли: %s", i.toString()));
		long id = message.getChatId();
		Player player = players.get(id);
		Ability<Item> fia = player.getFindItemAbility();
		if (fia.isUsable())
		{
			Item new_item = fia.use();
			sendMsg(message, String.format("\uD83C\uDF81\t Вы нашли: %s", new_item));
		}
		else
		{
			sendMsg(message, String.format("\u231B Вы не можете использовать эту способность в течение %s", PrettyDate.prettify(fia.getCDTimer(), TimeUnit.SECONDS)));
		}
	}

	public void command_balance(Message message, Inventory inv){
		inv = players.get(message.getChatId()).getInventory();
		sendMsg(message,  "\uD83D\uDCB2 Ваш баланс: " + "$" + inv.getBalance());
	}


	public void command_stats(Message message){
		sendMsg(message, "\uD83D\uDCBB Всего игроков: " + players.size());
	}

	public void command_top(Message message){
		sendMsg(message, "\uD83D\uDCBB Все игроки: ");
		for(Map.Entry<Long, Player> pair : players.entrySet()){
			sendMsg(message, "Игрок: " + pair.getValue().getUsername() + " | " + "$" + pair.getValue().getInventory().getBalance());
		}
	}

	public void command_info(Message message){
		sendMsg(message, "Needle - это многопользовательская телеграм игра, нацеленная на коллекционирование " +
				"предметов. Вам как игроку предстоит собирать их, открывать ачивки и соревноваться с другими " +
				"игроками. Предметы Вы можете продавать, тем самым увеличивая свой игровой баланс. Внутриигровую валюту " +
				"вы можете тратить на покупку предметов у других игроков, на внутриигровое казино, а также на поиск предметов " +
				"сокращая время ожидания для поиска. Предметы вы можете искать раз в 6 часов. Среди них есть обычные, редкие, коллекционные " +
				"и подарочные. Последняя категория не имеет цены, а это значит, что она может быть продана среди игроков за установленную " +
				"цену. Покупать и выставлять предметы можно на аукционе. Удачи и приятной игры. ");
	}

	public void command_sell(Message message, Inventory inv){
		inv = players.get(message.getChatId()).getInventory();

		if(inv.getInvSize() > 0){
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


	}

	//метод для приема сообщений и обновлений
	public void onUpdateReceived(Update update)
	{

		Message message = update.getMessage();
		long chatId = message.getChatId();
		//regex для ника
		String usernameTemplate = "(\\w{3,32})";



		if (message != null && message.hasText()){

			System.out.println("Текстик: " + message.getText());

			Inventory inv = null;
			Player player = null;

				switch (message.getText()){
					case "/start":
						if (players.containsKey(message.getChatId())) {
							sendMsg(message, "Вы уже зарегестрированы");
						} else {

							players.put(message.getChatId(), new Player(message.getChatId(), "player" + message.getChatId()));
							player = players.get(message.getChatId());
							sendMsg(message, "\uD83C\uDF77 Добро пожаловать в Needle");
							player.setState("awaitingNickname");
						}
						break;
				}

				player = players.get(message.getChatId());


				switch (message.getText()){

					case "/inv":
						command_inv(message, inv);
						break;

					case "/find":
						command_find(message, inv);
						break;

					case "/balance":
						command_balance(message, inv);
						break;
					case "/stats":
						command_stats(message);
						break;
					case "/sell":
						command_sell(message, inv);
						player.setState("sell");
						break;
					case "/top":
						command_top(message);
						break;
					case "/help":
						command_help(message);
						break;
					case "/info":
						command_info(message);
						break;
					default:

						if(player.getId() == message.getChatId()){
							if(player.getState() == "awaitingNickname"){
								String username = message.getText();

								if(username.matches(usernameTemplate)){
									player.setUsername(username);
									player.setState("");
									command_help(message);
								}else{
									//sendMsg(message, "Введите корректный ник: ");
									sendMsg(message, "Введите ник: ");
									player.setState("awaitingNickname");
								}


							}else if(player.getState() == "sell"){

								try{
									inv = players.get(message.getChatId()).getInventory();
									String sellID = message.getText();
									long idOfSeller = message.getChatId();
									Integer.parseInt(sellID);

									inv.sellItem(Integer.parseInt(sellID));
									sendMsg(message, "✔ Предмет успешно продан");
									player.setState("");
								}catch(NumberFormatException e) {
									e.printStackTrace();
									sendMsg(message, "⚠\t Пожалуйста, введите целое число");
									player.setState("");
								}catch(IndexOutOfBoundsException ee){
									ee.printStackTrace();
									sendMsg(message, "⚠\t Указан неверный ID");
									player.setState("");
								}
							}else{

								//небольшая проверка /start и чтобы не писало Неизвестная команда
								//FIX HERE
								if(message.getText() == "/start"){

								}else{
									sendMsg(message, "⚠\t Неизвестная команда");
								}



							}
						}


						break;



					 // чтобы сначала проверялся ID пользователя, а потом если его не существует то инстанцировать для него новый ID

					 //Ну и самое сложное пока что, это возможность /find ить предметы раз в 20 минут например, проверять дату нужно и время

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
