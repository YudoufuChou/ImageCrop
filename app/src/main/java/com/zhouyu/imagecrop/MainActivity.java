package com.zhouyu.imagecrop;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int FIXED_CRAMERA_REQ = 1001;
    public static final int UNFIXED_CRAMERA_REQ = 1002;
    public static final int FIXED_GALLERY_REQ = 1003;
    public static final int UNFIXED_GALLERY_REQ = 1004;

    public static final int FIXED_CRAMERA_CROP_REQ = 1011;
    public static final int UNFIXED_CRAMERA_CROP_REQ = 1012;
    public static final int FIXED_GALLERY_CROP_REQ = 1013;
    public static final int UNFIXED_GALLERY_CROP_REQ = 1014;

    public static final String FILE_SCHEME = "file";
    public static final String CONTENT_SCHEME = "content";

    private String imageSrcPath;

    private String imageSavePath;

    private ImageView resultShowIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.fixed_carmera_crop).setOnClickListener(this);
        findViewById(R.id.unfixed_carmera_crop).setOnClickListener(this);
        findViewById(R.id.fixed_gallery_crop).setOnClickListener(this);
        findViewById(R.id.unfixed_gallery_crop).setOnClickListener(this);
        resultShowIv = (ImageView) findViewById(R.id.result_show_iv);
    }

    @Override
    public void onClick(View view) {
        imageSavePath = getImageSavePath();
        imageSrcPath = getImageSrcPath();

        switch (view.getId()){
            case R.id.fixed_carmera_crop:
                takeFixedPhoto();
                break;
            case R.id.unfixed_carmera_crop:
                takeUnFixedPhoto();
                break;
            case R.id.fixed_gallery_crop:
                selectFixedPhoto();
                break;
            case R.id.unfixed_gallery_crop:
                selectUnFixedPhoto();
                break;
        }
    }

    private void selectUnFixedPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        intent.putExtra("return-data", true);
        startActivityForResult(intent, UNFIXED_GALLERY_REQ);
    }

    private void selectFixedPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        intent.putExtra("return-data", true);
        startActivityForResult(intent, FIXED_GALLERY_REQ);
    }

    private void takeFixedPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imageSrcPath)));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent,FIXED_CRAMERA_REQ);
    }

    private void takeUnFixedPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imageSrcPath)));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent,UNFIXED_CRAMERA_REQ);
    }

    private String getImageSrcPath() {
        String cachePath = this.getExternalCacheDir().getAbsolutePath();
        return cachePath + File.separator+"src" + System.currentTimeMillis() + ".jpg";
    }

    private String getImageSavePath() {
        String cachePath = this.getExternalCacheDir().getAbsolutePath();
        return cachePath + File.separator+"save" + System.currentTimeMillis() + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(FIXED_CRAMERA_REQ == requestCode){
                Intent intent = new Intent(this, CropImageActivity.class);
                intent.putExtra(CropImageActivity.IMAGE_PATH_TAG, imageSrcPath);
                intent.putExtra(CropImageActivity.SAVE_PATH_TAG, imageSavePath);
                startActivityForResult(intent, FIXED_CRAMERA_CROP_REQ);
            }else if(UNFIXED_CRAMERA_REQ == requestCode){
                Intent intent = new Intent(this, CropImageActivity.class);
                intent.putExtra(CropImageActivity.IMAGE_PATH_TAG, imageSrcPath);
                intent.putExtra(CropImageActivity.SAVE_PATH_TAG, imageSavePath);
                intent.putExtra(CropImageActivity.FIXED_FLAG, false);
                startActivityForResult(intent, UNFIXED_CRAMERA_CROP_REQ);
            }else if(FIXED_GALLERY_REQ == requestCode){
                imageSrcPath = getGalleryImagePath(data);
                Intent intent = new Intent(this, CropImageActivity.class);
                intent.putExtra(CropImageActivity.IMAGE_PATH_TAG, imageSrcPath);
                intent.putExtra(CropImageActivity.SAVE_PATH_TAG, imageSavePath);
                startActivityForResult(intent, FIXED_GALLERY_CROP_REQ);
            }else if(UNFIXED_GALLERY_REQ == requestCode){
                imageSrcPath = getGalleryImagePath(data);
                Intent intent = new Intent(this, CropImageActivity.class);
                intent.putExtra(CropImageActivity.IMAGE_PATH_TAG, imageSrcPath);
                intent.putExtra(CropImageActivity.SAVE_PATH_TAG, imageSavePath);
                intent.putExtra(CropImageActivity.FIXED_FLAG, false);
                startActivityForResult(intent, UNFIXED_GALLERY_CROP_REQ);
            }else if(FIXED_CRAMERA_CROP_REQ == requestCode || UNFIXED_CRAMERA_CROP_REQ == requestCode
                || FIXED_GALLERY_CROP_REQ == requestCode || UNFIXED_GALLERY_CROP_REQ == requestCode){
                if(new File(imageSavePath).exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(imageSavePath);
                    resultShowIv.setImageBitmap(bitmap);
                }
            }
        }
    }

    private String getGalleryImagePath(Intent data) {
        Uri imageUri = data.getData();
        String picPath = null;
        if(CONTENT_SCHEME.equals(imageUri.getScheme())){
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picPath = cursor.getString(columnIndex);
            cursor.close();
        }else if (FILE_SCHEME.equals(imageUri.getScheme())){
            picPath = imageUri.getPath();
        }

        return picPath;
    }
}
