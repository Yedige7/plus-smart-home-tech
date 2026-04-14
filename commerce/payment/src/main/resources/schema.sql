CREATE DATABASE payment_db;

CREATE TABLE IF NOT EXISTS  payments (
                          payment_id UUID PRIMARY KEY,
                          order_id UUID NOT NULL,

                          product_total NUMERIC(19, 2),
                          delivery_total NUMERIC(19, 2),
                          fee_total NUMERIC(19, 2),
                          total_payment NUMERIC(19, 2),

                          state VARCHAR(20) NOT NULL
);