package com.ntscorp.gpa.googleSheets;

public abstract class GPAEntity {
	private int rowNum;

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GPAEntity) {
			return this.rowNum == ((GPAEntity) obj).getRowNum();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.rowNum;
	}
}
