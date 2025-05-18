package com.game.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.game.entity.GameSales;
import com.game.model.GameSalesDto;
import com.game.model.GameSalesReportDto;
import com.game.service.CsvImportService;
import com.game.service.GameSalesDbService;
import com.game.service.GameSalesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameSalesServiceImpl implements GameSalesService {

	private final CsvImportService csvImportService;

	private final GameSalesDbService gameSalesDbService;

	@Override
	public boolean importCsvDataToDb(MultipartFile file) {
		log.info("Importing Game Sales data from Csv file to Db...");
		List<GameSales> gamesSalesList = csvImportService.importCsv(file);
		if (!CollectionUtils.isEmpty(gamesSalesList)) {
			gameSalesDbService.saveGameSalesToDb(gamesSalesList);
			return true;
		}
		return false;
	}

	@Override
	public void importCsvDataToDbByBatch(MultipartFile file) {
		log.info("Importing Game Sales data from Csv file to Db...");
		csvImportService.importCsvToDbByBatch(file);
	}

	@Override
	public Page<GameSalesDto> getAllGamesSalesFromDb(Specification<GameSales> spec, Pageable pageable) {
		log.info("Getting game sales from Db...");
		Page<GameSalesDto> gameSalesDtos = gameSalesDbService.getAllGameSales(spec, pageable).map(this::mapEntityToDto);
		return gameSalesDtos;
	}

	@Override
	public List<GameSalesReportDto> getTotalSalesByDate(LocalDateTime fromDate, LocalDateTime toDate) {
		return gameSalesDbService.getTotalSalesByDate(fromDate, toDate);
	}

	@Override
	public List<GameSalesReportDto> getTotalSalesByDateAndGameNo(LocalDateTime fromDate, LocalDateTime toDate, int gameNo) {
		return gameSalesDbService.getTotalSalesByDateAndGameNo(fromDate, toDate, gameNo);
	}

	private GameSalesDto mapEntityToDto(GameSales entity) {
		GameSalesDto gameSalesDto = 
				GameSalesDto.builder()
				.id(entity.getId())
				.gameNo(entity.getGameNo())
				.gameName(entity.getGameName())
				.gameCode(entity.getGameCode())
				.type(entity.getType())
				.costPrice(entity.getCostPrice())
				.tax(entity.getTax())
				.salePrice(entity.getSalePrice())
				.dateOfSale(entity.getDateOfSale())
				.build();
		return gameSalesDto;
	}
}
