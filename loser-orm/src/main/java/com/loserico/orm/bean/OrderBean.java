package com.loserico.orm.bean;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.NullPrecedence;
import org.hibernate.criterion.Order;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("排序")
public class OrderBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="排序的数据库字段名", required=true)
	private String orderBy = "create_time";
	
	@ApiModelProperty(value="正序还是倒序", required=true)
	private ORDER_BY direction = ORDER_BY.DESC;

	public OrderBean() {

	}

	public OrderBean(String orderBy, ORDER_BY direction) {
		this.setOrderBy(orderBy);
		this.setDirection(direction);
	}

	public enum ORDER_BY {
		ASC, DESC
	}

	@JsonIgnore
	public Order getOrder() {
		Objects.requireNonNull(getDirection(), "排序方向不可以为null");
		Order order = null;

		switch (getDirection()) {
		case ASC:
			order = Order.asc(getOrderBy());
			break;
		case DESC:
			order = Order.desc(getOrderBy());
		}

		return order.nulls(NullPrecedence.LAST).ignoreCase();
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public ORDER_BY getDirection() {
		return direction;
	}

	public void setDirection(ORDER_BY direction) {
		this.direction = direction;
	}

}
