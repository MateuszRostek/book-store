INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (10, 'john@basic.com', 'password', 'John', 'Smith', '123 Street, City, Country', 0);

INSERT INTO users_roles (user_id, role_id) VALUES (10, 1);

INSERT INTO categories (id, name, description, is_deleted) VALUES (10, 'Thriller',  'Thriller category', 0);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (10, 'Basic Book', 'Basic Author', '9911-123', 20.50, 'Basic Description', 'images.com/image.jpg', 0);

INSERT INTO books_categories (book_id, category_id) VALUES (10, 10);

INSERT INTO shopping_carts (id, user_id, is_deleted) VALUES (10, 10, 0);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity, is_deleted) VALUES (10, 10, 10, 2, 0);