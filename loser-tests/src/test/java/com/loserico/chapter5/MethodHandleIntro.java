package com.loserico.chapter5;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MethodHandleIntro {

	private static MethodHandleIntro inst = null;

	// Constructor
	private MethodHandleIntro() {
	}

	public String toString() {
		return "My toString()";
	}

	/*
	 * This is where your actual code will go
	 */
	private void run() throws Throwable {
		MethodHandle methodHandle = getToStringMH();
		/*
		 * Now that you have a method handle, the natural thing to do with it
		 * is to execute it. The Method Handles API provides two main ways to
		 * do this: the invokeExact() and invoke() methods. The invokeExact()
		 * method requires the types of arguments to exactly match what the
		 * underlying method expects. The invoke() method will perform some
		 * transformations to try to get the types to match if they’re not
		 * quite right (for example, boxing or unboxing as required).
		 */
		String s = (String) methodHandle.invoke(this);
		System.out.println(s);
	}

	/**
	 * The next listing shows how to get a method handle that points at the
	 * toString() method on the current class. Notice that mtToString exactly
	 * matches the signature of toString()—it has a return type of String and
	 * takes no arguments. This means that the corresponding MethodType
	 * instance is MethodType.methodType(String.class).
	 * 
	 * @return
	 */
	public MethodHandle getToStringMH() {
		MethodHandle methodHandle;
		MethodType methodType = MethodType.methodType(String.class);
		/*
		 * Obtain lookup context, This is an object that can provide a method
		 * handle on any method that’s visible from the execution context where
		 * the lookup was created.
		 */
		MethodHandles.Lookup lookup = MethodHandles.lookup();

		try {
			/*
			 * Look up handle from context 
			 * To get a method handle from a lookup
			 * object, you need to provide the class that holds the method you
			 * want, the name of the method, and a MethodType corresponding to
			 * the signature you want.
			 */
			methodHandle = lookup.findVirtual(getClass(), "toString", methodType);
		} catch (NoSuchMethodException | IllegalAccessException mhx) {
			throw (AssertionError) new AssertionError().initCause(mhx);
		}

		return methodHandle;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable {
		inst = new MethodHandleIntro();
		inst.run();
	}

}
