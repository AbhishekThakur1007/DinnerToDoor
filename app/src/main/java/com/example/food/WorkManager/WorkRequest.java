package com.example.food.WorkManager;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class WorkRequest {

    public WorkRequest(){}

    public void Start(){

        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(worker.class,5, TimeUnit.SECONDS)
                .setConstraints(constraints).build();

        WorkManager.getInstance().enqueue(periodicWorkRequest);
    }

}
