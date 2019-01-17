package com.loserico.web.enums;

/**
 * 异常代码，code是相关代码、msgTemplate是国际化消息模版、defaultMsg是模版对应的国际化消息不存在情况下的默认消息
 * 
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-07-18 13:25
 * @version 1.0
 *
 */
public enum StatusCode {

	/* 40xx 数据验证相关code */
	BAD_REQUEST(4000, "code.badrequest", "Bad Request"),
	GUARDIAN_EXISTS(4001, "guardian.exists", "监护人已存在"),
	STUDENT_EXISTS(4002, "student.exists", "学生已存在"),
	MOBILE_REGISTERED(4003, "mobile.registered", "此手机号已被注册"),
	NO_JOIN_DIFFERENT_FAMILY(4004, "no.join.different.family", "同一监护人不能加入不同的家庭"),
	FAMILY_EXIST(4005, "family.exist", "此监护人已在当前家庭中"),
	IS_JOIN_CURRENT_FAMILY(4006, "is.join.current.family", "是否加入当前家庭"),
	
	RFID_CARD_INUSE(4007, "rfid.card.inuse", "该RDID卡已被使用"),
	RFID_CARD_LOST(4008, "rfid.card.lost", "该RDID卡已被挂失"),
	RFID_CARD_DISABLED(4009, "rfid.card.disabled", "该RDID卡已被禁用"),
	RFID_CARD_AVAILABLE(4010, "rfid.card.available", "该RDID卡可用"),
	RFID_CARD_NOT_EXIST(4011, "rfid.card.not.exist", "该RDID卡不存在"),
	
	SEMESTER_NOT_EXIST(4012, "semester.not.exist", "该学期不存在"),
	FAMILY_EXIST_OTHER(4013, "guardian.family.exists", "此监护人已是其他家庭的监护人"),
	NOT_AUTHED_GUARDIAN(4014, "not.authed.guardian", "您不是认证监护人，无法登录"),
	NO_GUARDIAN_ALLOWED_LOGIN_BACKEND(4015, "no.guardian.allowed.login.backend", "家长不能登录后台系统"),
	
	MEDICALRECORD_EXISTS(4020, "medical.record_exists", "幼儿医疗记录已被录入"),
	EMERGENCYCONTACTS_EXISTS(4021, "emergencycontacts_exists", "小孩紧急联系人已存在"),
	DUPLICATE_SUBMISSION(4022, "duplicate.submit", "请勿重复提交"),

	/* 41xx 数据交互相关code */
	ENTITY_NOT_FOUND(4101, "code.entity.not.found", "请求的数据不存在"),
	USER_CREATE_FAILED(4102, "code.user.create.fail", "创建用户失败"),
	UNIQUE_VIOLATION(4103, "code.unique.violation", "违反唯一性约束"),

	/* 42xx 认证授权相关code */
	USERNAME_PASSWORD_ERROR(4200, "username.password.invalid", "用户名或密码错误"),
	NO_DEVICE_ID(4201, "not.empty.deviceid", "请提供deviceId"),
	PAD_STAFF_ONLY(4202, "Pad.grant.to.staff.only", "平板电脑仅限员工登录"),
	FORBIDDEN(4203, "code.forbidden", "不允许访问"),
	TOKEN_EXPIRED(4204, "code.token.expired", "您的token已过期"),
	ACCOUNT_LOCKED(4205, "account.locked", "您的账号已被冻结"),
	MOBILE_UNREGISTED(4206, "mobile.unregisted", "您的手机号尚未注册"),
	GUARDIAN_NO_CHILDREN(4207, "guardian.no.children", "您好，亲爱的家长，目前您名下暂无小孩入学。"),
	USER_NOT_EXISTS(4208, "user.not.exists", "该用户不存在"),
	SMS_CODE_INCORRECT(4209, "sms.code.incorrect", "短信验证码错误"),
	SMS_CODE_EXPIRED(4210, "sms.code.expired", "您的短信验证码不存在或已过期"),
	ACCOUNT_NOT_EXIST(4211, "account.not.exist", "您的账号不存在"),
	NO_PLATFORM(4212, "not.empty.platform", "请提供platform"),
	NO_VERSION(4213, "not.empty.version", "请提供version"),
	NO_MOBILE(4214, "not.empty.mobile", "请提供mobile"),
	FIRST_LOGIN(4215, "user.first.login", "首次登录请修改密码"),
	ACCOUNT_EXISTS(4216, "account.exists", "账号已存在"),
	NO_MAC(4217, "not.empty.mac", "请提供MAC地址"),
	NOT_IN_MAC_WHITELIST(4218, "not.in.mac.whitelist", "您不在MAC白名单中，无法登陆"),
	
	/* 43xx API 调用相关code */
	API_RPC_FAIL(4300, "rpc.call.failed", "RPC调用失败"),
	API_FILEUPLOAD_FAIL(4301, "file.upload.failed", "上传图片失败"),

	/*财务系统异常*/
	REFUND_AMOUNT_INCORRECT(4400, "refund.amount.incorrect", "退款金额不正确"),
	AMOUNT_NOT_CREDITED(4401, "amount.not.credited", "退款金额未到帐"),
	
	/* 50xx 未知异常 */
	INTERNAL_SERVER_ERROR(5000, "code.internal.server.error", "Internal Server Error");
	
	private StatusCode(int code, String msgTemplate, String defaultMsg) {
		this.setCode(code);
		this.setMsgTemplate(msgTemplate);
		this.setDefaultMsg(defaultMsg);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsgTemplate() {
		return msgTemplate;
	}

	public void setMsgTemplate(String msgTemplate) {
		this.msgTemplate = msgTemplate;
	}

	public String getDefaultMsg() {
		return defaultMsg;
	}

	public void setDefaultMsg(String defaultMsg) {
		this.defaultMsg = defaultMsg;
	}

	private int code;
	private String msgTemplate;
	private String defaultMsg;
}
