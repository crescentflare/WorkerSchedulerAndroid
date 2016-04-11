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
    private boolean aborted = false;


    /**
     * Initialization
     */
    public InternalWorkerItem(Worker worker, WorkerCompletionListener doneListener)
    {
        this.worker = worker;
        this.doneListener = doneListener;
    }

    public InternalWorkerItem(WorkerPool workerPool, WorkerCompletionListener doneListener)
    {
        this.workerPool = workerPool;
        this.doneListener = doneListener;
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
