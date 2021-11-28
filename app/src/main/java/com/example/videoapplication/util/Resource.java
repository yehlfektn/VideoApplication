package com.example.videoapplication.util;


import static com.example.videoapplication.util.Status.ERROR;
import static com.example.videoapplication.util.Status.LOADING;
import static com.example.videoapplication.util.Status.SUCCESS;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T>
 */
public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final String message;

    public final int messageId;

    public final T data;

    public Resource(@NonNull Status status, @Nullable T data, int messageId) {
        this.status = status;
        this.data = data;
        this.messageId = messageId;
        this.message = null;
    }

    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.messageId = 0;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(SUCCESS, data, null);
    }

    public static <T> Resource<T> error(@Nullable T data, String msg) {
        return new Resource<>(ERROR, data, msg);
    }

    public static <T> Resource<T> error(@Nullable T data, Integer msgId) {
        return new Resource<>(ERROR, data, msgId);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(LOADING, data, null);
    }

    public static <T> Resource<T> loading(@Nullable T data, String msg) {
        return new Resource<>(LOADING, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data, Integer msgId) {
        return new Resource<>(LOADING, data, msgId);
    }

    public String getErrorMessage(Context context) {
        if (message != null) {
            return message;
        }
        if (messageId != 0) {
            return context.getString(messageId);
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource<?> resource = (Resource<?>) o;

        if (status != resource.status) {
            return false;
        }
        if (!Objects.equals(message, resource.message)) {
            return false;
        }
        return Objects.equals(data, resource.data);
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", messageId='" + messageId + '\'' +
                ", data=" + data +
                '}';
    }
}
