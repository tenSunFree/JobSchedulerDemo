package com.home.jobschedulerdemo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class ExampleJobService extends JobService {

    private static final String TAG = "ExampleJobService";
    public boolean jobCancelled = false;

    /**
     * 返回true, 表示该工作耗时, 同时工作处理完成后需要调用onStopJob销毁(jobFinished)
     * 返回false, 任务运行不需要很长时间, 到return时已完成任务处理
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started");
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(final JobParameters jobParameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 61; i++) {
                    if (i < 10) {
                        JSDApplication.currentNumber = "0" + i;
                    } else if (i == 60) {
                        JSDApplication.currentNumber = "00";
                    } else {
                        JSDApplication.currentNumber = "" + i;
                    }
                    if (jobCancelled) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                jobFinished(jobParameters, false);                                 // 告知系统, 该任务已经处理完成
            }
        }).start();
    }

    /**
     * 有且仅有onStartJob返回值为true时, 才会调用onStopJob来销毁job
     * 返回false来销毁这个工作
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
