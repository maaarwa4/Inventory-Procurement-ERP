-- Admin table (unchanged)
CREATE TABLE admin (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Supplier table (unchanged)
CREATE TABLE supplier (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    company_name VARCHAR(100),
    phone VARCHAR(100),
    city VARCHAR(100),
    email VARCHAR(100),
    country VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE
);

-- Enhanced Product table (your requested changes)
CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    brand brand_enum NOT NULL,
    category category_enum NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER DEFAULT 0,
    image_url VARCHAR(255),
    model VARCHAR(100),
    color color_enum,
    storage storage_enum,
    screen_size DECIMAL(3,1),
    network_type VARCHAR(20)
);


-- Purchase Order tables (unchanged)
CREATE TYPE purchase_order_status AS ENUM (
    'PENDING',
    'APPROVED', 
    'CANCELED',
    'DELIVERED'
);

CREATE TABLE purchase_order (
    id SERIAL PRIMARY KEY,
    creation_date DATE NOT NULL DEFAULT CURRENT_DATE,
    total_amount DECIMAL(10,2) NOT NULL,
    status purchase_order_status NOT NULL DEFAULT 'PENDING',
    supplier_id BIGINT NOT NULL REFERENCES supplier(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0)
);