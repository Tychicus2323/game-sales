package com.game.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameSalesReportDto {
	private Date dateOfSale;

	private long totalCount;

	private BigDecimal totalSales;
}
