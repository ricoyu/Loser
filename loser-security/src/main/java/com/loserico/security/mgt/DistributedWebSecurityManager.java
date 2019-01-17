package com.loserico.security.mgt;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import com.loserico.security.concurrent.ThreadContext;

public class DistributedWebSecurityManager extends DefaultWebSecurityManager{
	
	public static final String PRINCIPAL_KEY = "distributed.primary.principal";
	public static final String PERMISSION_KEY = "distributed.checking.permission";

	/**
	 * 当Controller方法上的@RequiresPermissions("xxzx:view")，权限字符串有一个的情况走这个方法
	 */
	@Override
	public void checkPermission(PrincipalCollection principals, String permission) throws AuthorizationException {
		ThreadContext.put(PRINCIPAL_KEY, principals.getPrimaryPrincipal());
		ThreadContext.put(PERMISSION_KEY, permission);
		super.checkPermission(principals, permission);
	}
	
	/**
	 * 当Controller方法上的 @RequiresPermissions(value = {"student:leave:verify","leave:view"},logical = Logical.OR)，
	 * 权限字符串有多个的情况走这个方法
	 */
	@Override
    public boolean isPermitted(PrincipalCollection principals, String permissionString) {
		ThreadContext.put(PRINCIPAL_KEY, principals.getPrimaryPrincipal());
		ThreadContext.put(PERMISSION_KEY, permissionString);
        return super.isPermitted(principals, permissionString);
    }
}
