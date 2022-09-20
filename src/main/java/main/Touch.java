package main;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static main.RollerFactory.itemDAO;

public class Touch {
    Map<String, SendPhoto> magazines = new HashMap<>();
    Map<Item, String> info = new HashMap<>();


    public Touch(Player player, String anotherPlayer){
        infoInit(player, anotherPlayer);
        magazinesInit(player);

    }

    private SendPhoto getPhoto(String path, Player player){
        SendPhoto photo = new SendPhoto();
        photo.setPhoto(new InputFile(new File(path)));
        photo.setChatId(player.getId());
        return photo;
    }

    private void infoInit(Player player, String anotherPlayer){
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.format(new Date());

        info.put(itemDAO.getByNameFromCollection("Брелок с бабочкой"), "Красивый брелок, надеюсь не улетит...");
        info.put(itemDAO.getByNameFromCollection("Кейс Gift"), "Кажется, он пустой");
        info.put(itemDAO.getByNameFromCollection("Тег"), "Можно сменить ник на `super-" + player.getUsername() + "`, чтобы быть еще круче");
        info.put(itemDAO.getByNameFromCollection("Whirr - Feels Like You"), "У вас в руках лучший shoegaze альбом 2019 года");
        info.put(itemDAO.getByNameFromCollection("Рюкзак"), "В нем можно хранить дополнительные 4 предмета");
        info.put(itemDAO.getByNameFromCollection("Плюшевая Аянами Рей"), "Такая мягкая и такая chikita...");
        info.put(itemDAO.getByNameFromCollection("Поисковый фонарь"), "Светит ярко, особенно если в глаза");
        info.put(itemDAO.getByNameFromCollection("Чашка 'Египет'"), "Говорят кофе из этой чашки еще вкуснее");
        info.put(itemDAO.getByNameFromCollection("Удочка"), "Удочкой можно ловить рыбу");
        info.put(itemDAO.getByNameFromCollection("Текст песни 'FF'"), "FF, я уперся в потолок...");
        info.put(itemDAO.getByNameFromCollection("Бипки"), "Что такое бипки, кто-то знает?");
        info.put(itemDAO.getByNameFromCollection("Камень"), "Вы попали в голову игроку `" + anotherPlayer + "` ему не понравилось, странно...");
        info.put(itemDAO.getByNameFromCollection("Сим-карта 777"), "Ало, это пиццерия? Мне гавайскую");
        info.put(itemDAO.getByNameFromCollection("Пачка сигарет"), "Курить вредно, очень вредно...");
        info.put(itemDAO.getByNameFromCollection("Стиральный порошок"), "Порошок пахнет свежестью, теперь нужно понюхать стиральный");
        info.put(itemDAO.getByNameFromCollection("Зубная щетка"), "Щубная зетка");
        info.put(itemDAO.getByNameFromCollection("Цветная резинка для волос"), "Так сложно найти резинку, когда она так нужна...");
        info.put(itemDAO.getByNameFromCollection("Отвертка"), "Вы держите отвертку");
        info.put(itemDAO.getByNameFromCollection("Букет цветов"), "Пахнет хризантемами");
        info.put(itemDAO.getByNameFromCollection("Витаминки"), "Это были не витаминки...");
        info.put(itemDAO.getByNameFromCollection("Чулки"), "Слышно аромат мускуса, на фоне играет George Michael - Careless Whisper...");
        info.put(itemDAO.getByNameFromCollection("Чупа-чупс"), "Если долго сосать чупа-чупс, то в какой-то момент можно начать сосать палку");
        info.put(itemDAO.getByNameFromCollection("Ожерелье"), "Его можно подарить Вашей девушке, хотя на руке тоже ничего смотрится...");
        info.put(itemDAO.getByNameFromCollection("Кукурушки"), "Не хватает молока");
        info.put(itemDAO.getByNameFromCollection("Карась"), "Карась, кадвась, катрись...");
        info.put(itemDAO.getByNameFromCollection("Бычок"), "Погодите, это что окурок?!");
        info.put(itemDAO.getByNameFromCollection("Браслет 'Сириус'"), "Красивый браслет со звездочками");
        info.put(itemDAO.getByNameFromCollection("Шоколадка"), "Лучше съесть ее в сторонке, пока игрок `" + anotherPlayer + "` не видит");
        info.put(itemDAO.getByNameFromCollection("Стальной нож"), "Им можно порезать хлеб, остается найти кто такой Хлеб");
        info.put(itemDAO.getByNameFromCollection("USB провод"), "Черный и такой длиииинный");
        info.put(itemDAO.getByNameFromCollection("Энергетик"), "Вы делаете глоток и чувствуете как энергия течет в ваших венах");
        info.put(itemDAO.getByNameFromCollection("Бутылка"), "Не удалось рассмотреть бутылку, так как ее уже тестирует игрок `" + anotherPlayer + "`");
        info.put(itemDAO.getByNameFromCollection("Носки"), "Странно, что оба на месте");
        info.put(itemDAO.getByNameFromCollection("Баллончик с краской"), "Вы тегнули");
        info.put(itemDAO.getByNameFromCollection("Синий браслет"), "Для полной картины не хватает желтого браслета");
        info.put(itemDAO.getByNameFromCollection("Желтый браслет"), "Для полной картины не хватает синего браслета");
        info.put(itemDAO.getByNameFromCollection("Красный браслет"), "Пацаны с района с завистью смотрят на ваш красный браслет");
        info.put(itemDAO.getByNameFromCollection("Зеленый браслет"), "Зеленый браслет стильно смотрится на вашей руке");
        info.put(itemDAO.getByNameFromCollection("Браслет 'Орион'"), "Не показывайте этот браслет игроку `" +anotherPlayer + "` иначе отберет");
        info.put(itemDAO.getByNameFromCollection("Струны"), "Сейчас бы гитарку...");
        info.put(itemDAO.getByNameFromCollection("Журнал Евангелион"), "Вы детально рассматриваете Аянами Рей");
        info.put(itemDAO.getByNameFromCollection("Крем для рук"), "Интересно, а что если помазать ноги...");
        info.put(itemDAO.getByNameFromCollection("Бутылка вина 'Cabernet Sauvignon'"), "Оно просроченное");
        info.put(itemDAO.getByNameFromCollection("Банан"), "Если его съесть, то будет вкусно");
        info.put(itemDAO.getByNameFromCollection("Винтажный журнал"), "Вы рассматриваете винтажный журнал");
        info.put(itemDAO.getByNameFromCollection("Горбуша"), "Вы рассматриваете потенциальный ужин");
        info.put(itemDAO.getByNameFromCollection("Ключ от кейса"), "Им можно открыть кейс или дом игрока `" + anotherPlayer  +"`");
        info.put(itemDAO.getByNameFromCollection("Ручка"), "Она принадлежит игроку `" + anotherPlayer + "`, возможно рядом завалялась и ношка");
        info.put(itemDAO.getByNameFromCollection("Крекеры"), "Хорошо подходят, чтобы попить чай или кофе с игроком `" + anotherPlayer + "`");
        info.put(itemDAO.getByNameFromCollection("Платок"), "Сразу видно что краденный, с какой-то бабушки сняли. Вы ужасный человек");
        info.put(itemDAO.getByNameFromCollection("Подвеска 'Nosebleed'"), "Если надеть ее игрок `" + anotherPlayer + "` будет в шоке");
        info.put(itemDAO.getByNameFromCollection("Лопата"), "Пора картошку копать");
        info.put(itemDAO.getByNameFromCollection("Футболка 'Drain'"), "Если эта футболка у Вас, получается `" + anotherPlayer + "` сейчас без футболки?");
        info.put(itemDAO.getByNameFromCollection("Бусы"), "Красивые бусы, их можно продать скупщику");
        info.put(itemDAO.getByNameFromCollection("Саженец"), "Саженцы можно посадить в *Лесу* и получить за это опыт или деньги");
        info.put(itemDAO.getByNameFromCollection("Подшипник"), "Вы искачкали руки в мазуте");
        info.put(itemDAO.getByNameFromCollection("Часы"), String.format("На часах %s ", sdf.format(new Date())));
        info.put(itemDAO.getByNameFromCollection("Фея"), "Карманная фея, ну такого `" + anotherPlayer + "` точно не видел");
        info.put(itemDAO.getByNameFromCollection("Космический журнал"), "Вы рассматриваете космический журнал");
        info.put(itemDAO.getByNameFromCollection("Журнал Playboy 1/2"), "Вы рассматриваете редкий журнал Playboy 1/2");
        info.put(itemDAO.getByNameFromCollection("Журнал Playboy 2/2"), "Вы рассматриваете редкий журнал Playboy 2/2");
        info.put(itemDAO.getByNameFromCollection("Журнал 'Стальной алхимик'"), "Вы держите журнал по Стальному Алхимику");
        info.put(itemDAO.getByNameFromCollection("Журнал Vogue 1/5"), "Вы рассматриваете редкий журнал Vogue 1/5");
        info.put(itemDAO.getByNameFromCollection("Журнал Vogue 2/5"), "Вы рассматриваете журнал Vogue 2/5");
        info.put(itemDAO.getByNameFromCollection("Журнал Vogue 3/5"), "Вы рассматриваете журнал Vogue 3/5");
        info.put(itemDAO.getByNameFromCollection("Журнал Vogue 4/5"), "Вы рассматриваете редкий журнал Vogue 4/5");
        info.put(itemDAO.getByNameFromCollection("Журнал Vogue 5/5"), "Вы рассматриваете журнал Vogue 5/5");
        info.put(itemDAO.getByNameFromCollection("Журнал The Male Point Of View 1/3"), "Вы рассматриваете редкий журнал The Male Point Of View 1/3");
        info.put(itemDAO.getByNameFromCollection("Журнал The Male Point Of View 2/3"), "Вы рассматриваете редкий журнал The Male Point Of View 2/3");
        info.put(itemDAO.getByNameFromCollection("Журнал The Male Point Of View 3/3"), "Вы рассматриваете редкий журнал The Male Point Of View 3/3");
        info.put(itemDAO.getByNameFromCollection("Автомобильный журнал"), "Вы рассматриваете машину с глазками");
        info.put(itemDAO.getByNameFromCollection("Джинсы"), "Плотная ткань, удобный шов, глубокие карманы");
        info.put(itemDAO.getByNameFromCollection("Сомбреро"), "Ваша привлекательность в этой шляпе увеличивается на 35%");
        info.put(itemDAO.getByNameFromCollection("Медиатор"), "Та штука, которая постоянно падает в гитару");
        info.put(itemDAO.getByNameFromCollection("Пакет"), "В теории, в него можно положить хлеб");
        info.put(itemDAO.getByNameFromCollection("Курточка"), "Очень подходит для дождливой погоды");
        info.put(itemDAO.getByNameFromCollection("Петарда"), "У вас в руках корсар-1");
        info.put(itemDAO.getByNameFromCollection("Тетрадь"), "У вас в руках крутая тетрадка с машинами и голыми девушками, пруфов не будет, поверьте мне наслово");
    }

