package com.DingTu.Base;

import java.util.Comparator;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class FileComparator implements Comparator<String> {
    public int compare(String file1, String file2)
    {
        long t1 = Long.parseLong(file1.split(",")[1]);
        long t2 = Long.parseLong(file2.split(",")[1]);
        if(t1 > t2)  return -1;  else return 1;
    }
}
