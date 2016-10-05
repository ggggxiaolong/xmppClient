package com.github.ggggxiaolong.xmpp.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.support.annotation.RequiresApi;

import com.github.ggggxiaolong.xmpp.utils.XMPPUtil;

import timber.log.Timber;

/**
 * 当与服务器的连接断开时开始执行这个任务，
 * 1. 这个任务的执行条件为联网
 * 2. * 如果与服务器建立了连接则不在执行（onStartJob 返回false）
 * * 否则循环执行，直到建立连接
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class JobScheduleService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        //如果返回值是false,系统假设这个方法返回时任务已经执行完毕。
        // 如果返回值是true,那么系统假定这个任务正要被执行，执行任务的重担就落在了你的肩上。
        // 当任务执行完毕时你需要调用jobFinished()来通知系统
        if (XMPPUtil.isConnected()) {
            return false;
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(1, params));
            return true;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private HandlerThread mThread = new HandlerThread("jobThread", Process.THREAD_PRIORITY_BACKGROUND);

    private Handler mHandler = new Handler(mThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    try {
                        XMPPUtil.connect(null);
                        XMPPUtil.login();
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                    //当任务执行完毕之后，你需要调用jobFinished()来让系统知道这个任务已经结束，
                    // 系统可以将下一个任务添加到队列中。如果你没有调用jobFinished()，
                    // 你的任务只会执行一次，而应用中的其他任务就不会被执行
                    jobFinished((JobParameters) msg.obj, true);
                    break;
                }
            }
        }
    };
}
