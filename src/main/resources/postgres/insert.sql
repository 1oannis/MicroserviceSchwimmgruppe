INSERT INTO login (id, username, password) VALUES ('30000000-0000-0000-0000-000000000000','admin','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$iE4+NpU8xcjEu8z2YEtjYw$DbmfrGjL6ac04HGHQ0tdng6vxg3OG/A+GSY3WVUdbNU');
INSERT INTO login (id, username, password) VALUES ('30000000-0000-0000-0000-000000000001','alpha','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$iE4+NpU8xcjEu8z2YEtjYw$DbmfrGjL6ac04HGHQ0tdng6vxg3OG/A+GSY3WVUdbNU');
INSERT INTO login (id, username, password) VALUES ('30000000-0000-0000-0000-000000000002','alpha2','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$iE4+NpU8xcjEu8z2YEtjYw$DbmfrGjL6ac04HGHQ0tdng6vxg3OG/A+GSY3WVUdbNU');
INSERT INTO login (id, username, password) VALUES ('30000000-0000-0000-0000-000000000030','alpha3','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$iE4+NpU8xcjEu8z2YEtjYw$DbmfrGjL6ac04HGHQ0tdng6vxg3OG/A+GSY3WVUdbNU');
INSERT INTO login (id, username, password) VALUES ('30000000-0000-0000-0000-000000000040','delta','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$iE4+NpU8xcjEu8z2YEtjYw$DbmfrGjL6ac04HGHQ0tdng6vxg3OG/A+GSY3WVUdbNU');
INSERT INTO login (id, username, password) VALUES ('30000000-0000-0000-0000-000000000050','epsilon','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$iE4+NpU8xcjEu8z2YEtjYw$DbmfrGjL6ac04HGHQ0tdng6vxg3OG/A+GSY3WVUdbNU');
INSERT INTO login (id, username, password) VALUES ('30000000-0000-0000-0000-000000000060','phi','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$iE4+NpU8xcjEu8z2YEtjYw$DbmfrGjL6ac04HGHQ0tdng6vxg3OG/A+GSY3WVUdbNU');

INSERT INTO login_rollen (login_id, rolle) VALUES ('30000000-0000-0000-0000-000000000000','ADMIN');
INSERT INTO login_rollen (login_id, rolle) VALUES ('30000000-0000-0000-0000-000000000000','SCHWIMMGRUPPE');
INSERT INTO login_rollen (login_id, rolle) VALUES ('30000000-0000-0000-0000-000000000000','ACTUATOR');
INSERT INTO login_rollen (login_id, rolle) VALUES ('30000000-0000-0000-0000-000000000001','SCHWIMMGRUPPE');
INSERT INTO login_rollen (login_id, rolle) VALUES ('30000000-0000-0000-0000-000000000002','SCHWIMMGRUPPE');
INSERT INTO login_rollen (login_id, rolle) VALUES ('30000000-0000-0000-0000-000000000030','SCHWIMMGRUPPE');
INSERT INTO login_rollen (login_id, rolle) VALUES ('30000000-0000-0000-0000-000000000040','SCHWIMMGRUPPE');
INSERT INTO login_rollen (login_id, rolle) VALUES ('30000000-0000-0000-0000-000000000050','SCHWIMMGRUPPE');
INSERT INTO login_rollen (login_id, rolle) VALUES ('30000000-0000-0000-0000-000000000060','SCHWIMMGRUPPE');

INSERT INTO schwimmhalle (id, bezeichnung, plz, ort) VALUES ('00000000-1000-0000-0000-000000000000', 'Sonnenbad', '76185','Mühlburg');
INSERT INTO schwimmhalle (id, bezeichnung, plz, ort) VALUES ('00000000-1000-0000-0000-000000000001', 'Europabad', '76133','KA-Weststadt');
INSERT INTO schwimmhalle (id, bezeichnung, plz, ort) VALUES ('00000000-1000-0000-0000-000000000002', 'Hallenbad', '76137','KA-Südweststadt');
INSERT INTO schwimmhalle (id, bezeichnung, plz, ort) VALUES ('00000000-1000-0000-0000-000000000003', 'Freibad', '76189','Daxlanden');
INSERT INTO schwimmhalle (id, bezeichnung, plz, ort) VALUES ('00000000-1000-0000-0000-000000000004', 'Turmbergbad', '76227','Durlach');
INSERT INTO schwimmhalle (id, bezeichnung, plz, ort) VALUES ('00000000-1000-0000-0000-000000000005', 'Freibad', '73131','Neureut');

