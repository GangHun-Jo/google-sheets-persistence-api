package com.ntscorp.gpa.googleSheetsTest.config;

import java.time.LocalDateTime;
import java.util.List;

import com.ntscorp.gpa.annotation.GPAQuery;
import com.ntscorp.gpa.googleSheets.annotation.LeftJoin;
import com.ntscorp.gpa.googleSheets.entity.GPAEntity;

@GPAQuery
public class Employee extends GPAEntity {
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
