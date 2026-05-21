create table if not exists external_students
(
    id         uuid primary key,
    student_id uuid         not null unique,
    name       varchar(255) not null,
    email      varchar(255) not null,
    age        integer      not null,
    extra_info varchar(255) not null
);

create table if not exists processed_events
(
    id           uuid primary key,
    event_id     uuid                     not null unique,
    event_type   varchar(255)             not null,
    processed_at timestamp with time zone not null
);

create index if not exists idx_processed_events_event_id
    on processed_events (event_id);

create table if not exists outbox_events
(
    id             uuid primary key,
    event_id       uuid                     not null unique,
    aggregate_type varchar(255)             not null,
    aggregate_id   uuid                     not null,
    event_type     varchar(255)             not null,
    topic          varchar(255)             not null,
    payload        text                     not null,
    status         varchar(50)              not null,
    attempts       integer                  not null default 0,
    last_error     text,
    created_at     timestamp with time zone not null,
    published_at   timestamp with time zone
);

create index if not exists idx_outbox_events_status_created_at
    on outbox_events (status, created_at);