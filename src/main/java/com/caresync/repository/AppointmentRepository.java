package com.caresync.repository;

import com.caresync.entity.Appointment;
import com.caresync.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class AppointmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Appointment save(Appointment appointment) {
        entityManager.persist(appointment);
        return appointment;
    }

    @Transactional
    public Appointment update(Appointment appointment) {
        return entityManager.merge(appointment);
    }

    public Appointment findById(Long id) {
        return entityManager.find(Appointment.class, id);
    }

    public List<Appointment> findByPatient(User patient) {
        return entityManager.createQuery(
                "SELECT a FROM Appointment a " +
                "WHERE a.patient = :patient " +
                "ORDER BY a.appointmentDate ASC, " +
                "a.appointmentTime ASC",
                Appointment.class
        )
        .setParameter("patient", patient)
        .getResultList();
    }

    @Transactional
    public void delete(Appointment appointment) {
        entityManager.remove(
                entityManager.contains(appointment)
                        ? appointment
                        : entityManager.merge(appointment)
        );
    }
}