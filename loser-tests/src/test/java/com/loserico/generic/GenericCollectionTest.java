package com.loserico.generic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GenericCollectionTest {

	@Test
	public void test1() {
		List a1 = new ArrayList();
		a1.add(new Object());
		a1.add(new Integer(111));
		a1.add(new String("hello a1"));

		List<Object> a2 = a1;
		a2.add(new Object());
		a2.add(new Integer(222));
		a2.add(new String("hello a2"));

		List<Integer> a3 = a1;
		a3.add(new Integer(333));
		// 下面两行编译出错, 不允许增加非Integer类型进入集合
		// a3.add(new Object());
		// a3.add(new String("hello a3"));

		// List<?> 称为通配符集合, 它可以接收任何类型的集合引用赋值, 不能添加任何元素, 但可以remove和clear
		List<?> a4 = a1;
		// 允许删除和清除元素
		a4.remove(0);
		a4.clear();
		// 编译出错, 不允许增加任何元素
		// a4.add(new Object( ));
	}
}
