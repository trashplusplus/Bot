package main;

import commands.BaseState;
import commands.Command;
import commands.CommandProcessor;
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
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static commands.CommandProcessor.*;


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
	public static final Capitalgame capitalgame = new Capitalgame();


	ScheduledFuture<?> sf_timers;
	ScheduledFuture<?> sf_find;
	public final long expStepS = 5L;
	ScheduledFuture<?> sf_pockets;
	ScheduledFuture<?> sf_dump;
	public final long dump_timer_s = 120L;

	public final long findCooldown = 20L * 60L * 1000L;
	public final long pocketsCooldown = 30L * 1000L;

	private final String token;

	public KeyboardPaginator base_paginator;
	public KeyboardPaginator back_cancel_paginator;
	public KeyboardPaginator cancel_paginator;

	public CommandProcessor command_processor;


	public Bot(Connection connection) throws FileNotFoundException
	{
		playerDAO = new CachedPlayerDAO(connection, this);
		inventoryDAO = new InventoryDAO(connection);
		itemDAO = new ItemDAO(connection);
		shopDAO = new ShopDAO(connection, this);
		statsDAO = new StatsDAO(connection);
		abilityDAO = new AbilityDAO(connection, this);
		token = init_token();
		command_processor = new CommandProcessor(itemDAO, inventoryDAO, playerDAO, shopDAO, findRoller, mudRoller, fishRoller, moneyRoller, capitalgame);
		sf_timers = STPE.stpe.scheduleAtFixedRate(this::cleanShopFromExpired, 0L, 5L, TimeUnit.SECONDS);
		sf_find = STPE.stpe.scheduleAtFixedRate(this::sendFindCooldownNotification, 0L, expStepS, TimeUnit.SECONDS);
		sf_pockets = STPE.stpe.scheduleAtFixedRate(abilityDAO::expirePockets, 0L, expStepS, TimeUnit.SECONDS);  // remove this shit
		sf_dump = STPE.stpe.scheduleAtFixedRate(this::dump_database, dump_timer_s, dump_timer_s, TimeUnit.SECONDS);
		base_paginator = new KeyboardPaginator()
				.first(INV_BUTTON, HELP_BUTTON, ME_BUTTON, FIND_BUTTON, MUD_BUTTON, POCKETS_BUTTON, DROP_BUTTON, SHOPSHOW_BUTTON, SELL_BUTTON)
				.then(TOP_BUTTON, FISH_BUTTON, COIN_BUTTON, "/важная кнопк", CAPITALGAME_BUTTON, CASE_BUTTON, FOREST_BUTTON, TEA_BUTTON, COFFEE_BUTTON)
				.last(PAY_BUTTON, INFO_BUTTON, RENAME_BUTTON, SHOPPLACE_BUTTON, CHECK_BUTTON, SELLFISH_BUTTON, RECIPES_BUTTON, DONATE_BUTTON);
		back_cancel_paginator = new KeyboardPaginator().collect(BACK_BUTTON, CANCEL_BUTTON);
		cancel_paginator = new KeyboardPaginator().collect(CANCEL_BUTTON);

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
			if (player.state instanceof BaseState || player.state == null)
			{
				replyKeyboardMarkup.setKeyboard(base_paginator.get(player.page));
			}
			else
			{
				if (player.state.previous == null)
				{
					replyKeyboardMarkup.setKeyboard(cancel_paginator.get(0));
				}
				else
				{
					replyKeyboardMarkup.setKeyboard(back_cancel_paginator.get(0));
				}
			}

		}
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
	}

	@Override
	public void onUpdateReceived(Update update)
	{
		try
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
					if (player.state == null)
					{
						player.state = new BaseState(this, player);
					}
					if (player.state instanceof BaseState)
					{
						try
						{
							Command cmd = command_processor.get(text);
							cmd.consume(this, player);
						}
						catch (NullPointerException ex)
						{
							ex.printStackTrace();
							sendMsg(id, "Неизвестная команда");
						}
					}
					else
					{
						if (text.equals("/cancel") || text.equals(CANCEL_BUTTON))
						{
							player.state = new BaseState(this, player);
							sendMsg(id, player.state.hint);
						}
						else if (text.equals("/back") || text.equals(BACK_BUTTON))
						{
							if (player.state.previous != null)
							{
								player.state = player.state.previous;
								sendMsg(id, player.state.hint);
							}
							else
							{
								sendMsg(id, "Эту команду нельзя использовать в данном контексте");
							}
						}
						else
						{
							player.state.process(text);
						}
					}
				}
			}
			else
			{
				System.out.println(message);
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}


	public SendPhoto getPhoto(String path, Player player)
	{
		SendPhoto photo = new SendPhoto();
		photo.setPhoto(new InputFile(new File(path)));
		photo.setChatId(player.getId());
		return photo;
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
}
