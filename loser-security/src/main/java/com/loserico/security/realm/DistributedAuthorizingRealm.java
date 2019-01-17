package com.loserico.security.realm;

import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.realm.Realm;

/**
 * 通过这个接口，各子模块都通过认证中心获取授权信息
 * @author Rico Yu	ricoyu520@gmail.com
 * @since 2017-07-14 13:55
 * @version 1.0
 *
 */
public interface DistributedAuthorizingRealm extends Authorizer, Realm{

}
