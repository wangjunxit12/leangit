package com.meibanlu.driver.tool;


import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.Observable;

public class RxBus {
   private  volatile static RxBus instance;
   private final Relay<Object> mBus;

   private RxBus(){
       this.mBus= PublishRelay.create().toSerialized();
   }
   public static RxBus getInstance() {
       if (instance==null){
           synchronized (RxBus.class){
               if(instance==null){
                   instance=new RxBus();
               }
           }
       }
       return instance;
   }

   public void post(Object o){
       mBus.accept(o);
   }

   public <T> Observable<T> tObservable(Class<T> tClass){
       return mBus.ofType(tClass);
   }

   public Observable<Object> toObservable(){
       return mBus;
   }
   public void delete() {

   }
   public boolean hasObservers(){
       return mBus.hasObservers();
   }
}
