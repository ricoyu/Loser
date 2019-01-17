package com.peacefish.orm.commons.identifier;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {

	T getId();
}