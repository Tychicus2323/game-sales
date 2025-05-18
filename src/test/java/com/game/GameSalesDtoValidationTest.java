package com.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.game.enums.GameTypeEnums;
import com.game.model.GameSalesDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class GameSalesDtoValidationTest {

	private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDto() {
        GameSalesDto gameSalesDto = getMockData();
        Set<ConstraintViolation<GameSalesDto>> violations = validator.validate(gameSalesDto);
        assertTrue(violations.isEmpty(), "DTO should be valid");
    }
    
    @Test
    void testInvalidGameNo() {
        GameSalesDto gameSalesDto = getMockData();
        gameSalesDto.setGameNo(111);
        Set<ConstraintViolation<GameSalesDto>> violations = validator.validate(gameSalesDto);
        assertFalse(violations.isEmpty(), "Game No should be invalid");
        violations.forEach(v -> System.out.println("Violation: " + v.getMessage()));
    }
    
    private GameSalesDto getMockData() {
    	GameSalesDto gameSalesDto = 
        		GameSalesDto.builder()
        		.id(1L)
				.gameNo(10)
				.gameName("Game 1")
				.gameCode("A01")
				.type(GameTypeEnums.ONLINE.getValue())
				.costPrice(BigDecimal.valueOf(90))
				.tax(BigDecimal.valueOf(9))
				.salePrice(BigDecimal.valueOf(98.1))
				.dateOfSale(LocalDateTime.now())
        		.build();
    	return gameSalesDto;
    }
}
