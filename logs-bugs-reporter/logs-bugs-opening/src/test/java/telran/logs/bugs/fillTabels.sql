DELETE FROM bugs;
DELETE FROM artifacts;
DELETE FROM programmers;

insert into programmers (id,name) VALUES
(1, 'Avior'),
(2,	'Eina'),
(3,	'Lior'),
(4,	'Yonat');


insert into artifacts (artifact_id,programmer_id) VALUES
('LogTypeAndCountDto.class',	1),
('LogsDbRepo.class',	4),
('LogDto.class',	2),
('LogDoc.class',	4),
('ApiError.class',	1),
('LogsAnalyzer.class',	4),
('BugsOppenningService.class', 3);


