package com.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.game.entity.CsvImportInfo;

public interface CsvImportInfoRepository extends JpaRepository<CsvImportInfo, Long> {
}
