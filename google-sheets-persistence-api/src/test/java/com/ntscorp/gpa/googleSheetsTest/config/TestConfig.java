package com.ntscorp.gpa.googleSheetsTest.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.ntscorp.gpa.googleSheets.GoogleSheetsRepository;
import com.ntscorp.gpa.googleSheets.annotation.LeftJoin;
import com.ntscorp.gpa.googleSheets.connection.GoogleSheetsConnection;
import com.ntscorp.gpa.googleSheets.entity.GPAEntity;


@TestConfiguration
public class TestConfig {

	@Bean
	@Primary
	public GoogleSheetsConnection googleSheetsConnection() {
		return new TestGoogleSheetsConnection();
	}

	public static class Employee extends GPAEntity {
		private String name;
		private int age;
		private LocalDateTime birthday;
		@LeftJoin(joinColumn = "employeeId", targetClass = Asset.class)
		private List<Asset> assetList;

		public Employee() {
			super();
		}

		public Employee(String name, int age, LocalDateTime birthday, List<Asset> assetList) {
			this.name = name;
			this.age = age;
			this.birthday = birthday;
			this.assetList = assetList;
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

		public List<Asset> getAssetList() {
			return assetList;
		}

		public void setAssetList(List<Asset> assetList) {
			this.assetList = assetList;
		}

		@Override
		public String toString() {
			return String.join(" ", name, Integer.toString(age), birthday.toString(), assetList.toString());
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Employee) {
				return this.name.equals(((Employee) obj).getName())
					&& this.age == ((Employee) obj).getAge()
					&& this.birthday.isEqual(((Employee) obj).getBirthday())
					&& this.assetList.equals(((Employee) obj).getAssetList());
			}
			return false;
		}
	}

	public static class Asset extends GPAEntity {
		private int employeeId;
		private String name;
		private LocalDateTime purchaseDateTime;

		public Asset() {
			super();
		}

		public Asset(int employeeId, String name, LocalDateTime purchaseDateTime) {
			this.employeeId = employeeId;
			this.name = name;
			this.purchaseDateTime = purchaseDateTime;
		}

		public int getEmployeeId() {
			return employeeId;
		}

		public void setEmployeeId(int employeeId) {
			this.employeeId = employeeId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public LocalDateTime getPurchaseDateTime() {
			return purchaseDateTime;
		}

		public void setPurchaseDateTime(LocalDateTime purchaseDateTime) {
			this.purchaseDateTime = purchaseDateTime;
		}

		@Override
		public String toString() {
			return String.join(" ", Integer.toString(employeeId), name, purchaseDateTime.toString());
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Asset) {
				return this.employeeId == ((Asset) obj).getEmployeeId()
					&& this.name.equals(((Asset) obj).getName())
					&& this.purchaseDateTime.isEqual(((Asset) obj).getPurchaseDateTime());
			}
			return false;
		}
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
