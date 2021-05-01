package com.conor.gpa.googleSheetsTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import com.conor.gpa.googleSheetsTest.config.TestConfig;
import com.conor.gpa.exception.SheetDataMappingException;
import com.conor.gpa.googleSheetsTest.config.Asset;
import com.conor.gpa.googleSheetsTest.config.Employee;
import com.conor.gpa.googleSheetsTest.config.QueryEmployee;

@SpringBootTest
@Import(TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class googleSheetsRepositoryTest {

	@Autowired
	private TestConfig.EmployeeRepository employeeRepository;
	@Autowired
	private TestConfig.AssetRepository assetRepository;

	private List<Employee> employeeList;
	private List<Asset> assetList;

	@BeforeEach
	public void init() {

		Asset asset1 = new Asset(2, "맥북", LocalDateTime.of(2020, 8, 10, 13, 35));
		Asset asset2 = new Asset(2, "마우스", LocalDateTime.of(2020, 8, 10, 13, 45));
		Asset asset3 = new Asset(2, "의자", LocalDateTime.of(2020, 8, 10, 13, 55));
		Asset asset4 = new Asset(3, "맥북2", LocalDateTime.of(2020, 8, 11, 13, 35));
		Asset asset5 = new Asset(4, "맥북3", LocalDateTime.of(2020, 8, 12, 13, 35));
		Asset asset6 = new Asset(4, "마우스3", LocalDateTime.of(2020, 8, 12, 13, 45));

		Employee employee1 = new Employee("조강훈", 25, LocalDateTime.of(1995, 9, 26, 13, 35), new ArrayList<>(Arrays.asList(asset1, asset2, asset3)));
		Employee employee2 = new Employee("조강훈2", 26, LocalDateTime.of(1996, 9, 26, 13, 35), new ArrayList<>(Arrays.asList(asset4)));
		Employee employee3 = new Employee("조강훈3", 27, LocalDateTime.of(1997, 9, 26, 13, 35), new ArrayList<>(Arrays.asList(asset5, asset6)));

		employeeList = new ArrayList<>(Arrays.asList(employee1, employee2, employee3));
		assetList = new ArrayList<>(Arrays.asList(asset1, asset2, asset3, asset4, asset5, asset6));

	}

	@Test
	public void getAllTest() {
		assertEquals(employeeList, employeeRepository.getAll());
		assertEquals(assetList, assetRepository.getAll());
	}

	@Test
	void getByRowNumTest() {
		assertEquals(employeeList.get(0), employeeRepository.getByRowNum(2));
	}

	@Test
	public void addTest() {

		Asset asset1 = new Asset();
		asset1.setName("연관관계자산1");
		asset1.setPurchaseDateTime(LocalDateTime.of(2020, 8, 11, 12, 0));
		Asset asset2 = new Asset();
		asset2.setName("연관관계자산2");
		asset2.setPurchaseDateTime(LocalDateTime.of(2020, 8, 11, 12, 0));
		List<Asset> assetList = new ArrayList<>();
		assetList.add(asset1);
		assetList.add(asset2);

		Employee employee = new Employee();
		// TODO : add하는 순간에는 rouNum을 가져오지 못해서 many 쪽에 joincolumn 값을 설정하지 못한다.
		employee.setAge(21);
		employee.setBirthday(LocalDateTime.of(2020, 8, 11, 12, 0));
		employee.setName("연관관계테스트");
		employee.setAssetList(assetList);

		employeeRepository.add(employee);
		employeeList.add(employee);
		assertEquals(employeeList, employeeRepository.getAll());
	}

	@Test
	void updateTest() {
		Employee employee = employeeRepository.getByRowNum(2);
		employee.setName("changed");
		for (Asset asset : employee.getAssetList()) {
			asset.setName("changedAsset");
		}
		Asset asset1 = new Asset();
		asset1.setName("연관관계자산 추가");
		asset1.setPurchaseDateTime(LocalDateTime.of(2020, 8, 11, 12, 0));
		employee.getAssetList().add(asset1);

		employeeRepository.update(employee);
		employeeList.set(0, employee);
		assertEquals(employeeList, employeeRepository.getAll());
	}

	@Test
	void deleteTest() {
		Employee employee = employeeRepository.getByRowNum(2);
		employeeRepository.delete(employee);
		employeeList.remove(0);

		assertEquals(employeeList, employeeRepository.getAll());
	}

	@Test
	void deleteRelationMapping() {
		Employee employee = employeeRepository.getByRowNum(2);
		Asset deleteAsset = employee.getAssetList().get(0);

		assetRepository.delete(deleteAsset);
		employee.getAssetList().remove(deleteAsset);
		employeeList.get(0).getAssetList().remove(0);

		assertEquals(employeeList.get(0), employeeRepository.getByRowNum(2));
	}

	@Test
	void selectWhereTest() {
		QueryEmployee query = new QueryEmployee();
		assertEquals(employeeList.get(0), employeeRepository.selectOneWhere(query.name("조강훈").build()));
	}

	@Test
	void getEmptyDataTest() {
		assertNull(employeeRepository.getByRowNum(50));

		QueryEmployee query = new QueryEmployee();
		assertNull(employeeRepository.selectOneWhere(query.name("조강훈100").build()));
	}

	@Test
	void inValidUpdateTest() {
		Employee employee = new Employee();
		employee.setRowNum(100);
		employee.setAge(28);
		employee.setName("이름예시");
		employee.setBirthday(LocalDateTime.of(2020,3,1,12,0));
		assertThrows(SheetDataMappingException.class, () -> {employeeRepository.update(employee);});
	}
}
