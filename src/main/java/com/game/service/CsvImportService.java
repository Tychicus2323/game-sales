package com.game.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.game.entity.GameSales;

/**
 * Service class for handling csv import business logic.
 */
public interface CsvImportService {
	List<GameSales> importCsv(MultipartFile file);
	
	/**
	 * Imports game sales data from a CSV file and inserts the records into the database in batches,
	 * using a multithreaded executor for parallel processing.
	 * Records are processed in configurable batches and saved to the database using batch inserts to optimize performance.
	 * The method waits for all tasks to complete before returning.
	 *
	 * @param file the uploaded CSV file containing game sales data.
	 *             The file must have a header row, and each row should match the expected format and data types.
	 * @return a list of GameSales entities that were successfully validated and inserted into the database.
	 */
	void importCsvToDbByBatch(MultipartFile file);
}
