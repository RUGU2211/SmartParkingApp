package com.smartparking.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String email;
    private String password;
    private String name;
    private boolean isEmailVerified;
    private String verificationOTP;
    private long otpGeneratedTime;

    public User(@NonNull String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.isEmailVerified = false;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public String getVerificationOTP() {
        return verificationOTP;
    }

    public void setVerificationOTP(String verificationOTP) {
        this.verificationOTP = verificationOTP;
    }

    public long getOtpGeneratedTime() {
        return otpGeneratedTime;
    }

    public void setOtpGeneratedTime(long otpGeneratedTime) {
        this.otpGeneratedTime = otpGeneratedTime;
    }
}
