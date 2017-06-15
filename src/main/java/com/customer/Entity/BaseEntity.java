package com.customer.Entity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class BaseEntity<T, PK> {

	protected Class<T> type;

	
	private static ThreadLocal<EntityManager> em = new ThreadLocal<>();
    
	public BaseEntity() {

	}

	public BaseEntity(Class<T> type) {
		this.type = type;
	}

	public static EntityManager getEntityManager() {
		return em.get();
	}

    /**
     * Simpler entity manager access method
     * @return
     */
    public static EntityManager em(){
        return getEntityManager();
    }

    /**
     * Method used to inject the active entity manager so it gets stored on a thread local variable accessible to all the domain entities
     * @param entityManager
     */
    public static void setEntityManager(EntityManager entityManager){
        if (entityManager != null) {
            em.set(entityManager);
        }
    }
    
	@SuppressWarnings("unchecked")
	public T persist() {
		em().persist(this);
        return (T) this;
	}


	public void delete() {
		em().remove(this);
	}

    public static Object getSingleResultOrNull(Query query){
        try {
            return query.getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    public static void markQueryReadOnly(Query q){
        q.setHint("org.hibernate.readOnly", Boolean.TRUE);
    }

}
