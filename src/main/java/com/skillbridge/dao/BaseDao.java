package com.skillbridge.dao;

import java.util.List;
import com.skillbridge.exception.DatabaseException;

// Generic Base DAO interface demonstrating abstraction and collections
public interface BaseDao<T> {

    boolean insert(T entity) throws DatabaseException;

    T findById(int id) throws DatabaseException;

    List<T> findAll() throws DatabaseException;

    boolean update(T entity) throws DatabaseException;

    boolean delete(int id) throws DatabaseException;
}
