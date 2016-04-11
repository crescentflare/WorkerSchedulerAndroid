# WorkerSchedulerAndroid
Schedule and control the worker thread pool more easily.

For example: it's possible to create several network request queues running in parallel, but the tasks within each queue are executed one by one.

### Features
- Easily spawn threads using the default worker scheduler
- The worker scheduler has a limit on the amount of threads running at the same time based on the capabilities of the device
- If the limit prevents a thread from starting, it will be queued for later execution
- Share the same thread for multiple tasks in a worker sequence
- Spawn multiple tasks simultaneously using worker pools and get notified when all are finished
- For singleton task queues (used by queueing up network requests for example), which will be used throughout the whole application, use continuous worker queues
- Continuous worker queues can contain simple workers, worker sequences and worker pools
- Be able to customize the library by implementing your own worker scheduler

### Integration guide
When using gradle, the library can easily be imported into the build.gradle file of your project. Add the following dependency:

    compile 'com.crescentflare.workerscheduler:WorkerSchedulerLib:0.5.0'

Make sure that jcenter is added as a repository.

### Example
The provided example shows how to create workers and manage several kind of worker pools and queues. When running it, it's possible to see the state of the queues and log of workers being executed. This can be used to test the library itself.

### Status
The library should be useful in its basic form, however, there may be bugs. Improvements in features, stability and code structure are welcome.