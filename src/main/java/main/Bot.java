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
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static main.BotCommandProcessor.*;


public class Bot extends TelegramLongPollingBot
{
	private final IPlayerDAO playerDAO;
	private final InventoryDAO inventoryDAO;
	private final ItemDAO itemDAO;
	private final ShopDAO shopDAO;
	private final StatsDAO statsDAO;
	private final AbilityDAO abilityDAO;

	private static final Roller<Item> mudRoller = RollerFactory.getMudRoller(new Random());
	private static final Roller<Integer> moneyRoller = RollerFactory.getMoneyRoller(new Random());
	private static final Roller<Item> findRoller = RollerFactory.getFindRoller(new Random());
	private static final Roller<Item> fishRoller = RollerFactory.getFishRoller(new Random());
	private static final Capitalgame capitalgame = new Capitalgame();


	ScheduledFuture<?> sf_timers;
	ScheduledFuture<?> sf_find;
	private final long expStepS = 5L;
	ScheduledFuture<?> sf_pockets;
	ScheduledFuture<?> sf_dump;
	private final long dump_timer_s = 120L;

	public final long findCooldown = 20L * 60L * 1000L;
	public final long pocketsCooldown = 30L * 1000L;

	private final String token;

	KeyboardPaginator paginator;

	//Map<Long, Player> active_players;

	Map<Player.State, BiConsumer<Player, Message>> state_processor;
	Map<String, Consumer<Player>> command_processor;

	public Bot(Connection connection) throws FileNotFoundException
	{
		playerDAO = new CachedPlayerDAO(connection, this);
		inventoryDAO = new InventoryDAO(connection);
		itemDAO = new ItemDAO(connection);
		shopDAO = new ShopDAO(connection, this);
		statsDAO = new StatsDAO(connection);
		abilityDAO = new AbilityDAO(connection, this);
		token = init_token();
		state_processor = BotStateProcessor.get_map(this);
		command_processor = BotCommandProcessor.get_map(this);
		sf_timers = STPE.stpe.scheduleAtFixedRate(this::cleanShopFromExpired, 0L, 5L, TimeUnit.SECONDS);
		sf_find = STPE.stpe.scheduleAtFixedRate(this::sendFindCooldownNotification, 0L, expStepS, TimeUnit.SECONDS);
		sf_pockets = STPE.stpe.scheduleAtFixedRate(abilityDAO::expirePockets, 0L, expStepS, TimeUnit.SECONDS);  // remove this shit
		sf_dump = STPE.stpe.scheduleAtFixedRate(this::dump_database, dump_timer_s, dump_timer_s, TimeUnit.SECONDS);
		paginator = new KeyboardPaginator()
				.first(INV_BUTTON, HELP_BUTTON, ME_BUTTON, FIND_BUTTON, MUD_BUTTON, POCKETS_BUTTON, DROP_BUTTON, SHOPSHOW_BUTTON, SELL_BUTTON)
				.then(TOP_BUTTON, FISH_BUTTON, COIN_BUTTON, "/важная кнопк", CAPITALGAME_BUTTON, CASE_BUTTON, FOREST_BUTTON, TEA_BUTTON, COFFEE_BUTTON)
				.last(PAY_BUTTON, INFO_BUTTON, CHANGENICKNAME_BUTTON, SHOPPLACE_BUTTON, CHECK_BUTTON, SELLFISH_BUTTON, RECIPES_BUTTON, DONATE_BUTTON);

		capitalgame.init();
	}

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

	public void setButtons(SendMessage sendMessage)
	{
		long id = Long.parseLong(sendMessage.getChatId());
		Player player = playerDAO.get_by_id(id);
		//инициаллизация клавиатуры
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(false);

		if (player == null)
		{
			List<KeyboardRow> rows = new ArrayList<>();
			KeyboardRow row = new KeyboardRow();
			row.add(new KeyboardButton("⭐ Начать"));
			rows.add(row);
			replyKeyboardMarkup.setKeyboard(rows);
		}
		else
		{
			Player.State state = player.getState();
			if (state == Player.State.awaitingCommands)
			{
				replyKeyboardMarkup.setKeyboard(paginator.get(player.page));
			}
			else
			{
				KeyboardRow row = new KeyboardRow();
				switch (state)
				{
					case awaitingTeaNote:
					case awaitingCoffeeNote:
					case shopPlaceGood_awaitingCost:
					case payAwaitingAmount:
						//row.add(new KeyboardButton("/back"));
					case shopBuy:
					case coinDash:
					case awaitingTea:
					case awaitingCoffee:
					case awaitingSellArguments:
					case awaitingChangeNickname:
					case shopPlaceGood_awaitingID:
					case payAwaitingNickname:
					default:
						row.add(new KeyboardButton(CANCEL_BUTTON));
						break;
				}
				List<KeyboardRow> rows = new ArrayList<>();
				rows.add(row);
				replyKeyboardMarkup.setKeyboard(rows);
			}
		}
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
	}

	@Override
	public void onUpdateReceived(Update update)
	{
		Message message = update.getMessage();

		if (message != null && message.hasText())
		{
			long id = message.getChatId();
			String text = message.getText();

			Player player = playerDAO.get_by_id(id);

			System.out.printf("%s: %s [from %s | %d]\n", new Date(), text, player != null ? player.getUsername() : "new player", id);

			if (player == null)
			{
				if (text.equals("/start") || text.equals("⭐ Начать"))
				{
					player = new Player(id, this);
					playerDAO.put(player);

					sendMsg(id, "\uD83C\uDF77 Добро пожаловать в Needle");
					sendMsg(id, "Введите ник: ");
				}
				else
				{
					sendMsg(id, "⭐ Для регистрации введите команду /start");
				}
			}
			else
			{
				if (!text.equals(CANCEL_BUTTON))
				{
					state_processor.get(player.getState()).accept(player, message);
				}
				else
				{
					player.setState(Player.State.awaitingCommands);
					sendMsg(id, "Выберите действие");
				}
			}
		}
	}

	void awaitingNickname_processor(Player player, Message message)
	{
		long player_id = player.getId();
		String username = message.getText();
		//regex для ника
		String usernameTemplate = "[\\p{Alpha}[а-щА-ЩЬьЮюЯяЇїІіЄєҐґ'][0-9]]{2,32}";
		if (username.matches(usernameTemplate))
		{
			try
			{
				Player new_player = playerDAO.get_by_name(username);
				if (new_player == null)
				{
					player.setUsername(username);
					player.setState(Player.State.awaitingCommands);
					playerDAO.update(player);
					sendMsg(player_id, "Игрок `" + player.getUsername() + "` успешно создан");
					command_help(player);
				}
				else
				{
					throw new RuntimeException("REx");
				}
			}
			catch (RuntimeException e)  // TODO change to some reasonable <? extends Exception> class
			{
				e.printStackTrace();
				sendMsg(player_id, "⚠\t Игрок с таким ником уже существует");
			}
		}
		else
		{
			sendMsg(player_id, "⚠\t Введите корректный ник: ");
		}
	}

