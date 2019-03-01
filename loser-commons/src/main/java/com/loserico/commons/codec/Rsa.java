package com.loserico.commons.codec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.loserico.commons.exception.PrivateDecryptException;

/**
 * RSA 非对称加密
 * 
 * RSA 加密演算法是一种非对称加密演算法. 在公开密钥加密和电子商业中RSA被广泛使用. 
 * RSA是1977年由罗纳德·李维斯特(Ron Rivest), 阿迪·萨莫尔(Adi Shamir) 和伦纳德·阿德曼(Leonard Adleman)一起提出的
 * 当时他们三人都在麻省理工学院工作, RSA就是他们三人姓氏开头字母拼在一起组成的
 * 
 * <p>
 * Copyright: Copyright (c) 2018-07-30 13:29
 * <p>
 * Company: DataSense
 * <p>
 * 
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class Rsa {

	public static final String CHARSET = "UTF-8";

	public static final String RSA_ALGORITHM = "RSA";

	public static final String RSA_ALGORITHM_SIGN = "SHA256WithRSA";

	/**
	 * 密钥长度必须是64的倍数，在512到65536位之间
	 */
	private static final int KEY_SIZE = 1024;

	/**
	 * String to hold the name of the private key file.
	 */
	public static final String PRIVATE_KEY_FILE = System.getProperty("user.home") + "/private.key";

	/**
	 * String to hold name of the public key file.
	 */
	public static final String PUBLIC_KEY_FILE = System.getProperty("user.home") + "/public.key";

	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;

	@SuppressWarnings("resource")
	public static Rsa instance() {
		if (!keysPresent()) {
			try {
				KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);

				//初始化KeyPairGenerator对象
				keyPairGenerator.initialize(KEY_SIZE);
				//生成密匙对
				KeyPair keyPair = keyPairGenerator.generateKeyPair();
				//得到公钥
				RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
				//得到私钥
				RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

				File privateKeyFile = new File(PRIVATE_KEY_FILE);
				File publicKeyFile = new File(PUBLIC_KEY_FILE);

				if (publicKeyFile.getParentFile() != null) {
					publicKeyFile.getParentFile().mkdirs();
				}
				publicKeyFile.createNewFile();

				if (privateKeyFile.getParentFile() != null) {
					privateKeyFile.getParentFile().mkdirs();
				}
				privateKeyFile.createNewFile();

				ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
				publicKeyOS.writeObject(publicKey);
				publicKeyOS.close();

				ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
				privateKeyOS.writeObject(keyPair.getPrivate());
				privateKeyOS.close();

				return new Rsa(publicKey, privateKey);
			} catch (Exception e) {
				throw new RuntimeException("生成密钥对失败", e);
			}
		} else {
			try {
				ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
				RSAPublicKey publicKey = (RSAPublicKey) objectInputStream.readObject();

				objectInputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
				RSAPrivateKey privateKey = (RSAPrivateKey) objectInputStream.readObject();
				objectInputStream.close();

				return new Rsa(publicKey, privateKey);
			} catch (IOException | ClassNotFoundException e) {
				throw new RuntimeException("获取密钥对失败", e);
			}
		}
	}

	public Rsa(String publicKey, String privateKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

			//通过X509编码的Key指令获得公钥对象
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
			this.publicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
			//通过PKCS#8编码的Key指令获得私钥对象
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
			this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
		} catch (Exception e) {
			throw new RuntimeException("不支持的密钥", e);
		}
	}

	public Rsa(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public String publicEncrypt(String data) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return Base64.encodeBase64String(
					rsaSplitCodec(cipher,
							Cipher.ENCRYPT_MODE,
							data.getBytes(CHARSET),
							publicKey.getModulus().bitLength()));
		} catch (Exception e) {
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}

	public String publicEncrypt(String data, String key) {
		try {
			// 对公钥解密
			byte[] keyBytes = Base64.decodeBase64(key);
			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			Key publicKey = keyFactory.generatePublic(x509KeySpec);
			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));
			return Base64.encodeBase64String(bytes);
		} catch (Exception e) {
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}

	public String privateEncrypt(String data) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			return Base64.encodeBase64String(
					rsaSplitCodec(cipher,
							Cipher.ENCRYPT_MODE,
							data.getBytes(CHARSET),
							publicKey.getModulus().bitLength()));
		} catch (Exception e) {
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}

	public String publicDecrypt(String data) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			return new String(
					rsaSplitCodec(cipher,
							Cipher.DECRYPT_MODE,
							Base64.decodeBase64(data),
							publicKey.getModulus().bitLength()),
					CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
		}
	}

	public String privateDecrypt(String data) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(
					rsaSplitCodec(cipher,
							Cipher.DECRYPT_MODE,
							Base64.decodeBase64(data),
							publicKey.getModulus().bitLength()),
					CHARSET);
		} catch (Exception e) {
			throw new PrivateDecryptException("解密字符串[" + data + "]时遇到异常", e);
		}
	}
	
	public String privateDecrypt(String data, String privateKey) {
		try {
			 // 对密钥解密
	        byte[] keyBytes = Base64.decodeBase64(privateKey);
	        // 取得私钥
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
	        Key key = keyFactory.generatePrivate(pkcs8KeySpec);
	        // 对数据解密
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        byte[] bytes = cipher.doFinal(Base64.decodeBase64(data));
	        return new String(bytes, CHARSET);
		} catch (Exception e) {
			throw new PrivateDecryptException("解密字符串[" + data + "]时遇到异常", e);
		}
	}

	public String sign(String data) {
		try {
			//sign
			Signature signature = Signature.getInstance(RSA_ALGORITHM_SIGN);
			signature.initSign(privateKey);
			signature.update(data.getBytes(CHARSET));
			return Base64.encodeBase64String(signature.sign());
		} catch (Exception e) {
			throw new RuntimeException("签名字符串[" + data + "]时遇到异常", e);
		}
	}

	public boolean verify(String data, String sign) {
		try {
			Signature signature = Signature.getInstance(RSA_ALGORITHM_SIGN);
			signature.initVerify(publicKey);
			signature.update(data.getBytes(CHARSET));
			return signature.verify(Base64.decodeBase64(sign));
		} catch (Exception e) {
			throw new RuntimeException("验签字符串[" + data + "]时遇到异常", e);
		}
	}

	@SuppressWarnings("deprecation")
	private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
		int maxBlock = 0;
		if (opmode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try {
			while (datas.length > offSet) {
				if (datas.length - offSet > maxBlock) {
					buff = cipher.doFinal(datas, offSet, maxBlock);
				} else {
					buff = cipher.doFinal(datas, offSet, datas.length - offSet);
				}
				out.write(buff, 0, buff.length);
				i++;
				offSet = i * maxBlock;
			}
		} catch (Exception e) {
			throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
		}
		byte[] resultDatas = out.toByteArray();
		IOUtils.closeQuietly(out);
		return resultDatas;
	}

	public String publicKey() {
		byte[] bytes = publicKey.getEncoded();
		return Base64.encodeBase64String(bytes);
		//return Base64.encodeBase64String(bytes);
	}

	/**
	 * 检查公钥/私钥是否都已生成
	 * @return boolean
	 */
	private static boolean keysPresent() {
		File privateKey = new File(PRIVATE_KEY_FILE);
		File publicKey = new File(PUBLIC_KEY_FILE);

		return privateKey.exists() && publicKey.exists();
	}
}