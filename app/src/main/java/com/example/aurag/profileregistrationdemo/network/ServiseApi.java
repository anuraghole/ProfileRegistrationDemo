package com.example.aurag.profileregistrationdemo.network;

import com.example.aurag.profileregistrationdemo.models.UserPojo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ServiseApi {

    @FormUrlEncoded
    @POST("project1/Webservices/registration.php")
    Call<UserPojo> registerUser(@Field("Name") String name,
                                @Field("Email") String email,
                                @Field("Mobile") String mob,
                                @Field("DOB") String dob,
                                @Field("Gender") String gender,
                                @Field("Password") String pwd);


    @FormUrlEncoded
    @POST("project1/Webservices/login.php")
    Call<UserPojo> doLogin(@Field("Email") String user_email,
                           @Field("Password") String user_password);


    @FormUrlEncoded
    @POST("project1/Webservices/profile.php")
    Call<UserPojo> getUserProfile(@Field("Id") String id);


    @FormUrlEncoded
    @POST("project1/Webservices/updateProfile.php")
    Call<UserPojo> updateUser(@Field("Id") String id,
                              @Field("Name") String name,
                              @Field("Email") String email,
                              @Field("Mobile") String mobile,
                              @Field("Gender") String gender,
                              @Field("DOB") String dob,
                              @Field("Password") String password
                              );

}

