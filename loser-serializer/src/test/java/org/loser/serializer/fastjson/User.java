package org.loser.serializer.fastjson;

import static java.text.MessageFormat.format;

import java.time.LocalDate;

public class User {

    private Long   id;
    private String name;
    private LocalDate birthday;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public LocalDate getBirthday() {
		return birthday;
	}

	@Override
	public String toString() {
		return format("id[{0}], name[{1}], birthday[{2}]", id, name, birthday);
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}
}