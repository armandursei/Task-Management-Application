/**
 * Clasa pentru testarea funcționalităților de bază ale aplicației.
 * Include teste pentru verificarea încărcării contextului Spring Boot.
 * @author Ursei George-Armand
 * @version 10 Decembrie 2024
 */


package com.backend;

import com.backend.entity.Activitati;
import com.backend.enums.StatusActivitate;
import com.backend.repository.RepositoryActivitati;
import com.backend.service.ServiceActivitati;
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
	void testGetSetIdActivitati() {
		Activitati activitate = new Activitati();
		activitate.setIdActivitati(34);
		assertThat(activitate.getIdActivitati()).isEqualTo(34);
	}

	@Test
	void testGetSetNume() {
		Activitati activitate = new Activitati();
		activitate.setNume("Nume Test");
		assertThat(activitate.getNume()).isEqualTo("Nume Test");
	}

	@Test
	void testGetSetDescriere() {
		Activitati activitate = new Activitati();
		activitate.setDescriere("Descriere Test");
		assertThat(activitate.getDescriere()).isEqualTo("Descriere Test");
	}

	@Test
	void testGetSetOraInceput() {
		Activitati activitate = new Activitati();
		activitate.setOraInceput("12:30");
		assertThat(activitate.getOraInceput()).isEqualTo("12:30");
	}

	@Test
	void testGetSetDurata() {
		Activitati activitate = new Activitati();
		activitate.setDurata("90");
		assertThat(activitate.getDurata()).isEqualTo("90");
	}

	@Test
	void testGetSetDeFacut() {
		Activitati activitate = new Activitati();
		activitate.setDeFacut(true);
		assertThat(activitate.isDeFacut()).isTrue();
	}

	@Test
	void testGetSetStatus() {
		Activitati activitate = new Activitati();
		activitate.setStatus(StatusActivitate.in_progres);
		assertThat(activitate.getStatus()).isEqualTo(StatusActivitate.in_progres);
	}

	@Test
	void testGetSetProgres() {
		Activitati activitate = new Activitati();
		activitate.setProgres("50");
		assertThat(activitate.getProgres()).isEqualTo("50");
	}

	@Autowired
	private ServiceActivitati serviceActivitati;

	@Test
	void testSaveActivitate() {
		Activitati activitate = new Activitati();
		activitate.setNume("Activitate Test");
		activitate.setDescriere("Descriere Test");
		activitate.setOraInceput("10:00");
		activitate.setDurata("60");
		serviceActivitati.saveActivitate(activitate);

		List<Activitati> activitati = serviceActivitati.getActivitati();
		assertTrue(activitati.stream().anyMatch(a -> a.getNume().equals("Activitate Test")));
	}

	@Autowired
	private RepositoryActivitati repositoryActivitati;

	@Test
	void testFindByNumeContaining() {
		Activitati activitate = new Activitati();
		activitate.setNume("Test Nume");
		activitate.setDescriere("Descriere");
		activitate.setOraInceput("08:00");
		activitate.setDurata("120");
		repositoryActivitati.save(activitate);

		List<Activitati> found = repositoryActivitati.findByNumeContainingOrDescriereContaining("Test", "");
		assertFalse(found.isEmpty());
	}
//
//	@Test
//	void testValidateActivitate() {
//		Activitati activitate = new Activitati();
//		activitate.setNume("n"); // Invalid
//		activitate.setDescriere("n"); // Invalid
//		activitate.setOraInceput("22:00"); // Invalid
//		activitate.setDurata("55"); // Invalid
//
//		List<String> errors = serviceActivitati.validateActivitate(activitate);
//		assertEquals(4, errors.size()); // Verifică numărul de erori
//		assertTrue(errors.contains("Numele nu poate fi gol."));
//		assertTrue(errors.contains("Descrierea nu poate fi goală."));
//		assertTrue(errors.contains("Ora trebuie să fie în formatul HH:MM (ex: 14:30)."));
//		assertTrue(errors.contains("Durata trebuie să fie un număr pozitiv."));
//	}
//
//	@Test
//	void testCalculateProgress() {
//		Activitati activitate1 = new Activitati();
//		activitate1.setNume("Activitate 1");
//		activitate1.setDurata("60");
//		activitate1.setProgres("50");
//		activitate1.setDeFacut(true);
//		activitate1.setStatus(StatusActivitate.in_progres);
//
//		Activitati activitate2 = new Activitati();
//		activitate2.setNume("Activitate 2");
//		activitate2.setDurata("120");
//		activitate2.setProgres("100");
//		activitate2.setDeFacut(true);
//		activitate2.setStatus(StatusActivitate.finalizata);
//
//		repositoryActivitati.save(activitate1);
//		repositoryActivitati.save(activitate2);
//
//		double progress = serviceActivitati.calculateProgress();
//		assertEquals(75.0, progress, 0.1); // Verifică dacă progresul este 75%
//	}
//
	@Test
	void testEqualsMethod() {
		Activitati activitate1 = new Activitati();
		activitate1.setNume("Test");
		activitate1.setDescriere("Descriere");
		activitate1.setOraInceput("12:00");
		activitate1.setDurata("60");

		Activitati activitate2 = new Activitati();
		activitate2.setNume("Test");
		activitate2.setDescriere("Descriere");
		activitate2.setOraInceput("12:00");
		activitate2.setDurata("60");

		assertEquals(activitate1, activitate2); // Ar trebui să fie egale
	}


}
