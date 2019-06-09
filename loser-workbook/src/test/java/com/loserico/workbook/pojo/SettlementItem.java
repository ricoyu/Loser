package com.loserico.workbook.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.loserico.workbook.annotation.Col;
import com.loserico.workbook.enums.Ticket;

import lombok.Data;

@Data
public class SettlementItem {

	@Col(name = "结算单号")
	private String settlementId; // JD 京东结算单号

	@Col(name = "供应商名称")
	private String supplier; // 供应商名称

	@Col(name = "供应商简码")
	private String supplierCode; // 供应商简码

	@Col(name = "合同主体")
	private String contractSubject;// 合同主体

	@Col(name = "应付帐单据类型")
	private Ticket ticket; // 应付帐单据类型

	@Col(name = "单据编号")
	private String ticketId; // 单据编号

	@Col(name = "采购单号")
	private String businessId; // 采购单号

	@Col(name = "业务发生时间")
	private LocalDateTime businessTime; // 业务发生时间

	@Col(name = "台账类型")
	private String deskType; // 台账类型

	@Col(name = "采购员")
	private String purchaser; // 采购员

	@Col(name = "备注")
	private String remark; // 备注

	@Col(name = "SKU编码")
	private Long skuCode; // SKU编码

	@Col(name = "SKU名称")
	private String skuName; // SKU名称

	@Col(name = "单价")
	private BigDecimal unitPrice; // 单价

	@Col(name = "数量")
	private Long qty; // 数量

	@Col(name = "金额")
	private BigDecimal amount; // 金额

	@Col(name = "订单号")
	private String orderId; // 订单号

}
