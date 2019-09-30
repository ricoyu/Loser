package com.loserico.concurrent;

/**
 * java编译器会在生成指令系列时在适当的位置会插入内存屏障指令来禁止特定类型的处理器重排序. 为了实现volatile的内存语义,
 * JMM会限制特定类型的编译器和处理器重排序, JMM会针对编译器制定volatile重排序规则表
 * 
 * <h1>volatile重排序规则表</h1>
 * <img alt="重排序规则表" src="./doc-files/volatile重排序规则表.png"/>
 * 
 * <h1>volatile内存屏障分类表</h1>
 * <img alt="内存屏障分类表" src="./doc-files/内存屏障分类表.png"/>
 * <p/>
 * "NO"表示禁止重排序. 为了实现volatile内存语义, 编译器在生成字节码时, 会在指令序列中插入内存屏障来禁止特定类型的处理器重排序.
 * 对于编译器来说, 发现一个最优布置来最小化插入屏障的总数几乎是不可能的
 * 
 * 为此, JMM采取了保守策略:
 * <ol>
 * <li>在每个volatile写操作的前面插入一个StoreStore屏障
 * <li>在每个volatile写操作的后面插入一个StoreLoad屏障
 * <li>在每个volatile读操作的后面插入一个LoadLoad屏障
 * <li>在每个volatile读操作的后面插入一个LoadStore屏障
 * </ol>
 * 
 * 需要注意的是：volatile写是在前面和后面分别插入内存屏障; 而volatile读操作是在后面插入两个内存屏障
 * <ol>
 * <li><b>StoreStore屏障</b>		禁止上面的普通写和下面的volatile写重排序；
 * <li><b>StoreLoad屏障</b>		防止上面的volatile写与下面可能有的volatile读/写重排序
 * <li><b>LoadLoad屏障</b>		禁止下面所有的普通读操作和上面的volatile读重排序
 * <li><b>LoadStore屏障</b>		禁止下面所有的普通写操作和上面的volatile读重排序
 * </ol>
 * <p>
 * Copyright: Copyright (c) 2019-03-11 20:45
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class VolatileBarrieExample {

	int a;
	volatile int v1 = 1;
	volatile int v2 = 2;

	void readAndWrite() {
		int i = v1; // 第一个volatile读
		int j = v2; // 第二个volatile读
		a = i + j; // 普通写
		v1 = i + 1; // 第一个volatile写
		v2 = j * 2; // 第二个volatile写
	}
}
