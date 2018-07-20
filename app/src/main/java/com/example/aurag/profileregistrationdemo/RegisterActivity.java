package com.example.aurag.profileregistrationdemo;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.aurag.profileregistrationdemo.models.UserPojo;
import com.example.aurag.profileregistrationdemo.network.ApiClient;
import com.example.aurag.profileregistrationdemo.network.ServiseApi;
import com.example.aurag.profileregistrationdemo.session.UserSession;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = RegisterActivity.class.getName();

    private EditText name, email, mobile, password, dob;
    private RadioGroup rgGender;
    private RadioButton rbGender;
    private Button register, bnUpdate,bnLogin;
    private String user_name, user_email, user_mobile, user_dob, user_gender, user_password;
    private ProgressDialog progressDialog;

    private TextInputLayout layout_name, layout_email, layout_mobile, layout_password, layout_dob;

    private Toolbar toolbar;
    private Calendar c;
    private int year, month, day;

    boolean editable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        setToolBar();

        if (editable){
            if (new UserSession(this).isUserLoggedIn()){
                Intent intent=new Intent(this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        }

        if (getIntent().getExtras() != null) {
            editable = getIntent().getExtras().getBoolean("update");
            if (editable) {
                setToolBar();
                bnUpdate.setVisibility(View.VISIBLE);
                register.setVisibility(View.GONE);
                bnLogin.setVisibility(View.GONE);
                fetchUser();
            }

        }

        bnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    updateUser();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    registerUser();
                }
            }
        });
        bnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                DatePickerDialog dd = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                try {
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    String dateInString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                    Date date = formatter.parse(dateInString);
                                    dob.setText(formatter.format(date).toString());

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }, year, month, day);

                dd.getDatePicker().setMaxDate(System.currentTimeMillis());
                dd.show();
            }
        });
    }

    private void setToolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (editable) {
                getSupportActionBar().setTitle("Update Profile");

            } else {

                getSupportActionBar().setTitle("RegisterProfile");
            }
        }
    }

    private void fetchUser() {
        progressDialog.show();
        UserSession userSession = new UserSession(this);
        HashMap<String, String> usermap = userSession.getUserDetails();
        String id = usermap.get(UserSession.KEY_ID);

        ServiseApi serviseApi = ApiClient.getRetrofit().create(ServiseApi.class);
        Call<UserPojo> userPojoCall = serviseApi.getUserProfile(id);

        //Asyncronous method call because using userpojocall.enqueue()
        userPojoCall.enqueue(new Callback<UserPojo>() {
            @Override
            public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    UserPojo userPojo = response.body();

                    if (userPojo != null) {
                        if (userPojo.getSuccess()) {
                            name.setText(userPojo.getName());
                            email.setText(userPojo.getEmail());
                            mobile.setText(userPojo.getMobile());
                            if (userPojo.getGender().equals("Male")) {
                                RadioButton radioButton = findViewById(R.id.genderm);
                                radioButton.setChecked(true);
                            } else {
                                RadioButton radioButton = findViewById(R.id.genderf);
                                radioButton.setChecked(true);
                            }
                            dob.setText(userPojo.getDOB());
                            password.setText(userPojo.getPassword());

                        }else {
                            Toast.makeText(RegisterActivity.this, userPojo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserPojo> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updateUser() {
        getData();

        UserSession userSession = new UserSession(this);
        HashMap<String, String> umap = userSession.getUserDetails();
        String id = umap.get(UserSession.KEY_ID);

        ServiseApi serviseApi = ApiClient.getRetrofit().create(ServiseApi.class);
        Call<UserPojo> userPojoCall = serviseApi.updateUser(id,user_name,
                user_email,user_mobile,user_gender,user_dob,user_password);
        progressDialog.show();

        //Asyncronous method call because using userpojocall.enqueue() no need to call on seprate thread
        userPojoCall.enqueue(new Callback<UserPojo>() {
            @Override
            public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: Success");

                    UserPojo userPojo = response.body();

                    if (userPojo != null) {
                        if (userPojo.getSuccess()) {
                            Toast.makeText(RegisterActivity.this, userPojo.getMessage(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(RegisterActivity.this, userPojo.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<UserPojo> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailure: ");
            }
        });


    }

    private void getData() {
        user_name = name.getText().toString().trim();
        user_mobile = mobile.getText().toString().trim();
        user_email = email.getText().toString().trim();
        user_dob = dob.getText().toString().trim();
        user_gender = getGender();
        user_password = password.getText().toString().trim();
    }

    private void registerUser() {
        getData();

        ServiseApi serviseApi = ApiClient.getRetrofit().create(ServiseApi.class);
        Call<UserPojo> userPojoCall = serviseApi.registerUser(user_name, user_email, user_mobile,
                user_dob, user_gender, user_password);

        progressDialog.show();

        //Asyncronous method call because using userpojocall.enqueue() no need to call on seprate thread
        userPojoCall.enqueue(new Callback<UserPojo>() {
            @Override
            public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: Success");

                    UserPojo userPojo = response.body();

                    if (userPojo != null) {
                        if (userPojo.getSuccess()) {
                            Toast.makeText(RegisterActivity.this, userPojo.getMessage(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, userPojo.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<UserPojo> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailure: ");
            }
        });


    }


    private void initViews() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        dob = findViewById(R.id.dob);
        password = findViewById(R.id.password);

        register = findViewById(R.id.register);
        bnUpdate = findViewById(R.id.bnupdate);
        bnLogin=findViewById(R.id.bnlogin);

        layout_name = findViewById(R.id.layout_name);
        layout_email = findViewById(R.id.layout_email);
        layout_mobile = findViewById(R.id.layout_mobile);
        layout_password = findViewById(R.id.layout_password);
        layout_dob = findViewById(R.id.layout_dob);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");

        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


    }

    private boolean validateForm() {
        if (!validateName()) {
            return false;
        }
        if (!validateEmail()) {
            return false;
        }

        if (!validateMobile()) {
            return false;
        }
        if (!validateDob()) {
            return false;
        }

        if (!validatePassword()) {
            return false;
        }
        return true;
    }

    private boolean validateName() {

        String sname = name.getText().toString().trim();

        if (sname.isEmpty() || !sname.matches("[a-zA-Z ]+")) {
            layout_name.setError("Not valid name");
            name.requestFocus();
            name.setError(null);
            return false;
        } else {
            layout_name.setErrorEnabled(false);
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


    private boolean validateMobile() {

        String mob = mobile.getText().toString().trim();
        if (mob.isEmpty() || !mob.matches("[7-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]")) {
            layout_mobile.setError("Not Valid mobile");
            mobile.requestFocus();
            mobile.setError(null);
            return false;
        } else {
            layout_mobile.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDob() {
        String strDob = dob.getText().toString().trim();
        if (strDob.isEmpty()) {
            dob.setError("Enter Password");
            dob.requestFocus();
            dob.setError(null);
            return false;
        } else {
            layout_dob.setErrorEnabled(false);
        }
        return true;
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

    public String getGender() {
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        int genderId = rgGender.getCheckedRadioButtonId();
        rbGender = (RadioButton) findViewById(genderId);
        return rbGender.getText().toString().trim();
    }

}
