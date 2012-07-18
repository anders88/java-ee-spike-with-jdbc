create sequence person_seq;

create table person (
    id integer primary key,
    full_name varchar(100) not null
);
