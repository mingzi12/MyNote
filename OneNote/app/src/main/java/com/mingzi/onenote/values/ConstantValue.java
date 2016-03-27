/**
 * @author LHT
 */
package com.mingzi.onenote.values;

public class ConstantValue {
	
	public static final String DB_NAME = "notesqlite.db";
	public static final String TABLE_NAME = "note_sqlite";
	public static final int DB_VERSION = 1;
	
	public static class DB_MetaData{
		public static final String NOTEID_COL = "_id";
		public static final String NOTETITLE_COL = "notetitle";
		public static final String NOTECONTENT_COL = "notecontent";
		public static final String NOTEDATE_COL = "notedate";
		
		public static final String DEFAULT_ORDER = "_id desc";
	}
	
	public static final int THEME_BLUE = 0xff188ffc;
	public static final int THEME_GREEN = 0xff8fd400;
	public static final int THEME_YELLOW = 0xffffdd1e;
	public static final int THEME_RED = 0xfffc574f;
	public static final int THEME_BROWN = 0xffa85800;
	public static final int THEME_ORANGE = 0xfff89800;
	public static final int THEME_PURPLE = 0xffc898f8;
}
