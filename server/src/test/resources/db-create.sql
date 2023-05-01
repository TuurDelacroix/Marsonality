drop table if exists chip;
drop table if exists profile_trait;
drop table if exists martian_trait;
drop table if exists traits;
drop table if exists profile;
drop table if exists martian;
drop table if exists notification;

-- Notification table
create table notification
(
    id      int             auto_increment,
    title   varchar(255),
    options varchar(8000)
);

-- Stores all of our customers (Martians)
create table martian
(
    marsId      int             primary key,
    firstname   varchar(50),
    lastname    varchar(50),
    birthdate   date,                   -- Can be used to determine age of the Martian
    cooldown_start_moment   datetime    -- Is `null` when there is no cooldown
);

-- Stores all profiles Martians created, linked to their marsId
create table profile
(
    name                    varchar(25)     primary key,    -- Name of the profile. Example: Happy Hans
    marsId                  int,                            -- marsId of the Martian that created this profile
    price                   varchar(50),                    -- Price is stored as a varchar, because the highest price is too big to store as a regular number (BigInteger in Java needs a String to be created
    creation_date           date,
    counter                 int,                            -- How many times the Martian has switched to this profile
    in_use                  bit,                            -- Shows if the Martian is using this profile or not
    foreign key (marsId) references martian(marsId)
);

-- Stores all the traits we offer to Martians
create table traits
(
    name        varchar(50)     primary key,    -- The name of the trait. Example: Calm, Anxious, Asocial...
    description varchar(512),                   -- Short description of the trait
    type        varchar(8)                      -- Positive, Neutral or Negative
);

-- Stores for each Martian which traits they have available
create table martian_trait
(
    marsId      int,
    name        varchar(50),
    foreign key (name)   references traits(name),
    foreign key (marsId) references martian(marsId)
);

-- Stores all profiles with the traits linked to them
create table profile_trait
(
    profileName     varchar(25),
    traitName       varchar(50),
    foreign key (profileName)   references profile(name),
    foreign key (traitName)     references traits(name)
);

create table chip
(
    id          int,
    activated   bit,
    foreign key (id) references martian(marsId)
);