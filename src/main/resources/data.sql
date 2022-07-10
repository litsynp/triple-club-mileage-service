use testdb;

# =========================================== #
# user
# =========================================== #
# Set user ID
set @user_id = uuid_to_bin('31313130-3031-3131-3130-000000000000');
set @user_id2 = uuid_to_bin('7f7862f3-fcf5-11ec-b3c2-0242ac170002');

# Create user
insert
into users (email, password, id)
values ('testuser1@example.com', '12345678', @user_id);
insert
into users (email, password, id)
values ('testuser2@example.com', '12345678', @user_id2);

# =========================================== #
# place
# =========================================== #
# Set place ID
set @place_id = uuid_to_bin('8040a09f-fcf6-11ec-b3c2-0242ac170002');

# Create place
insert
into place
    (created_on, updated_on, name, id)
values (now(), now(), "해운대 수변공원", @place_id);

# =========================================== #
# Photo
# =========================================== #
# Set photo ID
set @photo_id = uuid_to_bin('48925641-70f3-4674-86e6-420bbab59bf8');
set @photo_id2 = uuid_to_bin('cf00ec57-563b-4f0e-b5bf-78ce28738efb');

# Create photos
insert
into photo
    (created_on, updated_on, filename, url, review_id, id)
values (now(), now(), "photo1.jpg", "https://photo.example.com/photo1.jpg", null, @photo_id);

insert
into photo
    (created_on, updated_on, filename, url, review_id, id)
values (now(), now(), "photo2.jpg", "https://photo.example.com/photo2.jpg", null, @photo_id2);
