package com.crescentflare.workerscheduler;

/**
 * Worker library: interface class
 * Provides an interface for the worker scheduler to pick up workers and handle callbacks
 */
public interface Worker
{
    void onRun();
    void onStart();
    void onFinish();
    void onCancel();
    boolean onRequestAbort();
}
