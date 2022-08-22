create table if not exists players
(
    id integer primary key,
    xp integer default 0,
    'level' integer default 0,
    name text unique,
    balance integer default 0,
    registered integer not null default 0,
    lastfia text default "NEVER",
    lastpockets text default "NEVER"
);

create table if not exists items
(
    id integer primary key,
    name text not null,
    rarity text not null,
    cost integer
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

insert or ignore into items (name, rarity, cost) values
    ('Лопата','Cheap',200),
    ('Поисковый фонарь','Rare',7000),
    ('Подвеска ''Nosebleed''','Rare',12000),
    ('Струны','Cheap',500),
    ('Футболка ''Drain''','Cheap',500),
    ('Банан','Cheap',100),
    ('Чашка ''Египет''','Rare',5000),
    ('Носки','Cheap',100),
    ('Ручка','Cheap',100),
    ('Баллончик с краской','Common',750),
    ('Платок','Common',150),
    ('Пачка сигарет','Cheap',50),
    ('Синий браслет','Common',300),
    ('Красный браслет','Common',300),
    ('Желтый браслет','Common',300),
    ('Зеленый браслет','Common',300),
    ('Браслет ''Орион''','Common',1000),
    ('Браслет ''Сириус''','Common',900),
    ('Зубная щетка','Cheap',50),
    ('Шоколадка','Cheap',200),
    ('Рюкзак','Rare',7500),
    ('Сим-карта 777','Rare',6000),
    ('Стальной нож','Common',600),
    ('Стиральный порошок','Cheap',100),
    ('💎 Плюшевая Аянами Рей','Gift',50000),
    ('Цветная резинка для волос','Gift',17000),
    ('Отвертка','Cheap',150),
    ('Брелок','Cheap',250),
    ('USB провод','Cheap', 100),
    ('Бутылка вина ''Cabernet Sauvignon''','Rare', 10000),
    ('Винтажный журнал','Gift', 25000),
    ('Бусы','Common', 900),
    ('Крекеры','Common', 450),
    ('Чулки','Cheap', 69),
    ('Чупа-чупс','Cheap', 69),
    ('Витаминки','Common', 100),
    ('Букет цветов','Common', 300),
    ('📀 Whirr - Feels Like You','Gift', 50000),
    ('Камень', 'Cheap', 1),
    ('Крем для рук', 'Cheap', 80),
    ('Энергетик', 'Common', 170),
    ('Ожерелье', 'Rare', 3500),
    ('Кукурушки))0', 'Rare', 9000),
    ('Бипки', 'Cheap', 700),
    ('Текст песни ''FF''', 'Rare', 2500),
    ('Журнал Евангелион', 'Rare', 6700),
    ('🐟Удочка', 'Gift', 10000),
    ('Карась', 'Common', 100),
    ('Горбуша', 'Common', 100),
    ('Журнал Евангелион', 'Rare', 6700),
    ('Бычок', 'Common', 100),
    ('Бутылка', 'Cheap', 5);



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
    player_id,
    bonus integer not null default 0,
    coinWins integer,
    coinLosses integer,
    coffee integer default 0,
    tea integer default 0,

    foreign key (player_id) references players (id) on update cascade

);