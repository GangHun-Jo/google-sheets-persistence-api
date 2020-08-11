package com.ntscorp.gpa.googleSheetsTest.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ntscorp.gpa.googleSheets.connection.GoogleSheetsConnection;

public class TestGoogleSheetsConnection implements GoogleSheetsConnection {

	private List<List<Object>> employeeList = new ArrayList<>();
	private List<List<Object>> assetList = new ArrayList<>();

	public TestGoogleSheetsConnection() {
		employeeList.add(Arrays.asList("name", "age", "birthday"));
		employeeList.add(Arrays.asList("조강훈", "25", "1995-09-26 13:35"));
		employeeList.add(Arrays.asList("조강훈2", "26", "1996-09-26 13:35"));
		employeeList.add(Arrays.asList("조강훈3", "27", "1997-09-26 13:35"));

		assetList.add(Arrays.asList("employeeId", "name", "purchaseDateTime"));
		assetList.add(Arrays.asList("2", "맥북", "2020-08-10 13:35"));
		assetList.add(Arrays.asList("2", "마우스", "2020-08-10 13:45"));
		assetList.add(Arrays.asList("2", "의자", "2020-08-10 13:55"));
		assetList.add(Arrays.asList("3", "맥북2", "2020-08-11 13:35"));
		assetList.add(Arrays.asList("4", "맥북3", "2020-08-12 13:35"));
		assetList.add(Arrays.asList("4", "마우스3", "2020-08-12 13:45"));
	}

	@Override
	public List<List<Object>> getSheet(String range) {
		if (range.equals(TestConfig.Employee.class.getSimpleName())) {
			return employeeList;
		} else if (range.equals(TestConfig.Asset.class.getSimpleName())) {
			return assetList;
		} else if (range.matches(TestConfig.Employee.class.getSimpleName() + "![0-9]:[0-9]")) {
			Pattern pattern = Pattern.compile(TestConfig.Employee.class.getSimpleName() + "!([0-9]):([0-9])");
			Matcher matcher = pattern.matcher(range);
			if (!matcher.matches() || Integer.parseInt(matcher.group(1)) != Integer.parseInt(matcher.group(2))) {
				return new ArrayList<>();
			}
			List<List<Object>> result = new ArrayList<>();
			result.add(employeeList.get(Integer.parseInt(matcher.group(1)) - 1));
			return result;
		} else if (range.matches(TestConfig.Asset.class.getSimpleName() + "![0-9]:[0-9]")) {
			Pattern pattern = Pattern.compile(TestConfig.Asset.class.getSimpleName() + "!([0-9]):([0-9])");
			Matcher matcher = pattern.matcher(range);
			if (!matcher.matches() || Integer.parseInt(matcher.group(1)) != Integer.parseInt(matcher.group(2))) {
				return new ArrayList<>();
			}
			List<List<Object>> result = new ArrayList<>();
			result.add(assetList.get(Integer.parseInt(matcher.group(1)) - 1));
			return result;
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public int add(String sheetName, List<Object> data) {
		if (sheetName.equals(TestConfig.Employee.class.getSimpleName())) {
			employeeList.add(data);
			return employeeList.size();
		} else if (sheetName.equals(TestConfig.Asset.class.getSimpleName())) {
			assetList.add(data);
			return assetList.size();
		}
		return 0;
	}

	@Override
	public int update(String range, List<Object> data) {
		if (range.matches(TestConfig.Employee.class.getSimpleName() + "![0-9]:[0-9]")) {
			Pattern pattern = Pattern.compile(TestConfig.Employee.class.getSimpleName() + "!([0-9]):([0-9])");
			Matcher matcher = pattern.matcher(range);
			if (!matcher.matches() || Integer.parseInt(matcher.group(1)) != Integer.parseInt(matcher.group(2))) {
				return -1;
			}
			employeeList.set(Integer.parseInt(matcher.group(1)) - 1, data);
			return Integer.parseInt(matcher.group(1));
		} else if (range.matches(TestConfig.Asset.class.getSimpleName() + "![0-9]:[0-9]")) {
			Pattern pattern = Pattern.compile(TestConfig.Asset.class.getSimpleName() + "!([0-9]):([0-9])");
			Matcher matcher = pattern.matcher(range);
			if (!matcher.matches() || Integer.parseInt(matcher.group(1)) != Integer.parseInt(matcher.group(2))) {
				return -1;
			}
			assetList.set(Integer.parseInt(matcher.group(1)) - 1, data);
			return Integer.parseInt(matcher.group(1));
		}
		return -1;
	}

	@Override
	public void clear(String range) {
		if (range.matches(TestConfig.Employee.class.getSimpleName() + "![0-9]:[0-9]")) {
			Pattern pattern = Pattern.compile(TestConfig.Employee.class.getSimpleName() + "!([0-9]):([0-9])");
			Matcher matcher = pattern.matcher(range);
			if (!matcher.matches() || Integer.parseInt(matcher.group(1)) != Integer.parseInt(matcher.group(2))) {
				return;
			}
			employeeList.set(Integer.parseInt(matcher.group(1)) - 1, new ArrayList<>());
		} else if (range.matches(TestConfig.Asset.class.getSimpleName() + "![0-9]:[0-9]")) {
			Pattern pattern = Pattern.compile(TestConfig.Asset.class.getSimpleName() + "!([0-9]):([0-9])");
			Matcher matcher = pattern.matcher(range);
			if (!matcher.matches() || Integer.parseInt(matcher.group(1)) != Integer.parseInt(matcher.group(2))) {
				return;
			}
			assetList.set(Integer.parseInt(matcher.group(1)) - 1, new ArrayList<>());
		}
	}

	public List<List<Object>> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<List<Object>> employeeList) {
		this.employeeList = employeeList;
	}

	public List<List<Object>> getAssetList() {
		return assetList;
	}

	public void setAssetList(List<List<Object>> assetList) {
		this.assetList = assetList;
	}
}
