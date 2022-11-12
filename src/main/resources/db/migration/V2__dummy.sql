#insert into student (id, handle, gebuchter_urlaub) values (1, 'ilumary', 100), (2, 'causebencancode', 50), (82348173, 'dniklas27', 180);
#insert into klausur values (123456, 111111, 'ProPra III', date(now()), time(now()), addtime(now(),'01:30:00'), false);
#insert into klausur values (123455, 111111, 'ProPra III', adddate(now(), 1), time(now()), addtime(now(),'01:30:00'), false);
#insert into klausurbuchungen (student_id, klausur_id) values (82348173, 123456), (82348173, 123455);
#insert into urlaub (student_id, datum, start, ende) values (82348173, date(now()), time(now()), addtime(now(),'01:30:00'));
#insert into urlaub (student_id, datum, start, ende) values (82348173, adddate(now(), 1), time(now()), addtime(now(),'01:30:00'));
