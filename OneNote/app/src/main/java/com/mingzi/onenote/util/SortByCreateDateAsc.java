package com.mingzi.onenote.util;

import com.mingzi.onenote.vo.Note;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/4/30.
 */
public class SortByCreateDateAsc implements Comparator<Note> {

    @Override
    public int compare(Note lhs, Note rhs) {
        return lhs.getCreateDate().compareTo(rhs.getCreateDate());
    }
}
