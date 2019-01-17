package com.loserico.io;

/**
 * 定义常用的ContentType类型
 * <p>
 * Copyright: Copyright (c) 2018-10-09 14:06
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 *
 */
public enum ContentTypes {

	OCTET("*", "application/octet-stream", "An unknown file type"),
	TXT("txt", "text/plain", "Default value for textual files"),
	CSV("csv", "text/csv", "Comma-separated values (CSV)"),
	DOC("doc", "application/msword", "Microsoft Word"),
	DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Microsoft Word (OpenXML)"),
	XLS("xls", "application/vnd.ms-excel", "Microsoft Excel"),
	XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Microsoft Excel (OpenXML)"),
	PDF("pdf", "application/pdf", "Adobe Portable Document Format (PDF)");

	private String ext;

	private String contentType;

	private String desc;

	private ContentTypes(String ext, String contentType, String desc) {
		this.ext = ext;
		this.contentType = contentType;
		this.desc = desc;
	}

	/**
	 * 返回文件后缀(不含.号)对应的Content-Type
	 * 没有找到对应值则返回 application/octet-stream
	 * @param ext
	 * @return String
	 * @on
	 */
	public static String contentType(String ext) {
		ContentTypes[] arr = ContentTypes.values();
		for (int i = 0; i < arr.length; i++) {
			ContentTypes contentTypes = arr[i];
			if (contentTypes.ext.equalsIgnoreCase(ext) || ("." + contentTypes.ext).equalsIgnoreCase(ext)) {
				return contentTypes.contentType;
			}
		}

		return OCTET.contentType;
	}

	/**
	 * 返回文件后缀(不含.号)代表的文件类型描述
	 * 没有找到对应值则返回: An unknown file type
	 * @param ext
	 * @return String
	 * @on
	 */
	public static String description(String ext) {
		ContentTypes[] arr = ContentTypes.values();
		for (int i = 0; i < arr.length; i++) {
			ContentTypes contentTypes = arr[i];
			if (contentTypes.ext.equalsIgnoreCase(ext)) {
				return contentTypes.desc;
			}
		}

		return OCTET.desc;
	}

	public String getExt() {
		return ext;
	}

	public String getContentType() {
		return contentType;
	}

	public String getDesc() {
		return desc;
	}

}
