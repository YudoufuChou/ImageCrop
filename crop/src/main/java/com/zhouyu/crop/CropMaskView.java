package com.zhouyu.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ZHOUYU on 2016/4/12.
 */
public class CropMaskView extends View {
    private static final int MASK_COLOR = 0x99000000;
    private static final int MOVE_OFFSET_DEFAULT = 10;
    private static final int CIRCLE_R_DEFAULT = 20;
    private static final int MARGIN_INIT_DEFAULT = 100;
    private static final float BORDER_WIDTH = 5;
    private CropSizeInfo sizeInfo;
    private  int downX=0;
    private  int downY=0;
    private  int lastActionX=0;
    private  int lastActionY=0;
    private int actionKind = CropSizeInfo.ActionKind.UNKNOW;
    private  int imageWidth=0;
    private int imageHight = 0;
    private int circleR = CIRCLE_R_DEFAULT;
    private int moveOffset = MOVE_OFFSET_DEFAULT;
    private int initMargin = MARGIN_INIT_DEFAULT;
    private int actionOffset = CropSizeInfo.ACTION_OFFSET_DEFAULT;
    private  int imageTargetWidth=CropSizeInfo.ORIGINAL_WIDTH;
    private int imageTargetHight = CropSizeInfo.ORIGINAL_HIGHT;
    private boolean isFixed;

    public CropMaskView(Context context) {
        super(context);
        init();
    }

