package com.crescentflare.workerscheduler;

/**
 * Worker library: interface class
 * Provides an interface for the worker scheduler itself
 */
public interface WorkerScheduler
{
    void addWorker(Worker worker, WorkerCompletionListener listener);
    boolean abortWorker(Worker worker);
    int getIdleWorkerCount();
    int getRunningWorkerCount();
}
