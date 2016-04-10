package com.crescentflare.workerscheduler;

import com.crescentflare.workerscheduler.internal.InternalWorkerItem;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Worker library: continuous worker queue
 * A queue of workers which run in sequence (one at a time), meant for long lifecycle use (like network request queues)
 */
public class ContinuousWorkerQueue
{
    /**
     * Members
     */
    private ArrayList<StatusListener>     statusListeners = new ArrayList<>();
    private ArrayList<InternalWorkerItem> workers         = new ArrayList<>();
    private InternalWorkerItem            runningWorker   = null;
    private boolean                       paused          = false;


    /**
     * Scheduling
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
        for (StatusListener statusListener : statusListeners)
        {
            statusListener.onAddWorker(worker);
        }
        tryStartWorker();
    }

    public void addWorkerPool(WorkerPool workerPool, WorkerCompletionListener listener)
    {
        workers.add(new InternalWorkerItem(workerPool, listener, false));
        for (StatusListener statusListener : statusListeners)
        {
            statusListener.onAddWorkerPool(workerPool);
        }
        tryStartWorker();
    }

    private boolean tryStartWorker()
    {
        if (!paused && runningWorker == null && workers.size() > 0)
        {
            runningWorker = workers.get(0);
            workers.remove(0);
            if (runningWorker.getWorkerPool() != null)
            {
                for (StatusListener statusListener : statusListeners)
                {
                    statusListener.onStartWorkerPool(runningWorker.getWorkerPool());
                }
                runningWorker.getWorkerPool().schedule(new WorkerCompletionListener()
                {
                    @Override
                    public void onFinish()
                    {
                        finishedWorker(runningWorker);
                    }
                });
            }
            else
            {
                for (StatusListener statusListener : statusListeners)
                {
                    statusListener.onStartWorker(runningWorker.getWorker());
                }
                WorkerSchedulerDefault.getInstance().addWorker(runningWorker.getWorker(), new WorkerCompletionListener()
                {
                    @Override
                    public void onFinish()
                    {
                        finishedWorker(runningWorker);
                    }
                }, runningWorker.isThreaded());
            }
        }
        return false;
    }

    /**
     * Pause/resume
     */
    public void pause()
    {
        paused = true;
    }

    public void resume()
    {
        paused = false;
        tryStartWorker();
    }

    /**
     * Cancellation
     */
    public void cancelAll()
    {
        for (InternalWorkerItem worker : workers)
        {
            if (worker.getWorkerPool() != null)
            {
            }
            else
            {
                for (StatusListener statusListener : statusListeners)
                {
                    statusListener.onCancelWorker(worker.getWorker());
                }
                worker.getWorker().onCancel();
            }
        }
        if (runningWorker != null && !runningWorker.isAborted())
        {
            if (runningWorker.getWorkerPool() != null)
            {
            }
            else
            {
                if (WorkerSchedulerDefault.getInstance().abortWorker(runningWorker.getWorker()))
                {
                    runningWorker.setAborted(true);
                }
            }
        }
        workers.clear();
    }

    public void cancelForClass(Class checkClass)
    {
        Iterator<InternalWorkerItem> i = workers.iterator();
        while (i.hasNext())
        {
            InternalWorkerItem testWorker = i.next();
            if (testWorker.getWorkerPool() != null)
            {
            }
            else
            {
                if (testWorker.getWorker().getClass() == checkClass)
                {
                    testWorker.getWorker().onCancel();
                    for (StatusListener statusListener : statusListeners)
                    {
                        statusListener.onCancelWorker(testWorker.getWorker());
                    }
                    i.remove();
                }
            }
        }
        if (runningWorker != null && runningWorker.getWorker().getClass() == checkClass && !runningWorker.isAborted())
        {
            if (runningWorker.getWorkerPool() != null)
            {
            }
            else
            {
                if (WorkerSchedulerDefault.getInstance().abortWorker(runningWorker.getWorker()))
                {
                    runningWorker.setAborted(true);
                }
            }
        }
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
        return runningWorker != null ? 1 : 0;
    }

    public boolean isEmpty()
    {
        return workers.isEmpty() && runningWorker == null;
    }

    /**
     * Listener handling
     */
    private void finishedWorker(InternalWorkerItem worker)
    {
        finish();
        for (StatusListener statusListener : statusListeners)
        {
            if (worker.getWorkerPool() != null)
            {
                if (worker.isAborted())
                {
                    statusListener.onCancelWorkerPool(worker.getWorkerPool());
                }
                else
                {
                    statusListener.onCompleteWorkerPool(worker.getWorkerPool());
                }
            }
            else
            {
                if (worker.isAborted())
                {
                    statusListener.onCancelWorker(worker.getWorker());
                }
                else
                {
                    statusListener.onCompleteWorker(worker.getWorker());
                }
            }
        }
    }

    private void finish()
    {
        if (runningWorker != null)
        {
            if (runningWorker.getDoneListener() != null)
            {
                runningWorker.getDoneListener().onFinish();
            }
            runningWorker = null;
        }
        boolean startResult = tryStartWorker();
        if (!startResult && !paused && runningWorker == null && workers.size() == 0)
        {
            for (StatusListener statusListener : statusListeners)
            {
                statusListener.onEmpty();
            }
        }
    }

    public void addStatusListener(StatusListener listener)
    {
        if (!statusListeners.contains(listener))
        {
            statusListeners.add(listener);
        }
    }

    public void removeStatusListener(StatusListener listener)
    {
        statusListeners.remove(listener);
    }

    public interface StatusListener
    {
        void onAddWorker(Worker worker);
        void onAddWorkerPool(WorkerPool worker);
        void onStartWorker(Worker worker);
        void onStartWorkerPool(WorkerPool worker);
        void onCancelWorker(Worker worker);
        void onCancelWorkerPool(WorkerPool worker);
        void onCompleteWorker(Worker worker);
        void onCompleteWorkerPool(WorkerPool worker);
        void onEmpty();
    }
}
