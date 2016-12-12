package com.zhouyu.crop;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * 截取尺寸信息
 * Created by ZHOUYU on 2016/4/12.
 */
public class CropSizeInfo {
    public static final int ACTION_OFFSET_DEFAULT = 50;
    private static final int MIN_WIDTH = 100;
    private static final int UNFIXED_MIN = 50;
    public static final int ORIGINAL_HIGHT = 480;
    public static final int ORIGINAL_WIDTH = 800;
    public int targetHight = ORIGINAL_HIGHT;
    public int targetWidth = ORIGINAL_WIDTH;
    public int resultPointX;
    public int resultPointY;
    public int resultWidth;
    public int resultHight;
    public int screenWidth;
    public int screenHight;
    public int marginTop;
    public int marginBottom;
    public int minWidth = MIN_WIDTH;
    private int actionOffSet = ACTION_OFFSET_DEFAULT;

    public CropSizeInfo(final int pointx, final int pointy, final int width, final int screenHight, final int screenWidth,int targetWidth,int targetHight) {
        this.screenWidth = screenWidth;
        this.screenHight = screenHight;
        this.targetWidth = targetWidth;
        this.targetHight = targetHight;
        int endPointX = pointx + width;
        if (endPointX > screenWidth) {
            resultWidth = width - (endPointX - screenWidth);
        }
        else{
            resultWidth = width;
        }
        int hight = width * targetHight / targetWidth;
        int endPointY = pointy + hight;
        if (endPointY > screenHight)
        {
            resultHight = hight - (endPointY - screenHight);
            resultWidth = hight * targetWidth / targetHight;
        }else{
            resultHight = hight;
        }
        resultPointX = pointx;
        resultPointY = pointy;
    }

    public Rect getLeftRect()
    {
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = resultPointY;
        rect.right = resultPointX;
        rect.bottom = resultPointY+resultHight;
        return rect;
    }

    public Rect getTopRect()
    {
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = 0;
        rect.right = screenWidth;
        rect.bottom = resultPointY;
        return rect;
    }

    public Rect getRightRect()
    {
        Rect rect = new Rect();
        rect.left = resultPointX+resultWidth;
        rect.top = resultPointY;
        rect.right = screenWidth;
        rect.bottom = resultPointY+resultHight;
        return rect;
    }

    public Rect getBottomRect()
    {
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = resultPointY+resultHight;
        rect.right = screenWidth;
        rect.bottom = screenHight;
        return rect;
    }

    public Rect getInnerRect()
    {
        Rect rect = new Rect();
        rect.left = resultPointX;
        rect.top = resultPointY;
        rect.right = resultPointX+resultWidth;
        rect.bottom = resultPointY+resultHight;
        return rect;
    }

    public void resizeLeftTop(boolean isFixed, int moveX, int moveY) {
        if(isFixed) {
            int scaleY = (resultPointY - marginTop) * targetWidth / targetHight;
            int baseWidth = resultPointX > scaleY ? scaleY : resultPointX;

            int tempResizeWidth = 0;
            if (moveX < 0) {
                tempResizeWidth = moveX + baseWidth < 0 ? -baseWidth : moveX;
            } else {
                if (moveX < resultWidth) {
                    tempResizeWidth = moveX;
                }
            }

            if (resultWidth - tempResizeWidth > minWidth) {
                int tempResizeHight = tempResizeWidth * targetHight / targetWidth;
                resultPointX += tempResizeWidth;
                resultPointY += tempResizeHight;
                resultWidth -= tempResizeWidth;
                resultHight -= tempResizeHight;
            }
        }else{
            int realMoveX = moveX;
            int realMoveY = moveY;

            if((resultPointX+ moveX)<0){
                realMoveX = -resultPointX;
            }

            if((resultPointX+ moveX)>(resultPointX+resultWidth-UNFIXED_MIN)){
                realMoveX = resultWidth-UNFIXED_MIN;
            }

            if((resultPointY + moveY)<marginTop){
                realMoveY = marginTop-resultPointY;
            }

            if((resultPointY+ moveY)>(resultPointY+resultHight-UNFIXED_MIN)){
                realMoveY = resultHight-UNFIXED_MIN;
            }

            resultPointX += realMoveX;
            resultPointY += realMoveY;
            resultWidth -= realMoveX;
            resultHight -= realMoveY;
        }
    }

