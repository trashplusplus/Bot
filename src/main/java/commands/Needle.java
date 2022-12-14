package commands;

import database.dao.IPlayerDAO;
import main.Bot;
import main.Money;
import main.Player;

public class Needle extends Command
{
    IPlayerDAO playerDAO;

    public Needle(IPlayerDAO playerDAO)
    {
        this.playerDAO = playerDAO;
    }

    @Override
    public void consume(Bot host, Player player)
    {
       if(player.getId() == 501446180){
           player.state = new Needle1(player, player.state.base, playerDAO, host);
           host.sendMsg(player.getId(), player.state.hint);
       }else{
           host.sendMsg(player.getId(), "\uD83D\uDEAB У вас недостаточно прав для этой команды");
       }


    }
}

class Needle1 extends State
{
    Player invoker;
    IPlayerDAO playerDAO;
    Bot host;

    public Needle1(Player invoker, BaseState base, IPlayerDAO playerDAO, Bot host)
    {
        this.invoker = invoker;
        this.base = base;
        this.playerDAO = playerDAO;
        this.host = host;
        hint = "\uD83E\uDDF7 Введите ник получателя:";
    }

    @Override
    public void process(String arg)
    {
        long player_id = invoker.getId();


            Player acceptor = playerDAO.get_by_name(arg);
            if (acceptor != null)
            {
                invoker.state = new Needle2(invoker, host, acceptor, this, base);
                host.sendMsg(player_id, invoker.state.hint);
            }
            else
            {
                host.sendMsg(player_id, "Такого игрока не существует");
            }


    }
}

class Needle2 extends State
{
    Player invoker;
    Bot host;
    Player acceptor;

    public Needle2(Player invoker, Bot host, Player acceptor, State previous, BaseState base)
    {
        this.invoker = invoker;
        this.host = host;
        this.acceptor = acceptor;
        this.previous = previous;
        this.base = base;
        hint = "\uD83E\uDDF7 Введите сумму: ";
    }

    @Override
    public void process(String arg)
    {
        try
        {
                Long cost = Long.parseLong(arg);
                acceptor.needle += cost;
                invoker.state = invoker.state.base;

                host.sendMsg(acceptor.getId(), String.format("\\[\uD83E\uDDF7] На ваш баланс зачислено: *%s*\uD83E\uDDF7", cost));
                host.sendMsg(invoker.getId(), "\uD83E\uDDF7 Булавки успешно отправлены");

        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            host.sendMsg(invoker.getId(), "⚠\t Вы ввели некорректную сумму");
        }

    }
}