    private void magazinesInit(Player player){
        magazines.put("Вы детально рассматриваете Аянами Рей", getPhoto(".\\pics\\mag\\magazine_evangelion.jpg", player));
        magazines.put("Вы рассматриваете винтажный журнал", getPhoto(".\\pics\\mag\\magazine_vintage.jpg", player));
        magazines.put("Вы рассматриваете космический журнал", getPhoto(".\\pics\\mag\\magazine_space.jpg", player));
        magazines.put("Продолжение следует", getPhoto(".\\pics\\mag\\magazine_playboy2.jpg", player));
        magazines.put("Бегом искать филосовский камень", getPhoto(".\\pics\\mag\\magazine_fullmetal.jpg", player));
        magazines.put("Вы рассматриваете редкий журнал Vogue 1/5", getPhoto(".\\pics\\mag\\magazine_fashion.jpg", player));
        magazines.put("Вы рассматриваете журнал Vogue 2/5", getPhoto(".\\pics\\mag\\magazine_fashion2.jpg", player));
        magazines.put("Вы рассматриваете журнал Vogue 3/5", getPhoto(".\\pics\\mag\\magazine_fashion3.jpg", player));
        magazines.put("Вы рассматриваете редкий журнал Vogue 4/5", getPhoto(".\\pics\\mag\\magazine_fashion4.jpg", player));
        magazines.put("Вы рассматриваете журнал Vogue 5/5", getPhoto(".\\pics\\mag\\magazine_fashion5.jpg", player));
        magazines.put("Вы рассматриваете редкий журнал Playboy 1/2", getPhoto(".\\pics\\mag\\magazine_playboy.jpg", player));
        magazines.put("Вы рассматриваете редкий журнал Playboy 2/2", getPhoto(".\\pics\\mag\\magazine_playboy2.jpg", player));
        magazines.put("Вы рассматриваете машину с глазками", getPhoto(".\\pics\\mag\\magazine_car.jpg", player));
        magazines.put("Вы рассматриваете редкий журнал The Male Point Of View 1/3", getPhoto(".\\pics\\mag\\magazine_point.jpg", player));
        magazines.put("Вы рассматриваете редкий журнал The Male Point Of View 2/3", getPhoto(".\\pics\\mag\\magazine_point2.jpg", player));
        magazines.put("Вы рассматриваете редкий журнал The Male Point Of View 3/3", getPhoto(".\\pics\\mag\\magazine_point3.jpg", player));
    }

    public Map<Item, String> getInfo(){
        return info;
    }

    public Map<String, SendPhoto> getMagazines(){
        return magazines;
    }

    public SendPhoto getMagazinePhoto(String responseText){
        return magazines.get(responseText);
    }
}
