package main;

import ability.Cooldown;
import database.dao.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;

import java.text.SimpleDateFormat;
import java.time.LocalTime;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class Bot extends TelegramLongPollingBot {
	private final PlayerDAO playerDAO;
	private final InventoryDAO inventoryDAO;
	private final ItemDAO itemDAO;
	private final ShopDAO shopDAO;
	private final StatsDAO statsDAO;
	private final AbilityDAO abilityDAO;

	private static final Roller<Item> mudRoller = RollerFactory.getMudRoller(new Random());
	private static final Roller<Integer> moneyRoller = RollerFactory.getMoneyRoller(new Random());
	private static final Roller<Item> findRoller = RollerFactory.getFindRoller(new Random());
	private static final Roller<Item> fishRoller = RollerFactory.getFishRoller(new Random());

	ScheduledFuture<?> sf_timers;
	ScheduledFuture<?> sf_find;
	private final long expStepS = 5L;
	ScheduledFuture<?> sf_pockets;
	ScheduledFuture<?> sf_dump;

	public final long findCooldown = 20L * 60L * 1000L;
	public final long pocketsCooldown = 30L * 1000L;

	private final String token;

	Map<Long, Player> active_players;

	Map<Player.State, BiConsumer<Player, Message>> state_processor;
	Map<String, Consumer<Player>> command_processor;

	public Bot(Connection connection) throws FileNotFoundException {
		playerDAO = new PlayerDAO(connection, this);
		inventoryDAO = new InventoryDAO(connection);
		itemDAO = new ItemDAO(connection);
		shopDAO = new ShopDAO(connection, this);
		statsDAO = new StatsDAO(connection);
		abilityDAO = new AbilityDAO(connection, this);
		token = init_token();
		state_processor = BotStateProcessor.get_map(this);
		command_processor = BotCommandProcessor.get_map(this);
		active_players = new HashMap<>();
		sf_timers = STPE.stpe.scheduleAtFixedRate(this::cleanShopFromExpired, 0L, 5L, TimeUnit.SECONDS);
		sf_find = STPE.stpe.scheduleAtFixedRate(this::sendFindCooldownNotification, 0L, expStepS, TimeUnit.SECONDS);
		sf_pockets = STPE.stpe.scheduleAtFixedRate(abilityDAO::expirePockets, 0L, expStepS, TimeUnit.SECONDS);  // remove this shit
		sf_dump = STPE.stpe.scheduleAtFixedRate(this::dump_database, 1L, 1L, TimeUnit.MINUTES);
	}

	public void sendMsg(Long chatId, String text) {
		SendMessage sendMessage = new SendMessage(chatId.toString(), text);
		sendMessage.enableMarkdown(true);

		//конкретно, на какое сообщение ответить
		//sendMessage.setReplyToMessageId(message.getMessageId());

		sendMessage.setText(text);
		try {
			//добавили кнопку и поместили в нее сообщение
			setButtons(sendMessage);
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public void setButtons(SendMessage sendMessage) {
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
		KeyboardRow keyboardSecondRow = new KeyboardRow();
		KeyboardRow keyboardThirdRow = new KeyboardRow();
		KeyboardRow keyboardFourthRow = new KeyboardRow();
		//добавили новую кнопку в первый ряд
		//KeyboardButton startButton = new KeyboardButton("/start");

		if (player == null) {
			keyboardFirstRow.add(new KeyboardButton("⭐ Начать"));
			//keyboardFirstRow.add(new KeyboardButton("/start"));
		} else {


			keyboardFirstRow.add(new KeyboardButton("\uD83C\uDF92 Инвентарь"));
			keyboardSecondRow.add(new KeyboardButton("\uD83D\uDC8E Искать редкие предметы"));
			keyboardSecondRow.add(new KeyboardButton("\uD83D\uDD26 Рыться в грязи"));
			keyboardSecondRow.add(new KeyboardButton("\uD83E\uDDF6 Проверить карманы"));


			keyboardFirstRow.add(new KeyboardButton("\uD83C\uDF3A Помощь"));
			keyboardFirstRow.add(new KeyboardButton("⭐️ Персонаж"));


			keyboardThirdRow.add(new KeyboardButton("\uD83D\uDCB0 Монетка"));
			keyboardThirdRow.add(new KeyboardButton("\uD83D\uDED2 Магазин"));
			keyboardThirdRow.add(new KeyboardButton("\uD83D\uDCDE Скупщик"));

			keyboardFourthRow.add(new KeyboardButton("\uD83C\uDF80 Топ 10"));
			keyboardFourthRow.add(new KeyboardButton("\uD83D\uDEE0 Продать Cheap"));


			keyboardFourthRow.add(new KeyboardButton("🐡 Рыбачить"));
			keyboardFourthRow.add(new KeyboardButton("\uD83E\uDD88 Сдать рыбу"));


			//keyboardFirstRow.add(new KeyboardButton("/me"));

		}

		//keyboardFirstRow.add(new KeyboardButton("/find"));
		//добавили в спиок всех кнопок
		keyboardRowList.add(keyboardFirstRow);
		keyboardRowList.add(keyboardSecondRow);
		keyboardRowList.add(keyboardThirdRow);
		keyboardRowList.add(keyboardFourthRow);
		replyKeyboardMarkup.setKeyboard(keyboardRowList);

	}

	@Override
	public void onUpdateReceived(Update update) {
		Message message = update.getMessage();

		if (message != null && message.hasText()) {
			long id = message.getChatId();
			String text = message.getText();

			Player player;
			if (active_players.containsKey(id)) {
				player = active_players.get(id);
			} else {
				player = playerDAO.get_by_id(id);
			}

			System.out.printf("%s: %s [from %s | %d]\n", new Date(), text, player != null ? player.getUsername() : "new player", id);

			if (player == null) {
				if (text.equals("/start")) {
					player = new Player(id, this);
					active_players.put(id, player);
					statsDAO.put(player.getStats(), player.getId());
					playerDAO.put(player);
					abilityDAO.put(id);

					sendMsg(id, "\uD83C\uDF77 Добро пожаловать в Needle");
					sendMsg(id, "Введите ник: ");
				} else {
					sendMsg(id, "⭐ Для регистрации введите команду /start");
				}
			} else {
				state_processor.get(player.getState()).accept(player, message);
			}
		}
	}

	void awaitingNickname_processor(Player player, Message message) {
		long player_id = player.getId();
		String username = message.getText();
		Item tag = itemDAO.getByName("\uD83D\uDCDD Тег");
		//regex для ника
		String usernameTemplate = "([А-Яа-яA-Za-z0-9]{3,32})";
		if (username.matches(usernameTemplate)) {
			try {
				Player new_player = playerDAO.get_by_name(username);
				if (new_player == null) {
					player.setUsername(username);
					player.setState(Player.State.awaitingCommands);
					playerDAO.update(player);
					active_players.remove(player_id);
					sendMsg(player_id, "Игрок `" + player.getUsername() + "` успешно создан");
					command_help(player);
				} else {
					throw new RuntimeException("REx");
				}
			} catch (RuntimeException e)  // TODO change to some reasonable <? extends Exception> class
			{
				e.printStackTrace();
				sendMsg(player_id, "⚠\t Игрок с таким ником уже существует");
			}
		} else {
			sendMsg(player_id, "⚠\t Введите корректный ник: ");
		}
	}

	void awaitingSellArguments_processor(Player player, Message message) {
		long player_id = player.getId();
		try {
			Inventory inventory = player.getInventory();
			String sellID = message.getText();
			int sell_id = Integer.parseInt(sellID);
			Item item = inventory.getItem(sell_id);
			player.balance += item.getCost();
			inventory.removeItem(sell_id);
			inventoryDAO.delete(player_id, item.getId(), 1);

			playerDAO.update(player);
			sendMsg(player_id, "✅ Предмет продан | + $" + item.getCost());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			sendMsg(player_id, "⚠\t Пожалуйста, введите целое число");
		} catch (IndexOutOfBoundsException ee) {
			ee.printStackTrace();
			sendMsg(player_id, "⚠\t Указан неверный ID");
		}
		active_players.remove(player_id);
	}

	void awaitingCommand_processor(Player player, Message message) {
		String text = message.getText();
		if (command_processor.containsKey(text)) {
			command_processor.get(text).accept(player);
		} else {
			sendMsg(player.getId(), "⚠\t Неизвестная команда\n");
		}
	}


	void awaitingChangeNickname_processor(Player player, Message message) {
		long player_id = player.getId();
		String nickname = message.getText();
		Item tag = itemDAO.getByName("\uD83D\uDCDD Тег");
		//regex для ника
		String usernameTemplate = "([А-Яа-яA-Za-z0-9]{3,32})";
		if (nickname.matches(usernameTemplate)) {
			try {
				player.setUsername(nickname);
				playerDAO.update(player);
                inventoryDAO.delete(player.getId(), tag.getId(), 1);
				sendMsg(player_id, "Ваш никнейм успешно изменен на `" + player.getUsername() + "`");
			} catch (RuntimeException e) {
				e.printStackTrace();
				sendMsg(player_id, "Игрок с таким ником уже есть");
			}
		} else {
			sendMsg(player_id, "Пожалуйста, введите корректный ник");
		}
		active_players.remove(player_id);
	}

	void coinDash_processor(Player player, Message message) {
		long player_id = player.getId();
		String dash = message.getText();
		try {
			int i_dash = Integer.parseInt(dash);

			if (i_dash > 0 && i_dash <= player.balance) {
				sendMsg(player_id, "\uD83C\uDFB0 Ваша ставка: $" + i_dash);

				sendMsg(player_id, "Подбрасываем монетку...");

				Cooldown kd = new Cooldown(2, () -> coin_dash_callback(player, i_dash));
				kd.startCooldown();
			} else {
				sendMsg(player_id, "⚠\t У вас нет такой суммы");
			}
		} catch (NumberFormatException e) {
			sendMsg(player_id, "⚠\tВаша ставка должна быть целым числом");
			e.printStackTrace();
		}
		active_players.remove(player_id);
	}

	void shopBuy_processor(Player player, Message message) {
		try {
			int userInput = Integer.parseInt(message.getText());

			synchronized (shopDAO) {
				ShopItem wanted_item = shopDAO.getByID(userInput);
				Item item = wanted_item.getItem();
				int itemCost = wanted_item.getCost();
				Player seller = wanted_item.getSeller();

				if (player.getMoney() >= itemCost) {
					player.balance -= itemCost;
					inventoryDAO.putItem(player.getId(), item.getId());
					sendMsg(player.getId(), String.format("\uD83C\uDF6D Предмет `%s` успешно куплен", item));
					sendMsg(seller.getId(), String.format("\uD83D\uDCC8 Ваш предмет `%s` купил игрок `%s` | + $%d", item.getTitle(), player.getUsername(), itemCost));

					seller.balance = seller.balance + itemCost;

					shopDAO.delete(userInput);
					playerDAO.update(player);
					playerDAO.update(seller);
				} else {
					sendMsg(player.getId(), "Недостаточно средств");
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			sendMsg(player.getId(), "Введите целое число");  // << звучит как приглашение, хотя стейт тут же меняется
			player.setState(Player.State.awaitingCommands);
			playerDAO.update(player);
		} catch (IndexOutOfBoundsException ee) {
			ee.printStackTrace();
			sendMsg(player.getId(), "Неверный ID");
			player.setState(Player.State.awaitingCommands);
			playerDAO.update(player);
		}
		active_players.remove(player.getId());
	}


	void shopPlaceGood_awaitingID_processor(Player player, Message message) {
		try {

			long id = player.getId();
			int itemID = Integer.parseInt(message.getText());
			if (itemID >= player.getInventory().getInvSize()) {
				throw new IndexOutOfBoundsException();
			}else if(player.getInventory().getInvSize() > 20){
				throw new BackpackException(itemID);
			}
			//playerDAO.update(player);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			sendMsg(player.getId(), "Введите целое число");
		} catch (IndexOutOfBoundsException ee) {
			ee.printStackTrace();
			sendMsg(player.getId(), "Неверный ID");
		} catch (BackpackException e){
			long id = player.getId();
			Item ii = player.getInventory().getItem(e.backpackID);
			Item backpack = itemDAO.getByName("\uD83C\uDF92 Рюкзак");
			if(ii.equals(backpack)){
				sendMsg(id, String.format("Избавьтесь от дополнительных слотов, прежде чем продать `%s`", backpack.getTitle()));
				player.setState(Player.State.awaitingCommands);
				playerDAO.update(player);
				active_players.remove(id);
			}else{
				player.to_place_item = e.backpackID;
				sendMsg(player.getId(), "Введите стоимость товара: ");
				player.setState(Player.State.shopPlaceGood_awaitingCost);
			}
		}
	}


	void shopPlaceGood_awaitingCost_processor(Player player, Message message) {
		long player_id = player.getId();
		try {
			int cost = Integer.parseInt(message.getText());
			if (cost > 0) {
				Inventory inventory = player.getInventory();
				ShopItem shopItem = new ShopItem(inventory.getItem(player.to_place_item), cost, player);
				shopDAO.put(shopItem);

				sendMsg(player_id, String.format("Товар `%s` выставлен на продажу", inventory.getItem(player.to_place_item).getTitle()));
				inventory.removeItem(player.to_place_item);

				inventoryDAO.delete(player_id, shopItem.getItem().getId(), 1);
				player.setState(Player.State.awaitingCommands);
				playerDAO.update(player);
			} else {
				sendMsg(player_id, "Сумма не может быть нулем");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			sendMsg(player_id, "⚠\t Пожалуйста, введите целое число");
		}
		active_players.remove(player_id);
	}

	public void payAwaitingNickname_processor(Player player, Message message) {
		long player_id = player.getId();
		String nickname = message.getText();

		if (!nickname.equals(player.getUsername())) {
			Player acceptor = playerDAO.get_by_name(nickname);
			if (acceptor != null) {
				player.payment_acceptor = acceptor;
				player.setState(Player.State.payAwaitingAmount);
				sendMsg(player_id, "\uD83D\uDCB3 Введите сумму: ");
			} else {
				sendMsg(player_id, "Такого игрока не существует");
				player.setState(Player.State.awaitingCommands);
			}
		} else {
			sendMsg(player_id, String.format("\uD83C\uDF38 Игрок `%s` очень богат и не нуждается в Ваших копейках", player.getUsername()));
		}
	}

	public void payAwaitingAmount_processor(Player player, Message message) {
		try {
			int cost = Integer.parseInt(message.getText());
			if (cost > player.getMoney() || cost <= 0) {
				sendMsg(player.getId(), "⚠\t Некорректная сумма");
			} else {
				Player receiver = player.payment_acceptor;
				player.balance -= cost;
				sendMsg(receiver.getId(), String.format("\uD83D\uDCB3 Вам начислено $%d | Отправитель: `%s` ", cost, player.getUsername()));
				sendMsg(player.getId(), "✅ Деньги отправлены");
				receiver.balance += cost;
				player.setState(Player.State.awaitingCommands);
				player.payment_acceptor = null;
				playerDAO.update(receiver);
				playerDAO.update(player);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			sendMsg(player.getId(), "⚠\t Вы ввели некорректную сумму");
		}
		active_players.remove(player.getId());
	}

	public void awaitingCoffee_processor(Player player, Message message) {
		long player_id = player.getId();
		String nickname = message.getText();

		if (!nickname.equals(player.getUsername())) {
			Player acceptor = playerDAO.get_by_name(nickname);
			if (acceptor != null) {
				player.coffee_acceptor = acceptor;
				sendMsg(player_id, "Введите сообщение для игрока (48 символов): ");
				player.setState(Player.State.awaitingCoffeeNote);

				/*
				player.balance -= 100;
				acceptor.stats.coffee++;
				statsDAO.update(acceptor.getStats(), acceptor.getId());
				sendMsg(player_id, "☕ Кофе отправлен");
				sendMsg(acceptor.getId(), String.format("☕ Игрок `%s` угостил вас кружечкой кофе", player.getUsername()));
				statsDAO.update(acceptor.getStats(), acceptor.getId());
				playerDAO.update(player);
				player.setState(Player.State.awaitingCommands);
				 */
			} else {
				sendMsg(player_id, "Такого игрока не существует");
				player.setState(Player.State.awaitingCommands);
			}
		} else {
			sendMsg(player_id, "\uD83C\uDF38 Кофе можно отправлять только другим игрокам");
		}

	}


	public void awaitingCoffeeNote_processor(Player player, Message message) {
		try {
			String note = message.getText();
			if (note.length() < 48) {
				Player receiver = player.coffee_acceptor;
				player.balance -= 500;
				receiver.stats.coffee++;
				statsDAO.update(receiver.getStats(), receiver.getId());
				sendMsg(player.getId(), "☕ Кофе отправлен");
				sendMsg(receiver.getId(), String.format("☕ Игрок `%s` угостил вас кружечкой кофе с сообщением: `%s`", player.getUsername(), note));
				statsDAO.update(receiver.getStats(), receiver.getId());
				playerDAO.update(player);
				player.setState(Player.State.awaitingCommands);
			} else {
				sendMsg(player.getId(), "Сообщение больше, чем 48 символов");
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			sendMsg(player.getId(), "⚠\t Некорректное сообщение");
		}
		active_players.remove(player.getId());
	}

	public void awaitingTea_processor(Player player, Message message) {
		long player_id = player.getId();
		String nickname = message.getText();

		if (!nickname.equals(player.getUsername())) {
			Player acceptor = playerDAO.get_by_name(nickname);
			if (acceptor != null) {
				player.tea_acceptor = acceptor;
				sendMsg(player_id, "Введите сообщение для игрока (48 символов): ");
				player.setState(Player.State.awaitingTeaNote);
			} else {
				sendMsg(player_id, "Такого игрока не существует");
				player.setState(Player.State.awaitingCommands);
			}
		} else {
			sendMsg(player_id, "\uD83C\uDF38 Чай можно отправлять только другим игрокам");
		}

	}

	public void awaitingTeaNote_processor(Player player, Message message) {
		try {
			String note = message.getText();
			if (note.length() < 48) {
				Player receiver = player.tea_acceptor;
				player.balance -= 500;
				receiver.stats.tea++;
				statsDAO.update(receiver.getStats(), receiver.getId());
				sendMsg(player.getId(), "\uD83C\uDF3F Чай отправлен");
				sendMsg(receiver.getId(), String.format("\uD83C\uDF3F Игрок `%s` угостил вас кружечкой чая с сообщением: `%s`", player.getUsername(), note));
				statsDAO.update(receiver.getStats(), receiver.getId());
				playerDAO.update(player);
				player.setState(Player.State.awaitingCommands);
			} else {
				sendMsg(player.getId(), "Сообщение больше, чем 48 символов");
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			sendMsg(player.getId(), "⚠\t Некорректное сообщение");
		}
		active_players.remove(player.getId());
	}


	public void command_help(Player player) {

		//playerDAO.update(player);

		sendMsg(player.getId(), "\\[`Needle`] Бот содержит следующие команды: \n" +
				"\n" +
				" \\[Команды поиска] \n" +
				"\uD83D\uDD0D /find - искать редкие предметы \n" +
				"\uD83D\uDD0D /pockets - проверить карманы \n" +
				"\uD83D\uDD0D /mud - рыться в грязи \n" +
				"\n" +
				" \\[Команды магазина] \n" +
				"\uD83D\uDD0D /shopshow - посмотреть магазин \n" +
				"\uD83D\uDD0D /shopplace - продать предмет \n" +
				"\uD83D\uDD0D /shopbuy - купить предмет \n" +
				"\n" +
				" \\[Команды игрока] \n" +
				"\uD83D\uDCC3 /inv - открыть инвентарь \n" +
				"\uD83D\uDCB0 /sell - продать скупщику\n" +
				"\uD83D\uDCB3 /balance - проверить баланс  \n" +
				"\uD83D\uDCB0 /pay - переслать деньги \n" +
				"\uD83D\uDC80 /changenickname - сменить никнейм \n" +
				"⭐ /me - ифнормация о персонаже \n" +
				"\n" +
				" \\[Общие команы] \n" +
				"\uD83D\uDCE9 /help - список всех команд \n" +
				"ℹ /info - информация об игре \n" +
				"\uD83C\uDF80 /top - посмотреть рейтинг игроков \n" +
				"\uD83D\uDCB9 /stats - онлайн игроков \n" +
				"\n" +
				" \\[Развлечения] \n" +
				"\uD83C\uDFB0 /coin - сыграть в Монетку \n" +
				"\uD83C\uDFB0 /tea - отправить чай \n" +
				"\uD83C\uDFB0 /coffee - отправить кофе \n" +
				"\n" +
				" \\[Локации] \n" +

				"\uD83D\uDC80 /forest - посетить Лес \n" +

				"\uD83D\uDC21 /fish - пойти на рыбалку \n"


		);
	}

	public void command_forest(Player player) {
		List<String> explore = new ArrayList<String>();
		explore.add("`Петуния`");
		explore.add("`Гардения`");
		explore.add("`Ромашки`");
		explore.add("`Лилии`");
		explore.add("`Ландыши`");
		explore.add("`Хризантемы`");

		Random r = new Random();
		int random = r.nextInt(explore.size());
		try {
			Item i = itemDAO.getByName("\uD83D\uDD26 Поисковый фонарь");
			if (player.getInventory().getItems().contains(i)) {

				sendMsg(player.getId(), "В лесу вы нашли: " + explore.get(random));

			} else {
				sendMsg(player.getId(), String.format("Для похода в лес вам нужен предмет `%s` \n\uD83D\uDED2 Его можно купить у других игроков в магазине", i.getTitle()));
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void command_achievements(Player player) {
		long id = player.getId();
		StringBuilder sb = new StringBuilder("Ваши достижения: \n\n");

		if (player.stats.coffee < 30) {
			sb.append("❌ Выпить 30 кружек кофе\n");
		} else {
			sb.append("✅ Выпить 30 кружек кофе\n");
		}

		if (player.stats.tea < 30) {
			sb.append("❌ Выпить 30 кружек чая\n");
		} else {
			sb.append("✅ Выпить 30 кружек кофе\n");
		}

		sendMsg(id, sb.toString());

	}

	public void command_fish(Player player) {
		//Item i = new Item(46, "Удочка", ItemRarity.Rare, 5000);
		Item i = itemDAO.getByName("\uD83D\uDC1FУдочка");
		int limitSpace;
		Item backpack = itemDAO.getByName("\uD83C\uDF92 Рюкзак");

		if(player.getInventory().getItems().contains(backpack)){
			limitSpace = 25;
		}else{
			limitSpace = 20;
		}


		if (player.getLevel() >= 5) {
			if (player.getInventory().getItems().contains(i)) {
				if (player.getInventory().getInvSize() < limitSpace) {
					Item item = fishRoller.roll();

					if (item != null) {
						inventoryDAO.putItem(player.getId(), item.getId());
						playerDAO.update(player);
						sendMsg(player.getId(), String.format("Вы поймали %s", item));
						player.addXp(1);
					} else {
						sendMsg(player.getId(), "Не клюет");
					}
				} else {
					sendMsg(player.getId(), "⚠ В вашем инвентаре нет места");
				}
			} else {
				sendMsg(player.getId(), String.format("Для похода на рыбалку вам нужен предмет `%s` \n\uD83D\uDED2 Его можно купить у других игроков в магазине или найти", i.getTitle()));
			}
		} else {
			sendMsg(player.getId(), "\uD83D\uDC7E Для похода на рыбалку вам нужен 5 уровень");
		}
	}



	public void command_sellfish(Player player) {
		long id = player.getId();



		LocalTime open = LocalTime.of(10, 00);
		LocalTime close = LocalTime.of(15, 00);

		LocalTime currentTime = LocalTime.now();

		if(currentTime.isBefore(open) || currentTime.isAfter(close)){
			sendMsg(id, "\uD83E\uDD88 Рыбная лавка работает с 10:00 до 15:00\n\nСдавая рыбу в лавке, Вы можете получить " +
					"в несколько раз больше выручки, чем если бы сдавали ее \uD83D\uDCDE Скупщику");
		}else{
			List<String> fish_titles = new ArrayList<String>();
			fish_titles.add("Горбуша");
			fish_titles.add("Бычок");
			fish_titles.add("Карась");
			int fee = 0;
			for (int i = 0; i < player.getInventory().getItems().size(); i++) {
				Item fish = player.getInventory().getItem(i);
				if (fish_titles.contains(fish.getTitle())) {
					fee += fish.getCost() * 7;
					inventoryDAO.delete(player, fish.getId(), 1);
				}
			}
			if (fee > 0) {
				sendMsg(id, String.format("\uD83E\uDD88 Покупатель выложил за всю рыбу $%d", fee));
				player.balance += fee;
				playerDAO.update(player);

			} else {
				sendMsg(id, "\uD83E\uDD88У вас нет рыбы\nЧтобы ловить рыбу, введите /fish");
			}
		}


	}


	public void command_drop(Player player) {

		long id = player.getId();
		int fee = 0;

		for (int i = 0; i < player.getInventory().getItems().size(); i++) {
			Item cheapItem = player.getInventory().getItem(i);
			if (cheapItem.getRarity() == ItemRarity.Cheap) {
				fee += cheapItem.getCost();
				inventoryDAO.delete(player, cheapItem.getId(), 1);
			}
		}

		if (fee > 0) {
			sendMsg(id, String.format("\uD83D\uDCB3 Вы продали все дешевые вещи за $%d", fee));
			player.balance += fee;
			playerDAO.update(player);
		} else {
			sendMsg(id, "У вас нет дешевых вещей");
		}

	}

	//SUPER SECRET BONUS
	public void command_bonus(Player player) {
		long id = player.getId();
		if (player.getStats().bonus == 0) {
			sendMsg(id, "\uD83C\uDF3A Вы получили бонус | + $15000");
			player.balance += 15000;
			player.stats.bonus++;
			playerDAO.update(player);
			statsDAO.update(player.getStats(), player.getId());
		} else {
			sendMsg(id, "Вы уже получили свой бонус");
		}
	}

	public void command_inv(Player player) {
		int limitSpace;
		Item backpack = itemDAO.getByName("\uD83C\uDF92 Рюкзак");
		if(player.getInventory().getItems().contains(backpack)){
			limitSpace = 25;
		}else{
			limitSpace = 20;
		}


		long player_id = player.getId();
		Inventory inventory = player.getInventory();
		if (inventory.getInvSize() != 0) {
			StringBuilder sb = new StringBuilder("\uD83C\uDF81\t Ваш инвентарь: ");
			sb.append("\n");
			sb.append("========================\n");
			for (int i = 0; i < inventory.getInvSize(); i++) {
				sb.append(String.format("Предмет #[%d] : %s\n", i, inventory.getItem(i).toString()));
			}
			sb.append("========================\n");
			//sendMsg(message, "\u26BD");
			sb.append("\uD83C\uDF81\t Всего предметов: ").append(inventory.getInvSize() + "/" + limitSpace);
			sendMsg(player_id, sb.toString());
		} else {
			sendMsg(player_id, "\uD83C\uDF81\t Ваш инвентарь пуст ");
		}
	}

	public void command_find(Player player) {
		int limitSpace;
		Item backpack = itemDAO.getByName("\uD83C\uDF92 Рюкзак");
		if(player.getInventory().getItems().contains(backpack)){
			limitSpace = 25;
		}else{
			limitSpace = 20;
		}


		long player_id = player.getId();
		long now_ts = System.currentTimeMillis();
		long cooldownMs = findCooldown;
		if (player.getInventory().getInvSize() < limitSpace) {
			if (player.findExpiration != null && player.findExpiration > now_ts) {
				sendMsg(player_id, String.format("\u231B Время ожидания: %s",
						PrettyDate.prettify(player.findExpiration - now_ts, TimeUnit.MILLISECONDS)));
			} else {
				Item new_item = findRoller.roll();
				inventoryDAO.putItem(player_id, new_item.getId());
				sendMsg(player_id, String.format("\uD83C\uDF81\t Вы нашли: %s", new_item));
				player.addXp(2);
				player.findExpiration = now_ts + cooldownMs;

				playerDAO.update(player);
				abilityDAO.updateFind(player_id, now_ts + cooldownMs);
			}
		} else {
			sendMsg(player_id, "⚠ В вашем инвентаре нет места");
		}
	}


	public void command_mud(Player player) {
		int limitSpace;
		Item backpack = itemDAO.getByName("\uD83C\uDF92 Рюкзак");
		if(player.getInventory().getItems().contains(backpack)){
			limitSpace = 25;
		}else{
			limitSpace = 20;
		}


		long id = player.getId();
		if (player.getInventory().getInvSize() < limitSpace) {
			Item item = mudRoller.roll();
			if (item != null) {
				inventoryDAO.putItem(id, item.getId());
				sendMsg(id, String.format("Вы нашли в грязи %s", item));
				player.addXp(1);
				playerDAO.update(player);
			} else {
				sendMsg(id, "Вы ничего не нашли");
			}
		} else {
			sendMsg(id, "⚠ В вашем инвентаре нет места");
		}
	}


	public void command_pockets(Player player) {
		long player_id = player.getId();
		long now_ts = System.currentTimeMillis();
		long cooldownMs = pocketsCooldown;
		if (player.pocketsExpiration != null && player.pocketsExpiration > now_ts) {
			sendMsg(player_id, String.format("\u231B Время ожидания: %s",
					PrettyDate.prettify(player.pocketsExpiration - now_ts, TimeUnit.MILLISECONDS)));
		} else {
			int money = moneyRoller.roll();
			if (money > 0) {
				sendMsg(player_id, String.format("Вы пошарили в карманах и нашли $%d", money));
				player.balance += money;
				//playerDAO.update(player);
			} else if (money == 0) {
				sendMsg(player.getId(), "Вы ничего не нашли в своих карманах");
			} else {
				throw new RuntimeException("WTF?");
			}
			player.pocketsExpiration = now_ts + cooldownMs;
			abilityDAO.updatePockets(player_id, now_ts + cooldownMs);
			playerDAO.update(player);
		}
	}


	public void command_balance(Player player) {
		sendMsg(player.getId(), String.format("\uD83D\uDCB2 Ваш баланс: $%d", player.balance));
	}


	public void command_stats(Player player) {
		sendMsg(player.getId(), "\uD83D\uDCBB Всего игроков: " + playerDAO.size());
	}

	public void command_top(Player player) {
		StringBuilder players_list = new StringBuilder("\uD83D\uDCBB Топ 10 игроков:\n");
		players_list.append("========================");
		players_list.append("\n");
		for (Player pl : playerDAO.getTopN("balance", false, 10)) {
			players_list.append(String.format("Игрок %s | $%d | %d LVL", "`" + pl.getUsername() + "`", pl.balance, pl.getLevel()));
			players_list.append("\n");
			players_list.append("========================");
			players_list.append("\n");

		}
		sendMsg(player.getId(), players_list.toString());
	}

	public void command_info(Player player) {
		long id = player.getId();

		StringBuilder sb = new StringBuilder("*Информация об игре*\n\n");
		sb.append("\uD83C\uDF38 Needle - многопользовательская телеграм игра, где можно весело проводить время с друзьями и другими игроками \n\n");
		sb.append("*Предметы делятся на 4 категории:*\n\n");
		sb.append("`Cheap` - их можно найти практически везде, не несут почти никакой ценности, их можно смело продавать \uD83D\uDCDE Скупщику\n\n");
		sb.append("`Common` - более ценные, чем Cheap. Могут быть проданы дороже, если их сдавать, например Рыба. Ее можно продать в 10 раз дороже");
		sb.append(" если дождаться ярмарки и сдать ее там\n\n");
		sb.append("`Rare` - редкие можно найти при \uD83D\uDC8E Поиске редких предметов, а также в Грязи и на Рыбалке, но с очень маленьким шансом, ");
		sb.append("среди редких предметов много тех, которые нужны для каких-то локаций или функция, как например `\uD83D\uDCDD Тег`, который можно использовать ");
		sb.append("для смены ника \n\n");
		sb.append("`Gift` - самая ценная категория предметов в игре, среди них либо дорогие предметы, либо важные внутриигровые, например `\uD83D\uDC1F Удочка` ");
		sb.append("такие предметы либо дают бонусы, либо нужны для каких-то функций\n\n");
		sb.append("Предметы, помеченные определенным значком перед названием, либо нужны для игры, либо просто являются редким экземпялром в коллекции игрока\n\n");
		sb.append("*☕️Кофе и чай*\n");
		sb.append("Кофе и чай выполняют функцию сообщений. Заказывать кофе или чай для игрока - означает написать ему сообщение в размере 48 символов\n");
		sb.append("услуга стоит $500, но некоторые предметы, например `\uD83C\uDF75 Кружка 'Египет'` могут опускать цену до $100 за раз\n\n");
		sb.append("*\uD83D\uDED2 Магазин*\n");
		sb.append("Магазин - это место, где игроки могут размещать свои предметы и устанавливать на них свою стоимость, а другие игроки могут купить их.\n");
		sb.append("В случае если предмет никто не покупает в течение 24 часов, он автоматически возвращается в инвентарь продавца\n\n");
		sb.append("*\uD83C\uDFB0 Монетка*\n");
		sb.append("В монетке игроки могут испытать удачу, поставив ставку и она либо удвоится, либо...\n\n");
		sb.append("*\uD83D\uDD26 Поиск предметов и уровни*\n");
		sb.append("За поиск предметов игрок получает опыт и прокачивает уровень. Система уровней позволяет игрокам открывать новые возможности в игре. ");
		sb.append("\uD83D\uDC8E Поиск редких предметов имеет задержку в 20 минут, в то время как \uD83D\uDD26 Рыться в грязи можно ежесекундно \n\n");
		sb.append("*\uD83C\uDF80 Топ 10*\n");
		sb.append("Топ-10 - это список самых настойчивых и верных игроков, которые приложили усилия, чтобы оказаться среди самых лучших\n\n");
		sb.append("*\uD83C\uDF3A Помощь*\n");
		sb.append("Для того, чтобы получить полный список команд, существует команда /help также игрок может воспользоваться нижней кнопочной панелью ");
		sb.append("для более быстрой и удобной навигации\n\n");
		sb.append("⚡ Ссылка на официальный телеграм канал Needle, где можно узнавать о новых обновлениях первыми: https://t.me/needlechat\n\n");
		sb.append("Удачной игры!\n");

		sendMsg(id, sb.toString());


	}

	public void command_sell(Player player) {
		Inventory inventory = player.getInventory();

		StringBuilder stringBuilder = new StringBuilder();
		if (inventory.getInvSize() > 0) {
			active_players.put(player.getId(), player);
			stringBuilder.append("\uD83E\uDDF6 Предметы, доступные к продаже:\n");
			stringBuilder.append("\n");
			stringBuilder.append("============================\n");
			for (int i = 0; i < inventory.getInvSize(); i++) {
				stringBuilder.append(String.format("Предмет #[%d] : %s\n", i, inventory.getItem(i).toString()));
			}

			stringBuilder.append("============================\n");
			stringBuilder.append("\n");
			stringBuilder.append("Введите номер предмета, который хотите продать:\n");
			player.setState(Player.State.awaitingSellArguments);
		} else {
			stringBuilder.append("⚠\t Ваш инвентарь пуст. Нет доступных вещей для продажи ");
		}

		sendMsg(player.getId(), stringBuilder.toString());
	}

	public void command_changeNickname(Player player) {
		long id = player.getId();
		Item i = itemDAO.getByName("\uD83D\uDCDD Тег");
		if (player.getInventory().getItems().contains(i)) {
			active_players.put(player.getId(), player);

			sendMsg(player.getId(), "Введите никнейм, на который вы хотите сменить: ");

			player.setState(Player.State.awaitingChangeNickname);
		} else {
			sendMsg(id, String.format("Для смены ника нужен предмет `%s`\n\uD83D\uDED2 Его можно купить у других игроков в магазине или найти", i.getTitle()));
		}
	}

	public void command_coin(Player player) {
		long player_id = player.getId();
		if (player.getLevel() >= 4) {
			if (player.balance > 0) {
				active_players.put(player_id, player);
				sendMsg(player_id, "\uD83D\uDCB0 Ваш баланс: $" + player.getMoney());
				sendMsg(player_id, "\uD83C\uDFB0 Введите ставку: ");
				player.setState(Player.State.coinDash);
			} else {
				sendMsg(player_id, "\uD83C\uDFB0 У вас недостаточно денег	");
			}
		} else {
			sendMsg(player_id, "\uD83D\uDC7E Для игры в монетку нужен 4 уровень \n⚡ Повысить уровень можно за поиск предметов");
		}
	}

	public void command_me(Player player) {
		long player_id = player.getId();
		String me = "Информация о персонаже\n" + "\n" +
				"⭐ Ваш ник: " + player.getUsername() +
				"\n" +
				"\n" +
				"\uD83D\uDCB0 Ваш баланс: $" + player.getMoney() +
				"\n" +
				"\n" +
				"\uD83C\uDF20 Ваш GameID: " + player_id +
				"\n" +
				"\n" +
				String.format("\uD83D\uDC7E Ваш уровень: %d (%d XP)", player.getLevel(), player.getXp()) +
				"\n" +

				"\uD83C\uDF3F Выпито кружек чая: " + player.stats.tea +
				"\n" +
				"☕️ Выпито кружек кофе: " + player.stats.coffee +
				"\n" +
				"\uD83C\uDFC6 Победы в монетке: " + player.stats.coinWins +
				"\n" +

				"\uD83D\uDCC9 Проигрыши в монетке: " + player.stats.coinLosses +
				"\n";

		sendMsg(player_id, me);
	}

	public void command_shopbuy(Player player) {
		long player_id = player.getId();
		int limitSpace;
		Item backpack = itemDAO.getByName("\uD83C\uDF92 Рюкзак");
		if(player.getInventory().getItems().contains(backpack)){
			limitSpace = 25;
		}else{
			limitSpace = 20;
		}

		if (player.getInventory().getInvSize() < limitSpace) {


			if (shopDAO.getAll().isEmpty()) {

				sendMsg(player_id, "\uD83D\uDC40 В магазине пока нет товаров\n");
			} else {
				active_players.put(player_id, player);
				StringBuilder sb = new StringBuilder("\uD83D\uDC5C Все предметы в магазине:\n\n");
				//sb.append("=====================\n");
				for (ShopItem i : shopDAO.getAll()) {
					//сделать привязку не по нику, а по playerID
					sb.append(String.format("\uD83C\uDFA9 Товар |# %d| `%s` | Цена: %d$ | Продавец: `%s` \n", i.getId(), i.getItem().getTitle(), i.getCost(), i.getSeller().getUsername()));
				}
				sb.append("\n");

				sendMsg(player_id, sb.toString());
				sendMsg(player_id, "Введите ID товара, который вы хотите купить: ");
				player.setState(Player.State.shopBuy);
				//playerDAO.update(player);
			}
		} else {
			sendMsg(player.getId(), "⚠ В вашем инвентаре нет места");
		}
	}

	public void command_shopshow(Player player) {
		try {
			SendPhoto photo = new SendPhoto();
			photo.setPhoto(new InputFile(new File(".\\pics\\shop.jpg")));
			photo.setChatId(player.getId());

			long player_id = player.getId();

			if (shopDAO.getAll().isEmpty()) {
				sendMsg(player_id, "\uD83D\uDC40 В магазине пока нет товаров\n");
			} else {
				StringBuilder sb = new StringBuilder("\uD83D\uDC5C Все предметы в магазине:\n\n");
				//sb.append("=====================\n");
				for (ShopItem i : shopDAO.getAll()) {
					//сделать привязку не по нику, а по playerID
					sb.append(i);
				}
				sb.append("\n");
				sb.append("\uD83D\uDCB3 Чтобы купить, введите /shopbuy \n");
				sb.append("\uD83D\uDED2 Чтобы разместить свой товар, введите /shopplace \n");
				//sb.append("=====================\n");

				//photo.setCaption(sb.toString());
				this.execute(photo);
				sendMsg(player_id, sb.toString());
			}
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

	public void command_shopplace(Player player) {
		long player_id = player.getId();

		if (player.getInventory().getInvSize() == 0) {
			sendMsg(player_id, "Вы не можете ничего продать, так как Ваш инвентарь пуст");
		} else {
			active_players.put(player_id, player);
			Inventory inventory = player.getInventory();

			StringBuilder sb = new StringBuilder("Предметы, доступные для продажи \n");
			sb.append("=====================\n");
			for (int i = 0; i < inventory.getInvSize(); i++) {

				sb.append(String.format("Предмет | %d |: ", i)).append(inventory.getItem(i)).append("\n");
			}
			sb.append("=====================\n");
			sendMsg(player_id, sb.toString());
			sendMsg(player_id, "Введите ID предмета, который хотите продать\n");

			player.setState(Player.State.shopPlaceGood_awaitingID);
			//playerDAO.update(player);
		}
	}


	public void command_pay(Player player) {
		if (player.getMoney() <= 0) {
			sendMsg(player.getId(), "У вас нет денег для перевода");
		} else {
			active_players.put(player.getId(), player);
			sendMsg(player.getId(), "Введите ник игрока: ");
			player.setState(Player.State.payAwaitingNickname);
		}
	}

	public void command_coffee(Player player) {
		if (player.getMoney() < 500) {
			sendMsg(player.getId(), "☕ Не хватает деняк на кофе :'(");
		} else {
			active_players.put(player.getId(), player);
			sendMsg(player.getId(), "☕($500) Введите ник игрока: ");
			player.setState(Player.State.awaitingCoffee);
		}
	}

	public void command_tea(Player player) {
		if (player.getMoney() < 500) {
			sendMsg(player.getId(), "\uD83C\uDF3F Не хватает деняк на чай :'(");
		} else {
			active_players.put(player.getId(), player);
			sendMsg(player.getId(), "\uD83C\uDF3F($500) Введите ник игрока: ");
			player.setState(Player.State.awaitingTea);
		}
	}

	public void command_start_already_registered(Player player) {
		sendMsg(player.getId(), "Вы уже зарегистрированы.\n");
	}

	public void cleanShopFromExpired() {
		List<ShopItem> shopItems = shopDAO.expire();
		for (ShopItem shopItem : shopItems) {
			Player seller = shopItem.getSeller();
			long seller_id = seller.getId();
			inventoryDAO.putItem(seller_id, shopItem.getItem().getId());
			sendMsg(seller_id, String.format("Ваш товар %sбыл снят с продажи, предмет добавлен в ваш инвентарь", shopItem));
		}
	}

	void sendFindCooldownNotification() {
		List<Long> expires = abilityDAO.expireFind();
		for (long id : expires) {
			sendMsg(id, "⭐ Вы снова можете искать редкие предметы!");
		}
	}

	public void dump_database() {
		System.out.println("Dump database fired (NO-OP)");
	}

	public void on_closing() {
		System.out.println("Exiting...");
		sf_dump.cancel(false);
		sf_find.cancel(false);
		sf_timers.cancel(false);
		STPE.stpe.shutdown();
		dump_database();
		System.out.println("Goodbye!");
	}

	public void level_up_notification(Player player) {
		int fee = 350 * player.getLevel();
		sendMsg(player.getId(), String.format("\uD83C\uDF88 Поздравляем! Вы перешли на новый уровень (Уровень %d)\n\uD83C\uDF81 Бонус за переход на новый уровень +$%d",
				player.getLevel(), fee));
		player.balance += fee;
		playerDAO.update(player);
	}

	public String getBotUsername() {
		return "Needle";
	}

	private String init_token() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("token"));
		return scanner.nextLine();
	}

	public String getBotToken() {
		return token;
	}

	private void coin_dash_callback(Player player, int i_dash) {
		long player_id = player.getId();
		CoinGame coinGame = new CoinGame(i_dash);
		if (coinGame.roll()) {
			sendMsg(player_id, "\uD83D\uDCB0 Вы выиграли " + "$" + i_dash);
			coinGame.coinWin(player, i_dash);
			player.stats.coinWins++;


		} else {
			sendMsg(player_id, "❌ Вы проиграли " + "$" + i_dash);
			coinGame.coinLose(player, i_dash);

			player.stats.coinLosses++;

		}

		player.setState(Player.State.awaitingCommands);
		sendMsg(player_id, "Ваш баланс: " + player.balance + " \uD83D\uDCB2");

		statsDAO.update(player.getStats(), player.getId());
		playerDAO.update(player);


	}
}



