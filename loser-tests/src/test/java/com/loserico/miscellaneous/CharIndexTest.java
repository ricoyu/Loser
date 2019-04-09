package com.loserico.miscellaneous;

import org.junit.Test;

public class CharIndexTest {
	
	@Test
	public void testCharAtIndex() {
		String line = "2DBSSSGSGXXX073370027                         ELKE PUSPITASARI HANDOYO                                                                                                                    SGD000000000000027820FEB191STDD                         T1017307D                          COLLFEB191STDD                                                                                                                                                                                                                                                                              T1017307D           0                                 ";
		String customerReference = line.substring(561, line.length());
		System.out.println(customerReference);
	}

}
