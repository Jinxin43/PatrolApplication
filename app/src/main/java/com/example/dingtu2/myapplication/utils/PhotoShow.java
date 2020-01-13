package com.example.dingtu2.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.example.dingtu2.myapplication.AppSetting;
import com.example.dingtu2.myapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/23.
 */

public class PhotoShow {

    private Context mContext;

    public void showPhotos(Context context, List<String> photoNameList, GridView gridView) {
        mContext = context;

        String[] from = {"image", "text", "check"};
        int[] to = {R.id.iv_image, R.id.tv_info, R.id.cb_select};
        ArrayList<HashMap<String, Object>> data_list = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < photoNameList.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            //map.put("image", mSmallPhotoPath+"/"+mPhotoNameList.get(i));
            map.put("image", AppSetting.smallPhotoPath + "/" + photoNameList.get(i));
            map.put("text", photoNameList.get(i));
            map.put("check", false);
            data_list.add(map);
        }
        //sim_adapter = new ImageListAdapter(mOwnActivity, data_list, R.layout.photolistitem, from, to);
        SimpleAdapter sim_adapter = new SimpleAdapter(context, data_list, R.layout.photolistitem, from, to);

        Log.i("gridView", "gridView item:" + data_list.size());
        gridView.setAdapter(sim_adapter);
        gridView.setOnItemClickListener(new photoItemClickListener());
        gridView.invalidate();
    }

    //    照片单击展示
    class photoItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened
                                View arg1,//The view within the AdapterView that was clicked
                                int arg2,//The position of the view in the adapter
                                long arg3//The row id of the item that was clicked
        ) {
            //在本例中arg2=arg3
            HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
            //显示所选Item的ItemText

            Intent it = new Intent(Intent.ACTION_VIEW);
            String photoName = item.get("image") + "";
            photoName = photoName.replace("/samllPhoto", "");
            File file = new File(photoName);
            Uri fileName = FileProvider.getUriForFile(mContext, "com.example.dingtu2.myapplication.fileprovider", file);
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            it.setDataAndType(fileName, "image/*");

            mContext.startActivity(it);
        }
    }
}
