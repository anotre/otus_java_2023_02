create sequence address_seq start with 1 increment by 1;
create sequence client_seq start with 1 increment by 1;
create sequence phone_seq start with 1 increment by 1;

create table addresses (
    id bigint not null,
    street varchar(255),
    primary key (id)
);
create table clients (
    id bigint not null,
    name varchar(255),
    address_id bigint,
    primary key (id)
);
    create table phones (
    id bigint not null,
    number varchar(255),
    client_id bigint not null,
    primary key (id)
);

alter table if exists clients add constraint FK21gyuophuha3vq8t1os4x2jtl foreign key (address_id) references addresses;
alter table if exists phones add constraint FK2ovgkw92fjf0rn5yksjxa755b foreign key (client_id) references clients;