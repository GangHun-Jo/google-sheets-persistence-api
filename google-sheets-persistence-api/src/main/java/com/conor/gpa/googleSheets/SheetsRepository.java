package com.conor.gpa.googleSheets;

import java.util.List;
import java.util.function.Predicate;

import com.conor.gpa.googleSheets.entity.GPAEntity;
import com.conor.gpa.googleSheets.paging.GPAPage;
import com.conor.gpa.googleSheets.paging.GPAPageRequest;

public interface SheetsRepository<T extends GPAEntity> {
	List<T> getAll();

	GPAPage<T> getAll(GPAPageRequest pageRequest);

	T getByRowNum(int rowNum);

	List<T> selectWhere(Predicate<T> condition);

	GPAPage<T> selectWhere(Predicate<T> condition, GPAPageRequest pageRequest);

	T selectOneWhere(Predicate<T> condition);

	void add(T data);

	void update(T data);

	void delete(T data);

}
