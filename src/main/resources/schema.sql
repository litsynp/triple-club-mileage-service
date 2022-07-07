# drop table if exists photo;
# drop table if exists place;
# drop table if exists review;
# drop table if exists user_point;
# drop table if exists users;

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

create table place
(
    id         BINARY(16) not null,
    created_on datetime(6),
    updated_on datetime(6),
    name       varchar(255),
    primary key (id)
) engine = InnoDB;

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

create table users
(
    id       BINARY(16)   not null,
    email    varchar(255) not null,
    password varchar(255),
    primary key (id)
) engine = InnoDB;

alter table users
    add constraint user_AK01 unique (email);

alter table photo
    add constraint FKnx36dfpxbxmxifyiq7yvhiohn
        foreign key (review_id)
            references review (id);

alter table review
    add constraint FKn429agmmvh298piqrnnd4gbfg
        foreign key (place_id)
            references place (id);

alter table review
    add constraint FK6cpw2nlklblpvc7hyt7ko6v3e
        foreign key (user_id)
            references users (id);

alter table user_point
    add constraint FKem95jv3kjy52bx01ros3ct5ti
        foreign key (review_id)
            references review (id);

alter table user_point
    add constraint FKl7fer3vdwhdrg5tj3rhwf130n
        foreign key (user_id)
            references users (id);
