DELETE bugs;
DELETE programmers;

insert into programmers (id,name,email) VALUES
(1, 'Sara','sara@gmail.com'),
(2, 'Moshe','moshe@gmail.com');

insert into bugs (
					id,
					description,
					dateOppen,
					dateClose,
					status,
					seriosness,
					oppeningMethod,
					programmer
				)
VALUES
(1,'BLOCKING bug description','21/03/2018, 11:36:14',null,'ASSIGNED','BLOCKING','MANUAL',1),
(2,'CRITICAL bug description','21/03/2019, 11:36:14',null,'OPEND','CRITICAL','AUTOMATIC',1),
(3,'MINOR bug description','21/03/2019, 11:36:14','21/06/2019, 11:36:14','CLOSED','MINOR','AUTOMATIC',1),
(4,'CRITICAL bug description','21/03/2019, 11:36:14',null,'OPEND','CRITICAL','AUTOMATIC',null),
(5,'bug description','21/03/2018, 11:36:14','22/03/2018, 11:36:14', 'CLOSED', 'COSMETIC','MANUAL',2);

