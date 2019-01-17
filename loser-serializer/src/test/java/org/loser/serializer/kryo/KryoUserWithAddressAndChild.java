package org.loser.serializer.kryo;

import java.time.LocalDateTime;

public class KryoUserWithAddressAndChild {

	private String name;
	private LocalDateTime birthday;
	private KryoAddress address;
	private KryoUserWithAddressAndChild child;

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

	public KryoUserWithAddressAndChild getChild() {
		return child;
	}

	public void setChild(KryoUserWithAddressAndChild child) {
		this.child = child;
	}
}
