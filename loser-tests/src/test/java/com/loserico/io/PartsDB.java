package com.loserico.io;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * RandomAccessFile is useful for creating a flat file database, a single file organized into
 * records and fields. A record stores a single entry (such as a part in a parts database) and a
 * field stores a single attribute of the entry (such as a part number).
 * 
 * A flat file database typically organizes its content into a sequence of fixed-length records.
 * Each record is further organized into one or more fixed-length fields.
 * 
 * each field has a name (partnum, desc, qty, and ucost). Also, each record is assigned a number
 * starting at 0. This example consists of five records, of which only three are shown for
 * brevity.
 * 
 * @author Rico Yu
 * @since 2016-11-28 10:39
 * @version 1.0
 *
 */
public class PartsDB {
	/*
	 * PartsDB first declares constants that identify the lengths of the string and 32-bit
	 * integer fields. It then declares a constant that calculates the record length in terms of
	 * bytes. The calculation takes into account the fact that a character occupies two bytes in
	 * the file.
	 */
	public final static int PNUMLEN = 20;
	public final static int DESCLEN = 30;
	public final static int QUANLEN = 4;
	public final static int COSTLEN = 4;
	private final static int RECLEN = 2 * PNUMLEN + 2 * DESCLEN + QUANLEN + COSTLEN;
	private RandomAccessFile raf;

	/**
	 * PartsDB next declares append(), close(), numRecs(), select(), and update(). These methods
	 * append a record to the file, close the file, return the number of records in the file,
	 * select and return a specific record, and update a specific record:
	 * 
	 * @param path
	 * @throws IOException
	 */
	public PartsDB(String path) throws IOException {
		raf = new RandomAccessFile(path, "rw");
	}

	/**
	 * The append() method first calls length() and seek(). Doing so ensures that the file
	 * pointer is positioned at the end of the file before calling the private write() method to
	 * write a record containing this method’s arguments.
	 * 
	 * @param partnum
	 * @param partdesc
	 * @param qty
	 * @param ucost
	 * @throws IOException
	 */
	public void append(String partnum, String partdesc, int qty, int ucost) throws IOException {
		raf.seek(raf.length());
		write(partnum, partdesc, qty, ucost);
	}

	/**
	 * RandomAccessFile’s close() method can throw IOException. Because this is a rare
	 * occurrence, I chose to handle this exception in PartDB’s close() method, which keeps that
	 * method’s signature simple. However, I print a message when IOException occurs.
	 */
	public void close() {
		try {
			raf.close();
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	}

	/**
	 * The numRecs() method returns the number of records in the file. These records are
	 * numbered starting with 0 and ending with numRecs() - 1. Each of the select() and update()
	 * methods verifies that its recno argument lies within this range.
	 * 
	 * @return
	 * @throws IOException
	 */
	public int numRecs() throws IOException {
		return (int) raf.length() / RECLEN;
	}

	/**
	 * The select() method calls the private read() method to return the record identified by
	 * recno as an instance of the nested Part class. Part’s constructor initializes a Part
	 * object to a record’s field values, and its getter methods return these values.
	 * 
	 * @param recno
	 * @return
	 * @throws IOException
	 */
	public Part select(int recno) throws IOException {
		if (recno < 0 || recno >= numRecs()) {
			throw new IllegalArgumentException(recno + " out of range");
		}
		raf.seek(recno * RECLEN);
		return read();
	}

	/**
	 * The update() method is equally simple. As with select(), it first positions the file
	 * pointer to the start of the record identified by recno. As with append(), it calls
	 * write() to write out its arguments but replaces a record instead of adding one.
	 * 
	 * @param recno
	 * @param partnum
	 * @param partdesc
	 * @param qty
	 * @param ucost
	 * @throws IOException
	 */
	public void update(int recno, String partnum, String partdesc, int qty, int ucost) throws IOException {
		if (recno < 0 || recno >= numRecs()) {
			throw new IllegalArgumentException(recno + " out of range");
		}
		raf.seek(recno * RECLEN);
		write(partnum, partdesc, qty, ucost);
	}

	/**
	 * Records are read via the private read() method. read() removes the padding before saving
	 * a String-based field value in the Part object.
	 * 
	 * @return
	 * @throws IOException
	 */
	private Part read() throws IOException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < PNUMLEN; i++) {
			sb.append(raf.readChar());
		}
		String partnum = sb.toString().trim();
		sb.setLength(0);
		for (int i = 0; i < DESCLEN; i++) {
			sb.append(raf.readChar());
		}
		String partdesc = sb.toString().trim();
		int qty = raf.readInt();
		int ucost = raf.readInt();
		return new Part(partnum, partdesc, qty, ucost);
	}

	/**
	 * Records are written with the private write() method. Because fields must have exact
	 * sizes, write() pads String-based values that are shorter than a field size with spaces on
	 * the right and truncates these values to the field size when needed.
	 * 
	 * @param partnum
	 * @param partdesc
	 * @param qty
	 * @param ucost
	 * @throws IOException
	 */
	private void write(String partnum, String partdesc, int qty, int ucost) throws IOException {
		StringBuffer sb = new StringBuffer(partnum);
		if (sb.length() > PNUMLEN) {
			sb.setLength(PNUMLEN);
		} else if (sb.length() < PNUMLEN) {
			int len = PNUMLEN - sb.length();
			for (int i = 0; i < len; i++) {
				sb.append(" ");
			}
		}
		raf.writeChars(sb.toString());
		sb = new StringBuffer(partdesc);
		if (sb.length() > DESCLEN) {
			sb.setLength(DESCLEN);
		} else if (sb.length() < DESCLEN) {
			int len = DESCLEN - sb.length();
			for (int i = 0; i < len; i++) {
				sb.append(" ");
			}
		}
		raf.writeChars(sb.toString());
		raf.writeInt(qty);
		raf.writeInt(ucost);
	}

	public static class Part {
		private String partnum;
		private String desc;
		private int qty;
		private int ucost;

		public Part(String partnum, String desc, int qty, int ucost) {
			this.partnum = partnum;
			this.desc = desc;
			this.qty = qty;
			this.ucost = ucost;
		}

		String getDesc() {
			return desc;
		}

		String getPartnum() {
			return partnum;
		}

		int getQty() {
			return qty;
		}

		int getUnitCost() {
			return ucost;
		}
	}
}