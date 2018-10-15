package com.home.jobschedulerdemo;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean onPlaying = true, isStartScheduleJob = false;
    private TextView timeTiltTextView;
    private TimeCountingAsyncTask timeCountingAsyncTask;
    private ComponentName componentName;
    private JobInfo jobInfo;
    private JobScheduler jobScheduler;
    private Button scheduleJobButton, cancelJobButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializationView();
        initializeButtonAndListen();
        initializeAndExecuteAsyncTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onPlaying = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPlaying = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onPlaying = false;
        timeCountingAsyncTask.cancel(true);
    }

    private void initializationView() {
        timeTiltTextView = findViewById(R.id.timeTiltTextView);
    }

    private void initializeButtonAndListen() {
        scheduleJobButton = findViewById(R.id.scheduleJobButton);
        scheduleJobButton.setOnClickListener(initializationOnClickListener());
        cancelJobButton = findViewById(R.id.cancelJobButton);
        cancelJobButton.setOnClickListener(initializationOnClickListener());
    }

    @NonNull
    private View.OnClickListener initializationOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.scheduleJobButton:
                        componentName = new ComponentName(MainActivity.this, ExampleJobService.class);  // 打开另外一个应用中的Activity或者服务
                        jobInfo = new JobInfo.Builder(123, componentName)  // 指定哪个JobService执行操作
                                .setRequiresCharging(false)  // 不會因為设备沒有在充电, 这个任务就沒有執行
                                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)  // 表示设备不是蜂窝网络(比如在WIFI连接时)时任务才会被执行
                                .setPersisted(true)  // 告诉系统当你的设备重启之后你的任务是否还要继续执行
                                .setPeriodic(15 * 60 * 1000)  // 每隔15分鐘运行一次
                                .build();
                        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);  // 取得JobScheduler實例
                        int resultCode = jobScheduler.schedule(jobInfo);
                        if (resultCode == JobScheduler.RESULT_SUCCESS) {
                            Log.d(TAG, "Job scheduled");
                            isStartScheduleJob = true;
                        } else {
                            Log.d(TAG, "Job scheduling failed");
                        }
                        break;
                    case R.id.cancelJobButton:
                        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);  // 取得JobScheduler實例
                        jobScheduler.cancel(123);  // 取消jobId指定的JobInfo任務
                        Log.d(TAG, "Job cancelled");
                        isStartScheduleJob = false;
                        break;
                }
            }
        };
    }

    private class TimeCountingAsyncTask extends AsyncTask<String, Integer, String> {

        /**
         * 这个方法会在后台任务开始执行之间调用, 在主线程执行, 用于进行一些界面上的初始化操作, 比如显示一个进度条对话框等
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 更新UI
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        /**
         * 执行耗时操作
         */
        @Override
        protected String doInBackground(String... params) {
            while (true) {
                Log.d(TAG, "doInBackground");
                if (isCancelled()) {
                    break;
                }
                if (onPlaying) {
                    Log.d(TAG, "doInBackground, onPlaying...");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeTiltTextView.setText("00:" + JSDApplication.currentNumber);
                        }
                    });
                    if (isStartScheduleJob == true) {
                        scheduleJobButton.setClickable(false);
                    } else {
                        scheduleJobButton.setClickable(true);
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "finished";
        }

        /**
         * doInBackground结束后执行本方法, result是doInBackground方法返回的数据
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void initializeAndExecuteAsyncTask() {
        timeCountingAsyncTask = new TimeCountingAsyncTask();
        timeCountingAsyncTask.execute();
    }
}
