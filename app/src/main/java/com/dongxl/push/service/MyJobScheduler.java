package com.dongxl.push.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;

import java.util.List;

//public class MyJobScheduler extends JobScheduler {
//    /**
//     * 定义：安排一个Job任务。
//     * @param job
//     * @return
//     */
//    @Override
//    public int schedule(JobInfo job) {
//        return 0;
//    }
//
//    /**
//     * 定义：安排一个Job任务，但是可以将一个任务排入队列。
//     * @param job
//     * @param work
//     * @return
//     */
//    @Override
//    public int enqueue(JobInfo job, JobWorkItem work) {
//        return 0;
//    }
//
//    /**
//     * 定义：取消一个执行ID的Job。
//     * @param jobId
//     */
//    @Override
//    public void cancel(int jobId) {
//
//    }
//
//    /**
//     * 定义：取消该app所有的注册到JobScheduler里的任务。
//     */
//    @Override
//    public void cancelAll() {
//
//    }
//
//    /**
//     * 定义：获取该app所有的注册到JobScheduler里未完成的任务列表。
//     * @return
//     */
//    @Override
//    public List<JobInfo> getAllPendingJobs() {
//        return null;
//    }
//
//    /**
//     * 定义：按照ID检索获得JobScheduler里未完成的该任务的JobInfo信息。
//     * @param jobId
//     * @return
//     */
//    @Override
//    public JobInfo getPendingJob(int jobId) {
//        return null;
//    }
//}
