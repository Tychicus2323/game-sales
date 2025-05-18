package com.game.service.impl;

import static com.game.controller.constants.CommonConstants.APP_NAME;
import static com.game.controller.constants.CommonConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.game.entity.CsvImportInfo;
import com.game.entity.GameSales;
import com.game.enums.ImportStatusEnums;
import com.game.model.GameSalesDto;
import com.game.service.CsvImportService;
import com.game.service.GameSalesDbService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvImportServiceImpl implements CsvImportService {

	private final Validator validator;

	private final GameSalesDbService gameSalesDbService;

	private static final int BATCH_SIZE = 1000; // to move to environment variables

	private static final int THREAD_POOL_SIZE = 10; // this value cannot be more than db connection pool size

	@Override
	public List<GameSales> importCsv(MultipartFile file) {
		log.info("importing csv file...");

		// Read from csv file, validate each record before saving to DB
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
				CSVParser csvParser = CSVParser.parse(reader, 
						CSVFormat.RFC4180.builder()
						.setHeader()
						.setSkipHeaderRecord(true)
						.get())) {
			List<GameSales> gamesSalesList = csvParser.getRecords().stream()
					.map(record -> new AbstractMap.SimpleEntry<>(record, mapCsvToDto(record)))
					.filter(entry -> isValidGameSalesDto(entry.getValue(), entry.getKey()))
					.map(Map.Entry::getValue)
					.map(this::mapDtoToEntity)
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(gamesSalesList)) {
				log.debug("gamesSalesList: {}", gamesSalesList);
				return gamesSalesList;
			} else {
				log.error("gamesSalesList is empty");
			}
		} catch (Exception e) {
			log.error("Error importing CSV file: {}", e.getMessage(), e);
		}
		return null;
	}

	private GameSalesDto mapCsvToDto(CSVRecord record) {
		GameSalesDto gameSalesDto = 
				GameSalesDto.builder()
				.id(Long.parseLong(record.get("id")))
				.gameNo(Integer.parseInt(record.get("game_no")))
				.gameName(record.get("game_name"))
				.gameCode(record.get("game_code"))
				.type(Integer.parseInt(record.get("type")))
				.costPrice(new BigDecimal(record.get("cost_price")))
				.tax(new BigDecimal(record.get("tax")))
				.salePrice(new BigDecimal(record.get("sale_price")))
				.dateOfSale(LocalDateTime.parse(record.get("date_of_sale"), 
						DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)))
				.build();
		return gameSalesDto;
	}

	private GameSales mapDtoToEntity(GameSalesDto gameSalesDto) {
		GameSales entity = new GameSales();
		entity.setId(gameSalesDto.getId());
		entity.setGameNo(gameSalesDto.getGameNo());
		entity.setGameName(gameSalesDto.getGameName());
		entity.setGameCode(gameSalesDto.getGameCode());
		entity.setType(gameSalesDto.getType());
		entity.setCostPrice(gameSalesDto.getCostPrice());
		entity.setTax(gameSalesDto.getTax());
		entity.setSalePrice(gameSalesDto.getSalePrice());
		entity.setDateOfSale(gameSalesDto.getDateOfSale());
		return entity;
	}

	private boolean isValidGameSalesDto(GameSalesDto gameSales, CSVRecord record) {
		Set<ConstraintViolation<GameSalesDto>> violations = validator.validate(gameSales);
		if (!violations.isEmpty()) {
			violations.forEach(violation -> log.warn("Row: {}, Validation error: {}", 
					record.getRecordNumber(), violation.getMessage()));
			return false;
		}
		return true;
	}

	@Override
	public void importCsvToDbByBatch(MultipartFile file) {
		log.info("importing csv file to db by batch...");
		CsvImportInfo importInfo = CsvImportInfo.builder()
				.fileName(file.getOriginalFilename())
				.startTime(LocalDateTime.now())
				.createBy(APP_NAME)
				.build();
		gameSalesDbService.upsertStatusToDb(importInfo, ImportStatusEnums.PENDING.toString());

		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		AtomicInteger totalRecords = new AtomicInteger(0);
		AtomicInteger successfulRecords = new AtomicInteger(0);
		AtomicInteger failedRecords = new AtomicInteger(0);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
				CSVParser csvParser = CSVParser.parse(reader, 
						CSVFormat.RFC4180.builder()
						.setHeader()
						.setSkipHeaderRecord(true)
						.get())) {
			gameSalesDbService.upsertStatusToDb(importInfo, ImportStatusEnums.PROCESSING.toString());

			List<Map.Entry<CSVRecord, GameSalesDto>> currentBatchBuffer = new ArrayList<>(BATCH_SIZE);

			// handle batch processing in parallel
			Consumer<List<Map.Entry<CSVRecord, GameSalesDto>>> processBatchLambda = batchList -> {
				List<GameSales> gamesSalesList = batchList.parallelStream()
						.peek(entry -> totalRecords.incrementAndGet())
						.filter(entry -> {
							boolean valid = isValidGameSalesDto(entry.getValue(), entry.getKey());
							if (!valid) {
								failedRecords.incrementAndGet();
							}
							return valid;
						})
						.map(entry -> mapDtoToEntity(entry.getValue()))
						.collect(Collectors.toList());

				if (!CollectionUtils.isEmpty(gamesSalesList)) {
					gameSalesDbService.saveGameSalesToDbByBatch(gamesSalesList); // Save to db in batch
					successfulRecords.addAndGet(gamesSalesList.size());
				}
			};

			// Process CSV records in batches
			for (CSVRecord record : csvParser) {
				GameSalesDto dto = mapCsvToDto(record);
				currentBatchBuffer.add(Map.entry(record, dto));

				// Process when currentBatchBuffer size reached batch size
				if (currentBatchBuffer.size() == BATCH_SIZE) {
					List<Map.Entry<CSVRecord, GameSalesDto>> batchList = new ArrayList<>(currentBatchBuffer);
					executor.submit(() -> processBatchLambda.accept(batchList));
					currentBatchBuffer.clear();
				}
			}

			// Process any remaining records in the last batch
			if (!currentBatchBuffer.isEmpty()) {
				List<Map.Entry<CSVRecord, GameSalesDto>> batchList = new ArrayList<>(currentBatchBuffer);
				executor.submit(() -> processBatchLambda.accept(batchList));
			}
			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.HOURS);  // Wait up to 1 hour for all threads to finish
			gameSalesDbService.updateSuccessInfoToDb(importInfo, totalRecords.get(), successfulRecords.get(), failedRecords.get());
		} catch (Exception e) {
			log.error("Error importing CSV file: {}", e.getMessage(), e);
			gameSalesDbService.updateFailedInfoToDb(importInfo, e.getMessage());
		}
	}
}
