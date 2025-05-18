package com.game.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.game.entity.CsvImportInfo;
import com.game.entity.GameSales;
import com.game.model.GameSalesReportDto;

/**
 * Service class for handling GameSales database transaction.
 */
public interface GameSalesDbService {
	boolean saveGameSalesToDb(List<GameSales> gameSalesList);

	void saveGameSalesToDbByBatch(List<GameSales> gameSalesList);

	Page<GameSales> getAllGameSales(Specification<GameSales> spec, Pageable pageable);

	List<GameSalesReportDto> getTotalSalesByDate(LocalDateTime fromDate, LocalDateTime toDate);

	List<GameSalesReportDto> getTotalSalesByDateAndGameNo(LocalDateTime fromDate, LocalDateTime toDate, int gameNo);

	void upsertStatusToDb(CsvImportInfo importInfo, String status);

	void updateSuccessInfoToDb(CsvImportInfo importInfo, int totalRecords, int successfulRecords, int failedRecords);

	void updateFailedInfoToDb(CsvImportInfo importInfo, String errorMsg);
}
