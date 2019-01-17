package com.loserico.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.elasticsearch.common.metrics.EWMA;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.io.utils.IOUtils;

public class ReadPdf {

	private static final Logger logger = LoggerFactory.getLogger(ReadPdf.class);

	@Test
	public void testReadPDF() throws InvalidPasswordException, IOException {
		try (PDDocument document = PDDocument.load(IOUtils.readClasspathFileAsFile("OCBC GIRO Report.PDF"))) {
			document.getClass();

			if (!document.isEncrypted()) {
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);

				PDFTextStripper tStripper = new PDFTextStripper();
				String pdfFileInText = tStripper.getText(document);

				//				System.out.println(pdfFileInText);

				String lines[] = pdfFileInText.split("\\r?\\n");
				//				String lines[] = pdfFileInText.split(System.lineSeparator());
				for (String line : lines) {
					System.out.println(line);
				}
			}
		}
	}

	/**
	 * 以 Item Status Remarks 开始
	 * 中间以 "数字+空格" 开头的是一行的开始,如:
	 * 7 DBS BANK LTD 365553297 ANNABELLE SEAH 500.00 SGD COLL - Collection 
	 * 两实际行之间还会有很多换行,用空格join起来iu可以
	 * 8 DBS BANK LTD 183023128 CAYDEN TOH XU 
	 * 以 LOH FOO LOONGPrinted By结尾,即XXXPrinted By结尾, 最后一页在XXXPrinted By前一行还有** End of Report **
	 * @throws IOException 
	 * @throws InvalidPasswordException 
	 * @on
	 */
	@Test
	public void testFormatRead() throws InvalidPasswordException, IOException {
		try (PDDocument document = PDDocument.load(IOUtils.readClasspathFileAsFile("OCBC GIRO Report.PDF"))) {

			if (document.isEncrypted()) {
				logger.warn("PDF已加密, 无法读取!");
			}
			
			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			stripper.setSortByPosition(true);

			PDFTextStripper tStripper = new PDFTextStripper();
			String pdfFileInText = tStripper.getText(document);

			String lines[] = pdfFileInText.split("\\r?\\n");
			List<Integer> dataLineNos = new ArrayList<>(); //把所有数据行的行号记录下来
			for (int i = 0; i<lines.length; i++) {
				String line = lines[i];
				if (itemStart(line)) {
					
				}
			}
		}
	}
	
	private static class DataLineGenerator {
		private int start; //数据行起始行号, 包含
		private int end; //数据行结束行号, 包含
		
		public void setStart(int start) {
			this.start = start;
		}
		
		/**
		 * 每次start, end 填完就把[start-end] 之间的数字 add 进 dataLineNos
		 * 后续只要循环读取 dataLineNos 就可以得到所有数据行的行号了
		 * @param dataLineNos
		 * @param end
		 * @on
		 */
		public void generateDataLineIndex(List<Integer> dataLineNos, int end) {
			
		}
	}
	
	/**
	 * 每一页都以 Item Status Remarks 开头,下面的行就是具体的item列表了
	 * @param line
	 * @return boolean
	 */
	private boolean itemStart(String line) {
		return line.indexOf("Item Status Remarks") != -1;
	}
	
	/**
	 * 以 LOH FOO LOONGPrinted By结尾,即XXXPrinted By结尾的是列表的结束
	 * @param line
	 * @return boolean
	 */
	private boolean itemEnd(String line) {
		return line.indexOf("Printed By") != -1;
	}
	
	/**
	 * 出现** End of Report **这行表示PDF文档结束了
	 * @param line
	 * @return
	 */
	private boolean docEnd(String line) {
		return line.indexOf("End of Report") != -1;
	}
}