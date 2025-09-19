--USERS

-- you can user gen_random_uuid () to generate random IDs, use this only to generate testdata


insert into users (id, email,first_name,last_name, password)
values ('ba804cb9-fa14-42a5-afaf-be488742fc54', 'admin@example.com', 'James','Bond', '$2a$10$TM3PAYG3b.H98cbRrHqWa.BM7YyCqV92e/kUTBfj85AjayxGZU7d6' ), -- Password: 1234
('0d8fa44c-54fd-4cd0-ace9-2a7da57992de', 'user@example.com', 'Tyler','Durden', '$2a$10$TM3PAYG3b.H98cbRrHqWa.BM7YyCqV92e/kUTBfj85AjayxGZU7d6'), -- Password: 1234
('fa442c12-54fd-4cd0-ace9-2a7da5799114', 'user2@example.com', 'Weyo','Aller', '$2a$10$TM3PAYG3b.H98cbRrHqWa.BM7YyCqV92e/kUTBfj85AjayxGZU7d6') -- Password: 1234
ON CONFLICT DO NOTHING;

INSERT INTO blogpost (id, title, text, category, author, created_at) VALUES
-- === Admin (James Bond) ===========================================
('bc921161-d552-490b-804e-7b87f81bd5f3',
 'Dynamic Configuration Designer',
 'Excepturi autem fuga iure modi ipsam corrupti cum. Vitae est earum veritatis et autem quisquam consequatur. Maiores illum veritatis nisi repellendus. Excepturi odio minus placeat. Perferendis reprehenderit quia ducimus quae amet odit quas voluptas nam. Temporibus quia molestias veniam ut.',
 'SPORT',
 'ba804cb9-fa14-42a5-afaf-be488742fc54',
 '2025-09-18 13:43:32.396606'),

('9f6b9c3b-6bfc-4b7e-9f7e-3f0a5a9f3d11',
 'Event Stream Optimizer',
 'Maiores illum veritatis nisi repellendus. Excepturi odio minus placeat. Perferendis reprehenderit quia.',
 'SPORT',
 'ba804cb9-fa14-42a5-afaf-be488742fc54',
 '2025-09-12 10:05:00')
ON CONFLICT DO NOTHING;


--ROLES
INSERT INTO role(id, name)
VALUES
('ab505c92-7280-49fd-a7de-258e618df074', 'ADMIN'),
('c6aee32d-8c35-4481-8b3e-a876a39b0c02', 'USER')
ON CONFLICT DO NOTHING;

--AUTHORITIES
INSERT INTO authority(id, name)
VALUES ('2ebf301e-6c61-4076-98e3-2a38b31daf86', 'DEFAULT'),
('76d2cbf6-5845-470e-ad5f-2edb9e09a868', 'USER_MODIFY'),
('21c942db-a275-43f8-bdd6-d048c21bf5ab', 'USER_DEACTIVATE'),
('242ff7f7-7e6d-41eb-b52a-59b2e12c5189', 'BLOGPOST_READ'),
('cc9c3170-b2dc-4701-806a-998b74f23650', 'BLOGPOST_CREATE'),
('38296949-7a94-4827-9af5-6d381fd1a507', 'BLOGPOST_UPDATE'),
('a60481f1-8413-4789-9340-572827a140e5', 'BLOGPOST_DELETE')
ON CONFLICT DO NOTHING;

--assign roles to users
insert into users_role (users_id, role_id)
values
       ('ba804cb9-fa14-42a5-afaf-be488742fc54', 'ab505c92-7280-49fd-a7de-258e618df074'),
       ('ba804cb9-fa14-42a5-afaf-be488742fc54', 'c6aee32d-8c35-4481-8b3e-a876a39b0c02'),
       ('fa442c12-54fd-4cd0-ace9-2a7da5799114', 'c6aee32d-8c35-4481-8b3e-a876a39b0c02'),
       ('0d8fa44c-54fd-4cd0-ace9-2a7da57992de', 'c6aee32d-8c35-4481-8b3e-a876a39b0c02')
 ON CONFLICT DO NOTHING;

--assign authorities to roles
INSERT INTO role_authority(role_id, authority_id)
VALUES
('ab505c92-7280-49fd-a7de-258e618df074', '76d2cbf6-5845-470e-ad5f-2edb9e09a868'),
('c6aee32d-8c35-4481-8b3e-a876a39b0c02', '21c942db-a275-43f8-bdd6-d048c21bf5ab'),
-- Blogpost authorities for admin
('ab505c92-7280-49fd-a7de-258e618df074', '242ff7f7-7e6d-41eb-b52a-59b2e12c5189'),
('ab505c92-7280-49fd-a7de-258e618df074', 'cc9c3170-b2dc-4701-806a-998b74f23650'),
('ab505c92-7280-49fd-a7de-258e618df074', '38296949-7a94-4827-9af5-6d381fd1a507'),
('ab505c92-7280-49fd-a7de-258e618df074', 'a60481f1-8413-4789-9340-572827a140e5'),
-- Blogpost authorities for user
('c6aee32d-8c35-4481-8b3e-a876a39b0c02', '242ff7f7-7e6d-41eb-b52a-59b2e12c5189'),
('c6aee32d-8c35-4481-8b3e-a876a39b0c02', 'cc9c3170-b2dc-4701-806a-998b74f23650'),
('c6aee32d-8c35-4481-8b3e-a876a39b0c02', '38296949-7a94-4827-9af5-6d381fd1a507'),
('c6aee32d-8c35-4481-8b3e-a876a39b0c02', 'a60481f1-8413-4789-9340-572827a140e5')
 ON CONFLICT DO NOTHING;