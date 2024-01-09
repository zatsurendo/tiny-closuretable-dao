package com.roughandcheap.tinyclosuretabledao.commons;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

public class DbSessionJpaImpl implements DbSession {
    
    protected EntityManager entityManager;

    public DbSessionJpaImpl() {
        //
    }

    public DbSessionJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /** {@inheritDoc} */
    @Override
    public Object get(Class<?> entityClass, Serializable id) {
        return entityManager.find(entityClass, id);
    }

    /** {@inheritDoc} */
    @Transactional
    @Override
    public Object save(Object node) {
        return entityManager.merge(node);
    }

    @Transactional
    @Override
    public void persist(Object node) {
        entityManager.persist(node);
    }

    @Transactional
    @Override
    public Object merge(Object node) {
        return entityManager.merge(node);
    }

    /** {@inheritDoc} */
    @Transactional
    @Override
    public void flush() {
        entityManager.flush();
    }

    /** {@inheritDoc} */
    @Transactional
    @Override
    public void refresh(Object node) {
        entityManager.refresh(node);
    }

    /** {@inheritDoc} */
    @Transactional
    @Override
    public void delete(Object node) {
        entityManager.remove(entityManager.merge(node));
    }

    /** {@inheritDoc} */
    @Override
    public List<?> queryList(String queryText, Object[] parameters) {
        Query query = query(queryText, parameters);
        return query.getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public int queryCount(String queryText, Object[] parameters) {
        @SuppressWarnings("rawtypes")
        List result = queryList(queryText, parameters);
        return ((Number) result.get(0)).intValue();
    }

    /** {@inheritDoc} */
    @Transactional
    @Override
    public void executeUpdate(String sqlCommand, Object[] parameters) {
        Query query = query(sqlCommand, parameters);
        query .executeUpdate();
    }

    private Query query(String queryText, Object[] parameters) {
        Query query = entityManager.createQuery(queryText);
        if (parameters != null) {
            int i = 1;
            for (Object parameter : parameters) {
                if (parameter == null) {
					throw new IllegalArgumentException("Binding parameter at position "+i+" can not be null: "+queryText);
                }

                query.setParameter(i, parameter);
                i++;
            }
        }
        return query;
    }

    @Override
    public boolean contains(Object obj) {
        return entityManager.contains(obj);
    }

    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }
}
