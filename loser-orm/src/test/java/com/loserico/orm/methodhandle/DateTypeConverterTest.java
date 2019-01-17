package com.loserico.orm.methodhandle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cache;

import com.loserico.orm.convertor.DateTypeConverter;

public class DateTypeConverterTest {
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException {
		DateTypeConverterTest test = new DateTypeConverterTest();
		test.testBeanSetterWithMethodHandle();
	}

	private void testConverterCache() throws NoSuchMethodException, SecurityException, IllegalAccessException {
		DateTypeConverter dateTypeConverter = new DateTypeConverter();
		Map<Class<?>, Map<Class<?>, MethodHandle>> cache = new HashMap<Class<?>, Map<Class<?>, MethodHandle>>();
		cache.put(Date.class, dateTypeConverter.toMethodHandleCache());
		
		Method method1 = DateTypeConverter.class.getDeclaredMethod("convert", Long.class);
		Method method2 = DateTypeConverter.class.getDeclaredMethod("convert", Timestamp.class);
		
		MethodHandle methodHandle1 = MethodHandles.lookup().unreflect(method1);
		MethodHandle methodHandle2 = MethodHandles.lookup().unreflect(method2);
		
		long time = new Date().getTime();
		Timestamp timestamp = new Timestamp(time);
		
		int iterations = 1000000;
		try {
			
			for (int j = 0; j < 100; j++) {
				long t0 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					dateTypeConverter.convert(time);
					dateTypeConverter.convert(timestamp);
				}
				long t1 = System.nanoTime();
				System.out.printf("direct: %.2fs", (t1 - t0) * 1e-9);
				
				long t4 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					method1.invoke(dateTypeConverter, time);
					method2.invoke(dateTypeConverter, timestamp);
				}
				long t5 = System.nanoTime();
				System.out.printf("\treflect: %.2fs", (t5 - t4) * 1e-9);

				long t6 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					Date date = (Date)(methodHandle1.invoke(dateTypeConverter, time));
					Date date2 = (Date)(methodHandle2.invoke(dateTypeConverter, timestamp));
				}
				long t7 = System.nanoTime();
				System.out.printf("\tmethodHandle: %.2fs", (t7 - t6) * 1e-9);
				
				long t2 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					Date date = (Date)(cache.get(Date.class).get(Long.class).invoke(dateTypeConverter, time));
					Date date2 = (Date)(cache.get(Date.class).get(Timestamp.class).invoke(dateTypeConverter, timestamp));
				}
				long t3 = System.nanoTime();
				System.out.printf("\tmethodHandle from cache: %.2fs\n", (t3 - t2) * 1e-9);
				
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private static void testBeanSetter() {
		int iterations = 1000000;
		try {
			Object now = new Date();
			DateTypeConverterTest targetObj = new DateTypeConverterTest();
			Method setDateMethod = DateTypeConverterTest.class.getDeclaredMethod("setDate", Date.class);
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodHandle setDateHandle = lookup.unreflect(setDateMethod);
			
			Map<String, MethodHandle> methodHandleCache = new HashMap<>();
			methodHandleCache.put(now.getClass().getName(), setDateHandle);
			Map<Class<?>, MethodHandle> methodHandleCache2 = new HashMap<>();
			methodHandleCache2.put(now.getClass(), setDateHandle);

			for (int j = 0; j < 100; j++) {
				long t0 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					setDateMethod.invoke(targetObj, now);
				}
				long t1 = System.nanoTime();
				System.out.printf("reflect: %.2fs", (t1 - t0) * 1e-9);

				long t2 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					setDateHandle.invokeExact(targetObj, (Date)now);
				}
				long t3 = System.nanoTime();
				System.out.printf("\tmethodHandle: %.2fs", (t3 - t2) * 1e-9);
				
				long t4 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					methodHandleCache.get(now.getClass().getName()).invoke(targetObj, now);
				}
				long t5 = System.nanoTime();
				System.out.printf("\tmethodHandle: %.2fs", (t5 - t4) * 1e-9);
				
				long t6 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					methodHandleCache2.get(now.getClass()).invoke(targetObj, now);
				}
				long t7 = System.nanoTime();
				System.out.printf("\tmethodHandle: %.2fs", (t7 - t6) * 1e-9);
				
				long t8 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					lookup.unreflect(setDateMethod).invoke(targetObj, now);
				}
				long t9 = System.nanoTime();
				System.out.printf("\tmethodHandle: %.2fs\n", (t9 - t8) * 1e-9);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private static void testBeanSetterWithMethodHandle() {
		long time = new Date().getTime();
		Timestamp timestamp = new Timestamp(time);
		int iterations = 1000000;
		try {
			Object now = new Date();
			DateTypeConverterTest targetObj = new DateTypeConverterTest();
			Method setDateMethod = DateTypeConverterTest.class.getDeclaredMethod("setDate", Date.class);
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodHandle setDateHandle = lookup.unreflect(setDateMethod);
			
			Map<String, MethodHandle> methodHandleCache = new HashMap<>();
			methodHandleCache.put(now.getClass().getName(), setDateHandle);
			Map<Class<?>, MethodHandle> methodHandleCache2 = new HashMap<>();
			methodHandleCache2.put(now.getClass(), setDateHandle);

			DateTypeConverter dateTypeConverter = new DateTypeConverter();
			Map<Class<?>, Map<Class<?>, MethodHandle>> cache = new HashMap<Class<?>, Map<Class<?>, MethodHandle>>();
			cache.put(Date.class, dateTypeConverter.toMethodHandleCache());

			Method method1 = DateTypeConverter.class.getDeclaredMethod("convert", Long.class);
			Method method2 = DateTypeConverter.class.getDeclaredMethod("convert", Timestamp.class);

			MethodHandle methodHandle1 = MethodHandles.lookup().unreflect(method1);
			MethodHandle methodHandle2 = MethodHandles.lookup().unreflect(method2);
			
			for (int j = 0; j < 100; j++) {
				long t6 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					targetObj.setDate((Date)now);
				}
				long t7 = System.nanoTime();
				System.out.printf("direct: %.2fs", (t7 - t6) * 1e-9);
				
				long t0 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					setDateMethod.invoke(targetObj, method2.invoke(dateTypeConverter, timestamp));
				}
				long t1 = System.nanoTime();
				System.out.printf("\treflect: %.2fs", (t1 - t0) * 1e-9);
				
				long t2 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					setDateHandle.invokeExact(targetObj, (Date)methodHandle2.invoke(dateTypeConverter, timestamp));
//					setDateHandle.invokeExact(targetObj, (Date)cache.get(Date.class).get(Long.class).invoke(dateTypeConverter, time));
				}
				long t3 = System.nanoTime();
				System.out.printf("\tmethodHandle exact: %.2fs", (t3 - t2) * 1e-9);
				
				long t4 = System.nanoTime();
				for (int i = 0; i < iterations; i++) {
					setDateHandle.invoke(targetObj, cache.get(Date.class).get(Timestamp.class).invoke(dateTypeConverter, timestamp));
//					setDateHandle.invoke(targetObj, cache.get(Date.class).get(Long.class).invoke(dateTypeConverter, time));
				}
				long t5 = System.nanoTime();
				System.out.printf("\tmethodHandle from cache: %.2fs\n", (t5 - t4) * 1e-9);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void setDate(Date date) {
		Date date2 = date;
	}
}
