package com.loserico.io;

import java.io.Serializable;

/**
 * Let’s start by creating a serializable class, and declare a serialVersionUID identifier:
 * <p>
 * Copyright: Copyright (c) 2018-10-23 09:50
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class AppleProduct implements Serializable {
	//原始serialVersionUID
	//private static final long serialVersionUID = 7524559242837253281L;
	private static final long serialVersionUID = 7524559242837253282L;
	public String headphonePort;
	public String thunderboltPort;

	public String getHeadphonePort() {
		return headphonePort;
	}

	public void setHeadphonePort(String headphonePort) {
		this.headphonePort = headphonePort;
	}

	public String getThunderboltPort() {
		return thunderboltPort;
	}

	public void setThunderboltPort(String thunderboltPort) {
		this.thunderboltPort = thunderboltPort;
	}

}