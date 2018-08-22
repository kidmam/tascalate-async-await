package net.tascalate.async.examples.generator;

import static net.tascalate.async.CallContext.async;
import static net.tascalate.async.CallContext.await;
import static net.tascalate.async.StandardOperations.stream;

import java.util.Date;
import java.util.StringJoiner;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import net.tascalate.async.Scheduler;
import net.tascalate.async.SchedulerProvider;
import net.tascalate.async.Sequence;
import net.tascalate.async.async;
import net.tascalate.async.xpi.TaskScheduler;
import net.tascalate.concurrent.Promise;
import net.tascalate.concurrent.Promises;

public class SimpleArgs {
    final private static AtomicLong idx = new AtomicLong(0);
    final private static ExecutorService executor = Executors.newFixedThreadPool(4, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread result = Executors.defaultThreadFactory().newThread(r);
            result.setName("ABC-ARGS_TEST" + idx.getAndIncrement());
            return result;
        }
    });

    public static void main(String[] args) {
        //final SimpleArgs example = new SimpleArgs();
        //CompletionStage<?> f = example.testArgs("ABC", Scheduler.from(executor, true));
        CompletionStage<?> f = SimpleArgs.mergeStrings("|", new TaskScheduler(executor), 10);
        f.whenComplete((r, e) -> {
            System.out.println(r);
            executor.shutdownNow();
        });
    }

    @async CompletionStage<Date> testArgs(String abs, @SchedulerProvider Scheduler scheduler) {
        Integer x = Integer.valueOf(10);
        x.hashCode();
        System.out.println(Thread.currentThread().getName());
        System.out.println(abs + " -- " + x + ", " + scheduler);
        return async(new Date());
    }

    @async
    static Promise<String> mergeStrings(String delimeter, @SchedulerProvider Scheduler scheduler, int zz) {
        StringJoiner joiner = new StringJoiner(delimeter);
        try (Sequence<String, Promise<String>> generator = Sequence.of("ABC", "XYZ").stream().map(Promises::from).convert(stream.toSequence())) {
            System.out.println("%%MergeStrings - before iterations");
            CompletionStage<String> singleResult; 
            while (null != (singleResult = generator.next())) {
                //System.out.println(">>Future is ready: " + Future.class.cast(singleResult).isDone());
                String v = await(singleResult);
                System.out.println(Thread.currentThread().getName());
                System.out.println("Received: " + v);
            }
        }

        return async(joiner.toString());
    }
}
