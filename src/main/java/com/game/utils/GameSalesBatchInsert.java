package com.game.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.game.entity.GameSales;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameSalesBatchInsert {

	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public int[] insertBatch(List<GameSales> gameSalesList) {
		String sql = "INSERT INTO game_sales (id, game_no, game_name, game_code, type, "
				+ "cost_price, tax, sale_price, date_of_sale) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				GameSales game = gameSalesList.get(i);
				ps.setLong(1, game.getId());
				ps.setInt(2, game.getGameNo());
				ps.setString(3, game.getGameName());
				ps.setString(4, game.getGameCode());
				ps.setInt(5, game.getType());
				ps.setBigDecimal(6, game.getCostPrice());
				ps.setBigDecimal(7, game.getTax());
				ps.setBigDecimal(8, game.getSalePrice());
				ps.setTimestamp(9, Timestamp.valueOf(game.getDateOfSale()));
			}
			@Override
			public int getBatchSize() {
				return gameSalesList.size();
			}
		});
	}

	public void loadCsv(String csvFilePath) {
		String sql = "LOAD DATA LOCAL INFILE ? " +
				"INTO TABLE game_sales " +
				"FIELDS TERMINATED BY ',' " +
				"ENCLOSED BY '\"' " +
				"LINES TERMINATED BY '\\n' " +
				"IGNORE 1 LINES " +
				"(id, game_no, game_name, game_code, type, cost_price, tax, sale_price, date_of_sale)";

		jdbcTemplate.execute((Connection con) -> {
		    try (PreparedStatement ps = con.prepareStatement(sql)) {
		        ps.setString(1, csvFilePath);
		        ps.execute();
		    }
		    return null;
		});
	}
}