-- HTTP GET
INSERT INTO schwimmgruppe (id, name, aktiv, trainingstermin1, trainingstermin2, ligaklasse, schwimmhalle_id, erzeugt, aktualisiert) VALUES ('00000000-0000-0000-0000-000000000000', 'A-Jugend', true, '2024-07-20 16:00:00', '2024-07-23 16:00:00', '1.BL', '00000000-1000-0000-0000-000000000000', '2021-06-01 00:00:00', '2021-06-01 00:00:00');
INSERT INTO schwimmgruppe (id, name, aktiv, trainingstermin1, trainingstermin2, ligaklasse, schwimmhalle_id, erzeugt, aktualisiert) VALUES ('00000000-0000-0000-0000-000000000001', 'B-Jugend', true, '2024-07-20 18:00:00', '2024-07-22 16:00:00', 'LL', '00000000-1000-0000-0000-000000000001', '2021-06-01 00:00:00', '2021-06-01 00:00:00');
INSERT INTO schwimmgruppe (id, name, aktiv, trainingstermin1, trainingstermin2, ligaklasse, schwimmhalle_id, erzeugt, aktualisiert) VALUES ('00000000-0000-0000-0000-000000000002', 'C-Jugend', true, '2024-07-20 20:00:00', '2024-07-23 18:00:00', 'SL', '00000000-1000-0000-0000-000000000002', '2021-06-01 00:00:00', '2021-06-01 00:00:00');
INSERT INTO schwimmgruppe (id, name, aktiv, trainingstermin1, trainingstermin2, ligaklasse, schwimmhalle_id, erzeugt, aktualisiert) VALUES ('00000000-0000-0000-0000-000000000003', 'D-Jugend', false, '2024-07-21 15:00:00', '2024-07-25 17:00:00', 'KEINE', '00000000-1000-0000-0000-000000000004', '2021-06-01 00:00:00', '2021-06-01 00:00:00');
INSERT INTO schwimmgruppe (id, name, aktiv, trainingstermin1, trainingstermin2, ligaklasse, schwimmhalle_id, erzeugt, aktualisiert) VALUES ('00000000-0000-0000-0000-000000000004', 'E-Jugend', false, '2024-07-21 17:00:00', '2024-07-24 19:00:00', 'KEINE', '00000000-1000-0000-0000-000000000005', '2021-06-01 00:00:00', '2021-06-01 00:00:00');
-- HTTP PUT
INSERT INTO schwimmgruppe (id, name, aktiv, trainingstermin1, trainingstermin2, ligaklasse, schwimmhalle_id, erzeugt, aktualisiert) VALUES ('00000000-0000-0000-0000-000000000010', 'F-Jugend', true, '2024-07-21 16:00:00', '2024-07-24 16:00:00', 'SL', '00000000-1000-0000-0000-000000000000', '2021-06-01 00:00:00', '2021-06-01 00:00:00');
INSERT INTO schwimmgruppe (id, name, aktiv, trainingstermin1, trainingstermin2, ligaklasse, schwimmhalle_id, erzeugt, aktualisiert) VALUES ('00000000-0000-0000-0000-000000000011', 'G-Jugend', false, '2024-07-21 18:00:00', '2024-07-24 20:00:00', 'KEINE', '00000000-1000-0000-0000-000000000002', '2021-06-01 00:00:00', '2021-06-01 00:00:00');
-- HTTP PATCH
INSERT INTO schwimmgruppe (id, name, aktiv, trainingstermin1, trainingstermin2, ligaklasse, schwimmhalle_id, erzeugt, aktualisiert) VALUES ('00000000-0000-0000-0000-000000000020', 'H-Jugend', true, '2024-07-21 17:00:00', '2024-07-24 19:00:00', 'SL', '00000000-1000-0000-0000-000000000003', '2021-06-01 00:00:00', '2021-06-01 00:00:00');
INSERT INTO schwimmgruppe (id, name, aktiv, trainingstermin1, trainingstermin2, ligaklasse, schwimmhalle_id, erzeugt, aktualisiert) VALUES ('00000000-0000-0000-0000-000000000021', 'I-Jugend', true, '2024-07-21 17:00:00', '2024-07-24 19:00:00', '2.BL', '00000000-1000-0000-0000-000000000005', '2021-06-01 00:00:00', '2021-06-01 00:00:00');
