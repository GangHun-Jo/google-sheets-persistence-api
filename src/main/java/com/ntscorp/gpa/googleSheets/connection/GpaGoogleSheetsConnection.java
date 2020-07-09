package com.ntscorp.gpa.googleSheets.connection;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.ntscorp.gpa.exception.GoogleSheetConnectionException;
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
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Component
public class GpaGoogleSheetsConnection implements GoogleSheetsConnection{

	private final Sheets sheets;
	@Value("${spread.sheet.id}")
	private String SPREAD_SHEET_ID;
	private final String GOOGLE_AUTH_KEY_PATH = "key.json";

	public GpaGoogleSheetsConnection() {
		try {
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			GoogleCredentials credentials = GoogleCredentials.fromStream(
				new ClassPathResource(GOOGLE_AUTH_KEY_PATH).getInputStream())
				.createScoped(SheetsScopes.SPREADSHEETS_READONLY);
			credentials.refreshIfExpired();
			HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

			sheets = new Sheets.Builder(httpTransport, jsonFactory, requestInitializer)
				.setApplicationName("SEF")
				.build();

		} catch (IOException | GeneralSecurityException connectionException) {
			throw new GoogleSheetConnectionException("구글 독스 연결에 실패했습니다.", connectionException);
		}
	}

	public List<List<Object>> getSheet(String range) {
		try {
			Sheets.Spreadsheets.Values.Get request = sheets.spreadsheets().values()
				.get(SPREAD_SHEET_ID, range);
			return request.execute().getValues();
		} catch (GoogleJsonResponseException exception) {
			throw new IllegalArgumentException("잘못된 range 입니다:[" + range + "]",exception);
		} catch (IOException ioException) {
			throw new GoogleSheetConnectionException("구글 독스 연결에 실패했습니다.", ioException);
		}
	}
}