	void awaitingSellArguments_processor(Player player, Message message)
	{
		long player_id = player.getId();
		try
		{
			player.setState(Player.State.awaitingCommands);
			Inventory inventory = player.getInventory();
			String sellID = message.getText();
			int sell_id = Integer.parseInt(sellID);
			Item item = inventory.getItem(sell_id);
			if (item.getRarity() != ItemRarity.Limited)
			{
				player.balance.transfer(item.getCost().value);
				inventory.removeItem(sell_id);
				inventoryDAO.delete(player_id, item.getId(), 1);
				sendMsg(player_id, "✅ Предмет продан | + " + item.getCost());
			}
			else
			{
				sendMsg(player_id, "\uD83D\uDC8D Лимитированные вещи нельзя продавать");
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			sendMsg(player_id, "⚠\t Пожалуйста, введите целое число");
		}
		catch (IndexOutOfBoundsException ee)
		{
			ee.printStackTrace();
			sendMsg(player_id, "⚠\t Указан неверный ID");
		}
		catch (Money.MoneyException e)
		{
			e.printStackTrace();
			sendMsg(player_id, e.getMessage());
		}
	}

	void awaitingCommand_processor(Player player, Message message)
	{
		String text = message.getText();
		if (command_processor.containsKey(text))
		{
			command_processor.get(text).accept(player);
		}
		else
		{
			sendMsg(player.getId(), "⚠\t Неизвестная команда\n");
		}
	}

	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (chars[i]=='-') { // You can add other chars here
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	void capitalGame_processor(Player player, Message message)
	{
		Random ran = new Random();
		int money = ran.nextInt(2000);

		String input = message.getText();
		String firstLetterUp = capitalizeString(input);
		long id = player.getId();

		player.setState(Player.State.awaitingCommands);
		if (!firstLetterUp.equals(capitalgame.getCapital(player.countryKey)))
		{
			sendMsg(id, "❌ Неправильно");
		}
		else
		{
			sendMsg(id, "\uD83C\uDFC6 Правильно | + $" + money);
			try
			{
				player.getMoney().transfer(money);
			}
			catch (Money.MoneyException e)
			{
				e.printStackTrace();
			}
		}
	}


	void awaitingChangeNickname_processor(Player player, Message message)
	{
		long player_id = player.getId();
		String nickname = message.getText();
		Item tag = itemDAO.getByNameFromCollection("\uD83D\uDCDD Тег");
		int tag_idx = player.getInventory().getItems().indexOf(tag);
		//regex для ника
		String usernameTemplate = "[\\p{Alpha}[а-щА-ЩЬьЮюЯяЇїІіЄєҐґ'][0-9]]{2,32}";
		if (nickname.matches(usernameTemplate))
		{
			try
			{
				player.setUsername(nickname);
				inventoryDAO.delete(player.getId(), tag.getId(), 1);
				player.getInventory().removeItem(tag_idx);
				sendMsg(player_id, "Ваш никнейм успешно изменен на `" + player.getUsername() + "`");
				dump_database();
				dump_database();
			}
			catch (RuntimeException e)
			{
				e.printStackTrace();
				sendMsg(player_id, "Игрок с таким ником уже есть");
			}
		}
		else
		{
			sendMsg(player_id, "Пожалуйста, введите корректный ник");
		}
		player.setState(Player.State.awaitingCommands);

	}

	public SendPhoto getPhoto(String path, Player player)
	{
		SendPhoto photo = new SendPhoto();
		photo.setPhoto(new InputFile(new File(path)));
		photo.setChatId(player.getId());
		return photo;
	}

	void awaitingTouchId_processor(Player player, Message message)
	{
		Random randomPlayer = new Random();
		List<Player> players = playerDAO.get_all();
		int randomIndex = randomPlayer.nextInt(players.size());
		String anotherPlayer = players.get(randomIndex).getUsername();

		Touch touch = new Touch(player, anotherPlayer);

		long id = player.getId();
		try
		{
			String itemID = message.getText();
			int item_id = Integer.parseInt(itemID);
			Item energy = itemDAO.getByNameFromCollection("Энергетик");
			String responseText = touch.getInfo().get(player.getInventory().getItem(item_id));

			if (responseText != null && responseText != touch.getInfo().get(energy))
			{
				if (touch.getMagazines().containsKey(responseText))
					this.execute(touch.getMagazinePhoto(responseText));
				sendMsg(id, "\uD83E\uDEA1 " + responseText);
			}
			else
			{
				if (player.getInventory().getItem(item_id).getTitle().equals(energy.getTitle()))
				{
					if (player.findExpiration != null)
					{
						inventoryDAO.delete(id, energy.getId(), 1);
						sendMsg(id, "⚡ Вы чувствуете прилив сил, время ожидания снижено на 2 минуты");
						player.findExpiration -= 120L * 1000L;
						dump_database();
						dump_database();
					}
					else
					{
						sendMsg(id, "Вы и так полны энергии");
					}
				}
				else
				{
					sendMsg(id, "\uD83E\uDEA1 " + "Обычный предмет, его не интересно трогать");
				}
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			sendMsg(id, "⚠\t Пожалуйста, введите целое число");
		}
		catch (IndexOutOfBoundsException ee)
		{
			ee.printStackTrace();
			sendMsg(id, "⚠\t Указан неверный ID");
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
		player.setState(Player.State.awaitingCommands);
	}

	void coinDash_processor(Player player, Message message)
	{
		List<String> text = new ArrayList<>();
		text.add("Подбрасываем монетку...");
		text.add("Молим удачу...");
		text.add("Скрещиваем пальцы...");
		text.add("Не не надеемся не на проигрыш...");
		text.add("Закрываем глаза...");
		text.add("Держим кулачки...");
		text.add("Затаиваем дыхание...");
		text.add("Верим в Бога...");
		text.add("Надеемся на выигрыш...");
		text.add("Загадываем желание...");
		text.add("Думаем о победе...");
		text.add("Надеемся на решку...");
		text.add("Надеемся на орла...");
		text.add("И выпадает...");


		long player_id = player.getId();
		String dash = message.getText();
		try
		{
			int i_dash = Integer.parseInt(dash);
			Random r = new Random();
			int ran = r.nextInt(text.size());

			if (i_dash > 0 && i_dash <= player.balance.value)
			{

				sendMsg(player_id, "\uD83C\uDFB0 Ваша ставка: " + new Money(i_dash));

				sendMsg(player_id, text.get(ran));

				Cooldown kd = new Cooldown(2, () -> coin_dash_callback(player, i_dash));
				kd.startCooldown();
			}
			else
			{
				sendMsg(player_id, "⚠\t У вас нет такой суммы");
			}
		}
		catch (NumberFormatException e)
		{

			sendMsg(player_id, "⚠\tВаша ставка должна быть целым числом");
			e.printStackTrace();
		}
	}

	void shopBuy_processor(Player player, Message message)
	{
		try
		{
			int userInput = Integer.parseInt(message.getText());

			synchronized (shopDAO)
			{
				ShopItem wanted_item = shopDAO.getByID(userInput);
				Item item = wanted_item.getItem();
				long itemCost = wanted_item.getCost().value;
				Player seller = wanted_item.getSeller();

				if (player.equals(seller))
				{
					inventoryDAO.putItem(player.getId(), item.getId());
					player.getInventory().putItem(item);
					sendMsg(player.getId(), String.format("Ваш товар %s снят с продажи", item));
					shopDAO.delete(userInput);
				}
				else if (player.balance.value >= itemCost)
				{
					player.balance.transfer(-itemCost);
					seller.balance.transfer(itemCost);

					inventoryDAO.putItem(player.getId(), item.getId());
					player.getInventory().putItem(item);

					sendMsg(player.getId(), String.format("\uD83C\uDF6D Предмет `%s` успешно куплен", item));
					sendMsg(seller.getId(), String.format("\uD83D\uDCC8 Ваш предмет `%s` купил игрок `%s` | + %s", item.getTitle(), player.getUsername(), new Money(itemCost)));
					seller.addXp(3);

					shopDAO.delete(userInput);
				}
				else
				{
					sendMsg(player.getId(), "Недостаточно средств");
				}
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			sendMsg(player.getId(), "Введите целое число");  // << звучит как приглашение, хотя стейт тут же меняется
		}
		catch (IndexOutOfBoundsException ee)
		{
			ee.printStackTrace();
			sendMsg(player.getId(), "Неверный ID");
		}
		catch (Money.MoneyException e)
		{
			e.printStackTrace();
			sendMsg(player.getId(), e.getMessage());
		}
		player.setState(Player.State.awaitingCommands);
	}

	void craftAwaitingID_processor(Player player, Message message)
	{
		//улучшить код, добавить поощрение опытом
		long player_id = player.getId();
		try
		{
			Recipe recipe = new Recipe();
			Inventory inventory = player.getInventory();
			String craftID = message.getText();
			int craft_id = Integer.parseInt(craftID);
			Item craftName = recipe.choice(craft_id);
			List<Item> ingredients = recipe.recipes.get(craftName);

			if(recipe.hasRecipe(inventory, ingredients)){
				for(Item i: ingredients){
					inventoryDAO.delete(player_id, i.getId(), 1);
				}
				player.setState(Player.State.awaitingCommands);
				sendMsg(player_id, "\uD83D\uDD27 Предмет изготовлен " + craftName.getTitle());
				inventoryDAO.putItem(player_id, craftName.getId());
				player.addXp(4);
				dump_database();
				dump_database();

			}else{
				sendMsg(player_id, "\uD83E\uDE93 Для крафта нужно иметь: \n " + recipe.recipes.get(craftName).toString());
			}

		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			player.setState(Player.State.awaitingCommands);
			sendMsg(player_id, "⚠\t Пожалуйста, введите целое число");
		}
		catch (IndexOutOfBoundsException ee)
		{
			ee.printStackTrace();
			player.setState(Player.State.awaitingCommands);
			sendMsg(player_id, "⚠\t Указан неверный ID");
		}
	}


	void shopPlaceGood_awaitingID_processor(Player player, Message message)
	{
		long id = player.getId();
		try
		{
			int itemID = Integer.parseInt(message.getText());
			if (itemID >= player.getInventory().getInvSize())
			{
				throw new IndexOutOfBoundsException();
			}
			else if (player.getInventory().getInvSize() > 20)
			{
				throw new BackpackException(itemID);
			}
			player.to_place_item = itemID;
			sendMsg(id, "Введите стоимость товара: ");
			player.setState(Player.State.shopPlaceGood_awaitingCost);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			sendMsg(id, "Введите целое число");
		}
		catch (IndexOutOfBoundsException ee)
		{
			ee.printStackTrace();
			sendMsg(id, "Неверный ID");
		}
		catch (BackpackException e)
		{
			Item ii = player.getInventory().getItem(e.backpackID);
			Item backpack = itemDAO.getByName("\uD83C\uDF92 Рюкзак");
			if (ii.equals(backpack))
			{
				sendMsg(id, String.format("Избавьтесь от дополнительных слотов, прежде чем продать `%s`", backpack.getTitle()));
				player.setState(Player.State.awaitingCommands);
			}
			else
			{
				player.to_place_item = e.backpackID;
				sendMsg(player.getId(), "Введите стоимость товара: ");
				player.setState(Player.State.shopPlaceGood_awaitingCost);
			}
		}
	}


	void shopPlaceGood_awaitingCost_processor(Player player, Message message)
	{
		long player_id = player.getId();
		try
		{
			player.setState(Player.State.awaitingCommands);
			int cost = Integer.parseInt(message.getText());
			if (cost > 0)
			{
				Inventory inventory = player.getInventory();
				ShopItem shopItem = new ShopItem(inventory.getItem(player.to_place_item), cost, player);
				shopDAO.put(shopItem);

				sendMsg(player_id, String.format("Товар `%s` выставлен на продажу", inventory.getItem(player.to_place_item).getTitle()));
				inventory.removeItem(player.to_place_item);

				inventoryDAO.delete(player_id, shopItem.getItem().getId(), 1);
			}
			else
			{
				sendMsg(player_id, "Сумма не может быть нулем");
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			sendMsg(player_id, "⚠\t Пожалуйста, введите целое число");
		}
	}

	public void checkAwaitingNickname_processor(Player player, Message message)
	{
		long player_id = player.getId();
		String nickname = message.getText();

		Player anotherPlayer = playerDAO.get_by_name(nickname);
		
		player.setState(Player.State.awaitingCommands);
		if (anotherPlayer != null)
		{
			Inventory inventory = anotherPlayer.getInventory();
			if (inventory.getInvSize() != 0)
			{
				StringBuilder sb = new StringBuilder("\uD83D\uDC41 Инвентарь игрока `" + anotherPlayer.getUsername() + "`");
				sb.append("\n");
				sb.append("========================\n");
				for (int i = 0; i < inventory.getInvSize(); i++)
				{
					sb.append(String.format("Предмет |%d| : %s\n", i, inventory.getItem(i).toString()));
				}
				sb.append("========================\n");
				//sendMsg(message, "\u26BD");
				sb.append("\uD83D\uDC41 Всего предметов: ").append(inventory.getInvSize());
				sendMsg(player_id, sb.toString());
			}
			else
			{
				sendMsg(player_id, "\uD83C\uDF81\t Инвентарь `" + nickname + "` пуст\n");
			}
		}
		else
		{
			sendMsg(player_id, "Такого игрока не существует");
		}
	}

	public void giveID_processor(Player player, Message message){
		long player_id = player.getId();
		String itemTitle = message.getText();
		try{
			Item item = itemDAO.getByNameFromCollection(itemTitle);
			sendMsg(player_id, String.format("Предмет `%s` добавлен в Ваш инвентарь", item.getTitle()));
			inventoryDAO.putItem(player_id, item.getId());
			player.setState(Player.State.awaitingCommands);
			dump_database();
			dump_database();

		}catch (RuntimeException e){
			e.printStackTrace();
			sendMsg(player_id, "Такого предмета не существует");
		}
	}

	public void payAwaitingNickname_processor(Player player, Message message)
	{
		long player_id = player.getId();
		String nickname = message.getText();

		if (!nickname.equals(player.getUsername()))
		{
			Player acceptor = playerDAO.get_by_name(nickname);
			if (acceptor != null)
			{
				player.payment_acceptor = acceptor;
				player.setState(Player.State.payAwaitingAmount);
				sendMsg(player_id, "\uD83D\uDCB3 Введите сумму: ");
			}
			else
			{
				sendMsg(player_id, "Такого игрока не существует");
				player.setState(Player.State.awaitingCommands);
			}
		}
		else
		{
			sendMsg(player_id, String.format("\uD83C\uDF38 Игрок `%s` очень богат и не нуждается в Ваших копейках", player.getUsername()));
			player.setState(Player.State.awaitingCommands);
		}
	}

	public void payAwaitingAmount_processor(Player player, Message message)
	{
		try
		{
			long cost = Long.parseLong(message.getText());
			if (cost > player.balance.value || cost <= 0)
			{
				sendMsg(player.getId(), "⚠\t Некорректная сумма");
			}
			else
			{
				Player receiver = player.payment_acceptor;
				player.balance.transfer(-cost);
				receiver.balance.transfer(cost);
				sendMsg(receiver.getId(), String.format("\uD83D\uDCB3 Вам начислено %s | Отправитель: `%s` ", new Money(cost), player.getUsername()));
				sendMsg(player.getId(), "✅ Деньги отправлены");
				player.payment_acceptor = null;
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			sendMsg(player.getId(), "⚠\t Вы ввели некорректную сумму");
		}
		catch (Money.MoneyException ex)
		{
			ex.printStackTrace();
			sendMsg(player.getId(), ex.getMessage());
		}
		player.setState(Player.State.awaitingCommands);
	}

	public void awaitingCoffee_processor(Player player, Message message)
	{
		long player_id = player.getId();
		String nickname = message.getText();

		if (!nickname.equals(player.getUsername()))
		{
			Player acceptor = playerDAO.get_by_name(nickname);
			if (acceptor != null)
			{
				player.coffee_acceptor = acceptor;
				player.setState(Player.State.awaitingCoffeeNote);
				sendMsg(player_id, "Введите сообщение для игрока (48 символов): ");
			}
			else
			{
				player.setState(Player.State.awaitingCommands);
				sendMsg(player_id, "Такого игрока не существует");
			}
		}
		else
		{
			player.setState(Player.State.awaitingCommands);
			sendMsg(player_id, "\uD83C\uDF38 Кофе можно отправлять только другим игрокам");
		}
	}


	public void awaitingCoffeeNote_processor(Player player, Message message)
	{
		int goal;
		Item cup = itemDAO.getByName("☕ Чашка 'Египет'");
		if (player.getInventory().getItems().contains(cup))
		{
			goal = 200;
		}
		else
		{
			goal = 500;
		}

		try
		{
			String note = message.getText();
			if (note.length() < 48)
			{
				player.setState(Player.State.awaitingCommands);
				Player receiver = player.coffee_acceptor;

				player.balance.transfer(-goal);

				receiver.stats.coffee++;
				sendMsg(player.getId(), "☕ Кофе отправлен");
				if (player.coffee_acceptor.getStats().coffee == 75)
				{
					player.coffee_acceptor.ach_coffee();
				}
				player.addXp(1);
				sendMsg(receiver.getId(), String.format("☕ Игрок `%s` угостил вас кружечкой кофе с сообщением: `%s`", player.getUsername(), note));
			}
			else
			{
				sendMsg(player.getId(), "Сообщение больше, чем 48 символов");
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			sendMsg(player.getId(), "⚠\t Некорректное сообщение");
		}
		catch (Money.MoneyException ex)
		{
			ex.printStackTrace();
			sendMsg(player.getId(), ex.getMessage());
		}
	}

	public void awaitingTea_processor(Player player, Message message)
	{
		long player_id = player.getId();
		String nickname = message.getText();

		if (!nickname.equals(player.getUsername()))
		{
			Player acceptor = playerDAO.get_by_name(nickname);
			if (acceptor != null)
			{
				player.tea_acceptor = acceptor;
				player.setState(Player.State.awaitingTeaNote);
				sendMsg(player_id, "Введите сообщение для игрока (48 символов): ");
			}
			else
			{
				player.setState(Player.State.awaitingCommands);
				sendMsg(player_id, "Такого игрока не существует");
			}
		}
		else
		{
			player.setState(Player.State.awaitingCommands);
			sendMsg(player_id, "\uD83C\uDF38 Чай можно отправлять только другим игрокам");
		}
	}

	public void awaitingTeaNote_processor(Player player, Message message)
	{
		int goal;
		Item cup = itemDAO.getByName("☕ Чашка 'Египет'");
		if (player.getInventory().getItems().contains(cup))
		{
			goal = 200;
		}
		else
		{
			goal = 500;
		}

		try
		{
			player.setState(Player.State.awaitingCommands);
			String note = message.getText();
			if (note.length() < 48)
			{
				Player receiver = player.tea_acceptor;

				player.balance.transfer(-goal);

				receiver.stats.tea++;
				sendMsg(player.getId(), "\uD83C\uDF3F Чай отправлен");
				if (player.tea_acceptor.getStats().tea == 75)
				{
					player.tea_acceptor.ach_tea();
				}
				player.addXp(1);
				sendMsg(receiver.getId(), String.format("\uD83C\uDF3F Игрок `%s` угостил вас кружечкой чая с сообщением: `%s`", player.getUsername(), note));
			}
			else
			{
				sendMsg(player.getId(), "Сообщение больше, чем 48 символов");
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			sendMsg(player.getId(), "⚠\t Некорректное сообщение");
		}
		catch (Money.MoneyException ex)
		{
			ex.printStackTrace();
			sendMsg(player.getId(), ex.getMessage());
		}
	}

	public void command_drinks(Player player)
	{
		//DONATE DRINKS BOOSTERS HERE
		sendMsg(player.getId(), "Drinks module here");
	}

	public void command_recipes(Player player)
	{
		long id = player.getId();
		StringBuilder sb = new StringBuilder("*Рецепты*\n");
		sb.append("Здесь можно скрафтить полезные вещи, используя менее ценные предметы\n\n");
		sb.append("Предметы, доступные для крафта: \n");
		Recipe recipe = new Recipe();

		if (recipe.recipes != null)
		{
			sb.append("\n");
			sb.append("========================\n");
			int i = 0;

			for(Map.Entry<Item, List<Item>> entry : recipe.recipes.entrySet()){
				String craftName = entry.getKey().getTitle();
				sb.append(String.format("Рецепт |%d|: %s\n", i, craftName));
				i++;
			}


			sb.append("========================\n");
			sb.append("Чтобы скрафтить предмет введите его ID: ");
			player.setState(Player.State.craftAwaitingID);
			sendMsg(id, sb.toString());
		}
		else
		{
			player.setState(Player.State.awaitingCommands);
			sendMsg(id, "\uD83C\uDF81\t Список рецептов пуст ");
		}
	}

	public void command_help(Player player)
	{

		StringBuilder sb = new StringBuilder("\\[`Needle`] Бот содержит следующие команды: \n\n");
		sb.append("\\[Команды поиска] \n");


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
				"\uD83C\uDF3F /tea - отправить чай \n" +
				"☕️ /coffee - отправить кофе \n" +
				"\uD83D\uDD11 /case - открывать кейсы \n" +
				"\n" +
				" \\[Локации] \n" +

				"\uD83C\uDF33 /forest - посетить Лес \n" +

				"\uD83D\uDC21 /fish - пойти на рыбалку \n"
		);
	}

	public void command_forest(Player player)
	{
		Random r = new Random();
		boolean success = r.nextBoolean();
		long fee = r.nextInt(3500);
		try
		{
			Item flashlight = itemDAO.getByNameFromCollection("\uD83D\uDD26 Поисковый фонарь");
			Item seedling = itemDAO.getByNameFromCollection("Саженец");
			int seedling_index = player.getInventory().getItems().indexOf(seedling);
			Achievements a = new Achievements(player);
			if (player.getInventory().getItems().contains(flashlight))
			{
				if (seedling_index != -1)  // player has seedling
				{
					if (success)
					{
						sendMsg(player.getId(), "\uD83C\uDF33 Вы посадили саженец, природа это оценила | +$" + fee);
						player.getMoney().transfer(fee);
						player.addXp(1);
					}
					else
					{
						sendMsg(player.getId(), "\uD83C\uDF33 Вы посадили саженец");
						player.addXp(2);
					}
					player.stats.trees++;  // todo this is shit
					if (player.stats.trees == 50)
					{
						player.ach_treeHard();
					}
					inventoryDAO.delete(player.getId(), seedling.getId(), 1);
					player.getInventory().removeItem(seedling_index);
				}
				else
				{
					sendMsg(player.getId(), "У вас нет саженцов");
				}
			}
			else
			{
				sendMsg(player.getId(), String.format("Для похода в лес вам нужен предмет `%s` \n\uD83D\uDED2 Его можно купить у других игроков в магазине", flashlight.getTitle()));
			}
		}
		catch (RuntimeException | Money.MoneyException e)
		{
			e.printStackTrace();
		}
	}

	public void command_fish(Player player)
	{
		Item i = itemDAO.getByNameFromCollection("\uD83D\uDC1FУдочка");
		int limitSpace;
		Item backpack = itemDAO.getByNameFromCollection("\uD83C\uDF92 Рюкзак");

		if (player.getInventory().getItems().contains(backpack))
		{
			limitSpace = 25;
		}
		else
		{
			limitSpace = 20;
		}

		if (player.getLevel() >= 5)
		{
			if (player.getInventory().getItems().contains(i))
			{
				if (player.getInventory().getInvSize() < limitSpace)
				{
					Item item = fishRoller.roll();

					if (item != null)
					{
						inventoryDAO.putItem(player.getId(), item.getId());
						player.getInventory().putItem(item);
						sendMsg(player.getId(), String.format("Вы поймали %s", item));
						player.addXp(1);
					}
					else
					{
						sendMsg(player.getId(), "Не клюет");
					}
				}
				else
				{
					sendMsg(player.getId(), "⚠ В вашем инвентаре нет места");
				}
			}
			else
			{
				sendMsg(player.getId(), String.format("Для похода на рыбалку вам нужен предмет `%s` \n\uD83D\uDED2 Его можно купить у других игроков в магазине или найти", i.getTitle()));
			}
		}
		else
		{
			sendMsg(player.getId(), "\uD83D\uDC7E Для похода на рыбалку вам нужен 5 уровень");
		}
	}


	public void command_sellfish(Player player)
	{
		long id = player.getId();

		LocalTime open = LocalTime.of(10, 0);
		LocalTime close = LocalTime.of(15, 0);

		LocalTime currentTime = LocalTime.now();

		if (currentTime.isBefore(open) || currentTime.isAfter(close))  // wtf?
		{
			sendMsg(id, "\uD83E\uDD88 Рыбная лавка работает с 10:00 до 15:00\n\nСдавая рыбу в лавке, Вы можете получить " +
					"в несколько раз больше выручки, чем если бы сдавали ее \uD83D\uDCDE Скупщику");
		}
		else
		{
			List<String> fish_titles = new ArrayList<>();
			fish_titles.add("Горбуша");
			fish_titles.add("Бычок");
			fish_titles.add("Карась");
			long fee = 0L;

			Iterator<Item> iter = player.getInventory().getItems().iterator();
			while (iter.hasNext())
			{
				Item item = iter.next();
				if (fish_titles.contains(item.getTitle()))
				{
					fee += item.getCost().value * 7;
					inventoryDAO.delete(id, item.getId(), 1);
					iter.remove();
				}
			}

			if (fee > 0)
			{
				sendMsg(id, String.format("\uD83E\uDD88 Покупатель выложил за всю рыбу %s", new Money(fee)));
				try
				{
					player.balance.transfer(fee);
				}
				catch (Money.MoneyException e)
				{
					e.printStackTrace();
					sendMsg(player.getId(), e.getMessage());
				}
			}
			else
			{
				sendMsg(id, "\uD83E\uDD88У вас нет рыбы\nЧтобы ловить рыбу, введите /fish");
			}
		}
	}


	public void command_drop(Player player)
	{
		long id = player.getId();
		long fee = 0L;

		Iterator<Item> iter = player.getInventory().getItems().iterator();
		while (iter.hasNext())
		{
			Item item = iter.next();
			if (item.getRarity() == ItemRarity.Cheap && !item.getTitle().equals("Саженец"))
			{
				fee += item.getCost().value;
				inventoryDAO.delete(id, item.getId(), 1);
				iter.remove();
			}
		}

		if (fee > 0L)
		{
			sendMsg(id, String.format("\uD83D\uDCB3 Вы продали все дешевые вещи за %s", new Money(fee)));
			try
			{
				player.balance.transfer(fee);
			}
			catch (Money.MoneyException e)
			{
				e.printStackTrace();
				sendMsg(id, e.getMessage());
			}
		}
		else
		{
			sendMsg(id, "У вас нет дешевых вещей");
		}
	}

	//SUPER SECRET BONUS
	public void command_bonus(Player player)
	{
		long id = player.getId();
		if (player.getStats().bonus == 0)
		{
			sendMsg(id, "\uD83C\uDF3A Вы получили бонус | +" + new Money(15000L));
			try
			{
				player.balance.transfer(15000);
			}
			catch (Money.MoneyException e)
			{
				e.printStackTrace();
				sendMsg(id, e.getMessage());
			}
			player.stats.bonus++;
		}
		else
		{
			sendMsg(id, "Вы уже получили свой бонус");
		}
	}

	public void command_inv(Player player)
	{
		int limitSpace;
		Item backpack = itemDAO.getByNameFromCollection("\uD83C\uDF92 Рюкзак");
		if (player.getInventory().getItems().contains(backpack))
		{
			limitSpace = 25;
		}
		else
		{
			limitSpace = 20;
		}

		long player_id = player.getId();
		Inventory inventory = player.getInventory();
		if (inventory.getInvSize() != 0)
		{
			StringBuilder sb = new StringBuilder("\uD83C\uDF81\t Ваш инвентарь: ");
			sb.append("\n");
			sb.append("========================\n");
			for (int i = 0; i < inventory.getInvSize(); i++)
			{
				sb.append(String.format("Предмет |%d| : %s\n", i, inventory.getItem(i).toString()));
			}
			sb.append("========================\n");
			//sendMsg(message, "\u26BD");
			sb.append("\uD83C\uDF81\t Всего предметов: ").append(inventory.getInvSize()).append("/").append(limitSpace);
			sendMsg(player_id, sb.toString());
		}
		else
		{
			sendMsg(player_id, "\uD83C\uDF81\t Ваш инвентарь пуст ");
		}
	}

	public void command_donate(Player player){

		StringBuilder sb = new StringBuilder("*Булавки - ценная игровая валюта*\n\n");
		sb.append("- Их можно обменять на внутриигровые доллары по курсу 1 \uD83E\uDDF7 = $9,999\n");
		sb.append("- За булавки можно сокращать время ожидания (2\uD83E\uDDF7)\n");
		sb.append("- Только за булавки можно получить очень редких питомцев\n\n");
		sb.append("- *\uD83E\uDDF7 Булавки* можно обменять на `\uD83D\uDD11 Ключи`\n\n");
		sb.append("*Как их получить?*\n");
		sb.append("В качестве поддержки нашего небольшого проекта, который родился и продолжает " +
				"жить на чистом энтузиазме команда разработчиков будет признательна и очень благодарна за покупку *булавок* \uD83E\uDDF7 \n\n");

		sb.append("5 \uD83E\uDDF7 - 60 гривен\n");
		sb.append("10 \uD83E\uDDF7 - 115 гривен (5% экономия)\n");
		sb.append("25 \uD83E\uDDF7 - 215 гривен (10% экономия)\n");
		sb.append("50 \uD83E\uDDF7 - 385 гривен (20% экономия)\n\n");
		sb.append("или \n\n");

		sb.append("5 \uD83E\uDDF7 - 100 рублей\n");
		sb.append("10 \uD83E\uDDF7 - 190 рублей (5% экономия)\n");
		sb.append("25 \uD83E\uDDF7 - 450 рублей (10% экономия)\n");
		sb.append("50 \uD83E\uDDF7 - 800 рублей (20% экономия)\n\n");
		sb.append("Для покупки *\uD83E\uDDF7 Булавок* писать сюда @zeroxthree0x3\n");

		sendMsg(player.getId(), sb.toString());
	}

	public void command_find(Player player)
	{
		int limitSpace;
		Item backpack = itemDAO.getByNameFromCollection("\uD83C\uDF92 Рюкзак");
		if (player.getInventory().getItems().contains(backpack))
		{
			limitSpace = 25;
		}
		else
		{
			limitSpace = 20;
		}

		long player_id = player.getId();
		long now_ts = System.currentTimeMillis();
		if (player.getInventory().getInvSize() < limitSpace)
		{
			if (player.findExpiration != null && player.findExpiration > now_ts)
			{
				sendMsg(player_id, String.format("\u231B Вы устали, время ожидания:%s",
						PrettyDate.prettify(player.findExpiration - now_ts, TimeUnit.MILLISECONDS)));
			}
			else
			{
				Item new_item = findRoller.roll();
				inventoryDAO.putItem(player_id, new_item.getId());
				player.getInventory().putItem(new_item);
				sendMsg(player_id, String.format("\uD83C\uDF81\t Вы нашли: %s", new_item));
				player.addXp(9);
				player.findExpiration = now_ts + findCooldown;

				playerDAO.update(player);
				//abilityDAO.updateFind(player_id, now_ts + cooldownMs);
			}
		}
		else
		{
			sendMsg(player_id, "⚠ В вашем инвентаре нет места");
		}
	}


	public void command_mud(Player player)
	{
		int limitSpace;
		Item backpack = itemDAO.getByNameFromCollection("\uD83C\uDF92 Рюкзак");
		if (player.getInventory().getItems().contains(backpack))
		{
			limitSpace = 25;
		}
		else
		{
			limitSpace = 20;
		}


		long id = player.getId();
		if (player.getInventory().getInvSize() < limitSpace)
		{
			Item item = mudRoller.roll();
			if (item != null)
			{
				inventoryDAO.putItem(id, item.getId());
				player.getInventory().putItem(item);
				sendMsg(id, String.format("Вы нашли в грязи %s", item));
				player.addXp(1);
			}
			else
			{
				sendMsg(id, "Вы ничего не нашли");
			}
		}
		else
		{
			sendMsg(id, "⚠ В вашем инвентаре нет места");
		}
	}


	public void command_pockets(Player player)
	{
		long player_id = player.getId();
		long now_ts = System.currentTimeMillis();
		long cooldownMs = pocketsCooldown;
		if (player.pocketsExpiration != null && player.pocketsExpiration > now_ts)
		{
			sendMsg(player_id, String.format("\u231B Вы проверяете карманы, время ожидания:%s",
					PrettyDate.prettify(player.pocketsExpiration - now_ts, TimeUnit.MILLISECONDS)));
		}
		else
		{
			int money = moneyRoller.roll();
			if (money > 0)
			{
				sendMsg(player_id, String.format("Вы пошарили в карманах и нашли %s", new Money(money)));
				try
				{
					player.balance.transfer(money);
				}
				catch (Money.MoneyException e)
				{
					e.printStackTrace();
					sendMsg(player_id, e.getMessage());
				}
			}
			else if (money == 0)
			{
				sendMsg(player.getId(), "Вы ничего не нашли в своих карманах");
			}
			else
			{
				throw new RuntimeException("WTF?");
			}
			player.pocketsExpiration = now_ts + cooldownMs;
			//abilityDAO.updatePockets(player_id, now_ts + cooldownMs);
			playerDAO.update(player);
		}
	}


	public void command_capitalgame(Player player)
	{
		if (player.getLevel() < 7)
		{
			sendMsg(player.getId(), "⚡ Для мини-игры *Столицы* нужен 7 уровень");
		}
		else
		{
			Random ran = new Random();
			int random = ran.nextInt(capitalgame.getCountries().size());

			player.countryKey = capitalgame.getCountry(random);

			player.setState(Player.State.capitalGame);
			sendMsg(player.getId(), "\uD83E\uDDE9 Столица страны: " + player.countryKey + "");
		}
	}

	public void command_check(Player player)
	{
		player.setState(Player.State.checkAwaitingNickname);
		sendMsg(player.getId(), "\uD83D\uDC41 Введите ник игрока, чей инвентарь Вы хотите просмотреть: ");
	}


	public void command_top(Player player)
	{
		StringBuilder players_list = new StringBuilder("\uD83D\uDCBB Топ 10 самых богатых игроков:\n\n");
		players_list.append("========================");
		players_list.append("\n");
		for (Player pl : playerDAO.get_top("balance", false, 10))
		{
			if (pl.getInventory().getItems().contains(itemDAO.getByNameFromCollection("\uD83E\uDDDA\u200D♀ Фея")))
			{
				players_list.append(String.format("Игрок `%s` \\[\uD83E\uDDDA\u200D♀] | %s | %d Ур.", pl.getUsername(), pl.balance, pl.getLevel()));
				players_list.append("\n");
				players_list.append("========================");
				players_list.append("\n");
			}
			else
			{
				players_list.append(String.format("Игрок `%s` | %s | %d Ур.", pl.getUsername(), pl.balance, pl.getLevel()));
				players_list.append("\n");
				players_list.append("========================");
				players_list.append("\n");
			}
		}
		players_list.append("\n");
		players_list.append("\uD83D\uDCBB Всего игроков: ").append(playerDAO.get_all().size());
		sendMsg(player.getId(), players_list.toString());
	}

	public void command_info(Player player)
	{
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

	public void command_sell(Player player)
	{
		Inventory inventory = player.getInventory();

		StringBuilder stringBuilder = new StringBuilder();
		if (inventory.getInvSize() > 0)
		{
			stringBuilder.append("\uD83E\uDDF6 Предметы, доступные к продаже:\n");
			stringBuilder.append("\n");
			stringBuilder.append("============================\n");
			for (int i = 0; i < inventory.getInvSize(); i++)
			{
				stringBuilder.append(String.format("Предмет |%d| : %s\n", i, inventory.getItem(i).toString()));
			}

			stringBuilder.append("============================\n");
			stringBuilder.append("\n");
			stringBuilder.append("Введите номер предмета, который хотите продать:\n");
			player.setState(Player.State.awaitingSellArguments);
		}
		else
		{
			stringBuilder.append("⚠\t Ваш инвентарь пуст. Нет доступных вещей для продажи ");
		}

		sendMsg(player.getId(), stringBuilder.toString());
	}

	public void command_changeNickname(Player player)
	{
		long id = player.getId();
		Item i = itemDAO.getByNameFromCollection("\uD83D\uDCDD Тег");
		if (player.getInventory().getItems().contains(i))
		{
			player.setState(Player.State.awaitingChangeNickname);
			sendMsg(player.getId(), "Введите никнейм, на который вы хотите сменить: ");
		}
		else
		{
			sendMsg(id, String.format("Для смены ника нужен предмет `%s`\n\uD83D\uDED2 Его можно купить у других игроков в магазине или найти", i.getTitle()));
		}
	}

	public void command_coin(Player player)
	{
		long player_id = player.getId();
		if (player.getLevel() >= 4)
		{
			if (player.balance.value > 0)
			{
				sendMsg(player_id, "\uD83D\uDCB0 Ваш баланс: " + player.getMoney());
				player.setState(Player.State.coinDash);
				sendMsg(player_id, "\uD83C\uDFB0 Введите ставку: ");
			}
			else
			{
				sendMsg(player_id, "\uD83C\uDFB0 У вас недостаточно денег");
			}
		}
		else
		{
			sendMsg(player_id, "\uD83D\uDC7E Для игры в монетку нужен 4 уровень \n⚡ Повысить уровень можно за поиск предметов");
		}
	}


	public void command_touch(Player player)
	{
		Inventory inventory = player.getInventory();
		long id = player.getId();

		if (inventory.getInvSize() != 0)
		{
			StringBuilder sb = new StringBuilder("\uD83C\uDF81\t *Выберите предмет, который вы хотите осмотреть:* ");
			sb.append("\n");
			sb.append("========================\n");
			for (int i = 0; i < inventory.getInvSize(); i++)
			{
				sb.append(String.format("Предмет |%d| : %s\n", i, inventory.getItem(i).toString()));
			}
			sb.append("========================\n");
			player.setState(Player.State.touch);
			sendMsg(id, sb.toString());
		}
		else
		{
			sendMsg(id, "\uD83C\uDF81\t Потрогать не получится, так как инвентарь пуст ");
		}
	}

	public void command_me(Player player)
	{
		try
		{
			SendPhoto photo = new SendPhoto();
			photo.setPhoto(new InputFile(new File(".\\pics\\me.jpg")));
			photo.setChatId(player.getId());


			long player_id = player.getId();
			StringBuilder sb = new StringBuilder("*Информация о персонаже*\n\n");
			sb.append("Здесь показывается вся Ваша статистика и достижения\n\n");
			sb.append("⭐ Ваш ник: " + player.getUsername() + "\n\n");
			sb.append("\uD83D\uDCB0 Ваш баланс: " + player.getMoney() + "\n\n");
			sb.append("\uD83D\uDC8E Ваши булавки: *" + player.needle + "*\uD83E\uDDF7\n\n");
			sb.append("\uD83C\uDF20 Ваш GameID: " + player_id + "\n\n");
			sb.append(String.format("\uD83D\uDC7E Ваш уровень: %d (%d XP) \n", player.getLevel(), player.getXp()));
			sb.append("\uD83C\uDF3F Выпито кружек чая: " + player.stats.tea + "\n");
			sb.append("☕️ Выпито кружек кофе: " + player.stats.coffee + "\n");
			sb.append("\uD83C\uDFC6 Победы в монетке: " + player.stats.coinWins + "\n");
			sb.append("\uD83D\uDCC9 Проигрыши в монетке: " + player.stats.coinLosses + "\n");
			sb.append("\uD83C\uDF31 Посажено деревьев: " + player.stats.trees + "\n\n");

			Achievements a = new Achievements(player);

			this.execute(photo);
			sendMsg(player_id, sb + a.getTeaAch() + a.getCoffeeAch() + a.getTreesAch());
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
	}

	public void command_shopbuy(Player player)
	{
		long player_id = player.getId();
		int limitSpace;
		Item backpack = itemDAO.getByNameFromCollection("\uD83C\uDF92 Рюкзак");
		if (player.getInventory().getItems().contains(backpack))
		{
			limitSpace = 25;
		}
		else
		{
			limitSpace = 20;
		}

		if (player.getInventory().getInvSize() < limitSpace)
		{
			if (shopDAO.getAll().isEmpty())
			{
				sendMsg(player_id, "\uD83D\uDC40 В магазине пока нет товаров, чтобы разместить введите /shopplace\n");
			}
			else
			{
				StringBuilder sb = new StringBuilder("\uD83D\uDC5C Все предметы в магазине:\n\n");
				for (ShopItem i : shopDAO.getAll())
				{
					sb.append(String.format("\uD83C\uDFA9 Товар |# %d| `%s` | Цена: %s | Продавец: `%s` \n", i.getId(), i.getItem().getTitle(), i.getCost(), i.getSeller().getUsername()));
				}
				sb.append("\n");

				sendMsg(player_id, sb.toString());
				player.setState(Player.State.shopBuy);
				sendMsg(player_id, "Введите ID товара, который вы хотите купить: ");
			}
		}
		else
		{
			sendMsg(player.getId(), "⚠ В вашем инвентаре нет места");
		}
	}

	public void command_shopshow(Player player)
	{
		try
		{
			SendPhoto photo = new SendPhoto();
			photo.setPhoto(new InputFile(new File(".\\pics\\shop.jpg")));
			photo.setChatId(player.getId());

			long player_id = player.getId();

			if (shopDAO.getAll().isEmpty())
			{
				sendMsg(player_id, "\uD83D\uDC40 В магазине пока нет товаров, чтобы разместить введите /shopplace\n");
			}
			else
			{
				StringBuilder sb = new StringBuilder("\uD83D\uDCE1 Новости\n\nОфициальный телеграм канал: *@needlechat*\n\n");
				sb.append("\uD83D\uDC5C Все предметы в магазине:\n\n");
				//sb.append("=====================\n");
				for (ShopItem i : shopDAO.getAll())
				{
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
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
	}

	public void command_shopplace(Player player)
	{
		long player_id = player.getId();

		if (player.getInventory().getInvSize() == 0)
		{
			sendMsg(player_id, "Вы не можете ничего продать, так как Ваш инвентарь пуст");
		}
		else
		{
			player.setState(Player.State.shopPlaceGood_awaitingID);
			Inventory inventory = player.getInventory();

			StringBuilder sb = new StringBuilder("Предметы, доступные для продажи \n");
			sb.append("=====================\n");
			for (int i = 0; i < inventory.getInvSize(); i++)
			{
				sb.append(String.format("Предмет | %d |: ", i)).append(inventory.getItem(i)).append("\n");
			}
			sb.append("=====================\n");
			sendMsg(player_id, sb.toString());
			sendMsg(player_id, "Введите ID предмета, который хотите продать\n");
		}
	}


	public void command_case(Player player)
	{
		try
		{
			SendPhoto photo = new SendPhoto();
			photo.setPhoto(new InputFile(new File(".\\pics\\case.jpg")));
			photo.setChatId(player.getId());

			int casesCounter = 0;
			int keysCounter = 0;
			long id = player.getId();

			StringBuilder sb = new StringBuilder("*Открытие кейсов*\n\n");

			Item _case = itemDAO.getByNameFromCollection("\uD83D\uDCE6 Кейс Gift");
			Item _key = itemDAO.getByNameFromCollection("\uD83D\uDD11 Ключ от кейса");

			for (Item item : player.getInventory().getItems())
			{
				if (item.equals(_case))
				{
					casesCounter++;
				}
				if (item.equals(_key))
				{
					keysCounter++;
				}
			}

			sb.append("В кейсах могут выпадать различные предметы редкости `Gift` и `Rare`\n");
			sb.append("Кейсы можно скрафтить из `Отвертки` и `Подшипника` в разделе `\uD83E\uDD65 Рецепты`\n");
			sb.append("Чтобы открыть кейс нужен предмет `\uD83D\uDD11 Ключ от кейса`\n\n");

			sb.append(String.format("\uD83D\uDCE6 Кейсы: %d\n", casesCounter));
			sb.append(String.format("\uD83D\uDD11 Ключи: %d\n", keysCounter));
			sb.append("\n\n");

			sb.append("Введите /open чтобы открыть кейс: \n");
			this.execute(photo);
			sendMsg(id, sb.toString());
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
	}

	public void command_open(Player player)
	{
		Random ran = new Random();
		long id = player.getId();
		Item _case = itemDAO.getByNameFromCollection("\uD83D\uDCE6 Кейс Gift");
		Item _key = itemDAO.getByNameFromCollection("\uD83D\uDD11 Ключ от кейса");

		List<Item> loot;

		loot = itemDAO.getAllFromCollection().stream().filter((item -> item.getRarity().equals(ItemRarity.Gift) ||
				item.getRarity().equals(ItemRarity.Rare))).collect(Collectors.toList());

		int ranIndex = ran.nextInt(loot.size());

		Inventory inventory = player.getInventory();
		List<Item> player_items = inventory.getItems();
		int case_idx = player_items.indexOf(_case);
		int key_idx = player_items.indexOf(_key);
		if (case_idx != -1)  // player has case
		{
			if (key_idx != -1)  // player has key
			{
				Item prize = loot.get(ranIndex);
				sendMsg(id, String.format("\uD83C\uDF89 Ура! Вам выпал предмет: `%s`", prize.getTitle()));
				inventoryDAO.putItem(id, prize.getId());
				inventory.putItem(prize);

				inventoryDAO.delete(player.getId(), _key.getId(), 1);
				inventory.removeItem(key_idx);

				inventoryDAO.delete(player.getId(), _case.getId(), 1);
				inventory.removeItem(case_idx);

				dump_database();
				dump_database();
			}
			else
			{
				sendMsg(id, "У вас нет ключей");
			}
		}
		else
		{
			sendMsg(id, "У вас нет кейсов");
		}
	}

	public void command_pay(Player player)
	{
		if (player.balance.value <= 0)
		{
			sendMsg(player.getId(), "У вас нет денег для перевода");
		}
		else
		{
			player.setState(Player.State.payAwaitingNickname);
			sendMsg(player.getId(), "Введите ник игрока:");
		}
	}

	public void command_give(Player player){
		if (player.getId() == 501446180 || player.getId() == 684744711){
			sendMsg(player.getId(), "Введите название предмета, который вы хотите получить: ");
			player.setState(Player.State.giveID);

		}else{
			sendMsg(player.getId(), "У вас недостаточно прав для этой комманды");
		}

	}

	public void command_coffee(Player player)
	{
		int goal;
		Item cup = itemDAO.getByNameFromCollection("☕ Чашка 'Египет'");
		if (player.getInventory().getItems().contains(cup))
		{
			goal = 200;
		}
		else
		{
			goal = 500;
		}

		if (player.getMoney().value < goal)
		{
			sendMsg(player.getId(), "☕ Не хватает деняк на кофе :'(");
		}
		else
		{
			player.setState(Player.State.awaitingCoffee);
			sendMsg(player.getId(), String.format("☕($%d) Введите ник игрока: ", goal));
		}
	}

	public void command_tea(Player player)
	{
		int goal;
		Item cup = itemDAO.getByNameFromCollection("☕ Чашка 'Египет'");
		if (player.getInventory().getItems().contains(cup))
		{
			goal = 200;
		}
		else
		{
			goal = 500;
		}

		if (player.getMoney().value < goal)
		{
			sendMsg(player.getId(), "\uD83C\uDF3F Не хватает деняк на чай :'(");
		}
		else
		{
			player.setState(Player.State.awaitingTea);
			sendMsg(player.getId(), String.format("\uD83C\uDF3F($%d) Введите ник игрока: ", goal));
		}
	}

	public void command_start_already_registered(Player player)
	{
		sendMsg(player.getId(), "Вы уже зарегистрированы.\n");
	}

	public void command_previous(Player player)
	{
		if (player.page > 0)
		{
			player.page--;
		}
		sendMsg(player.getId(), String.format("Страница команд №%d", player.page + 1));
	}

	public void command_next(Player player)
	{
		if (player.page < paginator.size - 1)
		{
			player.page++;
		}
		sendMsg(player.getId(), String.format("Страница команд №%d", player.page + 1));
	}

	public void cleanShopFromExpired()
	{
		List<ShopItem> shopItems = shopDAO.expire();
		for (ShopItem shopItem : shopItems)
		{
			Player seller = shopItem.getSeller();
			long seller_id = seller.getId();
			inventoryDAO.putItem(seller_id, shopItem.getItem().getId());
			sendMsg(seller_id, String.format("Ваш товар %sбыл снят с продажи, предмет добавлен в ваш инвентарь", shopItem));
		}
	}

	void sendFindCooldownNotification()
	{
		List<Long> expires = abilityDAO.expireFind();
		for (long id : expires)
		{
			System.out.printf("Find expiration notification fired for player %s\n", playerDAO.get_by_id(id).getUsername());
			sendMsg(id, "⭐ Вы снова можете искать редкие предметы!");
			playerDAO.get_by_id(id).findExpiration = null;
		}
	}

	public void dump_database()
	{
		System.out.println("Dump database fired");
		if (playerDAO instanceof CachedPlayerDAO)
		{
			CachedPlayerDAO cpd = (CachedPlayerDAO) playerDAO;
			cpd.dump();
			System.out.printf("Active players:\n%s\n", cpd.cached_players().stream().map(Player::getUsername).collect(Collectors.toList()));
			System.out.println("Database dumped");
		}
	}

	public void on_closing()
	{
		System.out.println("Exiting...");
		sf_dump.cancel(false);
		sf_find.cancel(false);
		sf_timers.cancel(false);
		STPE.stpe.shutdown();
		dump_database();
		System.out.println("Goodbye!");
	}

	public void level_up_notification(Player player)
	{
		long fee = 1375L * player.getLevel();
		sendMsg(player.getId(), String.format("\uD83C\uDF88 Поздравляем! Вы перешли на новый уровень (Уровень %d)\n\uD83C\uDF81 Бонус за переход на новый уровень +%s",
				player.getLevel(), new Money(fee)));
		try
		{
			player.balance.transfer(fee);
		}
		catch (Money.MoneyException e)
		{
			e.printStackTrace();
			sendMsg(player.getId(), e.getMessage());
		}
	}

	public void achievement_notification(Player player, String congraText, int fee)
	{
		sendMsg(player.getId(), "✨" + congraText + " | + " + new Money(fee));
		try
		{
			player.balance.transfer(fee);
		}
		catch (Money.MoneyException e)
		{
			e.printStackTrace();
			sendMsg(player.getId(), e.getMessage());
		}
		playerDAO.update(player);
	}

	public String getBotUsername()
	{
		return "Needle";
	}

	private String init_token() throws FileNotFoundException
	{
		Scanner scanner = new Scanner(new File("token"));
		return scanner.nextLine();
	}

	public String getBotToken()
	{
		return token;
	}

	private void coin_dash_callback(Player player, int i_dash)
	{
		long player_id = player.getId();
		CoinGame coinGame = new CoinGame(i_dash);
		if (coinGame.roll())
		{
			sendMsg(player_id, "\uD83D\uDCB0 Вы выиграли " + new Money(i_dash));
			coinGame.coinWin(player, i_dash);
			player.addXp(1);
			player.stats.coinWins++;
		}
		else
		{
			sendMsg(player_id, "❌ Вы проиграли " + new Money(i_dash));
			coinGame.coinLose(player, i_dash);

			player.stats.coinLosses++;
		}

		player.setState(Player.State.awaitingCommands);
		sendMsg(player_id, "Ваш баланс: " + player.balance + " \uD83D\uDCB2");
	}
}
