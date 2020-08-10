package com.ntscorp.gpa.googleSheets;

import java.util.List;

import com.ntscorp.gpa.googleSheets.entity.GPAEntity;

public interface SheetsRepository<T extends GPAEntity> {
	List<T> getAll();

	T getByRowNum(int rowNum);

	void add(T data);

	void update(T data);

	void delete(T data);

}
