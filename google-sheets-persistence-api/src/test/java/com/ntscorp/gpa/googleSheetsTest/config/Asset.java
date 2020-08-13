package com.ntscorp.gpa.googleSheetsTest.config;

import java.time.LocalDateTime;

import com.ntscorp.gpa.googleSheets.entity.GPAEntity;

public class Asset extends GPAEntity {
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
