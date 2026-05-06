create table attractions (
    available_seats integer not null,
    maintenance_frequency_days integer not null,
    total_seats integer not null,
    id bigint not null auto_increment,
    description varchar(255) not null,
    image_url varchar(255) not null,
    name varchar(255) not null,
    size varchar(255) not null,
    status varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table bookings (
    email_sent bit not null,
    total_price decimal(38,2) not null,
    visit_date date not null,
    created_at datetime(6),
    hotel_id bigint,
    id bigint not null auto_increment,
    offer_id bigint,
    updated_at datetime(6),
    user_id bigint not null,
    board_type varchar(255) not null,
    emails_participants varbinary(255),
    primary key (id)
) engine=InnoDB;

create table employees (
    active bit not null,
    id bigint not null auto_increment,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    dni varchar(255) not null,
    email varchar(255) not null,
    employee_type varchar(255) not null,
    shift varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table hotels (
    available_places integer not null,
    available_rooms integer not null,
    full_board_price decimal(38,2) not null,
    half_board_price decimal(38,2) not null,
    total_places integer not null,
    total_rooms integer not null,
    id bigint not null auto_increment,
    description varchar(255) not null,
    image_url varchar(255) not null,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table internal_credentials (
    active bit not null,
    created_at datetime(6),
    id bigint not null auto_increment,
    updated_at datetime(6),
    email varchar(255) not null,
    password_hash varchar(255) not null,
    username varchar(255) not null,
    role enum ('ADMIN') not null,
    primary key (id)
) engine=InnoDB;

create table maintenance (
    scheduled_date date not null,
    attraction_id bigint not null,
    id bigint not null auto_increment,
    status varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table maintenance_technicians (
    employee_id bigint not null,
    maintenance_id bigint not null
) engine=InnoDB;

create table offers (
    included_tickets integer not null,
    total_price decimal(38,2) not null,
    hotel_id bigint not null,
    id bigint not null auto_increment,
    board_type varchar(255) not null,
    description varchar(255) not null,
    image_url varchar(255) not null,
    title varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table payments (
    amount decimal(38,2) not null,
    booking_id bigint not null,
    created_at datetime(6),
    id bigint not null auto_increment,
    updated_at datetime(6),
    payment_method varchar(255),
    transaction_reference varchar(255),
    status enum ('CANCELLED','FAILED','PAID','PENDING') not null,
    primary key (id)
) engine=InnoDB;

create table shifts (
    end_date date not null,
    start_date date not null,
    employee_id bigint not null,
    id bigint not null auto_increment,
    shift varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table tickets (
    price decimal(38,2) not null,
    booking_id bigint not null,
    id bigint not null auto_increment,
    age_range varchar(255) not null,
    holder_full_name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table users (
    birth_date date not null,
    created_at datetime(6),
    id bigint not null auto_increment,
    updated_at datetime(6),
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    dni varchar(255) not null,
    email varchar(255) not null,
    phone varchar(255),
    primary key (id)
) engine=InnoDB;

alter table employees add constraint UKo749373qjctaghb9gx23j7bm unique (dni);
alter table employees add constraint UKj9xgmd0ya5jmus09o0b8pqrpb unique (email);
alter table internal_credentials add constraint UKbttoaqwf3sv49b5enc27fhs7g unique (email);
alter table internal_credentials add constraint UKs0655tq5euxuc4fjx547h41xa unique (username);
alter table payments add constraint UKnuscjm6x127hkb15kcb8n56wo unique (booking_id);
alter table users add constraint UK6aphui3g30h49muho4c91n0yl unique (dni);
alter table users add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

alter table bookings add constraint FK7y09f5lun38jnooaw2hch0ke9 foreign key (hotel_id) references hotels (id);
alter table bookings add constraint FK1rpb9nchx8835ck1u3pla0t8k foreign key (offer_id) references offers (id);
alter table bookings add constraint FKeyog2oic85xg7hsu2je2lx3s6 foreign key (user_id) references users (id);
alter table maintenance add constraint FKjpl8fl7o9k4bi2qtnglu02c2y foreign key (attraction_id) references attractions (id);
alter table maintenance_technicians add constraint FK9jr0d1lm69okmiqch7ooaco8x foreign key (employee_id) references employees (id);
alter table maintenance_technicians add constraint FK806y3xepf2cf5ywwwo1iqn1bj foreign key (maintenance_id) references maintenance (id);
alter table offers add constraint FK9aljdqoi39d35fattfdgp95ac foreign key (hotel_id) references hotels (id);
alter table payments add constraint FKc52o2b1jkxttngufqp3t7jr3h foreign key (booking_id) references bookings (id);
alter table shifts add constraint FKtbsbc3nmr4b1vlwtnd944q9u7 foreign key (employee_id) references employees (id);
alter table tickets add constraint FKefja4avuu7g29t78mxifrsynb foreign key (booking_id) references bookings (id);
