package com.mingzi.onenote.util;

import com.mingzi.onenote.vo.Note;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/4/30.
 */
public class SortByUpadteDateDesc implements Comparator<Note>{

    @Override
    public int compare(Note lhs, Note rhs) {
        return rhs.getUpdateDate().compareTo(lhs.getUpdateDate());
    }
}
