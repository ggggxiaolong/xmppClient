package com.github.ggggxiaolong.xmpp.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;

import com.github.ggggxiaolong.xmpp.service.JobScheduleService;

import timber.log.Timber;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/**
 * Created by mrtan on 10/2/16.
 */

public final class JobUtil {

    public static boolean startConnectJob(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            JobScheduler scheduler = (JobScheduler) ObjectHolder.context.getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(CommonField.JOB_CONNECT, new ComponentName(ObjectHolder.context, JobScheduleService.class));
            /**
             setMinimumLatency(long minLatencyMillis):
             这个函数能让你设置任务的延迟执行时间(单位是毫秒),这个函数与setPeriodic(long time)方法不兼容，如果这两个方法同时调用了就会引起异常；

             setOverrideDeadline(long maxExecutionDelayMillis):
             这个方法让你可以设置任务最晚的延迟时间。如果到了规定的时间时其他条件还未满足，你的任务也会被启动。
             与setMinimumLatency(long time)一样，这个方法也会与setPeriodic(long time)，同时调用这两个方法会引发异常。

             setPersisted(boolean isPersisted):
             这个方法告诉系统当你的设备重启之后你的任务是否还要继续执行。

             setRequiredNetworkType(int networkType):
             这个方法让你这个任务只有在满足指定的网络条件时才会被执行。
             默认条件是JobInfo.NETWORK_TYPE_NONE，这意味着不管是否有网络这个任务都会被执行。
             另外两个可选类型，一种是JobInfo.NETWORK_TYPE_ANY，它表明需要任意一种网络才使得任务可以执行。
             另一种是JobInfo.NETWORK_TYPE_UNMETERED，它表示设备不是蜂窝网络( 比如在WIFI连接时 )时任务才会被执行。

             setRequiresCharging(boolean requiresCharging):
             这个方法告诉你的应用，只有当设备在充电时这个任务才会被执行。

             setRequiresDeviceIdle(boolean requiresDeviceIdle):
             这个方法告诉你的任务只有当用户没有在使用该设备且有一段时间没有使用时才会启动该任务。

             */
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);//在网络可用的情况下执行
            builder.setPersisted(true);//设备重启执行
            builder.setPeriodic(5000);//5s 执行一次

            if (scheduler.schedule(builder.build())< 0){
                //执行失败
                Timber.e("schedule connect job fail");
                return false;
            }
            return true;
        }
        return false;
    }

    public static void closeConnectJob(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            JobScheduler scheduler = (JobScheduler) ObjectHolder.context.getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.cancel(CommonField.JOB_CONNECT);
        }
    }
}
