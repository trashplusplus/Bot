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

insert or ignore into items values
    (1,'Лопата','Cheap',200),
    (2,'Поисковый фонарь','Rare',7000),
    (3,'Подвеска ''Nosebleed''','Rare',12000),
    (4,'Струны','Cheap',500),
    (5,'Футболка ''Drain''','Cheap',500),
    (6,'Банан','Cheap',100),
    (7,'Чашка ''Египет''','Rare',5000),
    (8,'Носки','Cheap',100),
    (9,'Ручка','Cheap',100),
    (10,'Баллончик с краской','Common',750),
    (11,'Платок','Common',150),
    (12,'Пачка сигарет','Cheap',50),
    (13,'Синий браслет','Common',300),
    (14,'Красный браслет','Common',300),
    (15,'Желтый браслет','Common',300),
    (16,'Зеленый браслет','Common',300),
    (17,'Браслет ''Орион''','Common',1000),
    (18,'Браслет ''Сириус''','Common',900),
    (19,'Зубная щетка','Cheap',50),
    (20,'Шоколадка','Cheap',200),
    (21,'Рюкзак','Rare',7500),
    (22,'Сим-карта 777','Rare',6000),
    (23,'Стальной нож','Common',600),
    (24,'Стиральный порошок','Cheap',100),
    (25,'💎 Плюшевая Аянами Рей','Gift',50000),
    (26,'Цветная резинка для волос','Gift',17000),
    (27,'Отвертка','Cheap',150),
    (28,'Брелок','Cheap',250),
    (29,'USB провод','Cheap', 100),
    (30,'Бутылка вина ''Cabernet Sauvignon''','Rare', 10000),
    (31,'Винтажный журнал','Gift', 25000),
    (32,'Бусы','Common', 900),
    (33,'Крекеры','Common', 450),
    (34,'Чулки','Cheap', 69),
    (35,'Чупа-чупс','Cheap', 69),
    (36,'Витаминки','Common', 100),
    (37,'Букет цветов','Common', 300),
    (38,'📀 Whirr - Feels Like You','Gift', 50000),
    (39,'Камень', 'Cheap', 1),
    (40,'Крем для рук', 'Cheap', 80),
    (41,'Энергетик', 'Common', 170),
    (42,'Ожерелье', 'Rare', 3500),
    (43,'Кукурушки))0', 'Rare', 9000),
    (44,'Бипки', 'Cheap', 700),
    (45,'Текст песни ''FF''', 'Rare', 2500),
    (46,'Бычок', 'Common', 100),
    (47,'Карась', 'Common', 100),
    (48,'Горбуша', 'Common', 100),
    (49,'Журнал Евангелион', 'Rare', 6700),
    (50,'Удочка', 'Rare', 5000),
    (51,'Бутылка', 'Cheap', 5);



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