package kr.co.clipsoft.func;

import android.graphics.Paint;
import android.graphics.Path;

public class draw {
        private Path mPath;
        private Paint mPaint;

        public draw(){
            mPath = new Path();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);//스타일 설정,채우기 없이 테두리만 그려집니다.
            mPaint.setColor(0xFF000000);//선 색깔
            mPaint.setStrokeWidth(5);//선 굵기
        }

        public draw(int num){
            mPath = new Path();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);//스타일 설정,채우기 없이 테두리만 그려집니다.
            mPaint.setColor(0xFF000000);//선 색깔
            mPaint.setStrokeWidth(num);//선 굵기
        }

        public Path getPath(){
            return mPath;
        }

        public Paint getPaint(){
            return mPaint;
        }

}
