package com.example.dingtu2.myapplication.controls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dingtu2.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2018/5/3.
 */

public class GridViewAdapter extends BaseAdapter {

    private static int ROW_NUMBER = 3;
    private Context context;
    private GridView mGv;
    private List<Picture> pictures;
    /**
     * 数据资源：标题 ＋ 图片
     */
    private String[] arrText = new String[]{
            "保护区介绍", "政策法规", "公告通知",
            "地图", "巡护", "巡护记录", "设置"
    };
    private int[] arrImages = new int[]{
            R.mipmap.main_menu_baohuqu, R.mipmap.mian_menu_fagui, R.mipmap.main_menu_tongzhi,
            R.mipmap.mainmenu_map, R.mipmap.main_menu_patrol, R.mipmap.main_menu_jilu, R.mipmap.main_menu_setting
    };

    public GridViewAdapter(Context context, GridView gv) {
        this.context = context;
        this.mGv = gv;
        pictures = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Picture pt = new Picture(arrText[i], arrImages[i]);
            pictures.add(pt);
        }

    }

    @Override
    public int getCount() {
        if (null != pictures) {
            return pictures.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.mainmenu_gridview_item, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_item);
            //设置显示图片
            holder.iv.setBackgroundResource(arrImages[position]);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_item);
            //设置标题
            holder.tv.setText(arrText[position]);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //高度计算
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mGv.getHeight() / ROW_NUMBER);

        convertView.setLayoutParams(param);
        return convertView;
    }

    class Holder {
        ImageView iv;
        TextView tv;
    }
}
