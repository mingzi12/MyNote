/**
 * @author LHT
 */
package com.mingzi.onenote.values;

public class ConstantValue {
	
	public static final String DB_NAME = "note.db";
	public static final String NOTE_TABLE_NAME = "notes";
    public static final String MEDIA_TABLE_NAME = "medias";
	public static final int VERSION = 1;
	
	public static class NoteMetaData {
		public static final String NOTE_ID = "_id";
		public static final String NOTE_TITLE = "noteTitle";
		public static final String NOTE_CONTENT = "noteContent";
		public static final String NOTE_DATE = "noteDate";
		
		public static final String DEFAULT_ORDER = "_id desc";
	}

    public static class MediaMetaData {
        public static String MEDIA_PATH = "mediaPath";
        public static String MEDIA_OWNER = "noteId";
    }

	public static final int THEME_BLUE = 0xff188ffc;
	public static final int THEME_GREEN = 0xff8fd400;
	public static final int THEME_YELLOW = 0xffffdd1e;
	public static final int THEME_RED = 0xfffc574f;
	public static final int THEME_BROWN = 0xffa85800;
	public static final int THEME_ORANGE = 0xfff89800;
	public static final int THEME_PURPLE = 0xffc898f8;
}
