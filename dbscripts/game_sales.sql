-- Store game records from the CSV
CREATE TABLE game_sales (
    id INT PRIMARY KEY,
    game_no INT NOT NULL,
    game_name VARCHAR(20) NOT NULL,
    game_code VARCHAR(5) NOT NULL,
    type TINYINT NOT NULL,
    cost_price DECIMAL(10, 2) NOT NULL,
    tax DECIMAL(3, 2) NOT NULL,
    sale_price DECIMAL(10, 2) NOT NULL,
    date_of_sale DATETIME(0) NOT NULL
);

-- Create Index for optimisation
CREATE INDEX idx_sale_price ON game_sales(sale_price);
CREATE INDEX idx_date_of_sale ON game_sales(date_of_sale);
CREATE INDEX idx_date_price ON game_sales(date_of_sale, sale_price);
CREATE INDEX idx_date_game_no ON game_sales(date_of_sale, game_no);