create table users
(
    id       BINARY(16)   not null,
    email    varchar(255) not null unique,
    password varchar(255),
    primary key (id)
) engine = InnoDB;

alter table users
    add index users_ak01 (email);

create table photo
(
    id         BINARY(16)   not null,
    created_on datetime(6),
    updated_on datetime(6),
    filename   varchar(255) not null,
    url        varchar(255) not null,
    review_id  BINARY(16),
    primary key (id)
) engine = InnoDB;

alter table photo
    add constraint photo_fk01
        foreign key (review_id) references review (id);

alter table photo
    add index photo_ak01 (review_id);

create table place
(
    id         BINARY(16) not null,
    created_on datetime(6),
    updated_on datetime(6),
    name       varchar(255),
    primary key (id)
) engine = InnoDB;

alter table place
    add unique place_ak01 (name);

alter table place
    add index place_ak02 (name);

create table review
(
    id         BINARY(16)   not null,
    created_on datetime(6),
    updated_on datetime(6),
    content    varchar(255) not null,
    place_id   BINARY(16)   not null,
    user_id    BINARY(16)   not null,
    primary key (id)
) engine = InnoDB;

alter table review
    add constraint review_fk01
        foreign key (user_id) references users (id);

alter table review
    add constraint review_fk02
        foreign key (place_id) references place (id);

ALTER TABLE review
    add unique review_ak01 (user_id, place_id);

alter table review
    add index review_ak02 (user_id);

alter table review
    add index review_ak03 (place_id);

create table user_point
(
    id         BINARY(16) not null,
    created_on datetime(6),
    updated_on datetime(6),
    amount     bigint     not null,
    review_id  BINARY(16) not null,
    user_id    BINARY(16) not null,
    primary key (id)
) engine = InnoDB;

alter table user_point
    add constraint user_point_fk01
        foreign key (user_id) references users (id);
alter table user_point
    add constraint user_point_fk02
        foreign key (review_id) references review (id);

alter table user_point
    add index user_point_ak01 (user_id);

alter table user_point
    add index user_point_ak02 (review_id)
