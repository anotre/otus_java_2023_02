create table clients
(
    id bigserial not null primary key,
    name varchar(255) not null
);
create table addresses 
(
    id bigserial not null primary key,
    client_id  bigint not null references clients (id), -- 
    street varchar(255) not null
);
create table phones 
(
    id bigserial not null primary key,
    number varchar(255) not null,
    client_id bigint not null references clients (id) -- 
);
create index idx_phone_client_id on phones (client_id);

 alter table if exists addresses add constraint FK21gyuophuha3vq8t1os4x2jtl foreign key (client_id) references clients;
 alter table if exists phones add constraint FK2ovgkw92fjf0rn5yksjxa755b foreign key (client_id) references clients;