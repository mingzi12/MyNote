/**
 * @author LHT
 */
package com.mingzi.onenote.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.mingzi.onenote.util.ConvertStringAndDate;

import java.util.Date;

public class Note implements Parcelable {
	
	private int noteId;
	private String noteTitle;
	private String noteContent;
	private Date noteDate;
	
	public Note() {
		
	}
	
	/**
	 * 存储日志
	 * @param noteTitle
	 * @param noteContent
	 * @param noteDate
	 */
	public Note(String noteTitle, String noteContent,
				Date noteDate) {
		this.noteTitle = noteTitle;
		this.noteContent = noteContent;
		this.noteDate = noteDate;
	}
	
	/**
	 * 读取日志
	 * @param noteId
	 * @param noteTitle
	 * @param noteContent
	 * @param noteDate
	 */
	public Note(int noteId, String noteTitle, String noteContent,
				Date noteDate) {
		this.noteId = noteId;
		this.noteTitle = noteTitle;
		this.noteContent = noteContent;
		this.noteDate = noteDate;
	}

	/**
	 * @return the noteId
	 */
	public int getNoteId() {
		return noteId;
	}

	/**
	 * @param noteId the noteId to set
	 */
	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}

	/**
	 * @return the noteTitle
	 */
	public String getNoteTitle() {
		return noteTitle;
	}

	/**
	 * @param noteTitle the noteTitle to set
	 */
	public void setNoteTitle(String noteTitle) {
		this.noteTitle = noteTitle;
	}

	/**
	 * @return the noteContent
	 */
	public String getNoteContent() {
		return noteContent;
	}

	/**
	 * @param noteContent the noteContent to set
	 */
	public void setNoteContent(String noteContent) {
		this.noteContent = noteContent;
	}

	/**
	 * @return the noteDate
	 */
	public Date getNoteDate() {
		return noteDate;
	}

	/**
	 * @param noteDate the noteDate to set
	 */
	public void setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
	}
	
	/**
	 * 读取序列化的数据
	 */
	public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>(){
		
		@Override
		public Note createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			Note note = new Note();
			note.setNoteId(source.readInt());
			note.setNoteTitle(source.readString());
			note.setNoteContent(source.readString());
			note.setNoteDate(ConvertStringAndDate.stringtodate(source.readString()));
			
			return note;
		}
		
		@Override
		public Note[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Note[size];
		}
		
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * 序列化对象写的操作
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(this.noteId);
		dest.writeString(this.noteTitle);
		dest.writeString(this.noteContent);
		dest.writeString(ConvertStringAndDate.datetoString(this.noteDate));
	}
	
}
