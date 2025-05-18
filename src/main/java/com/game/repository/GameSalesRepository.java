package com.game.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.game.entity.GameSales;
import com.game.model.GameSalesReportDto;

public interface GameSalesRepository extends JpaRepository<GameSales, Long>, JpaSpecificationExecutor<GameSales> {
	Page<GameSales> findAll(Specification<GameSales> spec, Pageable pageable);

	/**
	 * Retrieves the total sales count and total sales amount for each day in the given date range.
	 *
	 * @param fromDate the start date of the period
	 * @param toDate the end date of the period
	 * @return a list of GameSalesReportDto with aggregated sales data
	 */
	@Query(value = "SELECT date_of_sale AS dateOfSale, " +
			"COUNT(*) AS totalCount, SUM(sale_price) AS totalSales " +
			"FROM game_sales " +
			"WHERE date_of_sale BETWEEN :fromDate AND :toDate " +
			"GROUP BY date_of_sale", nativeQuery = true)
	List<GameSalesReportDto> getTotalSalesByDate(@Param("fromDate") LocalDateTime fromDate, 
			@Param("toDate") LocalDateTime toDate);

	/**
	 * Retrieves the total sales count and total sales amount for each day of a specific game number in the given date range.
	 *
	 * @param fromDate the start date of the period
	 * @param toDate the end date of the period
	 * @param gameNo the game number to filter sales by
	 * @return a list of GameSalesReportDto with aggregated sales data for a specific game number
	 */
	@Query(value = "SELECT date_of_sale AS dateOfSale, " +
			"COUNT(*) AS totalCount, SUM(sale_price) AS totalSales " +
			"FROM game_sales  " +
			"WHERE date_of_sale BETWEEN :fromDate AND :toDate " +
			"AND game_no = :gameNo " +
			"GROUP BY date_of_sale", nativeQuery = true)
	List<GameSalesReportDto> getTotalSalesByDateAndGameNo(
			@Param("fromDate") LocalDateTime fromDate, 
			@Param("toDate") LocalDateTime toDate,
			@Param("gameNo") int gameNo);
}
