package commands;

import database.dao.*;
import main.ActiveDuelPairs;
import main.Item;
import main.Roller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CommandProcessor
{
	public static final String SHOPSHOW_BUTTON = "\uD83D\uDED2 Маркет";
	public static final String SHOP_BUTTON = "\uD83C\uDFEA Магазин 24/7";
	public static final String SELL_BUTTON = "\uD83D\uDCDE Скупщик";
	public static final String TOP_BUTTON = "\uD83C\uDF80 Топ 10";
	public static final String POCKETS_BUTTON = "\uD83E\uDDF6 Проверить карманы";
	public static final String MUD_BUTTON = "\uD83D\uDD26 Рыться в грязи";
	public static final String FIND_BUTTON = "\uD83D\uDC8E Искать редкие предметы";
	public static final String INV_BUTTON = "\uD83C\uDF92 Инвентарь";
	public static final String ME_BUTTON = "⭐️ Персонаж";
	public static final String HELP_BUTTON = "\uD83C\uDF3A Помощь";
	public static final String COIN_BUTTON = "\uD83D\uDCB0 Монетка";
	public static final String DROP_BUTTON = "\uD83D\uDEE0 Продать Cheap";
	public static final String SELLFISH_BUTTON = "\uD83E\uDD88 Сдать рыбу";
	public static final String FISH_BUTTON = "🐡 Рыбачить";
	public static final String PREVIOUS_BUTTON = "◀️ Назад";
	public static final String BACK_BUTTON = "\uD83D\uDD19 Назад";
	public static final String NEXT_BUTTON = "▶️ Вперед";
	public static final String INFO_BUTTON = "ℹ️Как играть";
	public static final String PAY_BUTTON = "\uD83D\uDCB3 Передать деньги";
	public static final String COFFEE_BUTTON = "☕️Кофе";
	public static final String TEA_BUTTON = "\uD83C\uDF3F Чай";
	public static final String FOREST_BUTTON = "\uD83C\uDF33 Садить деревья";
	public static final String CASE_BUTTON = "\uD83D\uDD11 Кейсы";
	public static final String RENAME_BUTTON = "\uD83D\uDC80 Сменить ник";
	public static final String SHOPPLACE_BUTTON = "\uD83D\uDED2 Разместить товар";
	public static final String CHECK_BUTTON = "\uD83D\uDC41 Осмотреть игрока";
	public static final String CAPITALGAME_BUTTON = "\uD83C\uDDFA\uD83C\uDDE6 Столицы";
	public static final String DRINKS_BUTTON = "\uD83E\uDDC3 Напитки";
	public static final String RECIPES_BUTTON = "\uD83E\uDD65 Рецепты";
	public static final String CANCEL_BUTTON = "\uD83D\uDEAB Отменить";
	public static final String DONATE_BUTTON = "\uD83D\uDC8E Донат";
	public static final String STATUS_BUTTON = "✨ Статусы";
	public static final String STATS_BUTTON = "\uD83D\uDCCA Статистика";
	public static final String DUELS_BUTTON = "\uD83C\uDFF9 Дуэли";
	public static final String MYPET_BUTTON = "\uD83D\uDC36 Мой Питомец";
	public static final String SETTINGS_BUTTON = "⚙ Настройки";
	public static final String TOUCH_BUTTON = "\uD83C\uDFA8 Осмотреть";
	public static final String SKATE_BUTTON = "\uD83D\uDEF9 Катать на скейте";
	public static final String PHONE_BUTTON = "\uD83D\uDCF1 Использовать телефон";
	public static final String TRAVEL_BUTTON = "\uD83C\uDFCE Искать питомцев";
	public static final String MINE_BUTTON = "⛏ Работать в шахте";
	public static final String SELLORE_BUTTON = "\uD83E\uDEA8 Сдать руду";
	IItemDAO itemDAO;
	InventoryDAO inventoryDAO;
	IPlayerDAO playerDAO;
	ShopDAO shopDAO;
	ContainerDAO containerDAO;
	Roller<Item> find_roller;
	Roller<Item> mud_roller;
	Roller<Item> fish_roller;
	Roller<Integer> pockets_roller;

	public Map<String, Supplier<Command>> map;

	public CommandProcessor(IItemDAO itemDAO, InventoryDAO inventoryDAO, IPlayerDAO playerDAO, ShopDAO shopDAO, Roller<Item> find_roller, Roller<Item> mud_roller, Roller<Item> fish_roller, Roller<Integer> pockets_roller, main.Capitalgame capitalgame, ActiveDuelPairs activeDuelPairs, ContainerDAO containerDAO)
	{
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
		this.playerDAO = playerDAO;
		this.shopDAO = shopDAO;
		this.find_roller = find_roller;
		this.mud_roller = mud_roller;
		this.fish_roller = fish_roller;
		this.pockets_roller = pockets_roller;
		this.containerDAO = containerDAO;

		map = new HashMap<>();

		map.put("/give", () -> new Give(itemDAO, inventoryDAO));
		map.put("/mypet", () -> new Mypet(itemDAO));
		map.put(MYPET_BUTTON, () -> new Mypet(itemDAO));

		map.put("/stats", () -> new Stats());
		map.put("/shop", () -> new Shop(itemDAO, inventoryDAO, playerDAO));
		map.put(SHOP_BUTTON, () -> new Shop(itemDAO, inventoryDAO, playerDAO));
		map.put(STATS_BUTTON, () -> new Stats());

		map.put("/settings", () -> new Settings(playerDAO));
		map.put(SETTINGS_BUTTON, () -> new Settings(playerDAO));
		map.put("/duel", () -> new Duel(itemDAO, playerDAO,activeDuelPairs));
		map.put(DUELS_BUTTON, () -> new Duel(itemDAO, playerDAO,activeDuelPairs));

		//обернуть бы их в кнопки
		map.put("/accept", () -> new Accept(inventoryDAO,playerDAO,activeDuelPairs));
		map.put("/decline", () -> new Decline(activeDuelPairs));
		map.put("/needle", () -> new Needle(playerDAO));
		map.put("/broadcast", () -> new Broadcast(playerDAO));
		map.put("/online", () -> new Online(playerDAO));
		map.put("/skate", () -> new Skate(playerDAO, itemDAO));
		map.put(SKATE_BUTTON, () -> new Skate(playerDAO, itemDAO));
		map.put(PHONE_BUTTON, () -> new Phone(itemDAO));
		map.put("/phone", () -> new Phone(itemDAO));
		map.put("/phone", () -> new Phone(itemDAO));
		map.put("/travel", () -> new Travel(itemDAO, playerDAO, inventoryDAO));
		map.put(TRAVEL_BUTTON, () -> new Travel(itemDAO, playerDAO, inventoryDAO));
		map.put(MINE_BUTTON, () -> new Mine(itemDAO, inventoryDAO));
		map.put("/mine", () -> new Mine(itemDAO, inventoryDAO));
		map.put("/giveitem", () -> new Giveitem(playerDAO,itemDAO, inventoryDAO));
		map.put("/sellore", () -> new Sellore(inventoryDAO));
		map.put(SELLORE_BUTTON, () -> new Sellore(inventoryDAO));
		map.put("/contput", () -> new Contput(inventoryDAO,itemDAO,containerDAO));
		map.put("/cont", () -> new Cont(containerDAO, itemDAO));
		map.put("/contget", () -> new Contget(containerDAO, itemDAO, inventoryDAO));
		map.put("Тишина", () -> new Silence());
		map.put("Карусель", () -> new Carousel());
		map.put("While I watch you", () -> new WhileIWatchYou());



		map.put("/status", () -> new Status(inventoryDAO, itemDAO));
		map.put(STATUS_BUTTON, () -> new Status(inventoryDAO, itemDAO));

		map.put("/me", () -> new Me());
		map.put(ME_BUTTON, () -> new Me());

		map.put("/help", () -> new Help());
		map.put(HELP_BUTTON, () -> new Help());

		map.put("/info", () -> new Info());
		map.put(INFO_BUTTON, () -> new Info());

		map.put("/inv", () -> new Inv(itemDAO));
		map.put(INV_BUTTON, () -> new Inv(itemDAO));

		map.put("/find", () -> new Find(find_roller, itemDAO, inventoryDAO));
		map.put(FIND_BUTTON, () -> new Find(find_roller, itemDAO, inventoryDAO));

		map.put("/mud", () -> new Mud(mud_roller, itemDAO, inventoryDAO));
		map.put(MUD_BUTTON, () -> new Mud(mud_roller, itemDAO, inventoryDAO));

		map.put("/pockets", () -> new Pockets(pockets_roller));
		map.put(POCKETS_BUTTON, () -> new Pockets(pockets_roller));

		map.put("/top", () -> new Top(playerDAO, itemDAO));
		map.put(TOP_BUTTON, () -> new Top(playerDAO, itemDAO));

		map.put("/sell", () -> new Sell(inventoryDAO));
		map.put(SELL_BUTTON, () -> new Sell(inventoryDAO));

		map.put("/rename", () -> new Rename(playerDAO, itemDAO, inventoryDAO));
		map.put(RENAME_BUTTON, () -> new Rename(playerDAO, itemDAO, inventoryDAO));

		map.put("/coin", () -> new Coin());
		map.put(COIN_BUTTON, () -> new Coin());

		map.put("/pay", () -> new Pay(playerDAO));
		map.put(PAY_BUTTON, () -> new Pay(playerDAO));

		map.put("/shopshow", () -> new Shopshow(shopDAO));
		map.put(SHOPSHOW_BUTTON, () -> new Shopshow(shopDAO));

		map.put("/shopbuy", () -> new Shopbuy(shopDAO, itemDAO, inventoryDAO));

		map.put("/shopplace", () -> new Shopplace(itemDAO, inventoryDAO, shopDAO));
		map.put(SHOPPLACE_BUTTON, () -> new Shopplace(itemDAO, inventoryDAO, shopDAO));

		map.put("/tea", () -> new Tea(itemDAO, playerDAO, inventoryDAO));
		map.put(TEA_BUTTON, () -> new Tea(itemDAO, playerDAO, inventoryDAO));

		map.put("/coffee", () -> new Coffee(itemDAO, playerDAO, inventoryDAO));
		map.put(COFFEE_BUTTON, () -> new Coffee(itemDAO, playerDAO, inventoryDAO));

		map.put("/forest", () -> new Forest(itemDAO, inventoryDAO));
		map.put(FOREST_BUTTON, () -> new Forest(itemDAO, inventoryDAO));

		map.put("/fish", () -> new Fish(fish_roller, itemDAO, inventoryDAO));
		map.put(FISH_BUTTON, () -> new Fish(fish_roller, itemDAO, inventoryDAO));

		map.put("/sellfish", () -> new Sellfish(inventoryDAO));
		map.put(SELLFISH_BUTTON, () -> new Sellfish(inventoryDAO));

		map.put("/drop", () -> new Drop(inventoryDAO));
		map.put(DROP_BUTTON, () -> new Drop(inventoryDAO));

		map.put("Пирожок", () -> new Bonus());

		map.put("/case", () -> new Case(itemDAO));
		map.put(CASE_BUTTON, () -> new Case(itemDAO));

		map.put("/open", () -> new Open(itemDAO, inventoryDAO, playerDAO));

		map.put("/touch", () -> new Touch(inventoryDAO, itemDAO, playerDAO));
		map.put(TOUCH_BUTTON, () -> new Touch(inventoryDAO, itemDAO, playerDAO));

		map.put("/check", () -> new Check(playerDAO));
		map.put(CHECK_BUTTON, () -> new Check(playerDAO));

		map.put("/capitalgame", () -> new Capitalgame(capitalgame));
		map.put(CAPITALGAME_BUTTON, () -> new Capitalgame(capitalgame));

		map.put("/previous", () -> new Previous());
		map.put(PREVIOUS_BUTTON, () -> new Previous());

		map.put("/next", () -> new Next());
		map.put(NEXT_BUTTON, () -> new Next());

		map.put("/recipes", () -> new Recipes(inventoryDAO));
		map.put(RECIPES_BUTTON, () -> new Recipes(inventoryDAO));

		map.put("/donate", () -> new Donate());
		map.put(DONATE_BUTTON, () -> new Donate());

		map.put("/boost", () -> new Boost());

	}

	public Command get(String command)
	{
		return map.get(command).get();
	}

	public static List<String> cancel_only()
	{
		List<String> list = new ArrayList<>(1);
		list.add(CANCEL_BUTTON);
		return list;
	}

	public static List<String> back_cancel()
	{
		List<String> list = new ArrayList<>(2);
		list.add("BACK");
		list.add(CANCEL_BUTTON);
		return list;
	}
}
