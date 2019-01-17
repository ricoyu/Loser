package com.loserico.generic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

/**
 * https://mp.weixin.qq.com/s/mkfyZtBVeAxAes72TaiN4Q
 * <p>
 * Copyright: Copyright (c) 2018-07-31 15:37
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class GenericWildcardTest {

	class Base {
	}

	class Sub extends Base {
	}

	@Test
	public void testWildcard() {
		Sub sub = new Sub();
		Base base = sub;

		List<Sub> subs = new ArrayList<>();
		//List<Base> bases = subs; // 编译不通过
	}

	/**
	 * 无限定通配符 <?>
	 * 
	 * 方法内的参数是被无限定通配符修饰的 Collection 对象，它隐略地表达了一个意图或者可以说是限定，那就是
	 * testWidlCards() 这个方法内部无需关注 Collection 中的真实类型，因为它是未知的。所以，你只能调用
	 * Collection 中与类型无关的方法。
	 * 
	 * 我们可以看到，当 <?> 存在时，Collection 对象丧失了 add() 方法的功能，编译器不通过。
	 * 
	 * 有人说，<?> 提供了只读的功能，也就是它删减了增加具体类型元素的能力，只保留与具体类型无关的功能。
	 * 它不管装载在这个容器内的元素是什么类型，它只关心元素的数量、容器是否为空？我想这种需求还是很常见的吧。
	 * 
	 * 有同学可能会想，<?> 既然作用这么渺小，那么为什么还要引用它呢？
	 * 
	 * 个人认为，提高了代码的可读性，程序员看到这段代码时，就能够迅速对此建立极简洁的印象，能够快速推断源码作者的意图。
	 * 
	 * @param collection
	 */
	public static void asteriskWildCard(Collection<?> collection) {
		//collection.add("123"); // 编译不通过
		//collection.add(123); // 编译不通过
		//collection.add(new Object()); // 编译不通过

		collection.iterator().next();
		collection.size();

		List<?> wildList = new ArrayList<>();
		//wildList.add(123); // 编译不通过
	}

	/**
	 * <? extends T>
	 * 
	 * <?> 代表着类型未知，但是我们的确需要对于类型的描述再精确一点，我们希望在一个范围内确定类别，比如类型 A 及 类型 A 的子类都可以。
	 * 
	 * para 这个 Collection 接受 Base 及 Base 的子类的类型。
	 */
	public static void extendsWildCard(Collection<? extends Base> para) {
		/*
		 * 但是，它仍然丧失了写操作的能力。也就是说
		 */
		//para.add(new Sub()); // 编译不通过
		//para.add(new Base()); // 编译不通过
	}

	/**
	 * <? super T>
	 * 
	 * 这个和 <? extends T> 相对应，代表 T 及 T 的超类。
	 * 
	 * <? super T> 神奇的地方在于，它拥有一定程度的写操作的能力。
	 */
	public static void superWildCard(Collection<? super Sub> para) {
		//para.add(new Sub()); //编译通过
		//para.add(new Base()); //编译不通过
	}

	/**
	 * 通配符与类型参数的区别
	 * 
	 * public void testWildCards(Collection<?> collection){} 可以被 public <T>
	 * void test(Collection<T> collection){}
	 */
	public static <T> void wildCardDifference(Collection<T> collection) {
		/*
		 * 值得注意的是，如果用泛型方法来取代通配符，那么上面代码中 collection 是能够进行写操作的。只不过要进行强制转换。
		 */
		collection.add((T) new Integer(12));
		collection.add((T) "123");
	}

	/**
	 * 需要特别注意的是，类型参数适用于参数之间的类别依赖关系，举例说明。
	 */
	public class Test2<T, E extends T> {
		T value1;
		E value2;
	}

	public <D, S extends D> void test(D d, S s) {

	}

	/**
	 * E 类型是 T 类型的子类，显然这种情况类型参数更适合。 有一种情况是，通配符和类型参数一起使用。
	 */
	public <T> void test(T t, Collection<? extends T> collection) {

	}

	/**
	 * 类型擦除
	 * 
	 * 泛型是 Java 1.5 版本才引进的概念，在这之前是没有泛型的概念的，但显然，泛型代码能够很好地和之前版本的代码很好地兼容。
	 * 这是因为，泛型信息只存在于代码编译阶段，在进入 JVM 之前，与泛型相关的信息会被擦除掉，专业术语叫做类型擦除。
	 * 通俗地讲，泛型类和普通类在 java 虚拟机内是没有什么特别的地方。回顾文章开始时的那段代码 
	 * 
	 * List<String> l1 = new ArrayList<String>(); 
	 * List<Integer> l2 = new ArrayList<Integer>();
	 * System.out.println(l1.getClass() == l2.getClass());
	 * 
	 * 打印的结果为 true 是因为 List<String> 和 List<Integer> 在 jvm 中的 Class 都是 List.class。
	 * 泛型信息被擦除了。
	 * 
	 * 可能同学会问，那么类型 String 和 Integer 怎么办？
	 * 答案是泛型转译。
	 * @on
	 */
	public class Erasure<T> {
		T object;

		public Erasure(T object) {
			this.object = object;
		}

		/*
		 * add() 这个方法对应的 Method 的签名应该是 Object.class。
		 */
		public void add(T object) {

		}
	}

	/**
	 * Erasure 是一个泛型类，我们查看它在运行时的状态信息可以通过反射。
	 */
	@Test
	public void testErasureClass() {
		Erasure<String> erasure = new Erasure<String>("hello");
		Class eclz = erasure.getClass();
		/*
		 * Class 的类型仍然是 Erasure 并不是 Erasure<T> 这种形式，那我们再看看泛型类中 T 的类型在 jvm
		 * 中是什么具体类型。
		 */
		System.out.println("erasure class is:" + eclz.getName());

		Field[] fields = eclz.getDeclaredFields();
		for (Field field : fields) {
			//Field name object type:java.lang.Object
			System.out.println("Field name " + field.getName() + " type:" + field.getType().getName());
		}

	}

	/**
	 * 那我们可不可以说，泛型类被类型擦除后，相应的类型就被替换成 Object 类型呢？
	 * 
	 * 这种说法，不完全正确。
	 * 
	 * 我们更改一下代码。
	 */
	public class Erasure2<T extends String> {
		//  public class Erasure <T>{
		T object;

		public Erasure2(T object) {
			this.object = object;
		}

	}

	@Test
	public void testErasure2Class() {
		Erasure2<String> erasure = new Erasure2<String>("hello");
		Class eclz = erasure.getClass();
		/*
		 * Class 的类型仍然是 Erasure 并不是 Erasure<T> 这种形式，那我们再看看泛型类中 T 的类型在 jvm
		 * 中是什么具体类型。
		 */
		System.out.println("erasure class is:" + eclz.getName());

		Field[] fields = eclz.getDeclaredFields();
		for (Field field : fields) {
			//Field name object type:java.lang.String
			System.out.println("Field name " + field.getName() + " type:" + field.getType().getName());
		}

	}

	/**
	 * 我们现在可以下结论了，在泛型类被类型擦除的时候，之前泛型类中的类型参数部分如果没有指定上限，如 <T> 则会被转译成普通的 Object
	 * 类型，如果指定了上限如 <T extends String> 则类型参数就被替换成类型上限。
	 * 
	 * add() 这个方法对应的 Method 的签名应该是 Object.class。
	 * 
	 * 也就是说，如果你要在反射中找到 add 对应的 Method，你应该调用
	 * getDeclaredMethod("add",Object.class)
	 * 否则程序会报错，提示没有这么一个方法，原因就是类型擦除的时候，T 被替换成 Object 类型了。
	 */
	@Test
	public void testMethod() {
		Erasure<String> erasure = new Erasure<String>("hello");
		Class eclz = erasure.getClass();
		System.out.println("erasure class is:" + eclz.getName());

		Method[] methods = eclz.getDeclaredMethods();
		for (Method m : methods) {
			System.out.println(" method:" + m.toString());
		}
	}

	/**
	 * 类型擦除带来的局限性
	 * 
	 * 类型擦除，是泛型能够与之前的 java 版本代码兼容共存的原因。但也因为类型擦除，它会抹掉很多继承相关的特性，这是它带来的局限性。
	 * 
	 * 理解类型擦除有利于我们绕过开发当中可能遇到的雷区，同样理解类型擦除也能让我们绕过泛型本身的一些限制。比如
	 * 
	 * 正常情况下，因为泛型的限制，编译器不让最后一行代码编译通过，因为类似不匹配，但是，基于对类型擦除的了解，利用反射，我们可以绕过这个限制。
	 */
	@Test
	public void testAdd() {
		List<Integer> list = new ArrayList<>();
		list.add(123);
		//list.add("test");

		/*
		 * public interface List<E> extends Collection<E>{
		 *     boolean add(E e);
		 * }
		 * 
		 * 上面是 List 和其中的 add() 方法的源码定义。
		 * 
		 * 因为 E 代表任意的类型，所以类型擦除时，add 方法其实等同于: boolean add(Object obj);
		 * 
		 * 那么，利用反射，我们绕过编译器去调用 add 方法。
		 * @on
		 */
		try {
			Method method = list.getClass().getDeclaredMethod("add", Object.class);

			method.invoke(list, "test");
			method.invoke(list, 42.9f);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

		/*
		 * 打印结果是：
		 * 23 
		 * test 
		 * 42.9
		 * 
		 * 可以看到，利用类型擦除的原理，用反射的手段就绕过了正常开发中编译器不允许的操作限制。
		 * @on
		 */
		for (Object o : list) {
			System.out.println(o);
		}
	}

	/**
	 * 泛型中值得注意的地方
	 * 
	 * 泛型类或者泛型方法中，不接受 8 种基本数据类型。
	 * 
	 * 所以，你没有办法进行这样的编码 List<int> li = new ArrayList<>(); List<boolean> li =
	 * new ArrayList<>();
	 * 
	 * 需要使用它们对应的包装类。 List<Integer> li = new ArrayList<>(); List<Boolean> li1
	 * = new ArrayList<>();
	 */

	/**
	 * 对泛型方法的困惑
	 * 
	 * 有的同学可能对于连续的两个 T 感到困惑，其实 <T> 是为了说明类型参数，是声明,而后面的不带尖括号的 T 是方法的返回值类型。
	 * 
	 * 你可以相像一下，如果 test() 这样被调用
	 * 	test("123");
	 * 那么实际上相当于
	 * 	public String test(String t);
	 * @on
	 */
	public <T> T test(T t) {
		return null;
	}

	/**
	 * Java 不能创建具体类型的泛型数组
	 * 
	 * 
	 */
	@Test
	public void testName() {
		//这句话可能难以理解，代码说明。
		//List<Integer>[] li2 = new ArrayList<Integer>[];
		//List<Boolean> li3 = new ArrayList<Boolean>[];

		/*
		 * 这两行代码是无法在编译器中编译通过的。原因还是类型擦除带来的影响。
		 * 
		 * List<Integer> 和 List<Boolean> 在 jvm 中等同于List<Object>
		 * ，所有的类型信息都被擦除，程序也无法分辨一个数组中的元素类型具体是 List<Integer>类型还是 List<Boolean>
		 * 类型。
		 * 
		 * 但是，借助于无限定通配符却可以，前面讲过 ？ 代表未知类型，所以它涉及的操作都基本上与类型无关，因此 jvm
		 * 不需要针对它对类型作判断，因此它能编译通过，但是，只提供了数组中的元素因为通配符原因，它只能读，不能写。比如，上面的 v
		 * 这个局部变量，它只能进行 get() 操作，不能进行 add() 操作，这个在前面通配符的内容小节中已经讲过。
		 */
		List<?>[] li3 = new ArrayList<?>[10];
		li3[1] = new ArrayList<String>();
		List<?> v = li3[1];
	}

	/**
	 * 泛型，并不神奇
	 * 
	 * 我们可以看到，泛型其实并没有什么神奇的地方，泛型代码能做的非泛型代码也能做。
	 * 
	 * 而类型擦除，是泛型能够与之前的 java 版本代码兼容共存的原因。
	 * 
	 * 可是也正因为类型擦除导致了一些隐患与局限。
	 * 
	 * 但，我还是要建议大家使用泛型，如官方文档所说的，如果可以使用泛型的地方，尽量使用泛型。
	 * 
	 * 毕竟它抽离了数据类型与代码逻辑，本意是提高程序代码的简洁性和可读性，并提供可能的编译时类型转换安全检测功能。
	 * 
	 * 类型擦除不是泛型的全部，但是它却能很好地检测我们对于泛型这个概念的理解程度。
	 * 
	 * 我在文章开头将泛型比作是一个守门人，原因就是他本意是好的，守护我们的代码安全，然后在门牌上写着出入的各项规定，及“xxx禁止出入”的提醒。
	 * 但是同我们日常所遇到的那些门卫一般，他们古怪偏执，死板守旧，我们可以利用反射基于类型擦除的认识，来绕过泛型中某些限制，
	 * 现实生活中，也总会有调皮捣蛋者能够基于对门卫们生活作息的规律，选择性地绕开他们的监视，另辟蹊径溜进或者溜出大门，然后扬长而去，
	 * 剩下守卫者一个孤独的身影。
	 * 
	 * 所以，我说泛型，并不神秘，也不神奇。
	 */
}
