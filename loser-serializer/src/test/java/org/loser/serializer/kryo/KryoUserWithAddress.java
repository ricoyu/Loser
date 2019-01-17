package org.loser.serializer.kryo;

import java.time.LocalDateTime;

public class KryoUserWithAddress {

	private String name;
	private LocalDateTime birthday;
	private KryoAddress address;

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

	public KryoAddress getAddress() {
		return address;
	}

	public void setAddress(KryoAddress address) {
		this.address = address;
	}
}
