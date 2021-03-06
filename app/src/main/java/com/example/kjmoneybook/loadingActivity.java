package com.example.kjmoneybook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class loadingActivity extends AppCompatActivity implements AutoPermissionsListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AutoPermissions.Companion.loadAllPermissions(this,101);
    }// onCreate()..

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this , requestCode,permissions,this);
    }

    @Override
    public void onDenied(int i, String[] strings) {
        if(strings.length==2){
            Toast.makeText(getApplicationContext(),"권한 거부시 일부 서비스 이용에 문제가 생길수 있습니다.",Toast.LENGTH_SHORT).show();
        }
        Intent intent= new Intent(getApplicationContext(), PasswordConfirmActivity.class);
        startActivity(intent);  //Loagin화면을 띄운다.
        finish();   //현재 액티비티 종료
    }

    @Override
    public void onGranted(int i, String[] strings) {
        Intent intent= new Intent(getApplicationContext(), PasswordConfirmActivity.class);
        startActivity(intent);  //Loagin화면을 띄운다.
        finish();   //현재 액티비티 종료
    }

}// MainActivity Class..