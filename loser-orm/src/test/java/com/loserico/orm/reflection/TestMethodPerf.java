package com.loserico.orm.reflection;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.function.IntBinaryOperator;

import com.loserico.orm.convertor.AllInOneTypeConvertor;
import com.loserico.orm.convertor.DateTypeConverter;
import com.loserico.orm.convertor.IntConvertor;
import com.loserico.orm.utils.function.BigDecimal2IntFunction;
import com.peacefish.orm.commons.enums.ActiveStatus;
import com.peacefish.orm.commons.enums.Gender;

public class TestMethodPerf {
	public static void main(String... args) throws Throwable {
		//				demo1();
		//		demo2();
		testAllInOneTypeConvertor();
	}

	private static void testAllInOneTypeConvertor() {
		try {
			Method timestamp2Date = DateTypeConverter.class.getDeclaredMethod("convert", Timestamp.class);
//			Method timestamp2Date = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", Timestamp.class, Date.class);
			Method bigDecimal2Long = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", BigDecimal.class,
					Long.class);
			Method bigDecimal2Float = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", BigDecimal.class,
					Float.class);
			Method bigDecimal2Integer = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", BigDecimal.class,
					Integer.class);
			Method bigInteger2Long = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", BigInteger.class,
					Long.class);
			Method bigInteger2Integer = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", BigInteger.class,
					Integer.class);
			Method integer2Long = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", Integer.class, Long.class);
			Method integer2String = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", Integer.class, String.class);
			Method long2Integer = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", Long.class, Integer.class);
			Method sqlDate2UtilDate = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", java.sql.Date.class,
					Date.class);
			Method sqlTimestamp2UtilDate = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target",
					java.sql.Timestamp.class, Date.class);

			Method character2String = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", Character.class,
					String.class);
			Method string2Gender = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", String.class, Gender.class);
			Method string2ActiveStatus = AllInOneTypeConvertor.class.getDeclaredMethod("convert2Target", String.class,
					ActiveStatus.class);
			
//			AllInOneTypeConvertor targetObj = new AllInOneTypeConvertor();
			DateTypeConverter targetObj = new DateTypeConverter();
			Class<AllInOneTypeConvertor> targetClass = AllInOneTypeConvertor.class;

			Timestamp timestamp = new Timestamp(new Date().getTime());
			BigDecimal bigDecimal = new BigDecimal(100000);
			BigInteger bigInteger = BigInteger.valueOf(1000213);
			Integer integer = 1231283123;
			Long long1 = 123123123l;
			java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());
			java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(new Date().getTime());
			Character character = "asd".charAt(0);
			String male = "MALE";
			String activeStatus = "DISABLED";

			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodHandle handleBigDecimal2Long = lookup.findVirtual(targetClass, "convert2Target",
					MethodType.methodType(Object.class, BigDecimal.class, Long.class));
			MethodHandle handletimeStamp2Date = lookup.findVirtual(DateTypeConverter.class, "convert", MethodType.methodType(Date.class, Timestamp.class));

			long t0 = System.nanoTime();
			for (int i = 0; i < 1000000; i++) {
				timestamp2Date.invoke(targetObj, timestamp);
				/*bigDecimal2Long.invoke(targetObj, bigDecimal, null);
				bigDecimal2Float.invoke(targetObj, bigDecimal, null);
				bigDecimal2Integer.invoke(targetObj, bigDecimal, null);
				bigInteger2Long.invoke(targetObj, bigInteger, null);
				bigInteger2Integer.invoke(targetObj, bigInteger, null);
				integer2Long.invoke(targetObj, integer, null);
				integer2String.invoke(targetObj, integer, null);
				long2Integer.invoke(targetObj, long1, null);
				sqlDate2UtilDate.invoke(targetObj, sqlDate, null);
				sqlTimestamp2UtilDate.invoke(targetObj, sqlTimestamp, null);
				character2String.invoke(targetObj, character, null);
				string2Gender.invoke(targetObj, male, null);
				string2ActiveStatus.invoke(targetObj, activeStatus, null);*/
			}
			long t1 = System.nanoTime();
			System.out.printf("reflect: %.2fs", (t1 - t0) * 1e-9);

			long t2 = System.nanoTime();
			for (int i = 0; i < 1000000; i++) {
				new java.util.Date(timestamp.getTime());
				/*bigDecimal.longValue();
				bigDecimal.floatValue();
				bigDecimal.intValue();
				bigInteger.longValue();
				bigInteger.intValue();
				integer.longValue();
				integer.toString();
				long1.intValue();
				new java.util.Date(sqlDate.getTime());
				new java.util.Date(sqlTimestamp.getTime());
				character.toString();
				Gender.valueOf(male);
				ActiveStatus.valueOf(activeStatus);*/
			}
			long t3 = System.nanoTime();
			System.out.println("");
			System.out.printf("direct: %.2fs", (t3 - t2) * 1e-9);

			long t4 = System.nanoTime();
			for (int i = 0; i < 1000000; i++) {
				handletimeStamp2Date.invokeExact(timestamp);
				/*bigDecimal.longValue();
				bigDecimal.floatValue();
				bigDecimal.intValue();
				bigInteger.longValue();
				bigInteger.intValue();
				integer.longValue();
				integer.toString();
				long1.intValue();
				new java.util.Date(sqlDate.getTime());
				new java.util.Date(sqlTimestamp.getTime());
				character.toString();
				Gender.valueOf(male);
				ActiveStatus.valueOf(activeStatus);*/
			}
			long t5 = System.nanoTime();
			System.out.println("");
			System.out.printf("direct: %.2fs", (t5 - t4) * 1e-9);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void demo2() throws Throwable {
		int iterations = 100000;
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle methodHandle = lookup.findStatic(IntConvertor.class, "convert",
				MethodType.methodType(int.class, BigDecimal.class));
		BigDecimal2IntFunction bigDecimalFunction = (BigDecimal2IntFunction) LambdaMetafactory.metafactory(lookup, "apply",
				MethodType.methodType(BigDecimal2IntFunction.class), methodHandle.type(), methodHandle, methodHandle.type())
				.getTarget().invokeExact();

		Method method = IntConvertor.class.getDeclaredMethod("convert", BigDecimal.class);
		BigDecimal value = new BigDecimal(109);

		long t0 = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			bigDecimalFunction.apply(value);
		}
		long t1 = System.nanoTime();
		System.out.printf("lambda: %.2fs", (t1 - t0) * 1e-9);

		long t2 = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			IntConvertor.convert(value);
		}
		long t3 = System.nanoTime();
		System.out.printf("direct: %.2fs", (t3 - t2) * 1e-9);

		long t4 = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			method.invoke(IntConvertor.class, value);
		}
		long t5 = System.nanoTime();
		System.out.printf("reflect: %.2fs", (t5 - t4) * 1e-9);
	}

	private static void demo1() throws NoSuchMethodException, IllegalAccessException, Throwable, LambdaConversionException {
		final MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle methodHandle = lookup.findStatic(TestMethodPerf.class, "myMethod",
				MethodType.methodType(int.class, int.class, int.class));
		IntBinaryOperator intBinaryOperator = (IntBinaryOperator) LambdaMetafactory.metafactory(lookup, "applyAsInt",
				MethodType.methodType(IntBinaryOperator.class), methodHandle.type(), methodHandle, methodHandle.type())
				.getTarget().invokeExact();

		int result = intBinaryOperator.applyAsInt(1, 2);
		System.out.println(result);
	}

	private static int myMethod(int a, int b) {
		return a + b;
		//		return a < b ? a : b;
	}
}