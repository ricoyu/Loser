package com.loserico.junit.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * You might wonder what the difference is between JUnit 4's @before and
 * 
 * @after and the TestExecutionListener methods. The answer is you can access
 *        TestContext in the TestExecutionListener methods but not in JUnit
 *        annotated methods, and TestExecutionListener logic can be shared with many
 *        tests but JUnit annotations are test class specific. For example, our
 *        SysOutTestExecutionListener logic can be shared with any test class; but
 *        if we annotate a test method with a JUnit 4 annotation, then that method
 *        cannot be shared with all the test classes unless they extend the class.
 * @author Loser
 * @since Aug 8, 2016
 * @version
 *
 */
public class ExecutionListenerSysOutTest implements TestExecutionListener {

	/*
	 * The beforeTestClass method is invoked first, and it is invoked only once for
	 * the test class;
	 */
	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		System.out.println("In beforeTestClass for class = " + testContext.getTestClass());
	}

	/*
	 * The prepareTestInstance is invoked before any test method execution. We can
	 * get the test instance and prepare beans or initialize testspecific data from
	 * this method.
	 */
	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {
		System.out.println("In prepareTestInstance for=" + testContext.getTestInstance());
	}

	/*
	 * The beforeTestMethod is executed after prepareTestInstance but before any
	 * test method execution, and then a test is executed.
	 */
	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		System.out.println("In beforeTestMethod for = " + testContext.getTestMethod().getName());
	}

	/*
	 * The afterTestMethod is executed after any test method execution.
	 */
	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		System.out.println("In afterTestMethod for = " + testContext.getTestMethod().getName());
	}

	/*
	 * The afterTestClass method acts like the destructors in C++, and is invoked
	 * only once per class at the end of the last test method's afterTestMethod
	 * call.
	 */
	@SuppressWarnings("unused")
	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		ApplicationContext ctx = testContext.getApplicationContext();
		System.out.println("In afterTestClass for class = " + testContext.getTestClass());
	}
}