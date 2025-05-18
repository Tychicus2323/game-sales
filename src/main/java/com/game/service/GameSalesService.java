package com.game.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import com.game.entity.GameSales;
import com.game.model.GameSalesDto;
import com.game.model.GameSalesReportDto;

/**
 * Service class for handling GameSales business logic.
 */
public interface GameSalesService {
	boolean importCsvDataToDb(MultipartFile file);
	
	void importCsvDataToDbByBatch(MultipartFile file);

	Page<GameSalesDto> getAllGamesSalesFromDb(Specification<GameSales> spec, Pageable pageable);

	List<GameSalesReportDto> getTotalSalesByDate(LocalDateTime fromDate, LocalDateTime toDate);

	List<GameSalesReportDto> getTotalSalesByDateAndGameNo(LocalDateTime fromDate, LocalDateTime toDate, int gameNo);
}
