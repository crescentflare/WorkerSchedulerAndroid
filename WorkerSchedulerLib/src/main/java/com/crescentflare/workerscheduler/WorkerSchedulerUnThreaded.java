package com.crescentflare.workerscheduler;

import com.crescentflare.workerscheduler.internal.InternalWorkerItem;
import com.crescentflare.workerscheduler.internal.WorkerThread;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Worker library: non-threaded worker scheduler
 * Directly executes scheduled workers on the current thread
 */
public class WorkerSchedulerUnThreaded implements WorkerScheduler
{
    /**
     * Members
     */
    private ArrayList<WorkerThread> threads = new ArrayList<WorkerThread>();
    private ArrayList<InternalWorkerItem> workers = new ArrayList<InternalWorkerItem>();
    private ArrayList<InternalWorkerItem> runningWorkers = new ArrayList<InternalWorkerItem>();


    /**
     * Initialization
     */
    private static WorkerSchedulerUnThreaded instance;
    public static WorkerSchedulerUnThreaded getInstance()
    {
        if (instance == null)
        {
            instance = new WorkerSchedulerUnThreaded();
        }
        return instance;
    }

    /**
     * Implementation
     */
    public void addWorker(Worker worker)
    {
        addWorker(worker, null);
    }

    public void addWorker(Worker worker, WorkerCompletionListener listener)
    {
        worker.onStart();
        worker.onRun();
        worker.onFinish();
        if (listener != null)
        {
            listener.onFinish();
        }
    }

    public boolean abortWorker(Worker worker)
    {
        return false; //Workers can't be stopped if they are not run inside a thread
    }

    /**
     * Check status
     */
    public int getIdleWorkerCount()
    {
        return 0; //Workers are executed immediately, there can't be any idle ones
    }

    public int getRunningWorkerCount()
    {
        return 0; //A running worker blocks the main thread before this function has the time to execute
    }
}
