package com.example.dingtu2.myapplication.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.example.dingtu2.myapplication.R;
import com.example.dingtu2.myapplication.utils.Utils;

public class GuanHuZhiDuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guan_hu_zhi_du);

        TextView permissionText = (TextView) findViewById(R.id.content_text);
        String filename = "XunHuBanFa.html";
        String content = Utils.getStringFromHtmlFile(GuanHuZhiDuActivity.this, filename);
        permissionText.setText(Html.fromHtml(content));
    }
}
