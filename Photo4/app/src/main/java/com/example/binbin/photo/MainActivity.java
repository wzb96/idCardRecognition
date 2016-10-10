package com.example.binbin.photo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


        private SurfaceView surfaceview;
        private Camera camera;
        private Button take;




        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE); // 没有标题  必须在设置布局之前找到调用
            setContentView(R.layout.activity_main);

            /*
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, // 设置全屏显示
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            */

            take = (Button)findViewById(R.id.take);

            surfaceview = (SurfaceView) findViewById(R.id.surfaceview);
            SurfaceHolder holder = surfaceview.getHolder();
            holder.setFixedSize(176, 155);// 设置分辨率
            holder.setKeepScreenOn(true);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            // SurfaceView只有当activity显示到了前台，该控件才会被创建     因此需要监听surfaceview的创建
            holder.addCallback(new MySurfaceCallback());

            //拍照按钮
            take.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    takepicture();

                }
            });


        }


    //点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //对焦
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                camera.cancelAutoFocus();
            }
        });

        return super.onTouchEvent(event);
    }

    /**
         * 监听surfaceview的创建
         * @author Administrator
         *    Surfaceview只有当activity显示到前台，该空间才会被创建
         */
        private final class MySurfaceCallback implements SurfaceHolder.Callback {

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
                // TODO Auto-generated method stub

            }



            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub

                try {
                    // 当surfaceview创建就去打开相机
                    camera = Camera.open();
                    Camera.Parameters params = camera.getParameters();
                   // Log.i("i", params.flatten());
                    params.setJpegQuality(80);  // 设置照片的质量
                    params.setPictureSize(1024, 768);
                    params.setPreviewFrameRate(5);  // 预览帧率
                    camera.setParameters(params); // 将参数设置给相机
                    //右旋90度，将预览调正
                    camera.setDisplayOrientation(90);
                    // 设置预览显示
                    camera.setPreviewDisplay(surfaceview.getHolder());
                    // 开启预览
                    camera.startPreview();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                if(camera != null){
                    camera.release();
                    camera = null;
                }
            }

        }



      //拍照的函数
        public void takepicture(){
        /*
         * shutter:快门被按下
         * raw:相机所捕获的原始数据
         * jpeg:相机处理的数据
         */
            camera.takePicture(null, null, new MyPictureCallback());
        }

        //byte转Bitmap
        public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
         }

        //bitmap转byte
        public byte[] Bitmap2Bytes(Bitmap bm) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                return baos.toByteArray();
        }

    /**
     * 将彩色图转换为黑白图
     *
     */



    //照片回调函数，其实是处理照片的
        private final class MyPictureCallback implements Camera.PictureCallback {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // TODO Auto-generated method stub
                try {

                    Bitmap bitmap = Bytes2Bimap(data);
                    Matrix m = new Matrix(); int width = bitmap.getWidth(); int height = bitmap.getHeight(); m.setRotate(90);
                    //将照片右旋90度
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m,
                            true);

                    Log.d("TAG","width "+width);
                    Log.d("TAG","height "+height);

                    //截取透明框内照片(身份证)
                    Bitmap  bitmap1 = Bitmap.createBitmap(bitmap,50,270,680,450);

                    //住址
                    Bitmap bitmap2 = Bitmap.createBitmap(bitmap1,90,225,320,125);

                    //身份证号
                    Bitmap bitmap3 = Bitmap.createBitmap(bitmap1,195,350,400,60);

                    //姓名
                    Bitmap bitmap4 = Bitmap.createBitmap(bitmap1,105,50,110,55);



                    /*
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("photo", bitmap1);
                    bundle.putParcelable("address", bitmap2);
                    bundle.putParcelable("number", bitmap3);
                    bundle.putParcelable("name", bitmap4);
                    intent.putExtra("bundle", bundle);
                    */




                    data = Bitmap2Bytes(bitmap1);
                    File file = new File(Environment.getExternalStorageDirectory(),"身份证"+".jpg");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.close();


                    data = Bitmap2Bytes(bitmap2);
                    File file2 = new File(Environment.getExternalStorageDirectory(),"住址"+".jpg");
                    FileOutputStream fos2 = new FileOutputStream(file2);
                    fos2.write(data);
                    fos2.close();

                    data = Bitmap2Bytes(bitmap3);
                    File file3 = new File(Environment.getExternalStorageDirectory(),"身份证号"+".jpg");
                    FileOutputStream fos3 = new FileOutputStream(file3);
                    fos3.write(data);
                    fos3.close();

                    data = Bitmap2Bytes(bitmap4);
                    File file4 = new File(Environment.getExternalStorageDirectory(),"姓名"+".jpg");
                    FileOutputStream fos4 = new FileOutputStream(file4);
                    fos4.write(data);
                    fos4.close();

                    Intent intent = new Intent(MainActivity.this,ResultActivit.class);
                    startActivity(intent);



                    // 在拍照的时候相机是被占用的,拍照之后需要重新预览
                    //camera.startPreview();
                    //跳到新的页面

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }

}


