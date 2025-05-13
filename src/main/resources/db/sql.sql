create database defcode;

create type role as enum ('ADMIN', 'USER');
create type otp_status as enum ('ACTIVE', 'EXPIRED', 'USED');

create table users
(
    id       serial primary key,
    username varchar(64) unique not null,
    password varchar(32)        not null,
    role     role               not null
);

create table token
(
    id              serial primary key,
    user_id         int references users (id) not null,
    token           varchar(124) unique       not null,
    expiration_date timestamp                 not null,
    revoked         bool default false
);


create table otp_config
(
    id          serial primary key,
    code_length int      not null default 6,
    lifetime    interval not null default '1 minutes'
);
insert into otp_config values (1, 4);

create table otp_codes
(
    id              bigserial primary key,
    code            int                                         not null,
    user_id         int references users (id) on delete cascade not null,
    status          otp_status default 'ACTIVE',
    expiration_time timestamp                                   not null,
    operation_type  int references operation (operation_type) on delete set NULL                                        not null
);

create table operation
(
    operation_type int primary key,
    description    varchar(128) not null
);

insert into operation values (1, 'Login Verification');
insert into operation values (2, 'Account Registration');
insert into operation values (3, 'Password Reset');
insert into operation values (4, 'Transaction Confirmation');
insert into operation values (5, 'Update Contact Information');
insert into operation values (6, 'Account Deletion');
