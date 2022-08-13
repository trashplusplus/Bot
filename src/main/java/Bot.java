import ability.Cooldown;
import database.*;
import main.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class Bot extends TelegramLongPollingBot
{
	static PlayerDAO playerDAO = new PlayerDAO(SQLSession.sqlConnection);
	static InventoryDAO inventoryDAO = new InventoryDAO(SQLSession.sqlConnection);
	static ShopDAO shopDAO = new ShopDAO(SQLSession.sqlConnection);

	private static String token;

	public static void main(String[] args) throws SQLException, FileNotFoundException
	{
		initDB();
		init_token();

		try
		{
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class); //создание объекта в API
			telegramBotsApi.registerBot(new Bot());
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
	}

	private static void initDB() throws SQLException, FileNotFoundException
	{
		SQLExecutor executor = new SQLExecutor(new File("src\\main\\java\\database\\init.sql"), SQLSession.sqlConnection);
		executor.execute();
	}

	//что бот будет отвечать
	public void sendMsg(Long chatId, String text)
	{
		SendMessage sendMessage = new SendMessage(chatId.toString(), text);
		sendMessage.enableMarkdown(true);

		//конкретно, на какое сообщение ответить
		//sendMessage.setReplyToMessageId(message.getMessageId());

		sendMessage.setText(text);
		try
		{
			//добавили кнопку и поместили в нее сообщение
			setButtons(sendMessage);
			execute(sendMessage);
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
	}

	public void command_help(Long id)
	{
		sendMsg(id, "\\[`Needle`] Бот содержит следующие команды: \n" +
				"\n" +
				"\uD83D\uDD0D /find - искать предметы \n" +
				"\n" +
				"\uD83D\uDCC3 /inv - открыть инвентарь \n" +
				"\n" +
				"\uD83C\uDF80 /top - посмотреть рейтинг всех игроков \n" +
				"\n" +
				"\uD83D\uDCB9 /stats - онлайн игроков \n" +
				"\n" +
				"\uD83D\uDCB3 /balance - проверить баланс  \n" +
				"\n" +
				"\uD83D\uDCB0 /sell - продать предмет скупщику\n" +
				"\n" +
				"\uD83D\uDCE9 /help - список всех команд \n" +
				"\n" +
				"ℹ /info - информация об игре \n" +

				"\n" +
				"\uD83D\uDC80 /changenickname - сменить никнейм \n \n" +
				"\uD83C\uDFB0 /coin - сыграть в Монетку \n\n" +
				"⭐ /me - ифнормация о персонаже"
		);
	}

	public void command_inv(Long id)
	{
		Inventory inventory = inventoryDAO.get(id);
		if (inventory.getInvSize() != 0)
		{
			StringBuilder sb = new StringBuilder("\uD83C\uDF81\t Ваш инвентарь: ");
			sb.append("\n");
			sb.append("========================\n");
			for (int i = 0; i < inventory.getInvSize(); i++)
			{
				sb.append(String.format("Предмет #[%d] : %s\n", i, inventory.getItem(i).toString()));
			}
			sb.append("========================\n");
			//sendMsg(message, "\u26BD");
			sb.append("\uD83C\uDF81\t Всего предметов: " + inventory.getInvSize());
			sendMsg(id, sb.toString());
		}
		else
		{
			sendMsg(id, "\uD83C\uDF81\t Ваш инвентарь пуст ");

		}
	}

	public void command_find(Long id)
	{
		Player player = playerDAO.get_by_id(id);
		long now_ts = System.currentTimeMillis();
		long used_ts = player.last_fia;
		long cooldown_s = 1L;
		long cooldowns_ms = cooldown_s * 1000L;
		long left_ms = used_ts + cooldowns_ms - now_ts;

		if (left_ms > 0)
		{
			sendMsg(id, String.format("\u231B Время ожидания: %s",
					PrettyDate.prettify(left_ms, TimeUnit.MILLISECONDS)));
		}
		else
		{
			Item new_item = ItemFactory.getRandomItem();
			inventoryDAO.putItem(id, new_item.getId());
			sendMsg(id, String.format("\uD83C\uDF81\t Вы нашли: %s", new_item));
			player.last_fia = now_ts;
			player.addXp(2);
			if (player.getXp() >= 10)
			{
				player.levelUp();
				sendMsg(id, "\uD83D\uDC7E Вы перешли на " + player.getLevel() + " уровень");
			}

			playerDAO.update(player);
		}

		/*Player player = playerDAO.get(id);
		Ability<Item> fia = player.getFindItemAbility();
		if (fia.isUsable())
		{
			Item new_item = fia.use();
			inventoryDAO.putItem(id, new_item.getId());
			sendMsg(id, String.format("\uD83C\uDF81\t Вы нашли: %s", new_item));
		}
		else
		{
			sendMsg(id, String.format("\u231B Время ожидания: %s",
					PrettyDate.prettify(fia.getCDTimer(), TimeUnit.SECONDS)));
		}*/
	}

	public void command_balance(Long id)
	{
		sendMsg(id, String.format("\uD83D\uDCB2 Ваш баланс: $%d", playerDAO.get_by_id(id).balance));
	}


	public void command_stats(Long id)
	{
		sendMsg(id, "\uD83D\uDCBB Всего игроков: " + playerDAO.size());
	}

	public void command_top(Long id)
	{
		StringBuilder players_list = new StringBuilder("\uD83D\uDCBB Топ игроков:\n");
		players_list.append("========================");
		players_list.append("\n");
		for (Player player : playerDAO.getTopN("balance", false, 5))
		{
			players_list.append(String.format("Игрок %s | $%d | %d LVL", "`" + player.getUsername() + "`", player.balance, player.getLevel()));
			players_list.append("\n");
			players_list.append("========================");
			players_list.append("\n");

		}
		sendMsg(id, players_list.toString());
	}

	public void command_info(Long id)
	{
		sendMsg(id, "Needle - это многопользовательская телеграм игра, нацеленная на коллекционирование " +
				"предметов. Вам как игроку предстоит собирать их, открывать ачивки и соревноваться с другими " +
				"игроками. Предметы Вы можете продавать, тем самым увеличивая свой игровой баланс. Внутриигровую валюту " +
				"вы можете тратить на покупку предметов у других игроков, на внутриигровое казино, а также на поиск предметов " +
				"сокращая время ожидания для поиска. Предметы вы можете искать раз в 6 часов. Среди них есть обычные, редкие, коллекционные " +
				"и подарочные. Последняя категория не имеет цены, а это значит, что она может быть продана среди игроков за установленную " +
				"цену. Покупать и выставлять предметы можно на аукционе. Удачи и приятной игры. ");
	}

	public void command_sell(Long id)
	{
		Player player = playerDAO.get_by_id(id);
		Inventory inventory = player.getInventory();

		StringBuilder stringBuilder = new StringBuilder();
		if (inventory.getInvSize() > 0)
		{
			stringBuilder.append("\uD83E\uDDF6 Предметы, доступные к продаже:\n");
			stringBuilder.append("\n");
			stringBuilder.append("============================\n");
			for (int i = 0; i < inventory.getInvSize(); i++)
			{
				stringBuilder.append(String.format("Предмет #[%d] : %s\n", i, inventory.getItem(i).toString()));
			}

			stringBuilder.append("============================\n");
			stringBuilder.append("\n");
			stringBuilder.append("Введите номер предмета, который хотите продать:\n");
			player.setState(Player.State.awaitingSellArguments);
			playerDAO.update(player);
		}
		else
		{
			stringBuilder.append("⚠\t Ваш инвентарь пуст. Нет доступных вещей для продажи ");
		}

		sendMsg(id, stringBuilder.toString());
	}

	public void command_changeNickname(Long id)
	{
		Player player = playerDAO.get_by_id(id);
		sendMsg(id, "Введите никнейм, на который вы хотите сменить: ");
		player.setState(Player.State.awaitingChangeNickname);
		playerDAO.update(player);
	}

	//метод для приема сообщений и обновлений
	@Override
	public void onUpdateReceived(Update update)
	{
		Message message = update.getMessage();
		//regex для ника
		String usernameTemplate = "([А-Яа-яA-Za-z0-9]{3,32})";

		if (message != null && message.hasText())
		{
			Long id = message.getChatId();
			String text = message.getText();

			System.out.println("Текстик: " + message.getText());

			Player player = playerDAO.get_by_id(id);

			if (playerDAO.get_by_id(id) == null)
			{
				switch (text)
				{
					case "⭐ Начать":
					case "/start":
						if (playerDAO.get_by_id(id) != null)
						{
							sendMsg(id, "Вы уже зарегистрированы");
						}
						else
						{
							playerDAO.put(new Player(id, 0, 1, "player" + id, 0, Player.State.awaitingNickname, new Inventory(), 0L));
							sendMsg(id, "\uD83C\uDF77 Добро пожаловать в Needle");
							sendMsg(id, "Введите ник: ");
						}
						break;
					default:
						if (playerDAO.get_by_id(id) == null)
						{
							sendMsg(id, "⭐ Для регистрации введите команду /start");
						}
						break;
				}
			}

			switch (player.getState())
			{
				case awaitingNickname:
					String username = message.getText();
					if (username.matches(usernameTemplate))
					{
						try
						{
							player.setUsername(username);
							player.setState(Player.State.awaitingCommands);

							playerDAO.update(player);
							sendMsg(id, "Игрок " + "`" + player.getUsername() + "`" + " успешно создан");
							command_help(id);
						}
						catch (RuntimeException e)
						{
							e.printStackTrace();
							sendMsg(id, "Игрок с таким ником уже существует");
						}

					}
					else
					{
						sendMsg(id, "Введите корректный ник: ");

						//player.setState(main.Player.State.awaitingNickname);
					}
					break;
				case awaitingCommands:
					switch (text)
					{
						case "/inv":
							command_inv(id);
							break;
						case "/find":
							command_find(id);
							break;
						case "/balance":
							command_balance(id);
							break;
						case "/stats":
							command_stats(id);
							break;
						case "/sell":
							command_sell(id);
							break;
						case "/top":
							command_top(id);
							break;
						case "\uD83C\uDF3A Помощь":
							break;
						case "/help":
							command_help(id);
							break;
						case "/info":
							command_info(id);
							break;
						case "/changenickname":
							command_changeNickname(id);
							break;
						case "/cheat":
							sendMsg(id, "Игрок " + player.getUsername() + " обзавелся префиксом");
							player.setUsername("\uD83D\uDC33 " + player.getUsername());
							playerDAO.update(player);
							break;
						case "/coin":
							if (player.getLevel() >= 4)
							{
								if (player.balance > 0)
								{
									sendMsg(id, "\uD83C\uDFB0 Введите ставку: ");
									player.setState(Player.State.coinDash);
									playerDAO.update(player);
								}
								else
								{
									sendMsg(id, "\uD83C\uDFB0 У вас недостаточно денег	");
									player.setState(Player.State.awaitingCommands);
								}
							}
							else
							{
								sendMsg(id, "\uD83D\uDC7E Для игры в монетку нужен 4 уровень");
							}
							break;
						case "/broadcast":

							/*
							for(Player player1 : playerDAO.getAll()){
								long idd = player1.getId();
								sendMsg(idd, "\uD83D\uDC37 Ээй, давно тебя не было в игре, возращайся и сразись с нашим честным CoinDash \uD83D\uDC37");
							}
							*/

							for (Player player1 : playerDAO.getAll())
							{
								player1.last_fia = 0L;
								playerDAO.update(player1);
							}

							break;
						case "⭐️ Персонаж":
						case "/me":
							StringBuilder sb = new StringBuilder("Информация о персонаже\n");
							//sb.append("==============================\n");
							sb.append("\n");
							sb.append("⭐ Ваш ник: " + player.getUsername());
							sb.append("\n");
							//sb.append("==============================\n");
							sb.append("\n");
							sb.append("\uD83D\uDCB0 Ваш баланс: " + "$" + player.getMoney());
							sb.append("\n");
							//sb.append("==============================\n");
							sb.append("\n");
							sb.append("\uD83C\uDF20 Ваш GameID: " + player.getId());
							sb.append("\n");
							//sb.append("==============================\n");
							sb.append("\n");
							sb.append("\uD83D\uDC7E Ваш уровень: " + player.getLevel() + " (" + player.getXp() + " XP" + ")");
							sb.append("\n");
							//sb.append("==============================\n");

							sendMsg(id, sb.toString());
							break;
						case "/shop":
							player.setState(Player.State.shopAwaitingTypeOfShop);
							playerDAO.update(player);
							break;
						default:

							String getText = message.getText();

							if (!getText.equals("/start"))
							{
								sendMsg(id, "⚠\t Неизвестная команда");
							}

							break;
					}
					break;
				case awaitingSellArguments:
					try
					{
						Inventory inventory = player.getInventory();
						String sellID = message.getText();
						int sell_id = Integer.parseInt(sellID);
						Item item = inventory.getItem(sell_id);
						player.balance += item.getCost();
						inventory.removeItem(sell_id);
						inventoryDAO.delete(id, item.getId(), 1);

						player.setState(Player.State.awaitingCommands);
						playerDAO.update(player);
						sendMsg(id, "✅ Предмет продан | + " + "$" + item.getCost());
					}
					catch (NumberFormatException e)
					{

						e.printStackTrace();
						sendMsg(id, "⚠\t Пожалуйста, введите целое число");
						player.setState(Player.State.awaitingCommands);
						playerDAO.update(player);
					}
					catch (IndexOutOfBoundsException ee)
					{
						ee.printStackTrace();
						sendMsg(id, "⚠\t Указан неверный ID");
						player.setState(Player.State.awaitingCommands);
						playerDAO.update(player);
					}

					break;
				case awaitingChangeNickname:

					String nickname = message.getText();
					if (nickname.matches(usernameTemplate))
					{
						try
						{
							player.setUsername(nickname);
							player.setState(Player.State.awaitingCommands);
							playerDAO.update(player);
							sendMsg(id, "Ваш никнейм успешно изменен на " + "`" + player.getUsername() + "`");
						}
						catch (RuntimeException e)
						{
							e.printStackTrace();
							sendMsg(id, "Игрок с таким ником уже есть");
						}

					}
					else
					{
						sendMsg(id, "Пожалуйста, введите корректный ник");
						player.setState(Player.State.awaitingChangeNickname);
						playerDAO.update(player);
					}


					break;
				case coinDash:
					try
					{
						String dash = message.getText();
						int i_dash = Integer.parseInt(dash);

						if (i_dash > 0 && i_dash <= player.balance)
						{
							sendMsg(id, "\uD83C\uDFB0 Ваша ставка: " + "$" + i_dash);

							sendMsg(id, "Подбрасываем монетку...");

							Cooldown kd = new Cooldown(2, new CooldownForPlayer(player, id, i_dash, this));
							kd.startCooldown();
						}
						else
						{
							sendMsg(id, "⚠\t У вас нет такой суммы");

						}
					}
					catch (NumberFormatException e)
					{
						sendMsg(id, "⚠\tВаша ставка должна быть целым числом");
						e.printStackTrace();
						player.setState(Player.State.awaitingCommands);
						playerDAO.update(player);
					}
					break;
				case shopAwaitingTypeOfShop:
					sendMsg(id, "Веберите одно из двух: ");
					switch (text)
					{

						case "Купить":
							player.setState(Player.State.shopBuyGood);
							playerDAO.update(player);
							break;
						case "Продать":
							player.setState(Player.State.shopPlaceGood);
							playerDAO.update(player);
							break;

					}

					break;
				case shopBuyGood:
					sendMsg(id, "Все предметы в магазине:\n");
					for (ShopItem i : shopDAO.getAll())
					{
						sendMsg(id, String.format("Товар %s | Цена %d | Продавец %s", i.getItem().getTitle(), i.getCost(), i.getSeller()));
					}
					player.setState(Player.State.awaitingCommands);
					playerDAO.update(player);
					break;
				case shopPlaceGood:
					Inventory inventory = inventoryDAO.get(id);
					String itemID = message.getText();
					int cost = 0;

					sendMsg(id, "Предметы, доступные для продажи \n");

					for (Item i : inventory.getGiftItems())
					{

						sendMsg(id, i.getTitle() + "\n");

					}

					try
					{
						sendMsg(id, "Введите ID предмета, который хотите продать\n");
						ShopItem shopItem = new ShopItem(inventory.getItem(Integer.parseInt(itemID)), 1, player.getUsername());
						player.setState(Player.State.shopPlaceGood_awaitingCost);
						playerDAO.update(player);
					}
					catch (NumberFormatException e)
					{

						e.printStackTrace();
						sendMsg(id, "⚠\t Пожалуйста, введите целое число");
						player.setState(Player.State.awaitingCommands);
						playerDAO.update(player);
					}
					catch (IndexOutOfBoundsException ee)
					{
						ee.printStackTrace();
						sendMsg(id, "⚠\t Указан неверный ID");
						player.setState(Player.State.awaitingCommands);
						playerDAO.update(player);
					}

					//TODO
					break;
			}


			// чтобы сначала проверялся ID пользователя, а потом если его не существует то инстанцировать для него новый ID

			//Ну и самое сложное пока что, это возможность /find ить предметы раз в 20 минут например, проверять дату нужно и время

		}

		//playerDAO.update(player);
	}


	//кнопки

	public void setButtons(SendMessage sendMessage)
	{
		long id = Long.parseLong(sendMessage.getChatId());
		Player player = playerDAO.get_by_id(id);
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
		//KeyboardButton startButton = new KeyboardButton("/start");

		if (player == null)
		{
			keyboardFirstRow.add(new KeyboardButton("⭐ Начать"));
		}
		else
		{

			if (player.getState() == Player.State.shopAwaitingTypeOfShop)
			{
				keyboardFirstRow.add(new KeyboardButton("Купить"));
				keyboardFirstRow.add(new KeyboardButton("Продать"));
			}
			else
			{
				keyboardFirstRow.add(new KeyboardButton("\uD83C\uDF3A Помощь"));
				keyboardFirstRow.add(new KeyboardButton("⭐️ Персонаж"));
			}

		}

		//keyboardFirstRow.add(new KeyboardButton("/find"));
		//добавили в спиок всех кнопок
		keyboardRowList.add(keyboardFirstRow);
		replyKeyboardMarkup.setKeyboard(keyboardRowList);

	}

	public String getBotUsername()
	{
		return "Needle";
	}

	private static void init_token() throws FileNotFoundException
	{
		Scanner scanner = new Scanner(new File("token"));
		token = scanner.nextLine();
	}

	public String getBotToken()
	{
		return token;
		//токен регается через бот самого тг BotFather, там же пишется описание, название и токен
	}

	private static class CooldownForPlayer implements Runnable
	{
		private final Player player;
		private final int i_dash;
		private final Long chatId;
		private final Bot botik;

		CooldownForPlayer(Player player, Long chatId, int i_dash, Bot botik)
		{
			this.player = player;
			this.i_dash = i_dash;
			this.chatId = chatId;
			this.botik = botik;
		}

		@Override
		public void run()
		{
			CoinGame coinGame = new CoinGame(i_dash);
			if (coinGame.roll())
			{
				botik.sendMsg(chatId, "\uD83D\uDCB0 Вы выиграли " + "$" + i_dash);
				coinGame.coinWin(player, i_dash);
			}
			else
			{
				botik.sendMsg(chatId, "❌ Вы проиграли " + "$" + i_dash);
				coinGame.coinLose(player, i_dash);
			}

			player.setState(Player.State.awaitingCommands);
			botik.sendMsg(chatId, "Ваш баланс: " + player.balance + " \uD83D\uDCB2");
			playerDAO.update(player);
		}
	}
}



