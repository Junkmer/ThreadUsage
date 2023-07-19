package com.junker.thread.usage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button mThreadPoolExecuteBtn1, mThreadPoolExecuteBtn2, mThreadPoolExecuteBtn3;
    private Button mFixedThreadPoolBtn1;
    private Button mSingleThreadPoolBtn1;
    private Button mCachedThreadPoolBtn1;
    private Button mScheduledThreadPoolBtn1;

    private int threadNumber;
    /**
     * ThreadPoolExecutor
     * (
     * int corePoolSize,                    //核心线程数，只要 allowCoreThreadTimeOut 不为ture，核心线程会一直存在，不会被回收
     * int maximumPoolSize,                 //最大线程数，活动线程数量超过该值时，后续任务线程会排队
     * long keepAliveTime,                  //超时时间，当非核心线程不处理任务，闲置时间超过该值，会被回收（如果 allowCoreThreadTimeOut 为true，核心线程闲置也会被回收）
     * TimeUnit unit,                       //keepAliveTime的超时时间单位，枚举类型
     * BlockingQueue<Runnable> workQueue,   //缓冲队列（堵塞队列），线程池的execute方法会将Runnable对象存储起来
     * ThreadFactory threadFactory          //线程工厂接口，只有一个new Thread(Runnable r)方法，可为线程池创建新线程
     * )
     *
     * 堵塞队列有：
     * ArrayBlockingQueue           ：一个由数组结构组成的有界阻塞队列。
     * LinkedBlockingQueue          ：一个由链表结构组成的有界阻塞队列。
     * PriorityBlockingQueue        ：一个支持优先级排序的无界阻塞队列。
     * DelayQueue                   ：一个使用优先级队列实现的无界阻塞队列。
     * SynchronousQueue             ：一个不存储元素的阻塞队列。
     * LinkedTransferQueue          ：一个由链表结构组成的无界阻塞队列。
     * LinkedBlockingDeque          ：一个由链表结构组成的双向阻塞队列。
     *
     * 线程池一般用法：
     * 1.shutDown()                             //关闭线程池，不影响已经提交的任务
     * 2.shutDownNow()                          //关闭线程池，并尝试去终止正在执行的线程
     * 3.allowCoreThreadTimeOut(boolean value)  //允许核心线程闲置超时时被回收
     * 4.submit                                 //一般情况下我们使用execute来提交任务，但是有时候可能也会用到submit，使用submit的好处是submit有返回值。
     * 5.beforeExecute()                        //任务执行前执行的方法
     * 6.afterExecute()                         //任务执行结束后执行的方法
     * 7.terminated()                           //线程池关闭后执行的方法
     */
    private ThreadPoolExecutor threadPoolExecutor;
    private ExecutorService fixedThreadPool;
    private ExecutorService singleThreadPool;
    private ExecutorService cachedThreadPool;
    private ScheduledExecutorService scheduledThreadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mThreadPoolExecuteBtn1 = findViewById(R.id.thread_pool_execute_btn1);
        mThreadPoolExecuteBtn2 = findViewById(R.id.thread_pool_execute_btn2);
        mThreadPoolExecuteBtn3 = findViewById(R.id.thread_pool_execute_btn3);
        mThreadPoolExecuteBtn1.setOnClickListener(this);
        mThreadPoolExecuteBtn2.setOnClickListener(this);
        mThreadPoolExecuteBtn3.setOnClickListener(this);

        mFixedThreadPoolBtn1 = findViewById(R.id.fixed_thread_pool_btn1);
        mFixedThreadPoolBtn1.setOnClickListener(this);

        mSingleThreadPoolBtn1 = findViewById(R.id.single_thread_pool_btn1);
        mSingleThreadPoolBtn1.setOnClickListener(this);

        mCachedThreadPoolBtn1 = findViewById(R.id.cached_thread_pool_btn1);
        mCachedThreadPoolBtn1.setOnClickListener(this);

        mScheduledThreadPoolBtn1 = findViewById(R.id.scheduled_thread_pool_btn1);
        mScheduledThreadPoolBtn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        threadNumber = 0;
        if (v.getId() == R.id.thread_pool_execute_btn1) {
            createThreadPool_1();
        } else if (v.getId() == R.id.thread_pool_execute_btn2) {
            createThreadPool_2();
        } else if (v.getId() == R.id.thread_pool_execute_btn3) {
            createThreadPool_3();
        } else if (v.getId() == R.id.fixed_thread_pool_btn1) {
            createFixedThreadPool_1();
        } else if (v.getId() == R.id.single_thread_pool_btn1) {
            createSingleThreadPool_1();
        } else if (v.getId() == R.id.cached_thread_pool_btn1) {
            createCachedThreadPool_1();
        } else if (v.getId() == R.id.scheduled_thread_pool_btn1) {
            createScheduledThreadPool_1();
        }
    }

    private void createThreadPool_1() {
        threadPoolExecutor = new ThreadPoolExecutor(3, 5, 1,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));
        for (int i = 0; i < 30; i++) {
            int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Log.d(TAG, "Thread, run: " + finalI);
                        Log.d(TAG, "当前线程：" + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            threadPoolExecutor.execute(runnable);
        }

        /**
         *  TODO:
         *      此实例中，创建的线程池 核心线程为 3个，线程总数为5个，队列数为 100
         *      需要运行的任务为 30个，大于核心线程数，小于 队列数；因此 30 - 3 = 27 个剩余的任务被放入 workQueue队列中等待执行
         */
    }

    private void createThreadPool_2() {
        threadPoolExecutor = new ThreadPoolExecutor(3, 5, 1,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(25));
        for (int i = 0; i < 30; i++) {
            int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Log.d(TAG, "Thread, run: " + finalI);
                        Log.d(TAG, "当前线程：" + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            threadPoolExecutor.execute(runnable);
        }

        /**
         *  TODO:
         *      此实例中，创建的线程池 核心线程为 3个，线程总数为 6个，队列数为 25个
         *      需要运行的任务为 30个，大于 核心线程数+队列数，小于 线程总数+队列数；因此会临时创建 30 - (25+3) = 2个非核心线程加入任务行列
         *      核心线程数+队列数（28） < 当前任务数（30） < （31）线程总数+队列数，非核心线程 会先执行 超出“核心线程数+队列数”的剩余任务（29、30）
         */
    }

    private void createThreadPool_3() {
        threadPoolExecutor = new ThreadPoolExecutor(3, 5, 1,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(20));
        for (int i = 0; i < 28; i++) {
            int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Log.d(TAG, "Thread, run: " + finalI);
                        Log.d(TAG, "当前线程：" + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            threadPoolExecutor.execute(runnable);
        }

        /**
         *  TODO:
         *      此实例中，创建的线程池 核心线程为 3个，非核心线程为 5个，队列数为 20个
         *      需要运行的任务为 30个 由于大于 线程总数+队列数，因此程序拒绝执行该任务，采取饱和策略，并抛出RejectedExecutionException异常。
         */
    }

    private void createFixedThreadPool_1() {
//        fixedThreadPool = Executors.newFixedThreadPool(2);//方式一
        fixedThreadPool = Executors.newFixedThreadPool(2, factory);//方式二
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Log.d(TAG, "Thread, run: " + finalI);
                        Log.d(TAG, "当前线程名：" + Thread.currentThread().getName() + "|线程ID：" + Thread.currentThread().getId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            fixedThreadPool.execute(runnable);
        }

        /**
         *  TODO:
         *      FixedThreadPool 特点：参数为核心线程数，只有核心线程，无非核心线程，堵塞队列数量无限制
         */
    }

    private void createSingleThreadPool_1() {
//        singleThreadPool = Executors.newSingleThreadExecutor();//方式一
        singleThreadPool = Executors.newSingleThreadExecutor(factory);//方式二
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Log.d(TAG, "Thread, run: " + finalI);
                        Log.d(TAG, "当前线程名：" + Thread.currentThread().getName() + "|线程ID：" + Thread.currentThread().getId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            singleThreadPool.execute(runnable);
        }

        /**
         *  TODO:
         *      SingleThreadPool 特点：只有一个核心线程，确保所有任务都在同一线程中按顺序执行。因此不需要考虑线程同步问题。
         */
    }

    private void createCachedThreadPool_1() {
//        cachedThreadPool = Executors.newCachedThreadPool();//方式一
        cachedThreadPool = Executors.newCachedThreadPool(factory);//方式二
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Log.d(TAG, "Thread, run: " + finalI);
                        Log.d(TAG, "当前线程名：" + Thread.currentThread().getName() + "|线程ID：" + Thread.currentThread().getId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            cachedThreadPool.execute(runnable);
        }

        /**
         *  TODO:
         *      CachedThreadPool 特点：只有非核心线程，没有核心线程，且非核心线程无上限，所有线程都活动时，
         *      会为新任务创建新线程，否则利用空闲线程（60s空闲时间，过了就会被回收，所以线程池中有0个线程的可能）处理任务。
         */
    }

    private void createScheduledThreadPool_1() {
        scheduledThreadPool = Executors.newScheduledThreadPool(5);//方式一
//        scheduledThreadPool = Executors.newScheduledThreadPool(5, factory);//方式二

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Log.d(TAG, "Thread, run: " + finalI);
                        Log.d(TAG, "当前线程名：" + Thread.currentThread().getName() + "|线程ID：" + Thread.currentThread().getId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
//        scheduledThreadPool.schedule(runnable, 5, TimeUnit.SECONDS);//延迟自动任务
//        scheduledThreadPool.scheduleAtFixedRate(runnable, 5, 1, TimeUnit.SECONDS);//延迟5s后启动，每1S执行一次
            scheduledThreadPool.scheduleWithFixedDelay(runnable, 1, 1, TimeUnit.SECONDS);//启动后第一次延迟5s执行，后面延迟1s执行
        }

//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "当前线程名：" + Thread.currentThread().getName() + "|线程ID：" + Thread.currentThread().getId());
//            }
//        };
////        scheduledThreadPool.schedule(runnable, 5, TimeUnit.SECONDS);//延迟自动任务
////        scheduledThreadPool.scheduleAtFixedRate(runnable, 5, 1, TimeUnit.SECONDS);//延迟5s后启动，每1S执行一次
//        scheduledThreadPool.scheduleWithFixedDelay(runnable, 5, 1, TimeUnit.SECONDS);//启动后第一次延迟5s执行，后面延迟1s执行
        /**
         *  TODO:
         *      ScheduledThreadPool 特点：唯一一个有延时执行和周期重复执行的线程池，核心线程数固定，非核心线程数无上限且无超时时间
         */
    }

    private final ThreadFactory factory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "custom—thread—name_" + (threadNumber++));
        }
    };

}