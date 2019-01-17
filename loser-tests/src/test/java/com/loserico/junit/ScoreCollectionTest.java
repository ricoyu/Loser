package com.loserico.junit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ScoreCollectionTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	/*
	 * We want to start with a scenario—a test case—that provides an example of
	 * expected behavior of the target code. To test a ScoreCollection object, we
	 * can add the numbers 5 and 7 to it and expect that the arithmeticMean method
	 * will return 6 (because (5 + 7) / 2 is equal to 6).
	 */
	@Test
	public void testAnswersArithmeticMeanOfTwoNumbers() {
		// Arrange
		ScoreCollection collection = new ScoreCollection();
		collection.add(() -> 5);
		collection.add(() -> 7);

		// Act
		int actualResult = collection.arithmeticMean();
		/*
		 * we assert that we get the expected result. We use the assertThat()
		 * method, which takes two arguments: the actual result and a matcher. The
		 * equalTo matcher compares the actual result to the expected value of 6.
		 */
		assertThat(actualResult, equalTo(62));
	}
}