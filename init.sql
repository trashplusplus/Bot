create table if not exists players
(
    id integer primary key,
    xp integer default 0,
    'level' integer default 0,
    needle integer default 0,
    name text unique,
    balance integer default 0
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
    ('Банан','Cheap',100,''),
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
    ('Фея', 'Limited', 0,'🧚‍♀'),
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
    ('Молния', 'Status', 1000,'⚡'),
    ('Звезда', 'Status', 1000,'💫');


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
    capitals integer default 0
);


drop view if exists player;
create view if not exists player as
    select
        id, name, xp, level, balance, needle,
        find_expiration as FIND, pockets_expiration as POCKETS,
        coinWins as W, coinLosses as L, coffee, tea, bonus, trees, capitals
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
