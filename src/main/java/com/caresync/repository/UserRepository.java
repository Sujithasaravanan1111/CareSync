package com.caresync.repository;

import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public User save(User user) {
        entityManager.persist(user);
        return user;
    }

    @Transactional(readOnly = true)
public User findByEmail(String email) {
        return entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email",
                User.class
        )
        .setParameter("email", email)
        .getResultStream()
        .findFirst()
        .orElse(null);
    }

    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }
}