    public CropMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circleR = (int)getResources().getDimension(R.dimen.crop_crycle_r);
        moveOffset = (int)getResources().getDimension(R.dimen.crop_move_offset);
        actionOffset = (int)getResources().getDimension(R.dimen.crop_action_offset);
        initMargin = (int)getResources().getDimension(R.dimen.crop_window_init_margin);
    }

    private void resizeReset(int screenWidth, int screenHight ) {
        int width = screenWidth - initMargin;
        int targetHight = screenWidth*imageTargetHight/imageTargetWidth;
        int imageH = Integer.MAX_VALUE;
        if(0!=imageHight){
            imageH = screenWidth*imageHight/imageWidth;
        }

        int viewH = Math.min(imageH,screenHight);
        if(targetHight>viewH){
            targetHight = viewH- initMargin;
            width = targetHight*imageTargetWidth/imageTargetHight;
        }

        int pointX = (screenWidth-width)/2;
        int pointY = (screenHight-targetHight)/2;
        sizeInfo = new CropSizeInfo(pointX,pointY,width,screenHight,screenWidth,imageTargetWidth,imageTargetHight);
        if(0 !=imageWidth ){
            sizeInfo.setMinWidth(screenWidth*imageTargetWidth/imageWidth);
            sizeInfo.setActionOffSet(actionOffset);

//            Log.d("MaskDebug","set Margin Ana, screenWidth:"+screenWidth+" imageHight:"+imageHight+
//                    " imageWidth:"+imageWidth);
            int viewHight = screenWidth*imageHight/imageWidth;
            if(viewHight<screenHight) {
                int marginTotal = screenHight - viewHight;
                int marginTop = marginTotal / 2;
                int marginBottom = marginTotal - marginTop;
                sizeInfo.setMargin(marginTop, marginBottom);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        resizeReset(w,h);
        super.onSizeChanged(w, h, oldw, oldh);
    }


    public CropSizeInfo getCropSize(){
        return sizeInfo;
    }
    @Override
    protected void onDraw(Canvas canvas) {
//        Log.d("MaskDebug","onDraw, actionKind:"+actionKind+" draw Rect:"+sizeInfo.getInnerRect());
        if(null != sizeInfo) {
            //绘制黑色天窗
            Paint paint = new Paint();
            paint.setColor(MASK_COLOR);
            canvas.drawRect(sizeInfo.getLeftRect(), paint);
            canvas.drawRect(sizeInfo.getTopRect(), paint);
            canvas.drawRect(sizeInfo.getRightRect(), paint);
            canvas.drawRect(sizeInfo.getBottomRect(), paint);

            //绘制白色边框
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(BORDER_WIDTH);
            paint.setColor(Color.WHITE);
            canvas.drawRect(sizeInfo.getInnerRect(),paint);

            //绘制白色远点
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            Point leftTopPoint = sizeInfo.getLeftTopPoint();
            canvas.drawCircle(leftTopPoint.x,leftTopPoint.y,circleR,paint);
            Point leftBottomPoint = sizeInfo.getLeftBottomPoint();
            canvas.drawCircle(leftBottomPoint.x,leftBottomPoint.y,circleR,paint);
            Point rightTopPoint = sizeInfo.getRightTopPoint();
            canvas.drawCircle(rightTopPoint.x,rightTopPoint.y,circleR,paint);
            Point rightBottomPoint = sizeInfo.getRightBottomPoint();
            canvas.drawCircle(rightBottomPoint.x,rightBottomPoint.y,circleR,paint);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != sizeInfo) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastActionX = 0;
                    lastActionY = 0;
                    downX = (int)event.getX();
                    downY = (int)event.getY();
                    actionKind = sizeInfo.getActionKind(downX,downY);
//                    Log.d("MaskDebug","down point, x:"+downX+" y:"+downY+" actionKind:"+actionKind);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int currentX = (int)event.getX();
                    int currentY = (int)event.getY();
//                    Log.d("MaskDebug","move point, x:"+currentX+" y:"+currentY);
                    if(actionKind!= CropSizeInfo.ActionKind.UNKNOW ){
                        if(0==lastActionX && 0== lastActionY){
                            doAction(actionKind,downX,downY,currentX,currentY);
                            lastActionX = currentX;
                            lastActionY = currentY;
                        }else {
//                            Log.d("MaskDebug","action  xMove:"+(currentX-lastActionX)+" yMove:"+(currentY-lastActionY));
                            if(Math.abs(currentX-lastActionX)>moveOffset || Math.abs(currentY-lastActionY)>moveOffset){
                                doAction(actionKind,lastActionX,lastActionY,currentX,currentY);
                                lastActionX = currentX;
                                lastActionY = currentY;
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
//                    Log.d("MaskDebug","action up");
                    actionKind = CropSizeInfo.ActionKind.UNKNOW;
                    break;
            }
        }

        return true;
    }

    private void doAction(int actionKind, int sourceX, int sourceY, int targetX, int targetY) {
//        Log.d("MaskDebug","doAction, actionKind:"+actionKind);
        int moveX = targetX - sourceX;
        int moveY = targetY - sourceY;
        if(CropSizeInfo.ActionKind.MOVE == actionKind) {
//            int moveX = targetX - sourceX;
//            int moveY = targetY - sourceY;
            sizeInfo.moveXY(isFixed,moveX, moveY);
        }else if(CropSizeInfo.ActionKind.LEFT_TOP_RESIZE == actionKind){
//            int moveX = targetX - sourceX;
//            int moveY = targetX - sourceX;
            sizeInfo.resizeLeftTop(isFixed,moveX,moveY);
        }else if(CropSizeInfo.ActionKind.LEFT_BOTTOM_RESIZE == actionKind){
//            int moveX = targetX - sourceX;
            sizeInfo.resizeLeftBottom(isFixed,moveX,moveY);
        }else if(CropSizeInfo.ActionKind.RIGHT_TOP_RESIZE == actionKind){
//            int moveX = targetX - sourceX;
            sizeInfo.resizeRightTop(isFixed,moveX,moveY);
        }else if(CropSizeInfo.ActionKind.RIGHT_BOTTOM_RESIZE == actionKind){
//            int moveX = targetX - sourceX;
            sizeInfo.resizeRightBottom(isFixed,moveX,moveY);
        }

        invalidate();
    }

    public void initCropWindowPropertyies(int width, int hight) {
        imageWidth = width;
        imageHight = hight;
    }

    public void setTargetSize(int targetHight, int targetWidth) {
        imageTargetWidth = targetWidth;
        imageTargetHight = targetHight;
    }

    public void setFixedFlage(boolean isFixed) {
        this.isFixed = isFixed;
    }
}
