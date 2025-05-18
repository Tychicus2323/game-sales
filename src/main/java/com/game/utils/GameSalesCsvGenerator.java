package com.game.utils;

import static com.game.controller.constants.CommonConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to generate random values into csv file based on the following requirement
 * 1. id (a running number starts with 1)
 * 2. game_no (an integer value between 1 to 100)
 * 3. game_name (a string value not more than 20 characters)
 * 4. game_code (a string value not more than 5 characters)
 * 5. type (an integer, 1 = Online | 2 = Offline)
 * 6. cost_price (decimal value not more than 100)
 * 7. tax (9%)
 * 8. sale_price (decimal value, cost_price inclusive of tax)
 * 9. date_of_sale (a timestamp of the sale)
 * 
 * @author Vincent Tay
 * Date: 18/05/2025
 */
@Slf4j
public class GameSalesCsvGenerator {
	private static final int ROW_COUNT = 1_000_000; // change accordingly to the number of records
	private static final String FILE_NAME = "game_sales_1_000_000.csv";
	private static final LocalDateTime START_DATE = LocalDateTime.of(2025, 4, 1, 0, 0); // 1st April 2025
	private static final LocalDateTime END_DATE = LocalDateTime.of(2025, 4, 30, 23, 59); // 30th April 2025
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);

	public static void main(String[] args) {
		Random random = new Random();

		try (FileWriter writer = new FileWriter(FILE_NAME)) {
			writer.write("id,game_no,game_name,game_code,type,cost_price,tax,sale_price,date_of_sale\n");

			for (int i = 1; i <= ROW_COUNT; i++) {
				int gameNo = random.nextInt(100) + 1;
				String gameName = randomString(random, 5 + random.nextInt(16));
				String gameCode = randomString(random, 2 + random.nextInt(4));
				int type = random.nextInt(2) + 1;

				BigDecimal costPrice = BigDecimal.valueOf(1 + (99 * random.nextDouble())).setScale(2, RoundingMode.HALF_UP);
				BigDecimal tax = BigDecimal.valueOf(9.0);
				BigDecimal salePrice = costPrice.multiply(tax.divide(BigDecimal.valueOf(100.0)).add(BigDecimal.ONE)).setScale(2, RoundingMode.HALF_UP);

				LocalDateTime randomDate = START_DATE.plusSeconds(random.nextInt((int) java.time.Duration.between(START_DATE, END_DATE).getSeconds()));

				writer.write(String.format("%d,%d,%s,%s,%d,%.2f,%.1f,%.2f,%s\n",
						i, gameNo, gameName, gameCode, type,
						costPrice.doubleValue(), tax.doubleValue(), salePrice.doubleValue(),
						randomDate.format(FORMATTER)));

				if (i % 100000 == 0) {
					log.info("Generated {} records...", i);
				}
			}
			log.info("CSV file generated: {}", FILE_NAME);
		} catch (IOException e) {
			log.error("Error while generating Csv file: {}", e.getMessage(), e);
		}
	}

	private static String randomString(Random random, int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < length; i++) {
			result.append(characters.charAt(random.nextInt(characters.length())));
		}
		return result.toString();
	}
}
