use testdb;

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
