package com.jasonoh.retrofitdatawithfileex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText etName;
    EditText etMsg;
    ImageView iv;

    String imgPath;

    final int REQUEST_PERMISSION_CODE = 100;
    final int REQUEST_INTENT_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.et_name);
        etMsg = findViewById(R.id.et_msg);
        iv = findViewById(R.id.iv);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE );
        }//if

    }//onCreate method

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( (requestCode == REQUEST_PERMISSION_CODE) && (grantResults[0] == PackageManager.PERMISSION_DENIED) ) {
            Toast.makeText(this, "앱 사용이 불가합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }//if
    }//onRequestPermissionsResult method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( (requestCode==REQUEST_INTENT_CODE) && resultCode == RESULT_OK ) {
            if( data.getData() != null ) {
                Glide.with(this).load(data.getData()).into(iv);

                imgPath = getRealPathFromUri( data.getData() );

                //확인용
                new AlertDialog.Builder(this).setMessage( imgPath ).show();
            }
        }//if

    }//onActivityResult method

    //Uri -- > 절대경로로 바꿔서 리턴시켜주는 메소드
    String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return  result;
    }

    public void clickBtn(View view) {

        switch (view.getId()) {
            case R.id.btn_select :
                Toast.makeText(MainActivity.this, "SELECT", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent( Intent.ACTION_PICK );
                intent.setType( "image/*" );
                startActivityForResult( intent, REQUEST_INTENT_CODE );

                break;

            case R.id.btn_upload :
                Toast.makeText(MainActivity.this, "UPLOAD", Toast.LENGTH_SHORT).show();

                //서버에 보낼 이미지 파일 MultiPartBody.Part 객체로 생성
                File file = new File( imgPath );
                RequestBody requestBody = RequestBody.create(MediaType.parse( "image/*" ), file);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData( "img", file.getName(), requestBody );

                //서버에 보낼 텍스트 작업
                Map<String, String> dataPart = new HashMap<>();
                dataPart.put( "name", etName.getText().toString() );
                dataPart.put( "msg", etMsg.getText().toString() );

                //Retrofit 이용하기
                RetrofitServicce retrofitServicce = RetrofitHelper.getInstance().create( RetrofitServicce.class );
                Call<String> call = retrofitServicce.postDataWithFile( dataPart, filePart );
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()) {
                            new AlertDialog.Builder(MainActivity.this).setMessage( response.body() ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }//switch case

    }//clickBtn method
}//MainActivity class
