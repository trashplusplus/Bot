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
    private Map<String, SendPhoto> magazines = new HashMap<>();
    private Map<String, SendPhoto> pets = new HashMap<>();
    private Map<Item, String> info = new HashMap<>();
    private String anotherPlayer;


    public Touch(Player player, String anotherPlayer){
        this.anotherPlayer = anotherPlayer;
        infoInit(player, this.anotherPlayer);
        magazinesInit(player);
        petsInit(player);

    }
    //pets constructor
    public Touch(Player player){
        infoInit(player, "*пасхалка*");
       // magazinesInit(player);
        petsInit(player);
    }

    private SendPhoto getPhoto(String path, Player player){
        SendPhoto photo = new SendPhoto();
        photo.setPhoto(new InputFile(new File(path)));
        photo.setChatId(player.getId());
        return photo;
    }

    private void newFlavourTxt(String item, String txt){
        info.put(itemDAO.get_by_name(item), txt);
    }

    private void infoInit(Player player, String anotherPlayer){
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.format(new Date());

        newFlavourTxt("Брелок с бабочкой", "Красивый брелок, надеюсь не улетит...");
        newFlavourTxt("Кейс Gift", "Кажется, он пустой");
        newFlavourTxt("Тег", "Можно сменить ник на `super-" + player.getUsername() + "`, чтобы быть еще круче");
        newFlavourTxt("Whirr - Feels Like You", "У вас в руках лучший shoegaze альбом 2019 года");
        newFlavourTxt("Рюкзак", "В нем можно хранить дополнительные 4 предмета");
        newFlavourTxt("Плюшевая Аянами Рей", "Такая мягкая и такая chikita...");
        newFlavourTxt("Поисковый фонарь", "Светит ярко, особенно если в глаза");
        newFlavourTxt("Чашка 'Египет'", "Говорят кофе из этой чашки еще вкуснее");
        newFlavourTxt("Удочка", "Удочкой можно ловить рыбу");
        newFlavourTxt("Текст песни 'FF'", "FF, я уперся в потолок...");
        newFlavourTxt("Бипки", "Что такое бипки, кто-то знает?");
        newFlavourTxt("Камень", "Вы попали в голову игроку `" + anotherPlayer + "` ему не понравилось, странно...");
        newFlavourTxt("Сим-карта 777", "Ало, это пиццерия? Мне гавайскую");
        newFlavourTxt("Пачка сигарет", "Курить вредно, очень вредно...");
        newFlavourTxt("Стиральный порошок", "Порошок пахнет свежестью, теперь нужно понюхать стиральный");
        newFlavourTxt("Зубная щетка", "Щубная зетка");
        newFlavourTxt("Цветная резинка для волос", "Так сложно найти резинку, когда она так нужна...");
        newFlavourTxt("Отвертка", "Вы держите отвертку");
        newFlavourTxt("Букет цветов", "Пахнет хризантемами");
        newFlavourTxt("Витаминки", "Это были не витаминки...");
        newFlavourTxt("Чулки", "Слышно аромат мускуса, на фоне играет George Michael - Careless Whisper...");
        newFlavourTxt("Чупа-чупс", "Если долго сосать чупа-чупс, то в какой-то момент можно начать сосать палку");
        newFlavourTxt("Ожерелье", "Его можно подарить Вашей девушке, хотя на руке тоже ничего смотрится...");
        newFlavourTxt("Кукурушки", "Не хватает молока");
        newFlavourTxt("Карась", "Карась, кадвась, катрись...");
        newFlavourTxt("Бычок", "Погодите, это что окурок?!");
        newFlavourTxt("Браслет 'Сириус'", "Красивый браслет со звездочками");
        newFlavourTxt("Шоколадка", "Лучше съесть ее в сторонке, пока игрок `" + anotherPlayer + "` не видит");
        newFlavourTxt("Стальной нож", "Им можно порезать хлеб, остается найти кто такой Хлеб");
        newFlavourTxt("USB провод", "Черный и такой длиииинный");
        newFlavourTxt("Энергетик", "Вы делаете глоток и чувствуете как энергия течет в ваших венах");
        newFlavourTxt("Бутылка", "Не удалось рассмотреть бутылку, так как ее уже тестирует игрок `" + anotherPlayer + "`");
        newFlavourTxt("Носки", "Странно, что оба на месте");
        newFlavourTxt("Баллончик с краской", "Вы тегнули");
        newFlavourTxt("Синий браслет", "Для полной картины не хватает желтого браслета");
        newFlavourTxt("Желтый браслет", "Для полной картины не хватает синего браслета");
        newFlavourTxt("Красный браслет", "Пацаны с района с завистью смотрят на ваш красный браслет");
        newFlavourTxt("Зеленый браслет", "Зеленый браслет стильно смотрится на вашей руке");
        newFlavourTxt("Браслет 'Орион'", "Не показывайте этот браслет игроку `" +anotherPlayer + "` иначе отберет");
        newFlavourTxt("Струны", "Сейчас бы гитарку...");
        newFlavourTxt("Журнал Евангелион", "Вы детально рассматриваете Аянами Рей");
        newFlavourTxt("Крем для рук", "Интересно, а что если помазать ноги...");
        newFlavourTxt("Бутылка вина 'Cabernet Sauvignon'", "Оно просроченное");
        newFlavourTxt("Банан", "Если его съесть, то будет вкусно");
        newFlavourTxt("Винтажный журнал", "Вы рассматриваете винтажный журнал");
        newFlavourTxt("Горбуша", "Вы рассматриваете потенциальный ужин");
        newFlavourTxt("Ключ от кейса", "Им можно открыть кейс или дом игрока `" + anotherPlayer  +"`");
        newFlavourTxt("Ручка", "Она принадлежит игроку `" + anotherPlayer + "`, возможно рядом завалялась и ношка");
        newFlavourTxt("Крекеры", "Хорошо подходят, чтобы попить чай или кофе с игроком `" + anotherPlayer + "`");
        newFlavourTxt("Платок", "Сразу видно что краденный, с какой-то бабушки сняли. Вы ужасный человек");
        newFlavourTxt("Подвеска 'Nosebleed'", "Если надеть ее игрок `" + anotherPlayer + "` будет в шоке");
        newFlavourTxt("Лопата", "Пора картошку копать");
        newFlavourTxt("Футболка 'Drain'", "Если эта футболка у Вас, получается `" + anotherPlayer + "` сейчас без футболки?");
        newFlavourTxt("Бусы", "Красивые бусы, их можно продать скупщику");
        newFlavourTxt("Саженец", "Саженцы можно посадить в *Лесу* и получить за это опыт или деньги");
        newFlavourTxt("Подшипник", "Вы испачкали руки в мазуте");
        newFlavourTxt("Часы", String.format("На часах %s ", sdf.format(new Date())));
        newFlavourTxt("Фея", "Карманная фея, ну такого `" + anotherPlayer + "` точно не видел");
        newFlavourTxt("Космический журнал", "Вы рассматриваете космический журнал");
        newFlavourTxt("Журнал Playboy 1/2", "Вы рассматриваете редкий журнал Playboy 1/2");
        newFlavourTxt("Журнал Playboy 2/2", "Вы рассматриваете редкий журнал Playboy 2/2");
        newFlavourTxt("Журнал 'Стальной алхимик'", "Вы прочитали журнал, но так и не нашли филосовский камень");
        newFlavourTxt("Журнал Vogue 1/5", "Вы рассматриваете редкий журнал Vogue 1/5");
        newFlavourTxt("Журнал Vogue 2/5", "Вы рассматриваете журнал Vogue 2/5");
        newFlavourTxt("Журнал Vogue 3/5", "Вы рассматриваете журнал Vogue 3/5");
        newFlavourTxt("Журнал Vogue 4/5", "Вы рассматриваете редкий журнал Vogue 4/5");
        newFlavourTxt("Журнал Vogue 5/5", "Вы рассматриваете журнал Vogue 5/5");
        newFlavourTxt("Журнал The Male Point Of View 1/3", "Вы рассматриваете редкий журнал The Male Point Of View 1/3");
        newFlavourTxt("Журнал The Male Point Of View 2/3", "Вы рассматриваете редкий журнал The Male Point Of View 2/3");
        newFlavourTxt("Журнал The Male Point Of View 3/3", "Вы рассматриваете редкий журнал The Male Point Of View 3/3");
        newFlavourTxt("Автомобильный журнал", "Вы рассматриваете машину с глазками");
        newFlavourTxt("Джинсы", "Плотная ткань, удобный шов, глубокие карманы");
        newFlavourTxt("Сомбреро", "Ваша привлекательность в этой шляпе увеличивается на 35%");
        newFlavourTxt("Медиатор", "Та штука, которая постоянно падает в гитару");
        newFlavourTxt("Пакет", "В теории, в него можно положить хлеб или русского солдата");
        newFlavourTxt("Курточка", "Очень подходит для дождливой погоды");
        newFlavourTxt("Петарда", "У вас в руках корсар-1");
        newFlavourTxt("Тетрадь", "У вас в руках крутая тетрадка с машинами и голыми девушками, пруфов не будет, поверьте мне наслово");
        newFlavourTxt("Журнал Hello Kitty 1/3", "Вы прочитали журнал Hello Kitty 1/3");
        newFlavourTxt("Журнал Hello Kitty 2/3", "Вы прочитали журнал Hello Kitty 2/3");
        newFlavourTxt("Журнал Hello Kitty 3/3", "Вы прочитали журнал Hello Kitty 2/3");
        newFlavourTxt("Статус поддержки Украины", "Статус можно повесить на военный корабль и он явно станет лучше");
        newFlavourTxt("Граффити", "Буквально граффити");
        //Питомцы
        newFlavourTxt("Пчелка", "Укуси меня пчела");
        newFlavourTxt("Бог Смерти", "Если поставить его в статус, то все будут думать что ты Кира");
        newFlavourTxt("Корова Бога", "Если Бога нет, то чья это корова? Молоко, конечно, она не несет, но башню кому-то снести может");
        newFlavourTxt("Вамп", "Bro thinks he carti");
        newFlavourTxt("Поня", "Ходят слухи, что у *Пони* тысяча лошадиных сил");
        newFlavourTxt("Кит", "Очень редкий кит, он плавно течет по волнам, наслаждаясь каждой минутой своего существования");
        newFlavourTxt("Кибо", "Очень редкий динозавр *Кибо*, который может делать *Ррр-Ррр*");
        newFlavourTxt("Стелла", "Очень редкая паучиха *Стелла*, обычно висит под мостом");
        newFlavourTxt("День бабочек", "День бабочек, они все летают по спирали в нашей комнате...");

        newFlavourTxt("Шина", "С ней не заносит на поворотах");
        newFlavourTxt("С4", "7355608");
        newFlavourTxt("Антидождик", "Fuck rain all my homies use umbrella");
        newFlavourTxt("Nosebleed", "Бей в мое лицо, с прицелом на нос...");
        newFlavourTxt("UFO", "Needle сделали инопланетяне");
        newFlavourTxt("Лапки", "Не могу, у меня *лапки*");
        newFlavourTxt("Вояджер-1", "" +
                "• ••• •−•• ••   •−− •−− • ••• − ••   •−−   −••• −−− − •−   •−−• •• •−• −−− •••− −−− −•− •−•−•−   − −−−   −− −−− •••− −• −−−   •−−• −−− •−•• ••− −−−• •• − −••−   −••• −−− −• ••− ••• ");
        newFlavourTxt("Записка от разработчика №101122", developerNote);

    }

    private void magazinesInit(Player player){
        magazines.put("Вы детально рассматриваете Аянами Рей", getPhoto(".\\pics\\mag\\magazine_evangelion.jpg", player));
        magazines.put("Вы рассматриваете винтажный журнал", getPhoto(".\\pics\\mag\\magazine_vintage.jpg", player));
        magazines.put("Вы рассматриваете космический журнал", getPhoto(".\\pics\\mag\\magazine_space.jpg", player));
        magazines.put("Продолжение следует", getPhoto(".\\pics\\mag\\magazine_playboy2.jpg", player));
        magazines.put("Вы прочитали журнал, но так и не нашли филосовский камень", getPhoto(".\\pics\\mag\\magazine_fullmetal.jpg", player));
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
        magazines.put("Вы прочитали журнал Hello Kitty 1/3", getPhoto(".\\pics\\mag\\magazine_kitty.jpg", player));
        magazines.put("Вы прочитали журнал Hello Kitty 2/3", getPhoto(".\\pics\\mag\\magazine_kitty2.jpg", player));
        magazines.put("Вы прочитали журнал Hello Kitty 3/3", getPhoto(".\\pics\\mag\\magazine_kitty3.jpg", player));
    }

    private void petsInit(Player player){
        pets.put(getPetDesc("Пчелка"), getPhoto(".\\pics\\pets\\pet_bee.jpg", player));
        //pets.put(getPetDesc("Корова Бога"), getPhoto(".\\pics\\pets\\pet_bee.jpg", player));
        pets.put(getPetDesc("Бог Смерти"), getPhoto(".\\pics\\pets\\pet_godofdeath.jpg", player));
        pets.put(getPetDesc("Вамп"), getPhoto(".\\pics\\pets\\pet_bat.jpg", player));
        //pets.put(getPetDesc("Стелла"), getPhoto(".\\pics\\pets\\pet_bat.jpg", player));
        pets.put(getPetDesc("Поня"), getPhoto(".\\pics\\pets\\pet_unicorn.jpg", player));
        pets.put(getPetDesc("Кибо"), getPhoto(".\\pics\\pets\\pet_kibo.jpg", player));
        pets.put(getPetDesc("Кит"), getPhoto(".\\pics\\pets\\pet_whale.jpg", player));
        pets.put(getPetDesc("День бабочек"), getPhoto(".\\pics\\pets\\pet_butterfly.jpg", player));




    }

    private String getPetDesc(String itemName){
        return info.get(itemDAO.get_by_name(itemName));
        /**
         * Returns description of any item from info list
         */
    }

    private String getMagDesc(String itemName){
        return info.get(itemDAO.get_by_name(itemName));
    }




    public Map<Item, String> getInfo(){
        return info;
    }

    public Map<String, SendPhoto> getMagazines(){
        return magazines;
    }

    public Map<String, SendPhoto> getPets(){
        return pets;
    }


    public SendPhoto getMagazinePhoto(String responseText){
        return magazines.get(responseText);
    }
    public SendPhoto getPetPhoto(String responseText){
        return pets.get(responseText);
    }

    String developerNote = "йо, если ты нашел этот пр" +
            "едмет то я тебя поздравляю, он действительно редкий и нигде" +
            " не упоминается в рамках игры. просто хотел сказать, что спасибо тебе, что играешь в б" +
            "ота. для меня это классный период, когда мы сидели в телеграме и думали что еще прикольного пр" +
            "икрутить к Needle. я получил огромный опыт работая именно в таком коллективе из двух человек и именно в т" +
            "акой обстановке. все эти ночи напролет сидя за компом, походы за чаем в 3 ночи, часы когда я залипал на кухне, гладя котов и выходы на улицу, чтобы посмотреть где пролетела очере" +
            "дная ракета - все это приятный период, хоть и конченное время. большинство людей, которым я говорил пр" +
            "о Needle, думали что это какая-то игрушка, с паруй сотней строк кода, мол они сами такое за день напишут, но на самом деле это что-то большее. проект, который рождался на глазах, буквально каждый день происходило что-то новое и" +
            " оглядываясь назад я понимаю, какая же огромная работа была сделана, все эти фокусы на детали, бесконечные те" +
            "сты с базами данных, фиксы тонн багов и конечно же мнения игроков, к которым мы старались прислушиваться трезво глядя на их пожелания. конкретно сейчас и конкретно сегодня есть твердое ощущение, что м" +
            "ы почти у цели - у цел" +
            "и запустить этого бота и кидать его всем не просто для теста, а для того чтобы вовлечь их в него - просто ради фана и фидбека. просто ради того, чтобы люди копались во всех дебрях, которые мы, как раз" +
            "работчики реализовали и сделал с" +
            "пециально для них. сегодня ты сидишь и придумываешь новые игровые механики (пусть и настолько сжатые, в силу ограничений телеграма), а уже завтра в это играют люди " +
            "и получают удовольствие." +
            " это круто, реально круто. и кто мы мог подумать, что случайно открывшаяся среда разработки именно на этом проекте, который я в ра" +
            "мках обучения и собственного любопытства начал где-то в 2018 перерастет во что-то большее...";
}
