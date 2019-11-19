package com.dongxl.push.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import com.dongxl.pushdeme.utils.LogUtils;

/**
 *
 * https://www.jianshu.com/p/8f9090e12015
 *
 * jobFinished();
 * 定义：Job的任务执行完毕后，APP端自己的调用，用以通知JobScheduler已经完成了任务。
 * 注意:该方法执行完后不会回调onStopJob(),但是会回调onDestroy()
 */
public class MyJobService extends JobService {
    private static final String TAG = MyJobService.class.getSimpleName();
    private int mJobId = 0x00200;
    private final long requestInterval = 5 * 60 * 1000;//间隔多久调用一次
    private Context mContext;

    /**
     * 定义：Service被初始化后的回调。
     * 作用：可以在这里设置BroadcastReceiver或者ContentObserver
     */
    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        LogUtils.i(TAG, "==onCreate==");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleJob(getJobInfo());
        LogUtils.i(TAG, "==onStartCommand==");
        return START_NOT_STICKY;
    }

    /**
     * 定义：Job开始的时候的回调，实现实际的工作逻辑。
     * 注意：如果返回false的话，系统会自动结束本job；
     *
     * @param params
     * @return
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        new MonitorAsyncTask().execute(params);
        LogUtils.i(TAG, "==onStartJob==");
        return true;
    }


    /**
     * 定义：停止该Job。当JobScheduler发觉该Job条件不满足的时候，或者job被抢占被取消的时候的强制回调。
     * 注意:如果想在这种意外的情况下让Job重新开始，返回值应该设置为true。
     *
     * @param params
     * @return
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        scheduleJob(getJobInfo());
        LogUtils.i(TAG, "==onStopJob==");
        return false;
    }

    /**
     * 定义：Service被销毁前的回调。
     * 作用：可以在这里注销BroadcastReceiver或者ContentObserver
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "==onDestroy==");
        Intent intent = new Intent();
        intent.setClass(this, MyJobService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
    }


    //将任务作业发送到作业调度中去
    public void scheduleJob(JobInfo t) {
        JobScheduler tm = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tm = getSystemService(JobScheduler.class);
        } else {
            tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
        if (tm != null) {
            tm.cancel(mJobId);
            tm.schedule(t);
        }
    }


    public JobInfo getJobInfo() {

        //代表一个任务 使用建造者模式建造
        JobInfo jobInfo;
        // Android7上,设置周期执行时间，会强制按照getMinPeriodMills阈值执行，此时设置任务执行最小时间间隔解决该问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(mJobId, new ComponentName(this, MyJobService.class))
                    .setBackoffCriteria(requestInterval, JobInfo.BACKOFF_POLICY_LINEAR)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setMinimumLatency(requestInterval)
                    .setOverrideDeadline(requestInterval)
                    .setPersisted(true)
                    .build();
        } else {
            jobInfo = new JobInfo.Builder(mJobId, new ComponentName(this, MyJobService.class))
                    .setBackoffCriteria(requestInterval, JobInfo.BACKOFF_POLICY_LINEAR)//设置线性重试策略
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) //设置网络类型
                    .setPeriodic(requestInterval) //设置执行周期
                    .setPersisted(true)//设置重启后任务是否保留
                    .build();
        }
        return jobInfo;
    }

    //AsyncTask处理耗时操作
    private class MonitorAsyncTask extends AsyncTask<JobParameters, Void, String> {
        private JobParameters mJobParameters;

        /**
         * @param jobParameters The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(JobParameters... jobParameters) {
            mJobParameters = jobParameters[0];
            // ..... 耗时操作
            return null;
        }

        //完成之后的处理工作
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //如果不调用此方法，任务只会执行一次
                jobFinished(mJobParameters, true);
            } else {
                //如果不调用此方法，任务只会执行一次
                jobFinished(mJobParameters, false);
            }

        }
    }
}
