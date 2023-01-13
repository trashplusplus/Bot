create table if not exists players
(
    id integer primary key,
    xp integer default 0,
    'level' integer default 0,
    needle integer default 0,
    name text unique,
    balance integer default 0,
    emojiStatus integer default null
);


create table if not exists cooldowns
(
    player_id integer primary key references players (id) on update cascade on delete cascade,
    find_expiration text default null,
    pockets_expiration text default null
);

create index if not exists find_ex_id on cooldowns (find_expiration);
create index if not exists pockets_ex_id on cooldowns (pockets_expiration);


create table if not exists items
(
    id integer primary key,
    name text unique not null,
    rarity text not null,
    cost integer,
    emoji text default null
);


create table if not exists shop
(
    id integer primary key,
    item_id,
    cost integer not null,
    seller_id,

    foreign key (item_id) references items (id) on update cascade,
    foreign key (seller_id) references players (id) on delete cascade
);


create table if not exists container
(
    id integer primary key,
    player_id,
    item_id,

    foreign key (player_id) references players (id) on delete cascade,
    foreign key (item_id) references items (id) on update cascade on delete cascade
);


create table if not exists shop_expiration
(
    shop_id integer references shop (id) on delete cascade on update cascade,
    exp_date text
);

create index if not exists exp_index on shop_expiration (exp_date);


