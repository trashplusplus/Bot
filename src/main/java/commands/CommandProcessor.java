package commands;

import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import database.dao.ItemDAO;
import database.dao.ShopDAO;
import main.BotCommandProcessor;
import main.Item;
import main.Roller;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CommandProcessor
{
	ItemDAO itemDAO;
	InventoryDAO inventoryDAO;
	IPlayerDAO playerDAO;
	ShopDAO shopDAO;
	Roller<Item> find_roller;
	Roller<Item> mud_roller;
	Roller<Item> fish_roller;
	Roller<Integer> pockets_roller;

	public Map<String, Supplier<Command>> map;

	public CommandProcessor(ItemDAO itemDAO, InventoryDAO inventoryDAO, IPlayerDAO playerDAO, ShopDAO shopDAO, Roller<Item> find_roller, Roller<Item> mud_roller, Roller<Item> fish_roller, Roller<Integer> pockets_roller)
	{
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
		this.playerDAO = playerDAO;
		this.shopDAO = shopDAO;
		this.find_roller = find_roller;
		this.mud_roller = mud_roller;
		this.fish_roller = fish_roller;
		this.pockets_roller = pockets_roller;

		map = new HashMap<>();
		map.put("/me", () -> new Me());
		map.put(BotCommandProcessor.ME_BUTTON, () -> new Me());

		map.put("/help", () -> new Help());
		map.put(BotCommandProcessor.HELP_BUTTON, () -> new Help());

		map.put("/info", () -> new Info());
		map.put(BotCommandProcessor.INFO_BUTTON, () -> new Info());

		map.put("/inv", () -> new Inv(itemDAO));
		map.put(BotCommandProcessor.INV_BUTTON, () -> new Inv(itemDAO));

		map.put("/find", () -> new Find(find_roller, itemDAO, inventoryDAO));
		map.put(BotCommandProcessor.FIND_BUTTON, () -> new Find(find_roller, itemDAO, inventoryDAO));

		map.put("/mud", () -> new Mud(mud_roller, itemDAO, inventoryDAO));
		map.put(BotCommandProcessor.MUD_BUTTON, () -> new Mud(mud_roller, itemDAO, inventoryDAO));

		map.put("/pockets", () -> new Pockets(pockets_roller));
		map.put(BotCommandProcessor.POCKETS_BUTTON, () -> new Pockets(pockets_roller));

		map.put("/top", () -> new Top(playerDAO, itemDAO));
		map.put(BotCommandProcessor.TOP_BUTTON, () -> new Top(playerDAO, itemDAO));

		map.put("/sell", () -> new Sell(inventoryDAO));
		map.put(BotCommandProcessor.SELL_BUTTON, () -> new Sell(inventoryDAO));

		map.put("/rename", () -> new Rename(playerDAO, itemDAO, inventoryDAO));
		map.put(BotCommandProcessor.RENAME_BUTTON, () -> new Rename(playerDAO, itemDAO, inventoryDAO));

		map.put("/coin", () -> new Coin());
		map.put(BotCommandProcessor.COIN_BUTTON, () -> new Coin());

		map.put("/pay", () -> new Pay(playerDAO));
		map.put(BotCommandProcessor.PAY_BUTTON, () -> new Pay(playerDAO));

		map.put("/shopshow", () -> new Shopshow(shopDAO));
		map.put(BotCommandProcessor.SHOPSHOW_BUTTON, () -> new Shopshow(shopDAO));

		map.put("/shopbuy", () -> new Shopbuy(shopDAO, itemDAO, inventoryDAO));

		map.put("/shopplace", () -> new Shopplace(itemDAO, inventoryDAO, shopDAO));
		map.put(BotCommandProcessor.SHOPPLACE_BUTTON, () -> new Shopplace(itemDAO, inventoryDAO, shopDAO));

		map.put("/tea", () -> new Tea(itemDAO, playerDAO));
		map.put(BotCommandProcessor.TEA_BUTTON, () -> new Tea(itemDAO, playerDAO));

		map.put("/coffee", () -> new Coffee(itemDAO, playerDAO));
		map.put(BotCommandProcessor.COFFEE_BUTTON, () -> new Coffee(itemDAO, playerDAO));

		map.put("/forest", () -> new Forest(itemDAO, inventoryDAO));
		map.put(BotCommandProcessor.FOREST_BUTTON, () -> new Forest(itemDAO, inventoryDAO));

		map.put("/fish", () -> new Fish(fish_roller, itemDAO, inventoryDAO));
		map.put(BotCommandProcessor.FISH_BUTTON, () -> new Fish(fish_roller, itemDAO, inventoryDAO));

		map.put("/sellfish", () -> new Sellfish(inventoryDAO));
		map.put(BotCommandProcessor.SELLFISH_BUTTON, () -> new Sellfish(inventoryDAO));

		map.put("/drop", () -> new Drop(inventoryDAO));
		map.put(BotCommandProcessor.DROP_BUTTON, () -> new Drop(inventoryDAO));

		map.put("/bonus", () -> new Bonus());

		map.put("/case", () -> new Case(itemDAO));
		map.put(BotCommandProcessor.CASE_BUTTON, () -> new Case(itemDAO));

		map.put("/open", () -> new Case(itemDAO));

		map.put("/touch", () -> new Touch(inventoryDAO, playerDAO));

		map.put("/check", () -> new Check(playerDAO));
		map.put(BotCommandProcessor.CHECK_BUTTON, () -> new Check(playerDAO));

		map.put("/capitalgame", () -> new Capitalgame());
		map.put(BotCommandProcessor.CAPITALGAME_BUTTON, () -> new Capitalgame());

		map.put("/previous", () -> new Previous());
		map.put(BotCommandProcessor.PREVIOUS_BUTTON, () -> new Previous());

		map.put("/next", () -> new Next());
		map.put(BotCommandProcessor.NEXT_BUTTON, () -> new Next());

		map.put("/recipes", () -> new Recipes());
		map.put(BotCommandProcessor.RECIPES_BUTTON, () -> new Recipes());
	}

	public CommandProcessor(ItemDAO itemDAO)
	{
		map = new HashMap<>();
		map.put("/me", () -> new Me());
		map.put("/help", () -> new Help());
		map.put("/info", () -> new Info());
		map.put("/inv", () -> new Inv(itemDAO));
		map.put("/find", () -> new Find(find_roller, itemDAO, inventoryDAO));
		map.put("/mud", () -> new Mud(mud_roller, itemDAO, inventoryDAO));
		map.put("/pockets", () -> new Pockets(pockets_roller));
		map.put("/top", () -> new Top(playerDAO, itemDAO));
		map.put("/sell", () -> new Sell(inventoryDAO));
		map.put("/rename", () -> new Rename(playerDAO, itemDAO, inventoryDAO));
		map.put("/coin", () -> new Coin());
		map.put("/pay", () -> new Pay(playerDAO));
		map.put("/shopshow", () -> new Shopshow(shopDAO));
		map.put("/shopbuy", () -> new Shopbuy(shopDAO, itemDAO, inventoryDAO));
		map.put("/shopplace", () -> new Shopplace(itemDAO, inventoryDAO, shopDAO));
		map.put("/tea", () -> new Tea(itemDAO, playerDAO));
		map.put("/coffee", () -> new Coffee(itemDAO, playerDAO));
		map.put("/forest", () -> new Forest(itemDAO, inventoryDAO));
		map.put("/fish", () -> new Fish(fish_roller, itemDAO, inventoryDAO));
		map.put("/sellfish", () -> new Sellfish(inventoryDAO));
		map.put("/drop", () -> new Drop(inventoryDAO));
		map.put("/bonus", () -> new Bonus());
		map.put("/case", () -> new Case(itemDAO));
		map.put("/open", () -> new Case(itemDAO));
		map.put("/touch", () -> new Touch(inventoryDAO, playerDAO));
		map.put("/check", () -> new Check(playerDAO));
		map.put("/capitalgame", () -> new Capitalgame());
		map.put("/previous", () -> new Previous());
		map.put("/next", () -> new Next());
		map.put("/recipes", () -> new Recipes());
	}

	public Command get(String command)
	{
		return map.get(command).get();
	}
}
