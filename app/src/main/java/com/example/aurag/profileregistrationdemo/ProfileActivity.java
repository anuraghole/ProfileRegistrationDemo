package com.example.aurag.profileregistrationdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aurag.profileregistrationdemo.models.UserPojo;
import com.example.aurag.profileregistrationdemo.network.ApiClient;
import com.example.aurag.profileregistrationdemo.network.ServiseApi;
import com.example.aurag.profileregistrationdemo.session.UserSession;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private  TextView name,email,mobile,gender,dob;
    private  ProgressDialog progressDialog;
    private Button update,bnLogout;
    private  Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initViews();
        setToolBar();
        fetchPofile();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,RegisterActivity.class);
                intent.putExtra("update",true);
                startActivity(intent);
                finish();
            }
        });

        bnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSession userSession=new UserSession(ProfileActivity.this);
                userSession.logoutUser();
                finish();
            }
        });


    }
    private void setToolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }
    }


    private void fetchPofile() {
        progressDialog.show();
        UserSession userSession=new UserSession(this);
        HashMap<String,String> usermap=userSession.getUserDetails();
        String id=usermap.get(UserSession.KEY_ID);

        ServiseApi serviseApi= ApiClient.getRetrofit().create(ServiseApi.class);
        Call<UserPojo> userPojoCall=serviseApi.getUserProfile(id);

        //Asyncronous method call because using userpojocall.enqueue() no need to call on seprate thread
        userPojoCall.enqueue(new Callback<UserPojo>() {
            @Override
            public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    UserPojo userPojo=response.body();

                    if (userPojo != null) {
                        name.setText(userPojo.getName());
                        email.setText(userPojo.getEmail());
                        mobile.setText(userPojo.getMobile());
                        gender.setText(userPojo.getGender());
                        dob.setText(userPojo.getDOB());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserPojo> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initViews() {
        name=findViewById(R.id.tvname);
        mobile=findViewById(R.id.tvmobile);
        dob=findViewById(R.id.tvdob);
        email=findViewById(R.id.tvemail);
        gender=findViewById(R.id.tvgender);
        update=findViewById(R.id.bnUpdate);
        bnLogout=findViewById(R.id.bnLogout);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
    }


}