insert or ignore into items (name, rarity, cost, emoji) values
    ('Лопата','Cheap',200, ''),
    ('Поисковый фонарь','Rare',7000, '🔦'),
    ('Подвеска ''Nosebleed''','Rare',5000,''),
    ('Струны','Cheap',300, ''),
    ('Футболка ''Drain''','Cheap',300, ''),
    ('Банан','Cheap',100,'🍌'),
    ('Чашка ''Египет''','Rare',5000,'☕'),
    ('Носки','Cheap',100,''),
    ('Ручка','Cheap',100,''),
    ('Баллончик с краской','Common',750,''),
    ('Платок','Common',150,''),
    ('Пачка сигарет','Cheap',50,''),
    ('Синий браслет','Common',300,''),
    ('Красный браслет','Common',300,''),
    ('Желтый браслет','Common',300,''),
    ('Зеленый браслет','Common',300,''),
    ('Браслет ''Орион''','Common',1000,''),
    ('Браслет ''Сириус''','Common',900,''),
    ('Зубная щетка','Cheap',50,''),
    ('Шоколадка','Cheap',200,''),
    ('Рюкзак','Rare',7500,'🎒'),
    ('Сим-карта 777','Rare',6000,''),
    ('Стальной нож','Common',600,''),
    ('Стиральный порошок','Cheap',100,''),
    ('Плюшевая Аянами Рей','Gift',50000,'💎'),
    ('Цветная резинка для волос','Gift',17000,''),
    ('Отвертка','Cheap',150,''),
    ('Брелок','Cheap',250,''),
    ('USB провод','Cheap', 100,''),
    ('Бутылка вина ''Cabernet Sauvignon''','Rare', 5000,''),
    ('Винтажный журнал','Gift', 15000,''),
    ('Бусы','Common', 900,''),
    ('Крекеры','Common', 450,''),
    ('Чулки','Cheap', 69,''),
    ('Чупа-чупс','Cheap', 69,''),
    ('Витаминки','Common', 100,''),
    ('Букет цветов','Common', 300,''),
    ('Whirr - Feels Like You','Gift', 35000,'📀'),
    ('Камень', 'Cheap', 1,''),
    ('Крем для рук', 'Cheap', 80,''),
    ('Энергетик', 'Common', 170,''),
    ('Ожерелье', 'Rare', 3500,''),
    ('Кукурушки', 'Rare', 5000,''),
    ('Бипки', 'Cheap', 700,''),
    ('Текст песни ''FF''', 'Rare', 2500,''),
    ('Журнал Евангелион', 'Rare', 6700,''),
    ('Удочка', 'Gift', 10000,'🐟'),
    ('Карась', 'Common', 100,''),
    ('Горбуша', 'Common', 100,''),
    ('Бычок', 'Common', 100,''),
    ('Бутылка', 'Cheap', 5,''),
    ('Тег', 'Rare', 5000,'📝'),
    ('Кейс Gift', 'Common', 500,'📦'),
    ('Ключ от кейса', 'Rare', 7000,'🔑'),
    ('Брелок с бабочкой', 'Gift', 25000,'🦋'),
    ('Саженец', 'Cheap', 150,''),
    ('Подшипник', 'Cheap', 50,''),
    ('Часы', 'Gift', 10000,'⌚'),
    ('Фея', 'Status', 7500000,'🧚‍♀'),
    ('Космический журнал', 'Rare', 7000,''),
    ('Журнал Playboy 1/2', 'Gift', 7000,'🍓'),
    ('Журнал Playboy 2/2', 'Gift', 11000,'🍓'),
    ('Журнал ''Стальной алхимик''', 'Common', 1500,''),
    ('Журнал Vogue 1/5', 'Gift', 10500,'🔮'),
    ('Журнал Vogue 2/5', 'Gift', 3000,'🔮'),
    ('Журнал Vogue 3/5', 'Gift', 5000,'🔮'),
    ('Журнал Vogue 4/5', 'Gift', 9000,'🔮'),
    ('Журнал Vogue 5/5', 'Gift', 5000,'🔮'),
    ('Журнал The Male Point Of View 1/3', 'Gift', 15000,'🍓'),
    ('Журнал The Male Point Of View 2/3', 'Gift', 20000,'🍓'),
    ('Журнал The Male Point Of View 3/3', 'Gift', 23000,'🍓'),
    ('Автомобильный журнал', 'Common', 1500,''),
    ('Джинсы', 'Common', 1000,''),
    ('Сомбреро', 'Common', 700,''),
    ('Медиатор', 'Cheap', 50,''),
    ('Пакет', 'Cheap', 35,''),
    ('Курточка', 'Common', 850,''),
    ('Петарда', 'Cheap', 15,''),
    ('Тетрадь', 'Cheap', 10,''),
    ('Веревка', 'Common', 320,''),
    ('Слеза Зевса', 'Status', 150000,'⚡'),
    ('Сияющий Ригель', 'Status', 100000,'💫'),
    ('Статусная Аянами', 'Status', 150000,'💎'),
    ('Рецепт Статусной Аянами', 'Rare', 9500,''),
    ('Сатурн', 'Status', 1000000,'🪐'),
    ('Мыльные пузыри', 'Status', 5000000,'🫧'),
    ('Скарлетт', 'Status', 250000,'❤'),
    ('Ангельская пыль', 'Status', 450000,'🤍'),
    ('Полумесяц', 'Status', 250000,'🌙'),
    ('Плантера', 'Status', 250000,'💚'),
    ('Дисперсия', 'Status', 250000,'🌈'),
    ('Вояджер-1', 'Status', 300000,'🛰'),
    ('Страсть', 'Status', 450000,'🖤'),
    ('Мишк Фреде', 'Status', 7500000,'🧸'),
    ('Форрест Гамп', 'Status', 9999,'🌳'),
    ('Джон Коффи', 'Status', 9999,'☕'),
    ('Гринфилд', 'Status', 9999,'🌿'),
    ('Гражданин Мира', 'Status', 9999,'🌏'),
    ('Лапки', 'Status', 111000,'🐾'),
    ('Пчелка', 'Pet', 5000000,'🐝'),
    ('Корова Бога', 'Pet', 5500000,'🐞'),
    ('Бог Смерти', 'Pet', 7500000,'🍎'),
    ('Вамп', 'Pet', 6666666,'🦇'),
    ('Стелла', 'Pet', 6666666,'🕷'),
    ('Поня', 'Pet', 7500000,'🦄'),
    ('Кибо', 'Pet', 8000000,'🦕'),
    ('Кит', 'Pet', 8000000,'🐳'),
    ('День бабочек', 'Pet', 8000000,'🦋'),
    ('UFO', 'Status', 75000,'👽'),
    ('Nosebleed', 'Status', 45000,'🩸'),
    ('C4', 'Status', 14000,'🧨'),
    ('Шина', 'Status', 12000,'🛞'),
    ('Антидождик', 'Status', 15000,'⛱'),
    ('Журнал Hello Kitty 1/3', 'Rare', 16100,'🔮'),
    ('Журнал Hello Kitty 2/3', 'Rare', 15000,'🔮'),
    ('Журнал Hello Kitty 3/3', 'Rare', 5900,'🔮'),
    ('Граффити', 'Rare', 9500,'🛢'),
    ('Статус поддержки Украины', 'Status', 10000,'🇺🇦'),
    ('Записка от разработчика №101122', 'Status', 10000,'📄'),
    ('Одеяло', 'Common', 275,''),
    ('Варежка', 'Cheap', 150,''),
    ('Варежки', 'Common', 300,''),
    ('Урановый стержень', 'Cheap', 235,''),
    ('Белый Monster Energy', 'Case', 3999,'🥤'),
    ('Литий', 'Cheap', 85,''),
    ('Микросхема', 'Craft', 1500,'💾'),
    ('Аккумулятор', 'Craft', 2000,'🔋'),
    ('Розовый телефон', 'Craft', 5000,'📱'),
    ('Антенна', 'Rare', 1500,'📡'),
    ('Связь', 'Common', 500,''),
    ('Гитара Fender', 'Gift', 9100,'🎸'),
    ('Дека', 'Common', 175,''),
    ('Скейтборд', 'Craft', 8500,'🛹'),
    ('Редкий кубок', 'Gift', 19000,'🏆'),
    ('Сердце в золотой корочке', 'Case', 19000,'💛'),
    ('Золото', 'Craft', 8560,'🥇'),
    ('Каменный уголь', 'Craft', 450,'🪨'),
    ('Кость', 'Cheap', 80,''),
    ('Клевер', 'Common', 450,'☘'),
    ('Конспект', 'Cheap', 300,''),
    ('Болтик', 'Cheap', 7,''),
    ('Обертка', 'Cheap', 64,''),
    ('Редкая ваза', 'Case', 85000,'🪘'),
    ('Четырехлистный клевер', 'Gift', 16250,'🍀'),
    ('Каменный уголь', 'Common', 450,''),
    ('Ёлка 2023', 'Status', 1000000,'🎄'),
    ('XJ9', 'Pet', 1000000,'🤖'),
    ('Коробка с игрой Bee The Warrior', 'Limited', 0,'📟'),
    ('Облако в бутылке', 'Gift', 25600,'☁'),
     ('Обычная машинка', 'Limited', 99000000,'🚗'),
     ('Легендарный спорткар', 'Limited', 0,'🏎'),
	  ('Редкое кольцо', 'Case', 713317,'💍'),
    ('Кирка', 'Gift', 11550,'⛏'),
    ('Каска', 'Gift', 5000,'🪖'),
    ('Якорь', 'Rare', 14350,'⚓'),
    ('Кулон удачи', 'Craft', 25000,'🏵'),
    ('Deftones - Adrenaline', 'Gift', 55000,'💽'),
    ('Титан', 'Craft', 1500,''),
    ('Ключ от контейнера', 'Craft', 100000,'🗝'),
    ('Вертиго', 'Gift', 17000,'🌀'),
    ('Шифр', 'Craft', 2000,'✉');


