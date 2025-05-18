package com.game.model;

import static com.game.controller.constants.CommonConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameSalesDto {
	@Min(value = 1, message = "ID should start from 1")
	private long id;

	@Min(value = 1, message = "Game number should be at least 1")
	@Max(value = 100, message = "Game number should not be more than 100")
	private int gameNo;

	@NotBlank(message = "Game Name cannot be blank")
	@Size(max = 20, message = "Game Name cannot exceed 20 characters")
	private String gameName;

	@NotBlank(message = "Game Code cannot be blank")
	@Size(max = 5, message = "Game Code cannot exceed 5 characters")
	private String gameCode;

	@Min(value = 1, message = "Type should be 1 (Online) or 2 (Offline)")
	@Max(value = 2, message = "Type should be 1 (Online) or 2 (Offline)")
	private int type;

	@DecimalMax(value = "100.0", message = "Cost Price cannot exceed 100.00")
	@DecimalMin(value = "0.0", inclusive = true, message = "Cost Price cannot be less than 0")
	private BigDecimal costPrice;

	@DecimalMin(value = "9.0", inclusive = true, message = "Tax should be exactly 9%")
	@DecimalMax(value = "9.0", inclusive = true, message = "Tax should be exactly 9%")
	private BigDecimal tax;

	@DecimalMin(value = "0.0", inclusive = true, message = "Sale Price cannot be less than 0")
	private BigDecimal salePrice;

	@NotNull(message = "Date of Sale cannot be null")
	@DateTimeFormat(pattern = DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)
	private LocalDateTime dateOfSale;
}
