package threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : [zqwzh]
 * @version : [v1.0]
 * @createTime : [2021-09-14 16:51]
 */
public class PauseableThreadPool extends ThreadPoolExecutor {
    private boolean isPaused;
    private ReentrantLock lock = new ReentrantLock();
    private Condition unpaused = lock.newCondition();

    public PauseableThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public PauseableThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public PauseableThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public PauseableThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        lock.lock();
        try {
            while (isPaused){
                unpaused.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

    /**
     * ????????????
     */
    private void pause() {
        lock.lock();
        try{
            isPaused = true;
        }finally {
            lock.unlock();
        }

    }

    /**
     * ??????????????????
     */
    public void resume() {
        lock.lock();
        try{
            isPaused = false;
            unpaused.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PauseableThreadPool pauseableThreadPool = new PauseableThreadPool(8,
                16, 10l, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                System.out.println("????????????");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        for (int i = 0; i < 1000; i++) {
            pauseableThreadPool.execute(runnable);
        }

        Thread.sleep(1500);
        System.out.println("???????????????");
        pauseableThreadPool.pause();
        Thread.sleep(1500);
        System.out.println("???????????????");
        pauseableThreadPool.resume();

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, //???????????????
                10, // ???????????????
                1,//??????????????????
                TimeUnit.SECONDS,//?????????????????????
                new LinkedBlockingQueue<>(),//????????????
                new AbortPolicy() //????????????
        );
    }
}
