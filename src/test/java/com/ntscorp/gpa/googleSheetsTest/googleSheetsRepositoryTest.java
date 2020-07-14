package com.ntscorp.gpa.googleSheetsTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.ntscorp.gpa.googleSheetsTest.config.TestConfig;
import com.ntscorp.gpa.googleSheetsTest.config.TestConfig.*;
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

		Asset asset1 = new Asset();
		asset1.setName("연관관계자산1");
		asset1.setPurchaseDateTime(LocalDateTime.now());
		Asset asset2 = new Asset();
		asset2.setName("연관관계자산2");
		asset2.setPurchaseDateTime(LocalDateTime.now());
		List<Asset> assetList = new ArrayList<>();
		assetList.add(asset1);
		assetList.add(asset2);

		Employee employee = new Employee();
		// TODO : add하는 순간에는 rouNum을 가져오지 못해서 many 쪽에 joincolumn 값을 설정하지 못한다.
		employee.setAge(21);
		employee.setBirthday(LocalDateTime.now());
		employee.setName("연관관계테스트");
		employee.setAssetList(assetList);

		employeeRepository.add(employee);
	}

	@Test
	void getByRowNumTest() {
		System.out.println(employeeRepository.getByRowNum(2));
	}

	@Test
	void updateTest() {
		Employee employee = employeeRepository.getByRowNum(2);
		employee.setName("changed");
		employeeRepository.update(employee);
	}
}
