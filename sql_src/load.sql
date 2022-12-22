create table tmp_staff(
    Name varchar(80),
    Type varchar(80),
    Company varchar(80),
    City varchar(80),
    Gender varchar(80),
    Age int,
    Phone varchar(80),
    Password varchar(80)
);



create table tmp_record(
    Item_Name varchar(80),
    Item_Class varchar(80),
    Item_Price float,
    Retrieval_City varchar(80),
    Retrieval_Courier varchar(80),
    Delivery_City varchar(80),
    Delivery_Courier varchar(80),
    Export_City varchar(80),
    Import_City varchar(80),
    Export_Tax float,
    Import_Tax float,
    Export_Officer varchar(80),
    Import_Officer varchar(80),
    Container_Code varchar(80),
    Container_Type varchar(80),
    Ship_Name varchar(80),
    Company_Name varchar(80),
    Item_State varchar(80)

);
copy tmp_staff from '/home/kali/Desktop/staffs.csv' csv;
copy tmp_record from '/home/kali/Desktop/records.csv' csv;

insert into City(
    select distinct city from tmp_staff where city is not null
);
insert into containers(
    select distinct Container_Code,Container_Type from tmp_record where Container_Code is not null
);
insert into company(
    select distinct company from tmp_staff where company is not null
);
insert into item (
    select Item_Name,Item_Class,Item_Price,Item_State,Container_Code from tmp_record
);
insert into courier (
    select Name,Phone,Age,Password,Gender,Company,City from tmp_staff where type='Courier'
);
insert into company_manager (
    select Name,Phone,Age,Password,Gender,Company,City from tmp_staff where type='Company Manager'
);
insert into seaport_officer (
    select Name,Phone,Age,Password,Gender,Company,City from tmp_staff where type='Seaport Officer'
);
insert into sustc_department_manager (
    select Name,Phone,Age,Password,Gender,Company,City from tmp_staff where type='SUSTC Department Manager'
);

insert into ship(
    select distinct Ship_Name,Company_Name from tmp_record where Ship_Name is not null
);

insert into import(
    select Item_Name,Import_Officer,Import_Tax from tmp_record
);

insert into export(
    select Item_Name,Export_Officer,Export_Tax from tmp_record
);

insert into retrieval(
    select Item_Name,Retrieval_Courier from tmp_record
);

insert into delivery(
    select Item_Name,Delivery_Courier from tmp_record where Delivery_Courier is not null
);









