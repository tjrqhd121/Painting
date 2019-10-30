package kr.co.clipsoft.hsh;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import kr.co.clipsoft.func.draw;
import kr.co.clipsoft.func.eraser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private int LineNumber = 5;
    private boolean EraserCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();//권한 설정
        Init();
    }

    @Override
    public void onBackPressed() {
        alertDialog();
    }

    private void Init() {
        final MyView vDraw = new MyView(MainActivity.this);
        FrameLayout DrawLinear = findViewById(R.id.frameLayout);
        DrawLinear.addView(vDraw);

        SeekBar PaintLine = findViewById(R.id.seekBar);

        ImageButton EraserBtn = findViewById(R.id.eraser);
        ImageButton DrawBtn = findViewById(R.id.draw);
        ImageButton CameraBtn = findViewById(R.id.camera);
        ImageButton ClearBtn = findViewById(R.id.clear);

        PaintLine.setProgress(5);
        PaintLine.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                LineNumber = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                LineNumber = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                LineNumber = seekBar.getProgress();
            }
        });

        CameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageScreenShot();
            }
        });
        DrawBtn.setOnClickListener(new View.OnClickListener() { //그리기 버튼 눌렸을때
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, R.string.draw,Toast.LENGTH_SHORT).show();
                EraserCheck = false;
            }
        });

        EraserBtn.setOnClickListener(new View.OnClickListener() { //지우개 버튼 눌렸을때
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, R.string.eraser,Toast.LENGTH_SHORT).show();
                EraserCheck = true;
            }
        });

        ClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, R.string.clear,Toast.LENGTH_SHORT).show();
                vDraw.DrawingList.clear();
                vDraw.Drawing.getPath().reset();
                vDraw.invalidate();
            }
        });
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(MainActivity.this, R.string.permit, Toast.LENGTH_SHORT).show();//메세지
        }
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, R.string.deny, Toast.LENGTH_SHORT).show();//스트링 리소스 따로관리
            MainActivity.this.finish();
        }
    };

    private void checkPermissions() {
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(R.string.deny)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    public File ScreenShot(View view){
        view.setDrawingCacheEnabled(true);//화면에 뿌릴 캐시를 사용
        Bitmap ScreenBitmap = view.getDrawingCache();//캐시를 비트맵으로 받는다

        Date DateTime = new Date();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String StringDate = transFormat.format(DateTime);
        String filename = StringDate+".png";//네이밍

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/Camera",filename);//파일 생성
        FileOutputStream fos = null; //변수명
        try{
            fos = new FileOutputStream(file);
            ScreenBitmap.compress(Bitmap.CompressFormat.PNG,90,fos);//스크린샷 화질에 따른 용량차이
            fos.close();
        }
        catch (IOException e){//실패 했을 시 그에대한 정보알림
            e.printStackTrace();//에러코드에 대한 정보
            return null;
        }
        view.setDrawingCacheEnabled(false);
        return file;
    }

    private void ImageScreenShot() {
        View rootView = getWindow().getDecorView();
        File ScreenShotImage = ScreenShot(rootView);//뷰를 file 객체에 담는다
        if(ScreenShotImage != null){
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(ScreenShotImage)));
        }
        Toast.makeText(MainActivity.this, R.string.screenshot, Toast.LENGTH_SHORT).show();
    }

    private void alertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        // 제목셋팅
        alertDialogBuilder.setTitle(R.string.end_title);
        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage(R.string.end_question)//메세지 타이틀
                .setCancelable(false)//뒤로가기 버튼 막기
                .setPositiveButton(R.string.end_success,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 프로그램을 종료한다
                                MainActivity.this.finish();
                            }
                        })
                .setNegativeButton(R.string.end_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
    }

    class MyView extends View {

        draw Drawing;
        eraser Eraser;
        ArrayList<draw> DrawingList = new ArrayList<draw>();

        public MyView(Context context)
        {
            super(context);
            Drawing = new draw();
            Eraser = new eraser();
        }

        @Override
        protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
            super.onSizeChanged(xNew, yNew, xOld, yOld);
            int width = xNew;
            int height = yNew;
        }

        @Override
        protected void onDraw(Canvas canvas) {//화면을 그려주는 메서드
            if(!EraserCheck) {
                canvas.drawPath(Drawing.getPath(), Drawing.getPaint());//그리는 선
                for (draw p : DrawingList) {//다그린 선
                    canvas.drawPath(p.getPath(), p.getPaint());
                }
            }else{
                canvas.drawRect(Eraser.getPath(),Eraser.getPaint());
                for (draw p : DrawingList) {//다그린 선
                    canvas.drawPath(p.getPath(), p.getPaint());
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN://터치시
                    TouchStart(x,y);
                    break;
                case MotionEvent.ACTION_MOVE://이동시
                    TouchMove(x,y);
                    EraserCheck(x,y);
                    break;
                case MotionEvent.ACTION_UP://손을 뗄 시
                    TouchUp();
                break;
            }
            invalidate();//화면 갱신 onDraw() 호출한다.
            return true;
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        private void TouchStart(float x, float y) {
            Drawing = null;
            Drawing = new draw(LineNumber);
            Drawing.getPath().moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void TouchMove(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                Drawing.getPath().quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }

        private void TouchUp() {
            Drawing.getPath().lineTo(mX, mY);

            if(!EraserCheck){
                DrawingList.add(Drawing);
            }
            Eraser.clearEraser();//네모가 화면에서 사라지는 부분
        }

        private void EraserCheck(float x,float y){
            if(EraserCheck) {
                Eraser.setEraser(x,y);
                int frameCount = 50;//한 선에 임의의 점 n개를 선택
                int length = DrawingList.size();
                float[] position = new float[2];
                float[] tangent = new float[2];

                try{
                    for(int i=0; i < length; i++) {
                        Path getList = DrawingList.get(i).getPath();
                        PathMeasure ArrayPathMeasure = new PathMeasure(getList, false);
                        float TotalLength = ArrayPathMeasure.getLength(); //선의 총 길이
                        float DistancePerFrame = TotalLength / frameCount; //점과 점 사이의 간격

                        for (int frame = 0; frame < frameCount; frame++) {
                            ArrayPathMeasure.getPosTan(DistancePerFrame * frame, position, tangent);//임의의 점 선정
                            PointF ArrayPointPosition = new PointF(position[0], position[1]);//임의의 점 추출
                            float Ax = ArrayPointPosition.x;
                            float Ay = ArrayPointPosition.y;

                            if (Eraser.containsPath(Ax,Ay)) {
                                DrawingList.remove(i);
                                break;
                            }
                        }
                    }
                }catch (IndexOutOfBoundsException e){
                    System.out.println(e);
                }//log처리
            }
        }

    }
}
