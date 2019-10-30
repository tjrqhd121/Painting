package kr.co.clipsoft.func;

import android.graphics.Paint;
import android.graphics.RectF;

public class eraser {
        private RectF rect;
        private Paint rectPaint;

        public eraser(){
            rect = new RectF();
            rectPaint = new Paint();
            rectPaint.setColor(0xFF000000);
            rectPaint.setStyle(Paint.Style.STROKE);//스타일 설정,채우기 없이 테두리만 그려집니다.
            rectPaint.setStrokeWidth(5f);//선 굵기
        }

        public void setEraser(float x, float y){
            rect.set(x,y,x+50,y+50);
        }

        public void clearEraser(){
            this.rect.setEmpty();
        }

        public RectF getPath(){
            return this.rect;
        }

        public Paint getPaint(){
            return this.rectPaint;
        }

        public boolean containsPath(float x,float y){
            return rect.contains(x,y);
        }

}
