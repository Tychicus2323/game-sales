package com.game.controller;

import static com.game.controller.constants.CommonConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.game.entity.GameSales;
import com.game.model.GameSalesDto;
import com.game.model.GameSalesReportDto;
import com.game.service.GameSalesService;
import com.game.utils.GameSalesCsvLoader;
import com.game.utils.GameSalesSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for handling GameSales requests.
 * Provides an endpoint to import from csv files by batch or using mysql LOAD DATA LOCAL INFILE into database.
 * Provides an endpoint to retrieve game sales by pagination.
 * Provides an endpoint to retrieve aggregated sales data based on the provided filters.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class GameSalesController {

	private final GameSalesService gameSalesService;

	private final GameSalesCsvLoader gameSalesCsvLoader;

	private static boolean enableBatchInsert = true;

	@PostMapping("/import")
	public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {
		log.info("importCsv start");
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("File is empty");
		}
		if (enableBatchInsert) {
			log.info("importCsv using batch");
			gameSalesService.importCsvDataToDbByBatch(file); // this is using batch insert
			return ResponseEntity.ok("Game sales data imported successfully to Db.");
		} else {
			if (gameSalesService.importCsvDataToDb(file)) {
				log.info("Game sales data imported successfully to Db.");
				return ResponseEntity.ok("Game sales data imported successfully to Db.");
			}
			return ResponseEntity.internalServerError().body("Error importing game sales data to Db");
		}
	}

	@PostMapping("/import/infile")
	public ResponseEntity<String> importCsvUsingLocalInFile(@RequestParam("file") MultipartFile file) {
		log.info("importCsvUsingLocalInFile start");
		try {
			File tempFile = File.createTempFile("gamesales_", ".csv"); //Save the uploaded file to a temp location
			file.transferTo(tempFile);
			gameSalesCsvLoader.loadCsv(tempFile.getAbsolutePath(), file.getOriginalFilename()); // Load using LOAD DATA LOCAL INFILE
			tempFile.delete();
			return ResponseEntity.ok("Game sales data imported successfully to Db.");
		} catch (Exception e) {
			log.error("Error importing Csv into Db :{}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Import failed: " + e.getMessage());
		}
	}

	@GetMapping("/getGameSales")
	public ResponseEntity<Page<GameSalesDto>> getGameSales(
			@RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT_YYYY_MM_DD_HH_MM_SS) LocalDateTime fromDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT_YYYY_MM_DD_HH_MM_SS) LocalDateTime toDate,
			@RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "100") int size) {
		if (size > 100) {
			log.warn("Page size cannot be more than 100, setting to 100");
			size = 100;
		}
		Specification<GameSales> spec = GameSalesSpecification.filter(fromDate, toDate, minPrice, maxPrice);
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		Page<GameSalesDto> gameSalesPage = gameSalesService.getAllGamesSalesFromDb(spec, pageable);
		 log.info("gameSalesList size: {}", gameSalesPage.getContent().size());
		return ResponseEntity.ok(gameSalesPage);
	}

	/**
	 * @param fromDate The start date of the range.
	 * @param toDate The end date of the range.
	 * @param gameNo The game number to filter records by.
	 * @return A list of GameSalesDto that retrieve the total number of games sold or total sales generated.
	 */
	@GetMapping("/getTotalSales")
	public List<GameSalesReportDto> getTotalSales(
			@RequestParam @DateTimeFormat(pattern = DATE_FORMAT_YYYY_MM_DD_HH_MM_SS) LocalDateTime fromDate,
			@RequestParam @DateTimeFormat(pattern = DATE_FORMAT_YYYY_MM_DD_HH_MM_SS) LocalDateTime toDate,
			@RequestParam(required = false) Integer gameNo) {
		if (Objects.nonNull(gameNo)) {
			List<GameSalesReportDto> reportList = gameSalesService.getTotalSalesByDateAndGameNo(fromDate, toDate, gameNo);
			log.info("reportList size:{} for game no: {}", reportList.size(), gameNo);
			return reportList;
		} else {
			List<GameSalesReportDto> reportList = gameSalesService.getTotalSalesByDate(fromDate, toDate);
			log.info("reportList size:{}", reportList.size());
			return reportList;
		}
	}
}
