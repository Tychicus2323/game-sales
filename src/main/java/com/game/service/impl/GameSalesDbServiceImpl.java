package com.game.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.game.entity.CsvImportInfo;
import com.game.entity.GameSales;
import com.game.enums.ImportStatusEnums;
import com.game.model.GameSalesReportDto;
import com.game.repository.CsvImportInfoRepository;
import com.game.repository.GameSalesRepository;
import com.game.service.GameSalesDbService;
import com.game.utils.GameSalesBatchInsert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameSalesDbServiceImpl implements GameSalesDbService{

	private final GameSalesRepository gameSalesRepository;

	private final GameSalesBatchInsert gameSalesBatchInsert;

	private final CsvImportInfoRepository importInfoRepository;

	@Override
	public boolean saveGameSalesToDb(List<GameSales> gameSalesList) {
		List<GameSales> saveRecords = gameSalesRepository.saveAll(gameSalesList);
		log.info("Successfully save {} record(s) to the database", saveRecords);
		return !CollectionUtils.isEmpty(saveRecords) ? true : false;
	}

	@Override
	public void saveGameSalesToDbByBatch(List<GameSales> gameSalesList) {
		gameSalesBatchInsert.insertBatch(gameSalesList);
	}

	@Override
	public Page<GameSales> getAllGameSales(Specification<GameSales> spec, Pageable pageable) {
		return gameSalesRepository.findAll(spec, pageable);
	}

	@Override
	public List<GameSalesReportDto> getTotalSalesByDate(LocalDateTime fromDate, LocalDateTime toDate) {
		return gameSalesRepository.getTotalSalesByDate(fromDate, toDate);
	}

	@Override
	public List<GameSalesReportDto> getTotalSalesByDateAndGameNo(LocalDateTime fromDate, LocalDateTime toDate, int gameNo) {
		return gameSalesRepository.getTotalSalesByDateAndGameNo(fromDate, toDate, gameNo);
	}

	@Override
	public void upsertStatusToDb(CsvImportInfo importInfo, String status) {
		importInfo.setStatus(status);
		importInfoRepository.save(importInfo);
	}

	@Override
	public void updateSuccessInfoToDb(CsvImportInfo importInfo, int totalRecords, int successfulRecords, int failedRecords) {
		importInfo.setStatus(ImportStatusEnums.COMPLETED.toString());
		importInfo.setTotalRecords(totalRecords);
		importInfo.setSuccessfulRecords(successfulRecords);
		importInfo.setFailedRecords(failedRecords);
		importInfo.setEndTime(LocalDateTime.now());
		importInfoRepository.save(importInfo);
	}

	@Override
	public void updateFailedInfoToDb(CsvImportInfo importInfo, String errorMsg) {
		importInfo.setStatus(ImportStatusEnums.FAILED.toString());
		importInfo.setErrorMessage(errorMsg);
		importInfo.setEndTime(LocalDateTime.now());
		importInfoRepository.save(importInfo);
	}
}
