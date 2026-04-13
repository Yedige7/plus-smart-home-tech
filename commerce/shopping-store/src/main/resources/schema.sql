-- CREATE DATABASE shopping_store;

CREATE TABLE IF NOT EXISTS products (
                                        id UUID PRIMARY KEY,
                                        product_name VARCHAR(255) NOT NULL,
    description TEXT,
    image_src TEXT,
    category VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    quantity_state VARCHAR(50) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );