package commands;

import main.Bot;
import main.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

public class Giveaway extends Command{
    @Override
    public void consume(Bot host, Player player) {
        host.sendMsg(player.getId(), "Конкурс! Игрок %s");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();



    }
}
