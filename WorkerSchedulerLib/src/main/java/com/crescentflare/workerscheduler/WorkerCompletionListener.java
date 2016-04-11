package com.crescentflare.workerscheduler;

/**
 * Worker library: completion listener class
 * Used for receiving a callback when a worker has finished
 */
public interface WorkerCompletionListener
{
    void onFinish();
}
