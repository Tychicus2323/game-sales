package com.game.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "game_sales")
public class GameSales {
	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "game_no", nullable = false)
	private Integer gameNo;

	@Column(name = "game_name", nullable = false)
	private String gameName;

	@Column(name = "game_code", nullable = false)
	private String gameCode;

	@Column(name = "type", nullable = false)
	private Integer type;

	@Column(name = "cost_price", nullable = false)
	private BigDecimal costPrice;

	@Column(name = "tax", nullable = false)
	private BigDecimal tax;

	@Column(name = "sale_price", nullable = false)
	private BigDecimal salePrice;

	@Column(name = "date_of_sale", nullable = false)
	private LocalDateTime dateOfSale;
}
