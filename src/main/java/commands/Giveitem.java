package commands;

import database.dao.IItemDAO;
import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import main.Bot;
import main.Item;
import main.Money;
import main.Player;

public class Giveitem extends Command
{
    IPlayerDAO playerDAO;
    IItemDAO itemDAO;
    InventoryDAO inventoryDAO;
    public Giveitem(IPlayerDAO playerDAO, IItemDAO itemDAO, InventoryDAO inventoryDAO)
    {
        this.playerDAO = playerDAO;
        this.itemDAO = itemDAO;
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public void consume(Bot host, Player player)
    {
        if(player.getId() == 501446180){
            player.state = new Giveitem1(player, player.state.base, playerDAO, host, itemDAO, inventoryDAO);
            host.sendMsg(player.getId(), player.state.hint);
        }else{
            host.sendMsg(player.getId(), "\uD83D\uDEAB У вас недостаточно прав для этой команды");
        }


    }
}

class Giveitem1 extends State
{
    Player invoker;
    IPlayerDAO playerDAO;
    Bot host;
    IItemDAO itemDAO;
    InventoryDAO inventoryDAO;
    public Giveitem1(Player invoker, BaseState base, IPlayerDAO playerDAO, Bot host, IItemDAO itemDAO, InventoryDAO inventoryDAO)
    {
        this.invoker = invoker;
        this.base = base;
        this.playerDAO = playerDAO;
        this.host = host;
        this.itemDAO = itemDAO;
        this.inventoryDAO = inventoryDAO;
        hint = "\uD83E\uDDF7 Введите ник получателя:";
    }

    @Override
    public void process(String arg)
    {
        long player_id = invoker.getId();


        Player acceptor = playerDAO.get_by_name(arg);
        if (acceptor != null)
        {
            invoker.state = new Giveitem2(invoker, host, acceptor, this, base, itemDAO, inventoryDAO);
            host.sendMsg(player_id, invoker.state.hint);
        }
        else
        {
            host.sendMsg(player_id, "Такого игрока не существует");
        }


    }
}

class Giveitem2 extends State
{
    Player invoker;
    Bot host;
    Player acceptor;
    IItemDAO itemDAO;
    InventoryDAO inventoryDAO;
    public Giveitem2(Player invoker, Bot host, Player acceptor, State previous, BaseState base,IItemDAO itemDAO, InventoryDAO inventoryDAO)
    {
        this.invoker = invoker;
        this.host = host;
        this.acceptor = acceptor;
        this.previous = previous;
        this.base = base;
        this.itemDAO = itemDAO;
        this.inventoryDAO = inventoryDAO;
        hint = "\uD83E\uDDF7 Введите предмет: ";
    }

    @Override
    public void process(String arg)
    {
        try
        {
            Item prize = itemDAO.get_by_name(arg);
            acceptor.inventory.putItem(prize);
            inventoryDAO.putItem(acceptor.getId(), prize.getId());

            invoker.state = invoker.state.base;

            host.sendMsg(acceptor.getId(), String.format("\uD83E\uDDF7 Вам выдан предмет: %s", prize));
            host.sendMsg(invoker.getId(), "\uD83E\uDDF7 Предмет успешно выдан");

        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            host.sendMsg(invoker.getId(), "⚠\t Вы ввели некорректный предмет");
        }

    }
}
