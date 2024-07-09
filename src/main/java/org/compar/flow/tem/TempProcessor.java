package org.compar.flow.tem;

import java.util.concurrent.Flow;

public class TempProcessor implements Flow.Processor<TempInfo,TempInfo> {
    private Flow.Subscriber<? super TempInfo> subscriber;

    @Override
    public void subscribe(Flow.Subscriber<? super TempInfo> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(TempInfo item) {
        final int temp = (item.getTemp() - 32) * 5 / 9; //转为摄氏度
        subscriber.onNext(new TempInfo(item.getTown(),temp));
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }
}
