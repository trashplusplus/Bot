package commands;

import database.dao.IItemDAO;
import main.Bot;
import main.Item;
import main.ItemRarity;
import main.Player;
import main.Touch;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mypet extends Command{
    IItemDAO itemDAO;
    HashMap<Item, SendPhoto> petPhotos = new HashMap<Item, SendPhoto>();

    public Mypet(IItemDAO itemDAO){
        this.itemDAO = itemDAO;
    }



    @Override
    public void consume(Bot host, Player player) {
        if(player.getInventory().hasPet()){
           Item pet;
           Touch touch = new Touch(player);
            String statusHint = "\uD83D\uDC36  Установите питомца в статус, чтобы он отображался";
            if(player.getStatusItem() != null){
                if(player.getStatusItem().getRarity().equals(ItemRarity.Pet)){
                    pet = player.getStatusItem();
                    try{
                        host.execute(touch.getPetPhoto(touch.getInfo().get(pet)));
                    }catch (TelegramApiException e){
                        e.printStackTrace();
                    }

                    host.sendMsg(player.getId(), String.format("%s Ваш питомец: %s", pet.getEmoji(), pet.getTitle()));
                }else{
                    host.sendMsg(player.getId(), statusHint);
                }

            }else{
                host.sendMsg(player.getId(), statusHint);
            }



        }else{
            host.sendMsg(player.getId(), "\uD83D\uDC36 У вас в статусе нет питомца :'(");
        }
    }
}
