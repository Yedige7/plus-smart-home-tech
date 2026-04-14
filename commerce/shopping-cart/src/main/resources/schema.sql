-- CREATE DATABASE shopping_cart;

CREATE TABLE IF NOT EXISTS shopping_carts (
                                              id UUID PRIMARY KEY,
                                              username VARCHAR(255) NOT NULL UNIQUE,
    state VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );


CREATE TABLE IF NOT EXISTS shopping_cart_items (
                                                   id UUID PRIMARY KEY,
                                                   cart_id UUID NOT NULL REFERENCES shopping_carts(id),
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity >= 0)
    );