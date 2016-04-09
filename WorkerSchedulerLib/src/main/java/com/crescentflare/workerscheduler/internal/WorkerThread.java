package com.crescentflare.workerscheduler.internal;

import android.os.Handler;

import com.crescentflare.workerscheduler.WorkerCompletionListener;

/**
 * Worker library: thread
 * A thread instance created by the work scheduler to process workers
 */
public class WorkerThread
{
    /**
     * Handle lifecycle
     */
    public void runWorker(final InternalWorkerItem worker, final WorkerCompletionListener doneListener)
    {
        final Handler handler = new Handler();
        worker.getWorker().onStart();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                worker.getWorker().onRun();
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (worker.isAborted())
                        {
                            worker.getWorker().onCancel();
                        }
                        else
                        {
                            worker.getWorker().onFinish();
                        }
                        doneListener.onFinish();
                    }
                });
            }
        });
        thread.start();
    }
}
