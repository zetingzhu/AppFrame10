package com.zzt.zt_pushevent;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.CoroutineWorker;
import androidx.work.WorkerParameters;

import kotlin.coroutines.Continuation;

/**
 * @author: zeting
 * @date: 2025/4/18
 */
public class B extends CoroutineWorker {
    public B(@NonNull Context appContext, @NonNull WorkerParameters params) {
        super(appContext, params);
    }

    @Nullable
    @Override
    public Object doWork(@NonNull Continuation<? super Result> continuation) {
        return null;
    }
}
