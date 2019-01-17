package com.loserico.concurrent.chapter4.pets;

public class Appointment<T> {
	private final T toBeSeen;

	public T getPatient() {
		return toBeSeen;
	}

	public Appointment(T incoming) {
		toBeSeen = incoming;
	}
}
