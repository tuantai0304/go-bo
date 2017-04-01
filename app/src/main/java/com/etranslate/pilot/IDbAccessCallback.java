package com.etranslate.pilot;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;


/**
 * Created by TuanTai on 1/04/2017.
 */

public interface IDbAccessCallback {
    public void beforeAccess();
    public void AfterAccess(@NonNull Task<Void> task);
}
