package commands;

import main.Bot;
import main.Item;
import main.Player;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.util.HashMap;

public class Mypet extends Command{
    HashMap<Item, SendPhoto> petPhotos = new HashMap<Item, SendPhoto>();

    @Override
    public void consume(Bot host, Player player) {
        if(player.getInventory().hasPet()){
            host.sendMsg(player.getId(), "У вас есть питомец");
        }else{
            host.sendMsg(player.getId(), "У вас пока нет питомца :'(");
        }
    }
}
