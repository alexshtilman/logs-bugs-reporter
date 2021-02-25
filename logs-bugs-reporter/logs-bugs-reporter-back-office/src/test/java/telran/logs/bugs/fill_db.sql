DELETE bugs;
DELETE programmers;

TRUNCATE TABLE bugs RESTART IDENTITY;

insert into programmers (id,name,email) VALUES
(1, 'Sara','sara@gmail.com'),
(2, 'Moshe','moshe@gmail.com'),
(3, 'new','new@gmail.com');

insert into bugs (
					id,
					description,
					date_oppen,
					date_close,
					status,
					seriosness,
					oppening_method,
					programmer_id
				)
VALUES
(1,'BLOCKING bug description','1991-01-01',null,'ASSIGNED','BLOCKING','MANUAL',1),
(2,'CRITICAL bug description','1991-01-01',null,'OPEND','CRITICAL','AUTOMATIC',1),
(3,'MINOR bug description','1991-01-01','2018-01-01','CLOSED','MINOR','AUTOMATIC',1),
(4,'CRITICAL bug description','1991-01-01',null,'OPEND','CRITICAL','AUTOMATIC',null),
(5,'COSMETIC bug description','1991-01-01','1991-02-01', 'CLOSED', 'COSMETIC','MANUAL',2);

