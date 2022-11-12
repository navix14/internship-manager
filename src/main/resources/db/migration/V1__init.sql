drop table if exists urlaub;
drop table if exists klausurbuchungen;
drop table if exists klausur;
drop table if exists student;

create table klausur
(
    id     int primary key auto_increment,
    lsf_id  int null,
    name   varchar(255) null,
    datum  date null,
    start  time null,
    ende   time null,
    online bool null
);

create table student
(
    id               int primary key auto_increment,
    github_id        int,
    handle           varchar(255),
    gebuchter_urlaub int null
);

create table klausurbuchungen
(
    id         int primary key auto_increment,
    student_id int,
    klausur_id int,
    constraint klausurbuchungen_klausur_fk
        foreign key (klausur_id) references klausur (id),
    constraint klausurbuchungen_student_fk
        foreign key (student_id) references student (id)
);

create table urlaub
(
    id          int primary key auto_increment,
    student_id  int null,
    datum       date null,
    start       time null,
    ende        time null,
    constraint fehlzeiten_student_handle_fk
        foreign key (student_id) references student (id)
);



