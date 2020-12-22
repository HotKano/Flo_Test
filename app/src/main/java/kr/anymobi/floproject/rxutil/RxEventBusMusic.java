package kr.anymobi.floproject.rxutil;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxEventBusMusic {
    private static RxEventBusMusic mInstance;
    private final PublishSubject<Object> mData;

    private RxEventBusMusic() {
        mData = PublishSubject.create();
    }

    public static RxEventBusMusic getInstance() {
        if (mInstance == null) {
            mInstance = new RxEventBusMusic();
        }

        return mInstance;
    }

    public void sendData(Object data) {
        mData.onNext(data);
    }

    public Observable<Object> getObjectObservable() {
        return mData;
    }

}
