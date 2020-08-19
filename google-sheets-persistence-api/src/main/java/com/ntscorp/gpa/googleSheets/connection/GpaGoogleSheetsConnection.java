package com.ntscorp.gpa.googleSheets.connection;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.ntscorp.gpa.exception.GoogleSheetConnectionException;

@Component
public class GpaGoogleSheetsConnection implements GoogleSheetsConnection {

	private Sheets sheets;
	@Value("${spread.sheet.id}")
	private String SPREAD_SHEET_ID;
	@Value("${spread.sheet.key.path}")
	private String GOOGLE_AUTH_KEY_PATH;

	private Logger logger = LoggerFactory.getLogger(GpaGoogleSheetsConnection.class);

	@PostConstruct
	public void init() {
		try {
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			GoogleCredentials credentials = GoogleCredentials.fromStream(
				new ClassPathResource(GOOGLE_AUTH_KEY_PATH).getInputStream())
				.createScoped(SheetsScopes.SPREADSHEETS);
			credentials.refreshIfExpired();
			HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

			sheets = new Sheets.Builder(httpTransport, jsonFactory, requestInitializer)
				.setApplicationName("SEF")
				.build();

		} catch (IOException | GeneralSecurityException connectionException) {
			throw new GoogleSheetConnectionException("구글 독스 연결에 실패했습니다.", connectionException);
		}
	}

	@Override
	public List<List<Object>> getSheet(String range) {
		try {
			Sheets.Spreadsheets.Values.Get request = sheets.spreadsheets().values()
				.get(SPREAD_SHEET_ID, range);
			return request.execute().getValues();
		} catch (GoogleJsonResponseException exception) {
			logger.debug("잘못된 range 입니다:[" + range + "]");
			logger.debug(exception.toString());
			return null;
		} catch (IOException ioException) {
			throw new GoogleSheetConnectionException("구글 독스 연결에 실패했습니다.", ioException);
		}
	}

	@Override
	public int add(String sheetName, List<Object> data) {
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(Arrays.asList(data));

		try {
			// range를 시트이름으로 하면 자동으로 테이블 마지막 row에 넣어주는 듯
			Sheets.Spreadsheets.Values.Append request = sheets.spreadsheets().values().append(SPREAD_SHEET_ID, sheetName, valueRange);
			request.setValueInputOption("USER_ENTERED");
			request.setInsertDataOption("INSERT_ROWS");
			return getUpdatedRowNum(request.execute().getUpdates().getUpdatedRange());
		} catch (IOException ioException) {
			throw new GoogleSheetConnectionException("구글 독스 연결에 실패했습니다.", ioException);
		}
	}

	@Override
	public int update(String range, List<Object> data) {
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(Arrays.asList(data));

		try {
			// range에 데이터가 없으면 append랑 같은 기
			Sheets.Spreadsheets.Values.Update request = sheets.spreadsheets().values().update(SPREAD_SHEET_ID, range, valueRange);
			request.setValueInputOption("USER_ENTERED");
			request.execute();
			return getUpdatedRowNum(request.execute().getUpdatedRange());
		} catch (IOException ioException) {
			throw new GoogleSheetConnectionException("구글 독스 연결에 실패했습니다.", ioException);
		}
	}

	@Override
	public void clear(String range) {
		try {
			Sheets.Spreadsheets.Values.Clear request =
				sheets.spreadsheets().values().clear(SPREAD_SHEET_ID, range, new ClearValuesRequest());
			request.execute();
		} catch (IOException ioException) {
			throw new GoogleSheetConnectionException("구글 독스 연결에 실패했습니다.", ioException);
		}
	}

	private int getUpdatedRowNum(String range) {
		// TODO: range의 컬럼에 숫자가 들어올 경우가 있나
		return Integer.parseInt(range.split(":")[1].replaceAll("[^0-9]", ""));
	}
}
