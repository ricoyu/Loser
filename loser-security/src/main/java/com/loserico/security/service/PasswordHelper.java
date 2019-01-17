package com.loserico.security.service;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Value;

public class PasswordHelper {

	private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

	@Value("${hashAlgorithm}")
	private String algorithmName = "SHA-512";
	@Value("${hashIterations}")
	private int hashIterations = 2;

	public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public void setHashIterations(int hashIterations) {
		this.hashIterations = hashIterations;
	}

	public String privateSalt() {
		return randomNumberGenerator.nextBytes().toHex();
	}

	public String encryptPassword(String password, String publicSalt, String privateSalt) {
		return new SimpleHash(algorithmName, password, ByteSource.Util.bytes(publicSalt + privateSalt), hashIterations)
				.toHex();
	}
	
	public String encryptPassword(String password, String privateSalt) {
		return new SimpleHash(algorithmName, password, ByteSource.Util.bytes(privateSalt), hashIterations)
				.toHex();
	}
}