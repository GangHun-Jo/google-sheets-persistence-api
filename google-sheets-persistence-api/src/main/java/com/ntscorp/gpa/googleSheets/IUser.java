package com.ntscorp.gpa.googleSheets;

import com.ntscorp.gpa.annotation.AutoImplement;
import com.ntscorp.gpa.annotation.Mandatory;

import java.time.LocalDate;

@AutoImplement(as = "User", builder = true)
public interface IUser {

	@Mandatory
	String getFirstName();

	@Mandatory
	String getLastName();

	LocalDate getDateOfBirth();

	String getPlaceOfBirth();

	String getPhone();

	String getAddress();
}
