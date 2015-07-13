package com.pb.sawdust.util.concurrent;

import com.pb.sawdust.util.Range;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.*;

/**
 * Smitty is for Simple MultIThreaded Task Executor. This was a stub for testing the ForkJoin framework until that
 * framework was available.
 *
 * @author crf <br/>
 *         Started: Jun 5, 2009 4:30:47 PM
 */
@Deprecated
public class Smitty {
    private final SmittyExecutorService smitty;
    private final int smittySize;
    private final AtomicInteger taskCount = new AtomicInteger(0);

    private static final Map<Thread,Smitty> THREAD_SMITTY_MAP = new ConcurrentHashMap<Thread,Smitty>();

    static void registerSmittyToThread(Thread thread, Smitty smitty) {
        THREAD_SMITTY_MAP.put(thread,smitty);
    }
    static void registerSmittyToThread(Smitty smitty) {
        THREAD_SMITTY_MAP.put(Thread.currentThread(),smitty);
    }

    static Smitty getRegisteredSmitty() {
        return THREAD_SMITTY_MAP.get(Thread.currentThread());
    }

    public Smitty(int maxThreads) {
        //smitty = Executors.newFixedThreadPool(maxThreads);
        smitty = new SmittyExecutorService(maxThreads);
        smittySize = maxThreads;
    }

    public Smitty() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public void shutdown() {
        smitty.shutdown();
    }

    public int getTaskCount() {
        return taskCount.get();
    }

    public int getEstimatedSurplusTaskCount() {
        int surplus = taskCount.get() - smittySize;
        return surplus > 0 ? surplus : 0;
    }

    public <T> T invoke(SmittyTask<T> task) {
        //call(task);
        callInThisThread(task);
        return task.waitJoin();
    }

    <T> T callInThisThread(SmittyTask<T> task) {
        Smitty.registerSmittyToThread(Smitty.this);
        return task.invoke();
    }

    <T> Future<T> call(final SmittyTask<T> task) {
        taskCount.incrementAndGet();
        return smitty.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                Smitty.registerSmittyToThread(Smitty.this);
                T returnValue = task.invoke();
                taskCount.decrementAndGet();
                return returnValue;
            }
        });
    }

    <T> Future<T> call(final Callable<T> task) {
        taskCount.incrementAndGet();
        return smitty.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                Smitty.registerSmittyToThread(Smitty.this);
                T returnValue = task.call();
                taskCount.decrementAndGet();
                return returnValue;
            }
        });
    }

    Future<?> call(final Runnable task) {
        taskCount.incrementAndGet();
        return smitty.submit(new Runnable() {
            public void run() {
                Smitty.registerSmittyToThread(Smitty.this);
                task.run();
                taskCount.decrementAndGet();
            }
        });
    }

    boolean unCall(Future<?> taskResult) {
        return smitty.pull((FutureTask<?>) taskResult);
    }

    @Deprecated
    public static abstract class SmittyTask<T> {
        private Future<T> result;

        abstract protected Future<T> getResultFuture(Smitty smitty);

        public T invoke() {
            fork();
            return join();
        }

        public void fork() {
            result = getResultFuture(Smitty.getRegisteredSmitty());
        }

        T waitJoin() {
            try {
            while (result == null)
                Thread.sleep(10);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return join();
        }

        public T join() {
            if (result == null)
                 throw new IllegalStateException("Cannot join before spit.");
            try {
                return result.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean tryUnfork() {
            return Smitty.getRegisteredSmitty().unCall(result);
        }

        public int getEstimatedSurplusTaskCount() {
            return Smitty.getRegisteredSmitty().getEstimatedSurplusTaskCount(); 
//            Smitty smitty = Smitty.getRegisteredSmitty();
//            return smitty == null ? 0 : smitty.getEstimatedSurplusTaskCount();
        }
    }

    @Deprecated
    public static abstract class SmittyRecursiveTask<T> extends SmittyTask<T> {
        abstract protected T compute();

        protected Future<T> getResultFuture(Smitty smitty) {
            return smitty.call(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return compute();
                }
            });
        }
    }

    @Deprecated
    public static abstract class SmittyRecursiveAction extends SmittyTask<Void> {
        abstract protected void compute();

        @SuppressWarnings("unchecked")
        protected Future<Void> getResultFuture(Smitty smitty) {
            return (Future<Void>) smitty.call(new Runnable() {
                @Override
                public void run() {
                    compute();
                }
            });
        }
    }

    private class SmittyExecutorService implements ExecutorService {
        private final FutureTask<?> shutdownFuture = new FutureTask<Object>(new Callable<Object>() {
            @Override public Object call() throws Exception { return null; }
        });
        private final BlockingDeque<FutureTask<?>> tasks;
        private final Set<Thread> runningThreads;
        private final AtomicBoolean shutdown;

        SmittyExecutorService(int maxThreadCount) {
            tasks = new LinkedBlockingDeque<FutureTask<?>>();
            runningThreads = new HashSet<Thread>();
            shutdown = new AtomicBoolean(false);
            for (int i : Range.range(maxThreadCount)) {
                Thread t = new SmittyThread("Smitty thread " + (i+1));
                runningThreads.add(t);
                t.start();
            }
        }

        private class SmittyThread extends Thread {

            public SmittyThread(String name) {
                super(name);
            }

            @Override
            public void run() {
                while(true) {
                    FutureTask<?> task;
                    try {
                        task = tasks.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (task == shutdownFuture) {
                        tasks.add(task); //to repropogate poison pill
                        break;
                    }
                    task.run();
                }
                runningThreads.remove(this);
            }
        }

        private boolean pull(FutureTask<?> task) {
            return tasks.remove(task);
        }

        @Override
        public void shutdown() {
            shutdown.set(true);
            tasks.add(shutdownFuture);
        }

        @Override
        public List<Runnable> shutdownNow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            return shutdown.get();
        }

        @Override
        public boolean isTerminated() {
            return isShutdown() && runningThreads.size() == 0;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            if (shutdown.get())
                throw new RejectedExecutionException();
            FutureTask<T> ft = new FutureTask<T>(task);
            tasks.add(ft);
            return ft;
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            if (shutdown.get())
                throw new RejectedExecutionException();
            FutureTask<T> ft = new FutureTask<T>(task,result);
            tasks.add(ft);
            return ft;
        }

        @Override
        public Future<?> submit(Runnable task) {
            if (shutdown.get())
                throw new RejectedExecutionException();
            FutureTask<?> ft = new FutureTask<Object>(task,null);
            tasks.add(ft);
            return ft;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void execute(Runnable command) {
            submit(command);
        }
    }
}
