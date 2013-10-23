create table todo ( 
	_id integer primary key autoincrement,
	category text not null,
	summary text not null, 
	description text not null
);
insert into todo values (1,'Urgent','summary 1','desc 1');
create table category (
	_id integer primary key autoincrement,
	name text not null
);
insert into category values (1,'Urgent');
insert into category values (2,'Normal');