package com.crescentflare.workerscheduler;

import android.os.Handler;
import android.os.Looper;

import com.crescentflare.workerscheduler.internal.InternalWorkerItem;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Worker library: worker sequence
 * A queue of workers which should be run in sequence (and will be run in the same thread)
 */
public class WorkerSequence implements Worker
{
    /**
     * Members
     */
    private ArrayList<InternalWorkerItem> workers = new ArrayList<InternalWorkerItem>();
    private InternalWorkerItem runningWorker = null;
    private Handler handler = null;
    private boolean aborted = false;


    /**
     * Scheduling
     */
    public void addWorker(Worker worker)
    {
        addWorker(worker, null);
    }

    public void addWorker(Worker worker, WorkerCompletionListener doneListener)
    {
        workers.add(new InternalWorkerItem(worker, doneListener));
    }

    /**
     * Check status
     */
    public int getIdleWorkerCount()
    {
        return workers.size();
    }

    public int getRunningWorkerCount()
    {
        return runningWorker != null ? 1 : 0;
    }

    /**
     * Life cycle
     */
    @Override
    public void onRun()
    {
        boolean isInMainThread = Looper.myLooper() == Looper.getMainLooper();
        if (isInMainThread)
        {
            while (workers.size() > 0)
            {
                runningWorker = workers.get(0);
                workers.remove(0);
                runningWorker.getWorker().onStart();
                runningWorker.getWorker().onRun();
                runningWorker.getWorker().onFinish();
                runningWorker = null;
            }
        }
        else
        {
            do
            {
                //Call start in ui thread and wait until done
                final CountDownLatch initLatch = new CountDownLatch(1);
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (workers.size() > 0 && !aborted)
                        {
                            runningWorker = workers.get(0);
                            workers.remove(0);
                            runningWorker.getWorker().onStart();
                        }
                        initLatch.countDown();
                    }
                });
                try
                {
                    initLatch.await();
                }
                catch (InterruptedException ignored){ }

                //Run worker (bail out if none was fetched)
                if (runningWorker == null)
                {
                    break;
                }
                runningWorker.getWorker().onRun();

                //Call finish in ui thread and wait until done
                final CountDownLatch finishLatch = new CountDownLatch(1);
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (runningWorker.isAborted())
                        {
                            runningWorker.getWorker().onCancel();
                        }
                        else
                        {
                            runningWorker.getWorker().onFinish();
                        }
                        runningWorker = null;
                        finishLatch.countDown();
                    }
                });
                try
                {
                    finishLatch.await();
                }
                catch (InterruptedException ignored){ }
            }
            while (true);
        }
    }

    @Override
    public void onStart()
    {
        handler = new Handler();
    }

    @Override
    public void onFinish()
    {
    }

    @Override
    public void onCancel()
    {
        for (InternalWorkerItem workerItem : workers)
        {
            workerItem.getWorker().onCancel();
        }
    }

    @Override
    public boolean onRequestAbort()
    {
        aborted = true;
        if (runningWorker != null)
        {
            if (runningWorker.getWorker().onRequestAbort())
            {
                runningWorker.setAborted(true);
            }
        }
        return true;
    }
}