    public void resizeLeftBottom(boolean isFixed, int moveX, int moveY) {
        if(isFixed) {
            int scaleY = (screenHight - resultHight - resultPointY - marginBottom) * targetWidth / targetHight;
            int baseWidth = resultPointX > scaleY ? scaleY : resultPointX;
            int tempResizeWidth = 0;
            if (moveX < 0) {
                tempResizeWidth = moveX + baseWidth < 0 ? -baseWidth : moveX;
            } else {
                if (moveX < resultWidth) {
                    tempResizeWidth = moveX;
                }
            }

            if (resultWidth - tempResizeWidth > minWidth) {
                int tempResizeHight = tempResizeWidth * targetHight / targetWidth;
                resultPointX += tempResizeWidth;
                resultWidth -= tempResizeWidth;
                resultHight -= tempResizeHight;
            }
        }else{
            int realMoveX = moveX;
            int realMoveY = moveY;

            if((resultPointX+ moveX)<0){
                realMoveX = -resultPointX;
            }

            if((resultPointX+ moveX)>(resultPointX+resultWidth-UNFIXED_MIN)){
                realMoveX = resultWidth-UNFIXED_MIN;
            }

            if((resultPointY+ resultHight + moveY)<(resultPointY+UNFIXED_MIN)){
                realMoveY = resultPointY+UNFIXED_MIN-resultPointY-resultHight;
            }

            if((resultPointY+resultHight+ moveY)>(screenHight-marginBottom)){
                realMoveY = screenHight-marginBottom-resultPointY-resultHight;
            }
            resultPointX += realMoveX;
            resultWidth -= realMoveX;
            resultHight += realMoveY;
        }
    }

    public void resizeRightTop(boolean isFixed, int moveX, int moveY) {
        if(isFixed) {
            int scaleY = (resultPointY - marginTop) * targetWidth / targetHight;
            int rightMagin = screenWidth - resultPointX - resultWidth;
            int baseWidth = rightMagin > scaleY ? scaleY : rightMagin;
            int tempResizeWidth = 0;
            if (moveX > 0) {
                tempResizeWidth = moveX > baseWidth ? baseWidth : moveX;
            } else {
                if (Math.abs(moveX) < resultWidth) {
                    tempResizeWidth = moveX;
                }
            }

            if (resultWidth + tempResizeWidth > minWidth) {
                int tempResizeHight = tempResizeWidth * targetHight / targetWidth;
                resultPointY -= tempResizeHight;
                resultWidth += tempResizeWidth;
                resultHight += tempResizeHight;
            }
        }else{
            int realMoveX = moveX;
            int realMoveY = moveY;

            if((resultPointX+resultWidth+ moveX)<resultPointX+UNFIXED_MIN){
                realMoveX = UNFIXED_MIN-resultWidth;
            }

            if((resultPointX+resultWidth+ moveX)>screenWidth){
                realMoveX = screenWidth-resultPointX-resultWidth;
            }


            if((resultPointY + moveY)<marginTop){
                realMoveY = marginTop-resultPointY;
            }

            if((resultPointY+ moveY)>(resultPointY+resultHight-UNFIXED_MIN)){
                realMoveY = resultHight-UNFIXED_MIN;
            }

            resultPointY += realMoveY;
            resultWidth += realMoveX;
            resultHight -= realMoveY;
        }
    }

