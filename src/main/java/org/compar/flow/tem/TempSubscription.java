package org.compar.flow.tem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class TempSubscription implements Flow.Subscription {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Flow.Subscriber<? super TempInfo> subscriber;
    private final String town;
    public TempSubscription(Flow.Subscriber<? super TempInfo> subscriber, String town) {
        this.subscriber = subscriber;
        this.town = town;
    }
    @Override
    public void request(long n) {
        executor.submit(()->{  // 另起线程发送，避免栈溢出
            for (int i = 0; i < n; i++) {
                try {
                    subscriber.onNext(TempInfo.fetch(town));
                }catch (Exception e){
                    subscriber.onError(e);
                    break;
                }
            }
        });

    }

    @Override
    public void cancel() {
        subscriber.onComplete();
    }


}
