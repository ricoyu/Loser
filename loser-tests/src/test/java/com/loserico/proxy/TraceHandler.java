package com.loserico.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 定义我们自己的InvocationHandler 的时候, 一般会在构造函数中传入一个被代理的对象(委托类),
 * 在invoke方法内部调用method.invoke时, 传入的第一个参数即这个target对象
 * 
 * @author Rico Yu
 * @since 2016-10-02 14:55
 * @version 1.0
 *
 */
public class TraceHandler implements InvocationHandler {

	private Object target;

	public TraceHandler(Object target) {
		this.target = target;
	}

	/**
	 * 参数proxy即通过Proxy.newProxyInstance生成的代理类的instance
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("Before calling target method!");

		/*
		 * 第一个参数绝对不能是proxy, 不然将陷入死循环, 以下一段话来自InvocationHandler类, 从中可以看出问题的根源: Each
		 * proxy instance has an associated invocation handler. When a method is
		 * invoked on a proxy instance, the method invocation is encoded and
		 * dispatched to the invoke method of its invocation handler. 也就是说这边如果这样调用:
		 * method.invoke(proxy, args), 即调用代理类的这个方法, 因此委派给这个代理类的
		 * InvocationHandler的invoke方法, 陷入死循环...
		 */
		return method.invoke(this.target, args);
	}

}
