package com.ntscorp.gpa.googleSheets.connection;

import java.util.List;

public interface GoogleSheetsConnection {
	List<List<Object>> getSheet(String range);

	// insert 한 rowNum을 return
	int add(String sheetName, List<Object> data);
	// update 한 rowNum을 return
	int update(String range, List<Object> data);

	void clear(String range);

}
