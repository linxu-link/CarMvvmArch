package com.mvvm.hmi.full.api;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;

public class ApiResponse<T> {

    private static final String TAG = ApiResponse.class.getSimpleName();

    public static <T> ApiErrorResponse<T> create(Throwable error) {
        return new ApiErrorResponse<T>(TextUtils.isEmpty(error.getMessage()) ? "unknown error" : error.getMessage());
    }

    public static <T> ApiResponse<T> create(Response<T> response) {
        if (response.isSuccessful()) {
            T body = response.body();
            if (body == null || response.code() == 204) {
                return new ApiEmptyResponse<>();
            } else {
                return new ApiSuccessResponse<>(response.headers().get("link"), body);
            }
        } else {
            try {
                String errorMsg;
                String msg = response.errorBody().string();
                if (TextUtils.isEmpty(msg)) {
                    errorMsg = response.message();
                    if (TextUtils.isEmpty(errorMsg)) {
                        errorMsg = "unknown error";
                    }
                } else {
                    errorMsg = msg;
                }
                return new ApiErrorResponse<>(errorMsg);
            } catch (NullPointerException|IOException e) {
                Log.e(TAG, "create: " + e.toString());
                return new ApiErrorResponse<>(e.toString());
            }
        }
    }

    public static class ApiEmptyResponse<T> extends ApiResponse<T> {

    }

    public static class ApiSuccessResponse<T> extends ApiResponse<T> {

        public Map<String, String> mLinkHeader;
        public T mBody;

        public ApiSuccessResponse(String linkHeader, T body) {
            mLinkHeader = extractLinks(linkHeader);
            mBody = body;
        }

        private static final Pattern LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"");

        private static Map<String, String> extractLinks(String linkHeader) {
            Map<String, String> links = new HashMap<>();
            if (TextUtils.isEmpty(linkHeader)) {
                return links;
            }
            Matcher matcher = LINK_PATTERN.matcher(linkHeader);

            while (matcher.find()) {
                int count = matcher.groupCount();
                if (count == 2) {
                    links.put(matcher.group(2), matcher.group(1));
                }
            }
            return links;
        }

    }

    public static class ApiErrorResponse<T> extends ApiResponse<T> {

        public String mErrorMessage;

        public ApiErrorResponse(String errorMessage) {
            mErrorMessage = errorMessage;
        }
    }

}
