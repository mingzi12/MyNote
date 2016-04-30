package com.mingzi.onenote.util;

import com.mingzi.onenote.vo.Note;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/4/29.
 */
public class SortByUpdateDateAsc implements Comparator<Note> {
    @Override
    public int compare(Note lhs, Note rhs) {
        return lhs.getUpdateDate().compareTo(rhs.getUpdateDate());
    }
}
