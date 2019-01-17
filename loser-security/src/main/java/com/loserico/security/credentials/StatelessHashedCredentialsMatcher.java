package com.loserico.security.credentials;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.loserico.security.token.StatelessToken;

public class StatelessHashedCredentialsMatcher extends HashedCredentialsMatcher {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate = null;

	public StatelessHashedCredentialsMatcher() {
	}

	public StatelessHashedCredentialsMatcher(String hashAlgorithmName) {
		super(hashAlgorithmName);
	}

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		if (token instanceof StatelessToken) {
			//Redis中存在该token就表示“密码匹配”，即登录通过
			return redisTemplate.opsForValue().get(((StatelessToken) token).getAccessToken()) != null;
		}

		//如果是UsernamePasswordToken则通过密码匹配
		Object tokenHashedCredentials = hashProvidedCredentials(token, info);
		Object accountCredentials = getCredentials(info);
		return equals(tokenHashedCredentials, accountCredentials);
	}
}
