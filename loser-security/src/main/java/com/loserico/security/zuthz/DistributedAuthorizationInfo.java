package com.loserico.security.zuthz;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;

public class DistributedAuthorizationInfo extends SimpleAuthorizationInfo {

	private static final long serialVersionUID = 6834832761800758519L;
	
	private List<Object> privileges = new ArrayList<>();

	private String visiableScope;

	private String permission;
	
	//0 员工 1家长
	private int userType;
	
	private PrincipalCollection principals;

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getVisiableScope() {
		return visiableScope;
	}

	public void setVisiableScope(String visiableScope) {
		this.visiableScope = visiableScope;
	}

	public List<Object> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Object> privileges) {
		this.privileges = privileges;
	}

	public PrincipalCollection getPrincipals() {
		return principals;
	}

	public void setPrincipals(PrincipalCollection principals) {
		this.principals = principals;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}
}
