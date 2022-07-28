create table photo
(
    id         BINARY(16)   not null,
    filename   varchar(255) not null,
    url        varchar(255) not null,
    review_id  BINARY(16),
    created_on datetime(6),
    updated_on datetime(6),
    primary key (id)
) engine = InnoDB;

alter table photo
    add constraint photo_fk01
        foreign key (review_id) references review (id)
            on delete cascade
            on update cascade;

alter table photo
    add index photo_ak01 (review_id);

create table review
(
    id         BINARY(16)   not null,
    content    varchar(255) not null,
    user_id    BINARY(16)   not null,
    place_id   BINARY(16)   not null,
    created_on datetime(6),
    updated_on datetime(6),
    primary key (id)
) engine = InnoDB;

alter table review
    add unique review_ak01 (user_id, place_id);

alter table review
    add index review_ak02 (user_id);

alter table review
    add index review_ak03 (place_id);

create table user_point
(
    id         BINARY(16) not null,
    user_id    BINARY(16) not null,
    review_id  BINARY(16),
    amount     bigint     not null,
    created_on datetime(6),
    updated_on datetime(6),
    primary key (id)
) engine = InnoDB;

alter table user_point
    add constraint user_point_fk02
        foreign key (review_id) references review (id)
            on delete set null
            on update cascade;

alter table user_point
    add index user_point_ak01 (user_id);

alter table user_point
    add index user_point_ak02 (review_id);
