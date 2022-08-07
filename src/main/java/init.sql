create table if not exists players
(
    id integer primary key,
    name text,
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

insert or ignore values into items (name, rarity, cost)
    ("Needle", "Common", 100),
    ("Book", "Common", 300),
    ("Teabag", "Rare", 50),
    ("Old coin", "Rare", 100),
    ("Diamond", "Rare", 20000);


create table if not exists inventory
(
    id integer primary key,
    player_id,
    item_id,

    foreign key (player_id) references players (id) on delete cascade,
    foreign key (item_id) references items (id) on update cascade on delete cascade
);