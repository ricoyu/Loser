package org.loser.serializer.kryo;

import java.time.LocalDateTime;

public class KryoUserWithAddressAndChildFather {

	private String name;
	private LocalDateTime birthday;
	private KryoAddress address;
	private KryoUserWithAddressAndChildFather child;
	private KryoUserWithAddressAndChildFather father;

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

	public KryoUserWithAddressAndChildFather getChild() {
		return child;
	}

	public void setChild(KryoUserWithAddressAndChildFather child) {
		this.child = child;
	}

	public KryoUserWithAddressAndChildFather getFather() {
		return father;
	}

	public void setFather(KryoUserWithAddressAndChildFather father) {
		this.father = father;
	}
}
