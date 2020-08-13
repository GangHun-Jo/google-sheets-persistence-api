package com.ntscorp.gpa.entity;

import java.time.LocalDateTime;

import com.ntscorp.gpa.annotation.GPAQuery;
import com.ntscorp.gpa.googleSheets.entity.GPAEntity;

@GPAQuery
public class User extends GPAEntity {
	private int id;
	private String name;
	private LocalDateTime birthDay;

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(LocalDateTime birthDay) {
		this.birthDay = birthDay;
	}
}
