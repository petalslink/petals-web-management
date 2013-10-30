# --- !Ups

create table alert (
  id                        bigint not null,
  read                      boolean,
  received_at               timestamp,
  source                    varchar(255),
  message                   varchar(255),
  sequence_nb               bigint,
  type                      varchar(255),
  constraint pk_alert primary key (id))
;

create table artifact_url (
  id                        bigint not null,
  name                      varchar(255),
  version                   varchar(255),
  url                       varchar(255),
  date                      timestamp,
  local                     boolean,
  constraint pk_artifact_url primary key (id))
;

create table base_event (
  id                        bigint not null,
  date                      timestamp,
  message                   varchar(255),
  type                      varchar(255),
  emit                      boolean,
  constraint pk_base_event primary key (id))
;

create table node (
  id                        bigint not null,
  host                      varchar(255),
  port                      integer,
  login                     varchar(255),
  password                  varchar(255),
  constraint pk_node primary key (id))
;

create table subscription (
  id                        bigint not null,
  component                 varchar(255),
  component_type            varchar(8),
  host                      varchar(255),
  port                      integer,
  login                     varchar(255),
  password                  varchar(255),
  date                      timestamp,
  status                    varchar(255),
  unsubscribed_at           timestamp,
  constraint pk_subscription primary key (id))
;

create sequence alert_seq;

create sequence artifact_url_seq start with 1000;

create sequence base_event_seq;

create sequence node_seq start with 1000;

create sequence subscription_seq;


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists alert;

drop table if exists artifact_url;

drop table if exists base_event;

drop table if exists node;

drop table if exists subscription;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists alert_seq;

drop sequence if exists artifact_url_seq;

drop sequence if exists base_event_seq;

drop sequence if exists node_seq;

drop sequence if exists subscription_seq;

