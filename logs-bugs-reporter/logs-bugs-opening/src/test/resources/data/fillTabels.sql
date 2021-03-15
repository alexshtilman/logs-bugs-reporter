DELETE FROM bugs;
DELETE FROM artifacts;
DELETE FROM programmers;

insert into programmers (id,name,email) VALUES
(1, 'Avior','Avior@gmail.com'),
(2,	'Eina','Eina@gmail.com'),
(3,	'Lior','Lior@gmail.com'),
(4,	'Yonat','Yonat@gmail.com');


insert into artifacts (artifact_id,programmer_id) VALUES
('LogTypeAndCountDto.class',	1),
('LogsDbRepo.class',	4),
('LogDto.class',	2),
('LogDoc.class',	4),
('ApiError.class',	1),
('LogsAnalyzer.class',	4),
('BugsOppenningService.class', 3);


