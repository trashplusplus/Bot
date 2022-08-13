create table if not exists players
(
    id integer primary key,
    xp integer default 0,
    'level' integer default 0,
    name text unique,
    balance integer default 0,
    state text not null,
    lastfia text default "NEVER"
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
    name text not null,
    cost integer,
    sellerName text not null
);

insert or ignore into items values
    (1,'Лопата','Common',200),
    (2,'Поисковый фонарь','Rare',7000),
    (3,'Подвеска ''Nosebleed''','Rare',30000),
    (4,'Струны','Common',500),
    (5,'Футболка ''Drain''','Common',500),
    (6,'Банан','Common',100),
    (7,'Чашка ''Египет''','Rare',1000),
    (8,'Носки','Common',100),
    (9,'Ручка','Common',100),
    (10,'Баллончик с краской','Common',750),
    (11,'Платок','Common',150),
    (12,'Пачка сигарет','Common',50),
    (13,'Синий браслет','Common',300),
    (14,'Красный браслет','Common',300),
    (15,'Желтый браслет','Common',300),
    (16,'Зеленый браслет','Common',300),
    (17,'Браслет ''Орион''','Common',1000),
    (18,'Браслет ''Сириус''','Common',900),
    (19,'Зубная щетка','Common',50),
    (20,'Шоколадка','Common',200),
    (21,'Рюкзак','Rare',700),
    (22,'Синий фонарик','Gift',25000);


create table if not exists inventory
(
    id integer primary key,
    player_id,
    item_id,

    foreign key (player_id) references players (id) on delete cascade,
    foreign key (item_id) references items (id) on update cascade on delete cascade
);