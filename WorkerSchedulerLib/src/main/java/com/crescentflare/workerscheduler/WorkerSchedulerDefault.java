package com.crescentflare.workerscheduler;

import com.crescentflare.workerscheduler.internal.InternalWorkerItem;
import com.crescentflare.workerscheduler.internal.WorkerThread;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Worker library: default worker scheduler
 * Manages worker threads and schedules new threads for new workers being added, up to a certain limit
 */
public class WorkerSchedulerDefault implements WorkerScheduler
{
    /**
     * Members
     */
    private ArrayList<WorkerThread> threads = new ArrayList<WorkerThread>();
    private ArrayList<InternalWorkerItem> workers = new ArrayList<InternalWorkerItem>();
    private ArrayList<InternalWorkerItem> runningWorkers = new ArrayList<InternalWorkerItem>();
    private int maxThreads = 1;


    /**
     * Initialization
     */
    private static WorkerSchedulerDefault instance;
    public static WorkerSchedulerDefault getInstance()
    {
        if (instance == null)
        {
            instance = new WorkerSchedulerDefault();
        }
        return instance;
    }

    private WorkerSchedulerDefault()
    {
        maxThreads = Math.max(1, Runtime.getRuntime().availableProcessors() * 3);
    }

    /**
     * Thread management
     */
    public void addWorker(Worker worker)
    {
        addWorker(worker, null);
    }

    public void addWorker(Worker worker, WorkerCompletionListener listener)
    {
        workers.add(new InternalWorkerItem(worker, listener));
        tryWorkerStart();
    }

    private void tryWorkerStart()
    {
        Iterator<InternalWorkerItem> iterator = workers.iterator();
        while (iterator.hasNext())
        {
            final InternalWorkerItem worker = iterator.next();
            if (threads.size() < maxThreads)
            {
                final WorkerThread thread = new WorkerThread();
                iterator.remove();
                runningWorkers.add(worker);
                threads.add(thread);
                thread.runWorker(worker, new WorkerCompletionListener()
                {
                    @Override
                    public void onFinish()
                    {
                        threads.remove(thread);
                        runningWorkers.remove(worker);
                        if (worker.getDoneListener() != null)
                        {
                            worker.getDoneListener().onFinish();
                        }
                        tryWorkerStart();
                    }
                });
            }
        }
    }

    /**
     * Modify worker state
     */
    public boolean abortWorker(Worker worker)
    {
        Iterator<InternalWorkerItem> iterator = workers.iterator();
        boolean hasAborted = false;
        while (iterator.hasNext())
        {
            final InternalWorkerItem checkWorker = iterator.next();
            if (checkWorker.getWorker() == worker)
            {
                worker.onCancel();
                iterator.remove();
                hasAborted = true;
            }
        }
        for (InternalWorkerItem checkWorker : runningWorkers)
        {
            if (checkWorker.getWorker() == worker && !checkWorker.isAborted())
            {
                if (worker.onRequestAbort())
                {
                    checkWorker.setAborted(true);
                    hasAborted = true;
                }
            }
        }
        return hasAborted;
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
        return runningWorkers.size();
    }
}
