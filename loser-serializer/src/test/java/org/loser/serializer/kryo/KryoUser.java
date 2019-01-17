package org.loser.serializer.kryo;

import java.time.LocalDateTime;

public class KryoUser {

	private String name;
	private LocalDateTime birthday;

	public LocalDateTime getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDateTime birthday) {
		this.birthday = birthday;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
