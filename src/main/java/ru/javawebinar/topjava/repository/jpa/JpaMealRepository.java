package ru.javawebinar.topjava.repository.jpa;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JpaMealRepository implements MealRepository {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        if (meal == null) {
            return null;
        }
        User reference = em.getReference(User.class, userId);
        meal.setUser(reference);
        if (meal.isNew()) {
            em.persist(meal);
        } else if (get(meal.id(), userId) == null) {
            return null;
        }
        return em.merge(meal);
    }
    
    @Transactional
    @Override
    public boolean delete(int id, int userId) {
        return em.createNamedQuery("Delete.Meal")
                       .setParameter("id", id)
                       .setParameter("user_id", userId)
                       .executeUpdate() != 0;
    }
    
    @Override
    public Meal get(int id, int userId) {
        Meal meal = em.find(Meal.class, id);
        return meal !=null && em.find(User.class, userId).id() == userId ? meal: null;
    }
    
    @Override
    public List<Meal> getAll(int userId) {
        return em.createQuery("SELECT m FROM Meal m WHERE m.user.id = :user_id", Meal.class).setParameter("user_id", userId).getResultList();
    }
    
    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
      return em.createNamedQuery("Get.Between", Meal.class)
                .setParameter("startDate", startDateTime)
                .setParameter("endDate", endDateTime)
                .setParameter("user_id", userId)
                .getResultList();
    }
}