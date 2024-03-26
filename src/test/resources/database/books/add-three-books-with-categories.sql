INSERT INTO categories (id, name, description, is_deleted) VALUES (1, 'Thriller',  'Thriller category', 0);
INSERT INTO categories (id, name, description, is_deleted) VALUES (2, 'Romance novel',  'Romance novel category', 0);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'Basic Book', 'Basic Author', '9911-123', 20.50, 'Basic Description', 'images.com/image.jpg', 0);
    INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (2, 'Basic Book 2', 'Basic Author 2', '9911-124', 25.50, 'Basic Description 2', 'images.com/image2.jpg', 0);
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (3, 'Different Book', 'Different Author', '9911-125', 14.50, 'Different Description', 'images.com/image3.jpg', 0);

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
INSERT INTO books_categories (book_id, category_id) VALUES (2, 1);
INSERT INTO books_categories (book_id, category_id) VALUES (3, 2);