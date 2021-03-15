DELETE FROM artifacts;
DELETE FROM programmers;

insert into programmers (id,name,email) VALUES
(1, 'Avior','Avior@gmail.com');


insert into artifacts (artifact_id,programmer_id) VALUES
('LogTypeAndCountDto.class',	1);


