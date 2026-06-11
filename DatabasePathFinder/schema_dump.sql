-- ========================================================================
-- DATABASE SCHEMA WITH INLINE FOREIGN KEYS
-- ========================================================================

CREATE TABLE USERS (
    user_id INT NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

CREATE TABLE CATEGORIES (
    cat_id INT NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (cat_id)
);

CREATE TABLE SUPPLIERS (
    supplier_id INT NOT NULL,
    supplier_name VARCHAR(100) NOT NULL,
    CONSTRAINT pk_suppliers PRIMARY KEY (supplier_id)
);

-- Contains a single inline foreign key linking to SUPPLIERS
-- and another linking to CATEGORIES
CREATE TABLE PRODUCTS (
    prod_id INT NOT NULL,
    supplier_id INT NOT NULL,
    category_id INT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2),
    CONSTRAINT pk_products PRIMARY KEY (prod_id, supplier_id),
    CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES SUPPLIERS (supplier_id),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES CATEGORIES (cat_id)
);

-- Contains an inline foreign key linking to USERS
CREATE TABLE ORDERS (
    order_id INT NOT NULL,
    branch_id INT NOT NULL,
    user_id INT NOT NULL,
    order_date DATE,
    CONSTRAINT pk_orders PRIMARY KEY (order_id, branch_id),
    FOREIGN KEY (user_id) REFERENCES USERS (user_id)
);

-- Contains two separate composite inline foreign keys linking to ORDERS and PRODUCTS
CREATE TABLE ORDER_ITEMS (
    item_id INT NOT NULL,
    order_id INT NOT NULL,
    branch_id INT NOT NULL,
    product_id INT NOT NULL,
    supplier_id INT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (item_id),
    FOREIGN KEY (order_id, branch_id) REFERENCES ORDERS (order_id, branch_id),
    FOREIGN KEY (product_id, supplier_id) REFERENCES PRODUCTS (prod_id, supplier_id)
);