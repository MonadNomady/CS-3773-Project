-- 1. Insert Preloaded Products
INSERT IGNORE INTO product (name, description, price, is_available, picture_url) VALUES
                                                                              ('Organic Bananas', 'Fresh organic bananas per bunch', 1.99, true, 'https://m.media-amazon.com/images/I/61fZ+YAYGaL.jpg'),
                                                                              ('Whole Milk 1 Gal', 'Grade A pasteurized whole milk', 3.49, true, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSOcKuINVm-OYC-J9c46vypUQ0sDzqGYbxSXVvHoY-9eg&s=10'),
                                                                              ('Sourdough Bread', 'Artisanal fresh baked sourdough loaf', 4.29, true, 'https://img.magnific.com/premium-photo/sliced-sourdough-bread-isolated-white-background-homemade-bakery_34435-5559.jpg'),
                                                                              ('Avocados (4 Pack)', 'Fresh ripe Hass avocados', 4.99, true, 'https://img.imageboss.me/fourwinds/width/425/dpr:2/shop/products/shutterstock_100322618hass_avocado.jpg?v=1729718212'),
                                                                              ('Boneless Chicken Breast', '1 lb fresh organic chicken breast', 6.99, true, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRlS8iAxBSTmpSUfM4INyN2f1Q8JaaepyHC9PBkMj6-WyK-M-_9OkvHOjLw&s=10'),
                                                                              ('Fresh Honeycrisp Apples', 'Crisp and sweet honeycrisp apples, 3 lb bag', 5.49, true, 'https://www.melissas.com/cdn/shop/files/4-pounds-image-of-honeycrisp-apples-fruit-1125637083_512x512.jpg?v=1738777800'),
                                                                              ('Vanilla Greek Yogurt', '32 oz organic whole milk Greek yogurt', 4.79, true, 'https://d2lnr5mha7bycj.cloudfront.net/product-image/file/large_613e712b-0367-4ffc-86e2-606e29412eb8.jpg'),
                                                                              ('Extra Virgin Olive Oil', 'Cold-pressed Italian extra virgin olive oil 16.9 oz', 8.99, true, 'https://d2lnr5mha7bycj.cloudfront.net/product-image/file/large_32e27015-635d-4394-9502-a7f848590b74.jpg');

-- 2. Insert Preloaded Test Customers
INSERT IGNORE INTO customer (name, email, password) VALUES
                                                 ('John Doe', 'john@example.com', 'password123'),
                                                 ('Jane Smith', 'jane@example.com', 'securepass');