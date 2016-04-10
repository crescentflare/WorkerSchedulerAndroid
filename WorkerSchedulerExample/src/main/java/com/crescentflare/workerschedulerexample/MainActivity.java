package com.crescentflare.workerschedulerexample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crescentflare.workerscheduler.ContinuousWorkerQueue;
import com.crescentflare.workerscheduler.WorkerCompletionListener;
import com.crescentflare.workerscheduler.WorkerPool;
import com.crescentflare.workerscheduler.WorkerSchedulerDefault;
import com.crescentflare.workerscheduler.WorkerSequence;
import com.crescentflare.workerschedulerexample.utility.ExampleLogger;
import com.crescentflare.workerschedulerexample.utility.ExampleWorker;


/**
 * The example activity provides a test suite for spawning workers in several queues
 */
public class MainActivity extends AppCompatActivity
{
    private ContinuousWorkerQueue continuousPool = new ContinuousWorkerQueue();
    private WorkerPool parallelPool = new WorkerPool();
    private WorkerSequence serialPool = new WorkerSequence();
    private ExampleLogger logger = null;
    private boolean serialPoolScheduled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Set content view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logger = new ExampleLogger((LinearLayout)findViewById(R.id.activity_main_worker_log));

        //Spawn thread buttons
        findViewById(R.id.activity_main_scheduler_spawn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                WorkerSchedulerDefault.getInstance().addWorker(new ExampleWorker(logger));
            }
        });
        findViewById(R.id.activity_main_continuous_pool_spawn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                continuousPool.addWorker(new ExampleWorker(logger));
            }
        });
        findViewById(R.id.activity_main_parallel_pool_spawn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                parallelPool.addWorker(new ExampleWorker(logger));
                parallelPool.schedule(null);
            }
        });
        findViewById(R.id.activity_main_serial_pool_spawn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                serialPool.addWorker(new ExampleWorker(logger));
                if (!serialPoolScheduled)
                {
                    serialPoolScheduled = true;
                    WorkerSchedulerDefault.getInstance().addWorker(serialPool, new WorkerCompletionListener()
                    {
                        @Override
                        public void onFinish()
                        {
                            serialPoolScheduled = false;
                        }
                    });
                }
            }
        });

        //Initialize other controls
        logger.log(getString(R.string.message_log_started));
        updateQueueStatuses();
    }

    private void updateQueueStatuses()
    {
        ((TextView)findViewById(R.id.activity_main_scheduler_idle)).setText("" + WorkerSchedulerDefault.getInstance().getIdleWorkerCount());
        ((TextView)findViewById(R.id.activity_main_scheduler_run)).setText("" + WorkerSchedulerDefault.getInstance().getRunningWorkerCount());
        ((TextView)findViewById(R.id.activity_main_continuous_pool_idle)).setText("" + continuousPool.getIdleWorkerCount());
        ((TextView)findViewById(R.id.activity_main_continuous_pool_run)).setText("" + continuousPool.getRunningWorkerCount());
        ((TextView)findViewById(R.id.activity_main_parallel_pool_idle)).setText("" + parallelPool.getIdleWorkerCount());
        ((TextView)findViewById(R.id.activity_main_parallel_pool_run)).setText("" + parallelPool.getRunningWorkerCount());
        ((TextView)findViewById(R.id.activity_main_serial_pool_idle)).setText("" + serialPool.getIdleWorkerCount());
        ((TextView)findViewById(R.id.activity_main_serial_pool_run)).setText("" + serialPool.getRunningWorkerCount());
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                updateQueueStatuses();
            }
        }, 200);
    }
}
