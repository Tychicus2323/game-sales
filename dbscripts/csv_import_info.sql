-- Track the progress of each import
CREATE TABLE csv_import_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_records INT DEFAULT 0,
    successful_records INT DEFAULT 0,
    failed_records INT DEFAULT 0,
    error_message TEXT,
	start_time DATETIME,
    end_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
	create_by VARCHAR(100),
    update_time DATETIME,
	update_by VARCHAR(100)
);

-- CREATE Index later
CREATE INDEX idx_status ON csv_import_info(status);
CREATE INDEX idx_file_name ON csv_import_info(file_name);