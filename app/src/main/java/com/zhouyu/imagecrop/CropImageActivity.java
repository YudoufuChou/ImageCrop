package com.zhouyu.imagecrop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.zhouyu.crop.CropView;

public class CropImageActivity extends AppCompatActivity {

    public static final String IMAGE_PATH_TAG = "imageFilePath" ;
    public static final String SAVE_PATH_TAG = "saveFilePath" ;
    public static final String FIXED_FLAG = "fixed_flag";

    private String srcPath;
    private String savePath;
    private boolean isFixed;

    private CropView cropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        Intent intent  = getIntent();
        srcPath = intent.getStringExtra(IMAGE_PATH_TAG);
        savePath = intent.getStringExtra(SAVE_PATH_TAG);
        isFixed = intent.getBooleanExtra(FIXED_FLAG,true);
        cropView = (CropView) findViewById(R.id.crop_view);
        cropView.setBitmapPath(srcPath);
        cropView.setFixed(isFixed);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.page_action_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.save) {
            cropView.saveCropImage(savePath);
            setResult(RESULT_OK);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
