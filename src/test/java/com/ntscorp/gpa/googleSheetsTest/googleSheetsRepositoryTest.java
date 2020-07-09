package com.ntscorp.gpa.googleSheetsTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.ntscorp.gpa.googleSheetsTest.config.TestConfig;
import com.ntscorp.gpa.googleSheetsTest.config.TestConfig.EmployeeRepository;

@SpringBootTest
@Import(TestConfig.class)
public class googleSheetsRepositoryTest {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
	public void ex() {

		System.out.println(employeeRepository.getAll());
	}
}
