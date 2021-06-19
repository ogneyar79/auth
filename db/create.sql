create table employee
(
    id         serial primary key not null,
    name       varchar(50),
    surname    varchar(50),
    codeI      varchar(30),
    hiringDate timestamp default now()
);


create table employee_person
(
    employee_id int references employee (id),
    person_id   int references person (id)
) ;
insert into  employee_person (person_id) values (1);
insert into  employee_person (person_id) values (5),(6),(7);