package com.example.fbclone.data.remote;

import android.util.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

public class ApiError {

    public static class ErrorMessage {
        public String message;
        public int status;

        public ErrorMessage(String message, int status) {
            this.message = message;
            this.status = status;
        }
    }

    public static ErrorMessage getErrorFromThrowable(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return new ErrorMessage(throwable.getMessage(), ((HttpException) throwable).code());
        } else if (throwable instanceof SocketTimeoutException) {
            return new ErrorMessage("Time out", 0);
        } else if (throwable instanceof IOException) {
            if (throwable instanceof MalformedJsonException) {
                return new ErrorMessage("MalformedJsonException json from Server", 0);
            } else if (throwable instanceof ConnectException) {
                return new ErrorMessage(throwable.getMessage() + " Your xampp is not running or\n You have different IP address", 0);
            } else {
                return new ErrorMessage("No internet connection", 0);
            }
        } else {
            return new ErrorMessage("Unknown", 0);
        }
    }
    public static ErrorMessage getErrorFromException(Exception e){
        return new ErrorMessage(e.getMessage(), e.hashCode());
    }

}
