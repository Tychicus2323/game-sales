package com.game.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Table(name = "csv_import_info")
@Builder
public class CsvImportInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "file_name", nullable = false, length = 255)
	private String fileName;

	@Column(name = "status", nullable = false, length = 20)
	private String status;

	@Column(name = "total_records", nullable = false)
	private int totalRecords = 0;

	@Column(name = "successful_records", nullable = false)
	private int successfulRecords = 0;

	@Column(name = "failed_records", nullable = false)
	private int failedRecords = 0;

	@Column(name = "error_message", columnDefinition = "TEXT")
	private String errorMessage;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Column(name = "create_time", updatable = false)
	private LocalDateTime createTime;

	@Column(name = "create_by", length = 100)
	private String createBy;

	@Column(name = "update_time")
	private LocalDateTime updateTime;

	@Column(name = "update_by", length = 100)
	private String updateBy;

	@PrePersist
	public void prePersist() {
		this.createTime = LocalDateTime.now();
		this.updateTime = createTime;
	}

	@PreUpdate
	public void preUpdate() {
		this.updateTime = LocalDateTime.now();
	}
}
