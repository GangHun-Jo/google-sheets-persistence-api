package com.ntscorp.gpa.googleSheets.connection;

import java.util.List;

public interface GoogleSheetsConnection {
	List<List<Object>> getSheet(String range);
}
