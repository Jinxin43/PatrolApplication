package com.example.dingtu2.myapplication.View;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dingtu2.myapplication.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ZhouBaoJieShaoActivity extends AppCompatActivity {

    private String TAG = "BaoHuQuJieShao";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhou_bao_jie_shao);

//        getActionBar().setTitle("保护区介绍");

        final int PhotoPath = this.getIntent().getIntExtra("PhotoName", R.mipmap.zhoubao_internet);
        final String filename = this.getIntent().getStringExtra("FileName");
        String title = this.getIntent().getStringExtra("Title");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("保护区介绍");
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ((TextView) findViewById(R.id.title)).setText(title);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView permissionText = (TextView) findViewById(R.id.content_text);
                String content = getStringFromHtmlFile(ZhouBaoJieShaoActivity.this, filename);

                permissionText.setText(Html.fromHtml(content));

                try {
                    ImageView imageView = (ImageView) findViewById(R.id.image_zhoubao);
                    imageView.setImageResource(PhotoPath);
                } catch (Exception ex) {

                }

            }
        }, 100);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get help string from html file
     *
     * @param context:  Context object
     * @param filePath: html file path
     * @return string
     */
    public String getStringFromHtmlFile(Context context, String filePath) {
        String result = "";
        if (null == context || null == filePath) {
            return result;
        }

        InputStream stream = null;
        BufferedReader reader = null;
        InputStreamReader streamReader = null;
        try {
            // Read html file into buffer
            stream = context.getAssets().open(filePath);
            streamReader = new InputStreamReader(stream, "utf-8");
            reader = new BufferedReader(streamReader);
            StringBuilder builder = new StringBuilder();
            String line = null;

            boolean readCurrentLine = true;
            // Read each line of the html file, and build a string.
            while ((line = reader.readLine()) != null) {
                // Don't read the Head tags when CSS styling is not supporeted.
                if (line.contains("<style")) {
                    readCurrentLine = false;
                } else if (line.contains("</style")) {
                    readCurrentLine = true;
                }
                if (readCurrentLine) {
                    builder.append(line).append("\n");
                }
            }
            result = builder.toString();
        } catch (FileNotFoundException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            if (null != streamReader) {
                try {
                    streamReader.close();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
        return result;
    }
}
