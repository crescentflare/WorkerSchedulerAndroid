package com.crescentflare.workerscheduler;

/**
 * Worker library: default threaded worker interface implementation
 * Convenience class to easily create threaded workers
 */
public abstract class SimpleWorker implements Worker
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onFinish()
    {
    }

    @Override
    public void onCancel()
    {
    }

    @Override
    public boolean onRequestAbort()
    {
        return false;
    }

    public void start()
    {
        start(WorkerSchedulerDefault.getInstance());
    }

    public void start(WorkerScheduler scheduler)
    {
        scheduler.addWorker(this, null);
    }
}
