DROP TABLE IF EXISTS "urlaub";
DROP TABLE IF EXISTS "klausurbuchungen";
DROP TABLE IF EXISTS "klausur";
DROP TABLE IF EXISTS "student";
CREATE TABLE "klausur"
(
    id     int PRIMARY KEY AUTO_INCREMENT,
    lsf_id int          NULL,
    name   varchar(255) NULL,
    datum  date         NULL,
    start  time         NULL,
    ende   time         NULL,
    online bool         NULL
);
CREATE TABLE "student"
(
    id               int PRIMARY KEY AUTO_INCREMENT,
    github_id        int,
    handle           varchar(255),
    gebuchter_urlaub int NULL
);
CREATE TABLE "klausurbuchungen"
(
    id           int PRIMARY KEY AUTO_INCREMENT,
    "student_id" int,
    "klausur_id" int,
    FOREIGN KEY ("student_id") REFERENCES "student" (id),
    FOREIGN KEY ("klausur_id") REFERENCES "klausur" (id)
);
CREATE TABLE "urlaub"
(
    id           int PRIMARY KEY AUTO_INCREMENT,
    "student_id" int  NULL,
    datum        date NULL,
    start        time NULL,
    ende         time NULL,
    FOREIGN KEY ("student_id") REFERENCES "student" (id)
);

INSERT INTO "student" (github_id, handle, gebuchter_urlaub)
VALUES (777777,'ilumary', 100),
       (888888,'causebencancode', 50),
       (999999,'dniklas27', 180);
INSERT INTO "klausur"
VALUES (123456, 111111, 'Propra II', CURRENT_DATE, TIME '09:00:00', TIME '10:30:00', FALSE);
INSERT INTO "klausur"
VALUES (123455, 222222, 'Algorithmen & Datenstrukturen', DATEADD('DAY', 2, CURRENT_DATE), TIME '10:30:00',
        TIME '12:00:00', FALSE);
INSERT INTO "klausurbuchungen" ("student_id", "klausur_id")
VALUES (1, 123456),
       (1, 123455);
//INSERT INTO URLAUB (STUDENT_ID, DATUM, START, ENDE) VALUES (82348173, ADDDATE(NOW(), 1), TIME(NOW()), ADDTIME(NOW(),'01:30:00'));
//INSERT INTO URLAUB (STUDENT_ID, DATUM, START, ENDE) VALUES (82348173, DATE(NOW()), TIME(NOW()), ADDTIME(NOW(),'01:30:00'));