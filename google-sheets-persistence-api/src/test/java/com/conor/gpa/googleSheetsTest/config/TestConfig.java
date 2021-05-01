package com.conor.gpa.googleSheetsTest.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.conor.gpa.googleSheets.GoogleSheetsRepository;
import com.conor.gpa.googleSheets.connection.GoogleSheetsConnection;


@TestConfiguration
public class TestConfig {

	@Bean
	@Primary
	public GoogleSheetsConnection googleSheetsConnection() {
		return new TestGoogleSheetsConnection();
	}

	// left join시 bean 의 이름으로 검색하므로 이름을 설정해주어야함
	// TODO : bean의 이름 대신 type으로 검색하는 방식으로 구현해보
	@Repository(value = "assetRepository")
	public static class AssetRepository extends GoogleSheetsRepository<Asset> {
	}

	@Repository(value = "employeeRepository")
	public static class EmployeeRepository extends GoogleSheetsRepository<Employee> {
	}
}