    public void resizeRightBottom(boolean isFixed, int moveX, int moveY) {
        if(isFixed) {
            int scaleY = (screenHight - resultHight - resultPointY - marginBottom) * targetWidth / targetHight;
            int rightMagin = screenWidth - resultPointX - resultWidth;
            int baseWidth = rightMagin > scaleY ? scaleY : rightMagin;
            int tempResizeWidth = 0;
            if (moveX > 0) {
                tempResizeWidth = moveX > baseWidth ? baseWidth : moveX;
            } else {
                if (Math.abs(moveX) < resultWidth) {
                    tempResizeWidth = moveX;
                }
            }

            if (resultWidth + tempResizeWidth > minWidth) {
                int tempResizeHight = tempResizeWidth * targetHight / targetWidth;
                resultWidth += tempResizeWidth;
                resultHight += tempResizeHight;
            }
        }else{

            int realMoveX = moveX;
            int realMoveY = moveY;

            if((resultPointX+resultWidth+ moveX)<resultPointX+UNFIXED_MIN){
                realMoveX = UNFIXED_MIN-resultWidth;
            }

            if((resultPointX+resultWidth+ moveX)>screenWidth){
                realMoveX = screenWidth-resultPointX-resultWidth;
            }
            if((resultPointY+ resultHight + moveY)<(resultPointY+UNFIXED_MIN)){
                realMoveY = resultPointY+UNFIXED_MIN-resultPointY-resultHight;
            }

            if((resultPointY+resultHight+ moveY)>(screenHight-marginBottom)){
                realMoveY = screenHight-marginBottom-resultPointY-resultHight;
            }
            resultWidth += realMoveX;
            resultHight += realMoveY;
        }
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public void setActionOffSet(int actionOffSet) {
        this.actionOffSet = actionOffSet;
    }

    public void setMargin(int marginTop, int marginBottom) {
//        Log.d("MaskDebug","set Margin xy after, marginTop:"+marginTop+" marginBottom:"+marginBottom);
        this.marginBottom = marginBottom;
        this.marginTop = marginTop;
    }

    public interface ActionKind{
        int MOVE = 0;
//        int LEFT_RESIZE = 1;
//        int RIGHT_RESIZE = 2;
//        int TOP_RESIZE = 3;
//        int BOTTOM_RESIZE = 4;

        int LEFT_TOP_RESIZE = 1;
        int RIGHT_TOP_RESIZE = 2;
        int RIGHT_BOTTOM_RESIZE = 3;
        int LEFT_BOTTOM_RESIZE = 4;
        int UNKNOW = -1;
    }

    public int getActionKind(int x,int y){
        int actionKind = ActionKind.UNKNOW;
        int xAddOffset = resultPointX+ actionOffSet;
        int xDevOffset = resultPointX- actionOffSet;
        int yAddOffset = resultPointY+ actionOffSet;
        int yDevOffset = resultPointY- actionOffSet;
        int yHightDevOffset = resultPointY+resultHight- actionOffSet;
        int yHightAddOffset = resultPointY+resultHight+ actionOffSet;
        int xWidthAddOffset = resultPointX+resultWidth+ actionOffSet;
        int xWidthDevOffset = resultPointX+resultWidth- actionOffSet;
//        if(x>xDevOffset && x<xAddOffset && y>yDevOffset && y<yHightAddOffset){
//            actionKind = ActionKind.LEFT_RESIZE;
//        }else if(x>xDevOffset && x<xWidthAddOffset && y>yDevOffset && y<yAddOffset){
//            actionKind = ActionKind.TOP_RESIZE;
//        }else if(x>xWidthDevOffset && x<xWidthAddOffset && y>yDevOffset && y<yHightAddOffset){
//            actionKind = ActionKind.RIGHT_RESIZE;
//        }else if(x>xDevOffset && x<xWidthAddOffset && y>yHightDevOffset && y<yHightAddOffset){
//            actionKind = ActionKind.BOTTOM_RESIZE;
//        }else if(x>xAddOffset && x<xWidthDevOffset && y>yAddOffset && y<yHightDevOffset){
//            actionKind = ActionKind.MOVE;
//        }

        if(x>xDevOffset && x<xAddOffset && y>yDevOffset && y<yAddOffset){
            actionKind = ActionKind.LEFT_TOP_RESIZE;
        }else if(x>xWidthDevOffset && x<xWidthAddOffset && y>yDevOffset && y<yAddOffset){
            actionKind = ActionKind.RIGHT_TOP_RESIZE;
        }else if(x>xWidthDevOffset && x<xWidthAddOffset && y>yHightDevOffset && y<yHightAddOffset){
            actionKind = ActionKind.RIGHT_BOTTOM_RESIZE;
        }else if(x>xDevOffset && x<xAddOffset &&  y>yHightDevOffset && y<yHightAddOffset){
            actionKind = ActionKind.LEFT_BOTTOM_RESIZE;
        }else if(x>resultPointX && x<(resultPointX+resultWidth) && y>resultPointY && y<(resultPointY+resultHight)){
            actionKind = ActionKind.MOVE;
        }
        return actionKind;
    }

    public void moveXY(boolean isFixed, int movX, int movY){
        if(isFixed) {
            if (resultPointX + movX < 0) {
                resultPointX = 0;
            } else if ((resultPointX + resultWidth + movX) > screenWidth) {
                resultPointX = screenWidth - resultWidth;
            } else {
                resultPointX += movX;
            }


//        Log.d("MaskDebug","move xy before resultPointY:"+resultPointY+" y:"+y+" marginTop:"+marginTop+" marginBottom:"+marginBottom);
            if (resultPointY + movY < marginTop) {
                resultPointY = marginTop;
            } else if ((resultPointY + resultHight + movY) > (screenHight - marginBottom)) {
                resultPointY = screenHight - resultHight - marginBottom;
            } else {
                resultPointY += movY;
            }
//        Log.d("MaskDebug","move xy after, resultPointY:"+resultPointY);
        }else{
            int realMovX=movX;
            int realMovY = movY;

            if(resultPointX+movX<0){
                realMovX = - resultPointX;
            }

            if((resultPointX+resultWidth+movX)>screenWidth){
                realMovX = screenWidth-resultPointX-resultWidth;
            }

            if((resultPointY+movY)<marginTop){
                realMovY = marginTop-resultPointY;
            }

            if((resultPointY+resultHight+movY)>(screenHight-marginBottom)){
                realMovY = screenHight-marginBottom- resultPointY -resultHight;
            }
            resultPointX+=realMovX;
            resultPointY+=realMovY;
        }
    }

    public Point getLeftTopPoint(){
        return new Point(resultPointX,resultPointY);
    }
    public Point getLeftBottomPoint(){
        return new Point(resultPointX,resultPointY+resultHight);
    }
    public Point getRightTopPoint(){
        return new Point(resultPointX+resultWidth,resultPointY);
    }
    public Point getRightBottomPoint(){
        return new Point(resultPointX+resultWidth,resultPointY+resultHight);
    }
}
