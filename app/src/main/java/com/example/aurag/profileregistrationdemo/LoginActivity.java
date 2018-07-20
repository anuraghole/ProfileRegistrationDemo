package com.example.aurag.profileregistrationdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aurag.profileregistrationdemo.models.UserPojo;
import com.example.aurag.profileregistrationdemo.network.ApiClient;
import com.example.aurag.profileregistrationdemo.network.ServiseApi;
import com.example.aurag.profileregistrationdemo.session.UserSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getName();

    private EditText email, password;
    private ProgressDialog progressDialog;
    private TextInputLayout layout_email, layout_password;
    private Button bnlogin,bnReg;
    private String user_email, user_password;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        setToolBar();
        if (new UserSession(this).isUserLoggedIn()){
            Intent intent=new Intent(LoginActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        bnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    loginUser();
                }
            }
        });
        bnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setToolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("LoginActivity");
        }
    }

    private void initViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        layout_email = findViewById(R.id.layout_email);
        layout_password = findViewById(R.id.layout_password);
        bnlogin = findViewById(R.id.login);
        bnReg = findViewById(R.id.bnReg);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");

    }

    private boolean validateForm() {
        if (!validateEmail()) {
            return false;
        }
        if (!validatePassword()) {
            return false;
        }
        return true;
    }

    private boolean validateEmail() {

        String strEmail = email.getText().toString().trim();

        if (strEmail.isEmpty() || !isValidEmail(strEmail)) {
            layout_email.setError("Not valid email");
            email.requestFocus();
            email.setError(null);
            return false;
        } else {
            layout_email.setErrorEnabled(false);
        }
        return true;
    }

    private boolean isValidEmail(String email1) {

        return !TextUtils.isEmpty(email1) && android.util.Patterns.EMAIL_ADDRESS.matcher(email1).matches();
    }

    private boolean validatePassword() {
        String strPass = password.getText().toString().trim();
        if (strPass.isEmpty()) {
            layout_password.setError("Enter Password");
            password.requestFocus();
            password.setError(null);
            return false;
        } else {
            layout_password.setErrorEnabled(false);
        }
        return true;
    }


    private void loginUser() {
        user_email = email.getText().toString().trim();
        user_password = password.getText().toString().trim();


        ServiseApi serviseApi = ApiClient.getRetrofit().create(ServiseApi.class);
        Call<UserPojo> userPojoCall = serviseApi.doLogin(user_email, user_password);

        progressDialog.show();
        userPojoCall.enqueue(new Callback<UserPojo>() {
            @Override
            public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: Success");
                    UserPojo userPojo = response.body();
                    if (userPojo != null) {
                        if (userPojo.getSuccess()){

                            UserSession userSession = new UserSession(getApplicationContext());
                            userSession.createUserLoginSession(userPojo.getId());
                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, userPojo.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<UserPojo> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailure: ");
            }
        });


    }
}
