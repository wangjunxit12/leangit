package com.meibanlu.driver.webservice.mappers;

/**
 * Created by Administrator on 2018/5/28.
 */

public interface Mapper<V,T> {
      V transform(T t);
}
