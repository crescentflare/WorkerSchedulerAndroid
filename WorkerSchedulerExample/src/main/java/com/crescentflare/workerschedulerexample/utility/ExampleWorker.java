package com.crescentflare.workerschedulerexample.utility;

import com.crescentflare.workerscheduler.Worker;
import com.crescentflare.workerschedulerexample.R;

/**
 * The example worker is being used to spawn in the main program and can be part of several queues
 */
public class ExampleWorker implements Worker
{
    private Thread currentThread = null;
    private ExampleLogger logger = null;

    public ExampleWorker(ExampleLogger logger)
    {
        this.logger = logger;
    }

    @Override
    public void onRun()
    {
        currentThread = Thread.currentThread();
        try
        {
            Thread.sleep((int)(2000 + Math.random() * 1000));
        }
        catch (InterruptedException ignored)
        {
        }
    }

    @Override
    public void onStart()
    {
        logger.log(System.identityHashCode(this) + logger.getContext().getString(R.string.message_log_worker_start));
    }

    @Override
    public void onFinish()
    {
        logger.log(System.identityHashCode(this) + logger.getContext().getString(R.string.message_log_worker_end));
    }

    @Override
    public void onCancel()
    {
        logger.log(System.identityHashCode(this) + logger.getContext().getString(R.string.message_log_worker_cancel));
    }

    @Override
    public boolean onRequestAbort()
    {
        if (currentThread != null)
        {
            currentThread.interrupt();
        }
        return false;
    }
}