create table if not exists inventory
(
    id integer primary key,
    player_id,
    item_id,

    foreign key (player_id) references players (id) on delete cascade,
    foreign key (item_id) references items (id) on update cascade on delete cascade
);


create table if not exists stats
(
    player_id integer primary key references players (id) on delete cascade on update cascade,
    bonus integer not null default 0,
    coinWins integer default 0,
    coinLosses integer default 0,
    coffee integer default 0,
    tea integer default 0,
    trees integer default 0,
    capitals integer default 0,
    hideInv integer not null default 0,
    magazines integer default 0,
    totalWonMoney integer default 0,
    totalLostMoney integer default 0,
    findCounter integer default 0,
    mudCounter integer default 0,
    totalMud integer default 0,
    craftCounter integer default 0,
    duelWin integer default 0,
    duelLose integer default 0
);


drop view if exists player;
create view if not exists player as
    select
        id, name, xp, level, balance, needle, emojiStatus,
        find_expiration as FIND, pockets_expiration as POCKETS,
        coinWins as W, coinLosses as L, coffee, tea, bonus, trees, capitals,
        hideInv, magazines, totalWonMoney, totalLostMoney, findCounter, mudCounter, totalMud,
        craftCounter, duelWin, duelLose
    from
    (
        players
            left join
        cooldowns
            on id = cooldowns.player_id
    )
        left join
    stats
        on id = stats.player_id;
