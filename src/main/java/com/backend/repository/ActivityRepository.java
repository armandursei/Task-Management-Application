/**
 * Spring Data JPA repository for {@link Activity} entities.
 * @author Ursei George-Armand
 * @version 5 January 2025
 */


package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.entity.Activity;

import java.util.List;


public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    /** Activities whose name or description contains the given text. */
    List<Activity> findByNameContainingOrDescriptionContaining(String name, String description);

    /** Activities currently added to the to-do list. */
    List<Activity> findByInTodoListTrue();

}
