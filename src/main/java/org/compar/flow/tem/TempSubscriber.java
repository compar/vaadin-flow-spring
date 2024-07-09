package org.compar.flow.tem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Flow;

public class TempSubscriber implements Flow.Subscriber<TempInfo> {
    private static final Logger log = LoggerFactory.getLogger(TempSubscriber.class);
    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;  //保存一个订阅
        subscription.request(1);  //发出第一个请求
    }

    @Override
    public void onNext(TempInfo item) {
        log.info(item.toString());
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.info(throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("完成！");
    }
}
