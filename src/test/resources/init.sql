-- Extracting IDs into variables
SET SESSION vars.hometown_id = '3bfff94a-b70e-4b39-bd2a-be1c0f898556';
SET SESSION vars.user_id1 = 'd3256c76-62d7-4481-9d1c-a0ccc4da380f';
SET SESSION vars.user_id2 = 'ca3569ee-cb62-4f45-b1c2-199028ba5562';
SET SESSION vars.followship_id = '1b00ce80-806b-4d16-b0ec-32f5396ba4b0';
SET SESSION vars.moment_id1 = 'e1f6bea6-4684-403e-9c41-8704fb0600c0';
SET SESSION vars.moment_id2 = 'c3f6bea6-4684-403e-9c41-8704fb0600c0';
SET SESSION vars.moment_id3 = 'b3f6bea6-4684-403e-9c41-8704fb0600c0';
SET SESSION vars.moment_like_id1 = 'a1f6bea6-4684-403e-9c41-8704fb0600c0';
SET SESSION vars.moment_like_id2 = 'b1f6bea6-4684-403e-9c41-8704fb0600c0';
SET SESSION vars.comment_id = 'a2f6bea6-4684-403e-9c41-8704fb0600c0';
SET SESSION vars.message_id = 'a3f6bea6-4684-403e-9c41-8704fb0600c0';
SET SESSION vars.chat_id = 'a2f6bea6-4684-403e-9c41-8704fb0600f4';

INSERT INTO hometown(id, city, country)
VALUES (current_setting('vars.hometown_id')::uuid, 'anyCity', 'anyCountry');

INSERT INTO users (id, username, password, creation_date, dob, gender, handle, name, native_language, occupation,
                   places_to_visit,
                   self_introduction, status, subscription_type, target_language, hometown_id)
VALUES (current_setting('vars.user_id1')::uuid, 'usernamed', 'passwordd', '2022-12-01', 17121989, 'anyGender',
        'anyHandle', 'anyName',
        'anyNativeLanguage', 'anyOccupation', 'anyPlacesToVisit',
        'anySelfIntroduction', 'anyStatus', 'anySubscriptionType', 'anyTargetLanguage',
        current_setting('vars.hometown_id')::uuid);

INSERT INTO users (id, username, password, creation_date, dob, gender, handle, name, native_language, occupation,
                   places_to_visit,
                   self_introduction, status, subscription_type, target_language, hometown_id)
VALUES (current_setting('vars.user_id2')::uuid,
        'usernamec', 'passwordc', '2022-12-01', 17121989, 'anyGender',
        'anyHandle', 'anyName',
        'anyNativeLanguage', 'anyOccupation', 'anyPlacesToVisit',
        'anySelfIntroduction', 'anyStatus', 'anySubscriptionType', 'anyTargetLanguage',
        current_setting('vars.hometown_id')::uuid);

INSERT INTO followship (id, user_from_id, user_to_id)
VALUES (current_setting('vars.followship_id')::uuid,
        current_setting('vars.user_id2')::uuid,
        current_setting('vars.user_id1')::uuid);

INSERT INTO moment(id, creation_date, last_updated_date, content, user_id)
VALUES (current_setting('vars.moment_id1')::uuid, to_timestamp('1834147200'), to_timestamp('1835033600'), 'anyText',
        current_setting('vars.user_id1')::uuid);

INSERT INTO moment(id, creation_date, last_updated_date, content, user_id)
VALUES (current_setting('vars.moment_id2')::uuid, to_timestamp('1834147200'), to_timestamp('1835033600'), 'anyText',
        current_setting('vars.user_id2')::uuid);

INSERT INTO moment(id, creation_date, last_updated_date, content, user_id)
VALUES (current_setting('vars.moment_id3')::uuid,
        to_timestamp('1834147200'),
        to_timestamp('1835033600'),
        'anyText',
        current_setting('vars.user_id2')::uuid);

INSERT INTO moment_like(id, moment_id, user_id)
VALUES (current_setting('vars.moment_like_id1')::uuid,
        current_setting('vars.moment_id2')::uuid,
        current_setting('vars.user_id1')::uuid);

INSERT INTO moment_like(id, moment_id, user_id)
VALUES (current_setting('vars.moment_like_id2')::uuid,
        current_setting('vars.moment_id1')::uuid,
        current_setting('vars.user_id2')::uuid);

INSERT INTO comment(id, creation_date, last_updated_date, content, user_id, moment_id)
VALUES (current_setting('vars.comment_id')::uuid, to_timestamp('1834147200'), to_timestamp('1835033600'), 'anyText',
        current_setting('vars.user_id1')::uuid, 'b3f6bea6-4684-403e-9c41-8704fb0600c0');

INSERT INTO chat(id)
VALUES (current_setting('vars.chat_id')::uuid);

INSERT INTO message(id, creation_date, content, user_to_id, user_from_id, chat_id)
VALUES (current_setting('vars.message_id')::uuid,
        to_timestamp('1834147200'), 'anyText',
        current_setting('vars.user_id2')::uuid,
        current_setting('vars.user_id1')::uuid,
        current_setting('vars.chat_id')::uuid);


