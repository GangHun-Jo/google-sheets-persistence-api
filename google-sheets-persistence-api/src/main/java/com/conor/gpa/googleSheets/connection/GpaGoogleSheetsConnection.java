package com.conor.gpa.googleSheets.connection;

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
import com.conor.gpa.exception.GoogleSheetConnectionException;

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
			throw new GoogleSheetConnectionException("?????? ?????? ????????? ??????????????????.", connectionException);
		}
	}

	@Override
	public List<List<Object>> getSheet(String range) {
		try {
			Sheets.Spreadsheets.Values.Get request = sheets.spreadsheets().values()
				.get(SPREAD_SHEET_ID, range);
			return request.execute().getValues();
		} catch (GoogleJsonResponseException exception) {
			logger.debug("????????? range ?????????:[" + range + "]");
			logger.debug(exception.toString());
			return null;
		} catch (IOException ioException) {
			throw new GoogleSheetConnectionException("?????? ?????? ????????? ??????????????????.", ioException);
		}
	}

	@Override
	public int add(String sheetName, List<Object> data) {
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(Arrays.asList(data));

		try {
			// range??? ?????????????????? ?????? ???????????? ????????? ????????? row??? ???????????? ???
			Sheets.Spreadsheets.Values.Append request = sheets.spreadsheets().values().append(SPREAD_SHEET_ID, sheetName, valueRange);
			request.setValueInputOption("USER_ENTERED");
			request.setInsertDataOption("INSERT_ROWS");
			return getUpdatedRowNum(request.execute().getUpdates().getUpdatedRange());
		} catch (IOException ioException) {
			throw new GoogleSheetConnectionException("?????? ?????? ????????? ??????????????????.", ioException);
		}
	}

	@Override
	public int update(String range, List<Object> data) {
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(Arrays.asList(data));

		try {
			// range??? ???????????? ????????? append??? ?????? ???
			Sheets.Spreadsheets.Values.Update request = sheets.spreadsheets().values().update(SPREAD_SHEET_ID, range, valueRange);
			request.setValueInputOption("USER_ENTERED");
			request.execute();
			return getUpdatedRowNum(request.execute().getUpdatedRange());
		} catch (IOException ioException) {
			throw new GoogleSheetConnectionException("?????? ?????? ????????? ??????????????????.", ioException);
		}
	}

	@Override
	public void clear(String range) {
		try {
			Sheets.Spreadsheets.Values.Clear request =
				sheets.spreadsheets().values().clear(SPREAD_SHEET_ID, range, new ClearValuesRequest());
			request.execute();
		} catch (IOException ioException) {
			throw new GoogleSheetConnectionException("?????? ?????? ????????? ??????????????????.", ioException);
		}
	}

	private int getUpdatedRowNum(String range) {
		// TODO: range??? ????????? ????????? ????????? ????????? ??????
		return Integer.parseInt(range.split(":")[1].replaceAll("[^0-9]", ""));
	}
}
