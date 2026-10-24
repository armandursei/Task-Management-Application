/**
 * Tests for the Spring Boot context, the Activity entity, the service and the repository.
 * Runs against the configured datasource (MySQL by default).
 * @author Ursei George-Armand
 * @version 10 December 2024
 */


package com.backend;

import com.backend.entity.Activity;
import com.backend.enums.ActivityStatus;
import com.backend.repository.ActivityRepository;
import com.backend.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testGetSetId() {
		Activity activity = new Activity();
		activity.setId(34);
		assertThat(activity.getId()).isEqualTo(34);
	}

	@Test
	void testGetSetName() {
		Activity activity = new Activity();
		activity.setName("Test Name");
		assertThat(activity.getName()).isEqualTo("Test Name");
	}

	@Test
	void testGetSetDescription() {
		Activity activity = new Activity();
		activity.setDescription("Test Description");
		assertThat(activity.getDescription()).isEqualTo("Test Description");
	}

	@Test
	void testGetSetStartTime() {
		Activity activity = new Activity();
		activity.setStartTime("12:30");
		assertThat(activity.getStartTime()).isEqualTo("12:30");
	}

	@Test
	void testGetSetDuration() {
		Activity activity = new Activity();
		activity.setDuration("90");
		assertThat(activity.getDuration()).isEqualTo("90");
	}

	@Test
	void testGetSetInTodoList() {
		Activity activity = new Activity();
		activity.setInTodoList(true);
		assertThat(activity.isInTodoList()).isTrue();
	}

	@Test
	void testGetSetStatus() {
		Activity activity = new Activity();
		activity.setStatus(ActivityStatus.IN_PROGRESS);
		assertThat(activity.getStatus()).isEqualTo(ActivityStatus.IN_PROGRESS);
	}

	@Test
	void testGetSetProgress() {
		Activity activity = new Activity();
		activity.setProgress("50");
		assertThat(activity.getProgress()).isEqualTo("50");
	}

	@Autowired
	private ActivityService activityService;

	@Test
	void testSaveActivity() {
		Activity activity = new Activity();
		activity.setName("Test Activity");
		activity.setDescription("Test Description");
		activity.setStartTime("10:00");
		activity.setDuration("60");
		activityService.saveActivity(activity);

		List<Activity> activities = activityService.getActivities();
		assertTrue(activities.stream().anyMatch(a -> a.getName().equals("Test Activity")));
	}

	@Autowired
	private ActivityRepository activityRepository;

	@Test
	void testFindByNameContaining() {
		Activity activity = new Activity();
		activity.setName("Test Name");
		activity.setDescription("Description");
		activity.setStartTime("08:00");
		activity.setDuration("120");
		activityRepository.save(activity);

		List<Activity> found = activityRepository.findByNameContainingOrDescriptionContaining("Test", "");
		assertFalse(found.isEmpty());
	}

	@Test
	void testEqualsMethod() {
		Activity activity1 = new Activity();
		activity1.setName("Test");
		activity1.setDescription("Description");
		activity1.setStartTime("12:00");
		activity1.setDuration("60");

		Activity activity2 = new Activity();
		activity2.setName("Test");
		activity2.setDescription("Description");
		activity2.setStartTime("12:00");
		activity2.setDuration("60");

		assertEquals(activity1, activity2); // same fields -> equal
	}


}
