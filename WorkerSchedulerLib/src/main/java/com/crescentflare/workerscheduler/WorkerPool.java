package com.crescentflare.workerscheduler;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Worker library: worker pool
 * A pool of workers which can run in parallel
 */
public class WorkerPool
{
    /**
     * Members
     */
    private ArrayList<Worker> workers = new ArrayList<>();
    private ArrayList<Worker> scheduledWorkers = new ArrayList<>();
    private WorkerCompletionListener completionListener = null;


    /**
     * Scheduling
     */
    public void addWorker(Worker worker)
    {
        workers.add(worker);
    }

    public void schedule(WorkerCompletionListener doneListener)
    {
        completionListener = doneListener;
        final Iterator<Worker> iterator = workers.iterator();
        while (iterator.hasNext())
        {
            final Worker worker = iterator.next();
            scheduledWorkers.add(worker);
            iterator.remove();
            WorkerScheduler.getInstance().addWorker(worker, new WorkerCompletionListener()
            {
                @Override
                public void onFinish()
                {
                    scheduledWorkers.remove(worker);
                    checkFullFinish();
                }
            });
        }
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
        return scheduledWorkers.size();
    }

    /**
     * Trigger listener when empty
     */
    public void checkFullFinish()
    {
        if (scheduledWorkers.size() == 0 && completionListener != null)
        {
            completionListener.onFinish();
        }
    }
}
