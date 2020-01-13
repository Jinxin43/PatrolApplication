package com.DingTu.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/7/27.
 */

public class UnRedoParaStru {

    public ReUndoCommand Command;          //操作命令
    public List<UnRedoDataItem> DataItemList;   //操作信息

    public UnRedoParaStru()
    {
        this.DataItemList = new ArrayList<UnRedoDataItem>();
    }
}
