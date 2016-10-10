package com.example.binbin.photo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

import static android.os.Environment.getExternalStorageDirectory;

public class ResultActivit extends AppCompatActivity {

    Bitmap photo;
    Bitmap number;
    Bitmap address;
    Bitmap name;
    ImageView imageView;
    EditText nameText;
    EditText addressText;
    EditText numberText;
    String nameResult,addressResult,numberResult;
    ProgressDialog dialog;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        handler=new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                dialog.cancel();

                addressText.setText(addressResult);
                nameText.setText(nameResult);
                numberText.setText(numberResult);



            }
        };


        photo = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/身份证"+".jpg");
        imageView = (ImageView)findViewById(R.id.photo);
        imageView.setImageBitmap(photo);

        addressText = (EditText)findViewById(R.id.address);
        nameText = (EditText)findViewById(R.id.name);
        numberText = (EditText)findViewById(R.id.number);

        dialog = new ProgressDialog(this);
        dialog.setMessage("识别中");
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                number = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/身份证号"+".jpg");
                name = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/姓名"+".jpg");
                address = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/住址"+".jpg");

                numberResult = doOcr(number,"/chi_sim");
                nameResult = doOcr(name,"/chi_sim");
                addressResult = doOcr(address,"/chi_sim");

                if(numberResult!=null&&nameResult!=null&&addressResult!=null){

                    Message msg = new Message();
                    handler.sendMessage(msg);

                }






            }
        }).start();










    }

    /**
     * 进行图片识别
     *
     * @param bitmap
     *            待识别图片
     * @param language
     *            识别语言
     * @return 识别结果字符串
     */
    public String doOcr(Bitmap bitmap, String language) {
        TessBaseAPI baseApi = new TessBaseAPI();

        baseApi.init(getSDPath(), language);

        // 必须加此行，tess-two要求BMP必须为此配置
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        baseApi.setImage(bitmap);

        String text = baseApi.getUTF8Text();

        baseApi.clear();
        baseApi.end();

        return text;
    }



    /**
     * 获取sd卡的路径
     *
     * @return 路径的字符串
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取外存目录
        }
        return sdDir.toString();
    }




}
