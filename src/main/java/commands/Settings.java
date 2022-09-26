package commands;

import database.dao.IPlayerDAO;
import main.Bot;
import main.Player;

public class Settings extends Command{

    IPlayerDAO playerDAO;
    Settings(IPlayerDAO playerDAO){
        this.playerDAO = playerDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        StringBuilder sb = new StringBuilder("⚙ Меню настроек\n\n");
        if(player.stats.hideInv == 0){
            sb.append("\uD83D\uDC40 \\[0] Ваш инвентарь виден другим игрокам\n\n");
        }else{
            sb.append("\uD83D\uDD12 \\[0] Ваш инвентарь скрыт, от других игроков\n\n");
        }

        sb.append("Выберите цифру, чтобы изменить параметр:\n");
        host.sendMsg(player.getId(), sb.toString());
        player.state = new Settings1(playerDAO, player, host);
    }
}

class Settings1 extends State{

    IPlayerDAO playerDAO;
    Player player;
    Bot host;

    Settings1(IPlayerDAO playerDAO, Player player, Bot host){
        this.playerDAO = playerDAO;
        this.player = player;
        this.host = host;
    }


    @Override
    public void process(String arg) {
        try{
            if(arg.equals("0")){
                if(player.stats.hideInv == 0){
                    player.stats.hideInv = 1;
                }else{
                    player.stats.hideInv = 0;
                }

                host.sendMsg(player.getId(), "✅ Параметр успешно изменен");
                player.state = player.state.base;
            }else{
                throw new RuntimeException();
            }
        }catch (RuntimeException e){
            e.printStackTrace();
            host.sendMsg(player.getId(), "Введите корректный параметр");
        }
    }
}
