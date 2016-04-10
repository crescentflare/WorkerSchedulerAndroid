package com.crescentflare.workerscheduler;

import com.crescentflare.workerscheduler.internal.InternalWorkerItem;
import com.crescentflare.workerscheduler.internal.WorkerThread;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Worker library: default worker scheduler
 * Manages worker threads and schedules new threads for new workers being added, to a certain limit
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
        addWorker(worker, listener, true);
    }

    public void addWorker(Worker worker, boolean threaded)
    {
        addWorker(worker, null, threaded);
    }

    public void addWorker(Worker worker, WorkerCompletionListener listener, boolean threaded)
    {
        workers.add(new InternalWorkerItem(worker, listener, threaded));
        tryWorkerStart();
    }

    private void tryWorkerStart()
    {
        //First start non-threaded workers
        Iterator<InternalWorkerItem> iterator = workers.iterator();
        while (iterator.hasNext())
        {
            final InternalWorkerItem worker = iterator.next();
            if (!worker.isThreaded())
            {
                iterator.remove();
                runningWorkers.add(worker);
                worker.getWorker().onStart();
                worker.getWorker().onRun();
                if (worker.isAborted())
                {
                    worker.getWorker().onCancel();
                }
                else
                {
                    worker.getWorker().onFinish();
                }
                runningWorkers.remove(worker);
                if (worker.getDoneListener() != null)
                {
                    worker.getDoneListener().onFinish();
                }
            }
        }

        //Defer threaded workers to threads
        iterator = workers.iterator();
        while (iterator.hasNext())
        {
            final InternalWorkerItem worker = iterator.next();
            if (worker.isThreaded())
            {
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
