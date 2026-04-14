CREATE DATABASE order_db;

CREATE TABLE IF NOT EXISTS orders (
                        order_id UUID PRIMARY KEY,
                        username VARCHAR(255),
                        shopping_cart_id UUID,
                        payment_id UUID,
                        delivery_id UUID,
                        state VARCHAR(30) NOT NULL,

                        total_price NUMERIC(19,2),
                        delivery_price NUMERIC(19,2),
                        product_price NUMERIC(19,2),

                        delivery_weight DOUBLE PRECISION,
                        delivery_volume DOUBLE PRECISION,
                        fragile BOOLEAN,

                        delivery_country VARCHAR(255),
                        delivery_city VARCHAR(255),
                        delivery_street VARCHAR(255),
                        delivery_house VARCHAR(255),
                        delivery_flat VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS order_products (
                                order_id UUID NOT NULL,
                                product_id UUID NOT NULL,
                                quantity BIGINT,
                                PRIMARY KEY (order_id, product_id),
                                CONSTRAINT fk_order_products_order
                                    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
