package org.compar.flow.tem;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.IntStream;


public class Main {



    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    public static void main(String[] args) {


        getTemperatures("New York")
                .subscribe(new TempSubscriber());


    }


    //TODO ...
    private static Flow.Publisher<TempInfo> getTemperatures(String town){
        return  subscriber ->{
            var processor = new TempProcessor();
            processor.subscribe(subscriber);
            subscriber.onSubscribe(new TempSubscription(processor,town) );
        };
    }

}
