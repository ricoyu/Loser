package com.loserico.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletUtils {

	private static final Logger log = LoggerFactory.getLogger(ServletUtils.class);

	/**
	 * 获取项目网络路径
	 * @param request
	 * @return
	 */
	public static String getContentpath(HttpServletRequest request) {
		return request.getContextPath();
	}

	/**
	 * 获取项目磁盘绝对路径
	 */
	public static String getRealPath(HttpServletRequest request) {
		return request.getSession().getServletContext().getRealPath("/");
	}

	/**
	 * 使用了代理服务器的，无法获取正确地址的，使用这个方法获取访问者的IP地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 下载多个文件
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param files
	 *            File
	 */
	public void download(HttpServletResponse response, File... files) {

		for (File file : files) {
			download(response, file, file.getName());
		}

	}

	/**
	 * 下载文件
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param file
	 *            文件
	 * @param fileName
	 *            下载的输出文件名
	 */
	public void download(HttpServletResponse response, File file, String fileName) {
		InputStream is = null;
		String _fileName = null;
		try {
			is = new FileInputStream(file);
			_fileName = fileName == null ? file.getName() : fileName;
			download(response, is, _fileName);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param is
	 *            输入流
	 * @param fileName
	 *            文件名
	 * @param response
	 */
	public void download(HttpServletResponse response, InputStream is, String fileName) {
		OutputStream outputStream = null;
		try {
			response.reset();
			response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			outputStream = response.getOutputStream();

			byte buffer[] = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) > 0) {
				outputStream.write(buffer, 0, len);
			}

			outputStream.flush();
			outputStream.close();

			is.close();

		} catch (Exception e) {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e1) {
				log.error(e.getMessage(), e1);
			}
			try {
				if (is != null)
					is.close();
			} catch (IOException e1) {
				log.error(e1.getMessage(), e1);
			}
			log.error(e.getMessage(), e);
		}

	}

}
