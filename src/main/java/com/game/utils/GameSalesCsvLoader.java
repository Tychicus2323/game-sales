package com.game.utils;

import static com.game.controller.constants.CommonConstants.APP_NAME;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.game.entity.CsvImportInfo;
import com.game.enums.ImportStatusEnums;
import com.game.service.GameSalesDbService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for loading game sales data into the database using MySQL's
 * {@code LOAD DATA LOCAL INFILE} for fast and efficient bulk import.
 *
 * <p>This service expects a CSV file to be present on the local filesystem,
 * and the file must match the structure of the {@code game_sales} table.
 *
 * <p>To use this service:
 * <ol>
 *     <li>Ensure {@code local_infile} is enabled on the MySQL server</li>
 *     <li>Set {@code allowLoadLocalInfile=true} in the JDBC URL</li>
 *     <li>Provide an absolute path to a CSV file with the correct format</li>
 * </ol>
 *
 * <p>Example CSV format:
 * <pre>
 * id,game_no,game_name,game_code,type,cost_price,tax,sale_price,date_of_sale
 * 1,100,Super Mario,MARIO,1,59.99,5.00,64.99,2024-05-018 10:00:00
 * </pre>
 *
 * @author Vincent Tay
 * Date: 18/05/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameSalesCsvLoader {

	private final JdbcTemplate jdbcTemplate;

	private final GameSalesDbService gameSalesDbService;

	public void loadCsv(String csvFilePath, String fileName) {
		CsvImportInfo importInfo = CsvImportInfo.builder()
				.fileName(fileName)
				.startTime(LocalDateTime.now())
				.createBy(APP_NAME)
				.build();
		gameSalesDbService.upsertStatusToDb(importInfo, ImportStatusEnums.PENDING.toString());
		try {
			gameSalesDbService.upsertStatusToDb(importInfo, ImportStatusEnums.PROCESSING.toString());
			String sql = "LOAD DATA LOCAL INFILE ? " +
					"INTO TABLE game_sales " +
					"FIELDS TERMINATED BY ',' " +
					"ENCLOSED BY '\"' " +
					"LINES TERMINATED BY '\\n' " +
					"IGNORE 1 LINES " +
					"(id, game_no, game_name, game_code, type, cost_price, tax, sale_price, date_of_sale)";

			jdbcTemplate.execute((Connection con) -> {
				try (PreparedStatement ps = con.prepareStatement(sql)) {
					ps.setString(1, csvFilePath);
					int rowsInserted = ps.executeUpdate();
					gameSalesDbService.updateSuccessInfoToDb(importInfo, rowsInserted, rowsInserted, 0);
				}
				return null;
			});
		} catch (Exception e) {
			log.error("Error loading csv file :{}", e.getMessage(), e);
			gameSalesDbService.updateFailedInfoToDb(importInfo, e.getMessage());
		}
	}
}
