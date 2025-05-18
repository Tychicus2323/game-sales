package com.game.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.game.entity.GameSales;

import jakarta.persistence.criteria.Predicate;

/**
 * A specification class for filtering GameSales entities based on provided parameters.
 * This class provides a dynamic query builder that can generate predicates (filters) based on the
 * input parameters such as date range, minimum sales price, and maximum sales price.
 * It is used for creating flexible and reusable queries for the GameSales entity.
 */
public class GameSalesSpecification {

	/**
	 * @param fromDate The start date of the range.
	 * @param toDate The end date of the range.
	 * @param minPrice The minimum sale price to filter records by (inclusive).
	 * @param maxPrice The maximum sale price to filter records by (inclusive).
	 * @return A Specification that can be used for filtering GameSales entities based on the provided parameters.
	 */
	public static Specification<GameSales> filter(
			LocalDateTime fromDate,
			LocalDateTime toDate,
			BigDecimal minPrice,
			BigDecimal maxPrice
			) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (fromDate != null && toDate != null) {
				predicates.add(cb.between(root.get("dateOfSale"), fromDate, toDate));
			}
			if (minPrice != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("salePrice"), minPrice));
			}
			if (maxPrice != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("salePrice"), maxPrice));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
