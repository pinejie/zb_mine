package zibo.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerUtils {
//    public static void main(String[] args) throws Exception {
//        timerOfScheduledExecutorService();
//    }

    /**
     * 第一种方法：设定指定任务task在指定时间time执行 schedule(TimerTask task, Date time)
     * 只执行一次，且线程不关闭，多线程不安全
     */
    public static void timerOfTimerOne() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("-------设定要指定任务--------");
            }
        }, 2000);
    }

    /**
     * 第二种方法：设定指定任务task在指定延迟delay后进行固定延迟peroid的执行 循环执行
     */
    public static void timerOfTimerTwo() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("-------设定要指定任务--------");
            }
        }, 500, 1000);
    }

    /**
     * 第三种方法：设定指定任务task在指定开始时间后进行固定频率peroid的执行。
     */
    public static void timerOfTimerThree() {
        Date time = getFixDate(); // 得出执行任务的时间,此处为今天的12：00：00
        new Timer().scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println("-------设定要指定任务--------");
            }
        }, time, 1000);
    }

    /**
     * 第四种方法：使用线程实现定时
     */
    public static void timerOfThread() {
        final long timeInterval = 1000;
        final Date time = getFixDate(); // 得出执行任务的时间,此处为今天的12：00：00
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(time.getTime() - System.currentTimeMillis());
                    while (true) {
                        System.out.println("Hello !!");
                        Thread.sleep(timeInterval);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * ScheduledExecutorService是从Java SE5的java.util.concurrent里，做为并发工具类被引进的，这是最理想的定时任务实现方式。
     * 相比于上两个方法，它有以下好处：
     * 1>相比于Timer的单线程，它是通过线程池的方式来执行任务的
     * 2>可以很灵活的去设定第一次执行任务delay时间
     * 3>提供了良好的约定，以便设定执行的时间间隔
     */
    public static void timerOfScheduledExecutorService() {
        Runnable runnable = new Runnable() {
            public void run() {
                System.out.println("Hello !!");
                try {
                    throw new RuntimeException("fuck,chushile");
                } catch (Exception e) {
                }
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        System.out.println("开始...");
        service.scheduleAtFixedRate(runnable, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * <获取一个固定时间>
     *
     * @return
     */
    public static Date getFixDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 14); // 控制时
        calendar.set(Calendar.MINUTE, 42); // 控制分
        calendar.set(Calendar.SECOND, 0); // 控制秒
        return calendar.getTime();
    }
}
