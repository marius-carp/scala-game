# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "games" ("id_game" SERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL);

# --- !Downs

drop table "games";

