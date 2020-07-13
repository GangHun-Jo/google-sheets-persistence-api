package com.ntscorp.gpa.googleSheetsTest;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.ntscorp.gpa.googleSheetsTest.config.TestConfig;
import com.ntscorp.gpa.googleSheetsTest.config.TestConfig.Employee;
import com.ntscorp.gpa.googleSheetsTest.config.TestConfig.EmployeeRepository;

@SpringBootTest
@Import(TestConfig.class)
public class googleSheetsRepositoryTest {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
	public void getAllTest() {
		System.out.println(employeeRepository.getAll());
	}

	@Test
	public void addTest() {
		Employee employee = new Employee();
		employee.setAge(26);
		employee.setBirthday(LocalDateTime.now());
		employee.setName("조강훈3");
		employeeRepository.add(employee);
	}

	@Test
	void getByRowNumTest() {
		System.out.println(employeeRepository.getByRowNum(2));
	}
}
