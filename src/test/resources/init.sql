insert into hometown(id, city, country)
values ('2afff94a-b70e-4b39-bd2a-be1c0f898556', 'anyCity', 'anyCountry');

INSERT INTO users (id, creation_date, dob, gender, handle, name, native_language, occupation, places_to_visit,
                   self_introduction, status, subscription_type, target_language, hometown_id)
VALUES ('d3256c76-62d7-4481-9d1c-a0ccc4da380f', '2022-12-01', 17121989, 'anyGender', 'anyHandle', 'anyName',
        'anyNativeLanguage', 'anyOccupation', 'anyPlacesToVisit',
        'anySelfIntroduction', 'anyStatus', 'anySubscriptionType', 'anyTargetLanguage',
        '2afff94a-b70e-4b39-bd2a-be1c0f898556');

INSERT INTO users (id, creation_date, dob, gender, handle, name, native_language, occupation, places_to_visit,
                   self_introduction, status, subscription_type, target_language, hometown_id)
VALUES ('ca3569ee-cb62-4f45-b1c2-199028ba5562', '2022-12-01', 17121989, 'anyGender', 'anyHandle', 'anyName',
        'anyNativeLanguage', 'anyOccupation', 'anyPlacesToVisit',
        'anySelfIntroduction', 'anyStatus', 'anySubscriptionType', 'anyTargetLanguage',
        '2afff94a-b70e-4b39-bd2a-be1c0f898556');

INSERT INTO following_request (id, user_from_id, user_to_id)
VALUES ('1b00ce80-806b-4d16-b0ec-32f5396ba4b0', 'ca3569ee-cb62-4f45-b1c2-199028ba5562',
        'd3256c76-62d7-4481-9d1c-a0ccc4da380f');

INSERT INTO moment(id, creation_date, last_updated_date, text)
values ('e1f6bea6-4684-403e-9c41-8704fb0600c0', to_timestamp('1834147200'), to_timestamp('1835033600'), 'anyText')
