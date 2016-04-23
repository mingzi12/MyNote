/**
 * @author LHT
 */
package com.mingzi.onenote.values;

public class ConstantValue {
	/**
     *记事本数据库基本信息以及各个表的字段
     *@DB_NAME  数据库名
     *@NOTE_TABLE_NAME  文字内容表名
     *@MEDIA_TABLE_NAME  多媒体内容表名
     *@VERSION 数据库版本号
     * */
	public static final String DB_NAME = "note.db";
	public static final String NOTE_TABLE_NAME = "notes";
    public static final String MEDIA_TABLE_NAME = "medias";
	public static final int VERSION = 1;
    /**
     *记事本文字内容表基本信息
     * @NOTE_ID  各条记录的ID
     * @NOTE_TITLE  标题
     * @NOTE_CONTENT  中文
     * @NOTE_DATE 日期
     * @DEFAULT_ORDER 默认排序
     * */
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TITLE = "noteTitle";
    public static final String NOTE_CONTENT = "noteContent";
    public static final String CREATE_DATE = "createDate";
    public static final String UPDATE_DATE = "updateDate";
    public static final String DEFAULT_ORDER = "_id desc";
    /**
     * 记事本多媒体信息字段
     * @MEDIA_PATH  多媒体存放路径信息
     * @MEDIA_OWNER 多媒体所属便签
     * */

    public static final String MEDIA_PATH = "mediaPath";
    public static final String MEDIA_OWNER_ID = "noteId";
    public static final String MEDIA_DATE ="mediaDate";


    public static final int REQUEST_CODE_GET_PHOTO = 1;
    public static final int REQUEST_CODE_GET_VIDEO = 2;

    /**
     * 各种主题颜色值
     * */
	public static final int THEME_BLUE = 0xff188ffc;
	public static final int THEME_GREEN = 0xff8fd400;
	public static final int THEME_YELLOW = 0xffffdd1e;
	public static final int THEME_RED = 0xfffc574f;
	public static final int THEME_BROWN = 0xffa85800;
	public static final int THEME_ORANGE = 0xfff89800;
	public static final int THEME_PURPLE = 0xffc898f8;

}
