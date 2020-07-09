package com.ntscorp.gpa.googleSheetsTest;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Repository;

import com.ntscorp.gpa.googleSheets.GoogleSheetsRepository;

@SpringBootTest
public class googleSheetsRepositoryTest {

	public static class Employee {
		int id;
		String name;
		int age;
		LocalDateTime birthday;

		public Employee() {
			super();
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public LocalDateTime getBirthday() {
			return birthday;
		}

		public void setBirthday(LocalDateTime birthday) {
			this.birthday = birthday;
		}
	}


	@TestConfiguration
	static class TestConfig {
		@Repository
		static class EmployeeRepository extends GoogleSheetsRepository<Employee> {
		}
	}

	@Autowired
	private TestConfig.EmployeeRepository employeeRepository;

	@Test
	public void ex() {
		System.out.println(employeeRepository.getAll());
	}
}
