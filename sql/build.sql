create database chat;
use chat;

create table user(
	id int primary key auto_increment,
	name varchar(50),
	password varchar(50),
	age int,
	birthday varchar(20),
	sex int,
	imgpath varchar(50),
	sign varchar(50)
);

create table friends(
	user_id1 int not null,
	user_id2 int not null
);