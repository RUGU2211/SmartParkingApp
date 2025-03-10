package com.smartparking.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.smartparking.models.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND verificationOTP = :otp")
    User verifyOTP(String email, String otp);

    @Query("UPDATE users SET isEmailVerified = :verified WHERE email = :email")
    void updateEmailVerification(String email, boolean verified);

    @Query("UPDATE users SET verificationOTP = :otp, otpGeneratedTime = :time WHERE email = :email")
    void updateOTP(String email, String otp, long time);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    boolean isEmailExists(String email);
}
