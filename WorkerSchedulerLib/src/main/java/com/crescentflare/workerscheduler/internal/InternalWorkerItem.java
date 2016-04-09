package com.crescentflare.workerscheduler.internal;

import com.crescentflare.workerscheduler.Worker;
import com.crescentflare.workerscheduler.WorkerCompletionListener;
import com.crescentflare.workerscheduler.WorkerPool;

/**
 * Worker library: used for internal worker storage with properties for pools, schedulers and queues
 */
public class InternalWorkerItem
{
    /**
     * Members
     */
    private Worker worker = null;
    private WorkerPool workerPool = null;
    private WorkerCompletionListener doneListener = null;
    private boolean threaded = false;
    private boolean aborted = false;


    /**
     * Initialization
     */
    public InternalWorkerItem(Worker worker, WorkerCompletionListener doneListener, boolean threaded)
    {
        this.worker = worker;
        this.doneListener = doneListener;
        this.threaded = threaded;
    }

    public InternalWorkerItem(WorkerPool workerPool, WorkerCompletionListener doneListener, boolean threaded)
    {
        this.workerPool = workerPool;
        this.doneListener = doneListener;
        this.threaded = threaded;
    }

    /**
     * Simple getters
     */
    public Worker getWorker()
    {
        return worker;
    }

    public WorkerPool getWorkerPool()
    {
        return workerPool;
    }

    public WorkerCompletionListener getDoneListener()
    {
        return doneListener;
    }

    public boolean isThreaded()
    {
        return threaded;
    }

    /**
     * Adjust state during runtime
     */
    public boolean isAborted()
    {
        return aborted;
    }

    public void setAborted(boolean aborted)
    {
        this.aborted = aborted;
    }
}
