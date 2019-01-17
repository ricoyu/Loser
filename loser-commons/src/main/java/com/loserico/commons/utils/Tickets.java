package com.loserico.commons.utils;
public enum Tickets {

	INVOICE("INV", "应收单", "documents.invoice"),
	RECEIPT("RCP", "收款单", "documents.receipt"),
	ADVANCE_PAYMENT("ADV", "预收单", "documents.advance.payment"),
	DEPOSIT("DEP", "预付单", "documents.deposit"),
	CREDIT_NOTE("CRN", "贷方通知单", "documents.credit.note"),
	OVERPAYMENT("OVP", "超额支付", "documents.overpayment"),
	REFUND("REF", "退款单", "documents.refund"),
	ADJUSTMENT_NOTE("AJN", "调整单据", "documents.adjustment.note"),
	//这不是一张单据
	GL("GL", "总账明细", "account.gl");

	private String code;
	private String template;
	private String desc;

	private Tickets(String code, String desc, String template) {
		this.code = code;
		this.desc = desc;
		this.template = template;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}