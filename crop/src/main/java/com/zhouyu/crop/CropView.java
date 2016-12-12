package com.zhouyu.crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ZHOUYU on 2016/12/8.
 */
public class CropView extends FrameLayout {
    private ImageView imageShowView;
    private CropMaskView maskView;
    private int imageWidth;
    private int imageHight;
    private int targetWidth;
    private int targetHight;
    private String imageSrcPath ;
    private boolean isFixed;

    /**
     * 设置截取目标图片的宽与高
     * @param targetHight
     * @param targetWidth
     */
    public void setTargetWidthAndHight(int targetHight, int targetWidth) {
        this.targetWidth = targetWidth;
        this.targetHight = targetHight;
        maskView.setTargetSize(targetHight,targetWidth);
    }

    /**
     * 设置是否固定横款比
     * @param fixed
     */
    public void setFixed(boolean fixed) {
        isFixed = fixed;
        maskView.setFixedFlage(isFixed);
    }

    public CropView(Context context) {
        super(context);
        init();
    }

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttr(attrs);
    }

    public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttr(attrs);
    }

    private void init() {
        imageShowView = new ImageView(getContext());
        maskView = new CropMaskView(getContext());
        addView(imageShowView);
        addView(maskView);
        maskView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                intMarginView();
                maskView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        isFixed = true;
        targetHight = CropSizeInfo.ORIGINAL_HIGHT;
        targetWidth = CropSizeInfo.ORIGINAL_WIDTH;
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.CropZY);
        isFixed = a.getBoolean(R.styleable.CropZY_isFixed, true);
        targetWidth = a.getInt(R.styleable.CropZY_targetWidth,CropSizeInfo.ORIGINAL_WIDTH);
        targetHight = a.getInt(R.styleable.CropZY_targetHight,CropSizeInfo.ORIGINAL_HIGHT);
        setFixed(isFixed);
        setTargetWidthAndHight(targetHight,targetWidth);
        a.recycle();
    }

    private void intMarginView() {
        if(0!= imageWidth) {
            int screenWidth = getScreenWidth();
            int viewHight = screenWidth * imageHight / imageWidth;
            int imageViewHight = imageShowView.getHeight();
            if (imageViewHight > viewHight) {
                imageShowView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }
    }

    private int getScreenWidth() {
        WindowManager manager = ((AppCompatActivity)getContext()).getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private void setBitmap(Bitmap bitmap) {
        if(null != bitmap){
            imageShowView.setImageBitmap(bitmap);
            imageWidth = bitmap.getWidth()*2;
            imageHight = bitmap.getHeight()*2;
            maskView.initCropWindowPropertyies(imageWidth,imageHight);
        }
    }

//    public void setBitmapResId(int resId){
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),resId);
//        setBitmap(bitmap);
//    }

    /**
     * 设置截取图片的路径
     * @param path
     */
    public void setBitmapPath(String path){
        imageSrcPath = path;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        setBitmap(bitmap);
    }

    /**
     * 保存截取的图片到指定路径
     * @param savePath
     */
    public void saveCropImage(String savePath) {
        CropSizeInfo sizeOf = maskView.getCropSize();
        Bitmap bitmap = BitmapFactory.decodeFile(imageSrcPath);

        if(null != bitmap){
            Bitmap result;
            int bitmapWidth = bitmap.getWidth();
            int bitmapHight = bitmap.getHeight();


            try{
                //对截取坐标进行转换
                float resizeScale = (float) bitmapWidth/sizeOf.screenWidth;
                int cropStartX = 0;
                int cropStartY = 0;
                int cropWidth = 0;
                int cropHight = 0;
                if(sizeOf.marginTop>0){
                    cropStartY = (int)((sizeOf.resultPointY-sizeOf.marginTop)*resizeScale);
                    cropStartX = (int)(sizeOf.resultPointX*resizeScale);
                    cropWidth = (int)(sizeOf.resultWidth*resizeScale);
                    cropHight = (int)(sizeOf.resultHight*resizeScale);
                }else{
                    int resizeStartY = (int)(bitmapHight/resizeScale - sizeOf.screenHight)/2;
                    cropStartY = (int)((sizeOf.resultPointY+resizeStartY)*resizeScale);
                    cropStartX = (int)(sizeOf.resultPointX*resizeScale);
                    cropWidth = (int)(sizeOf.resultWidth*resizeScale);
                    cropHight = (int)(sizeOf.resultHight*resizeScale);
                }


                //对截图窗口所示图片进行截取
                File saveFile = new File(savePath);
                File  parentFile = saveFile.getParentFile();
                if(!parentFile.exists()){
                    parentFile.mkdirs();
                }

                if(!saveFile.exists()){
                    saveFile.createNewFile();
                }
                result = Bitmap.createBitmap(bitmap,cropStartX,cropStartY,cropWidth,cropHight);

                //缩放成目标尺寸
                if(isFixed) {
                    result = Bitmap.createScaledBitmap(result, targetWidth, targetHight, false);
                }else{
                    result = Bitmap.createScaledBitmap(result, cropWidth, cropHight, false);
                }
                FileOutputStream out = new FileOutputStream(saveFile);
                result.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.flush();
                out.close();
                bitmap.recycle();
            }catch (Exception e){
            }
        }
    }

}
