package com.aispeech.aios.adapter.localScanService.db;

import java.util.List;

/**
 * Created by Jervis on 2015/12/3.
 */
public interface IDAO<T> {

    void add(T t);

    boolean isExist(T t);

    void delete(T t);

    List<T> findAll();

    T findById(long id);

    void deleteAll();
}
