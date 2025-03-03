/**
 * Clasa pentru gestionarea cererilor HTTP și a interacțiunii dintre utilizator și aplicație.
 * @author Ursei George-Armand
 * @version 5 Ianuarie 2025
 */


package com.backend.controller;

import com.backend.entity.Activitati;
import com.backend.enums.StatusActivitate;
import com.backend.service.ServiceActivitati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Controller
public class ControllerActivitati {


    @Autowired
    private ServiceActivitati serviceActivitati;
    private boolean showDeleteConfirmation=false;
    private double progress;
    private Long deleteId;
    private boolean showSuccessOverlay = false;
    private int showList = 0; // Variabilă pentru starea listei
    private String listButtonText;
    private Map<Long, String> textDeFacut;

    @GetMapping("/")
    public String home(Model model) {

        System.out.println("Am intrat în metoda showIndexPage.");


        model.addAttribute("showList", showList); // Adăugăm starea listei în model

        if (showList == 1) {
            model.addAttribute("activitati", serviceActivitati.getActivitati()); // Lista activităților
        }
        listButtonText = showList == 1? "Ascunde Lista" : "Lista Activități";


        model.addAttribute("lucruriDeFacut", serviceActivitati.getLucruriDeFacutWithBreaks());
        model.addAttribute("statusOptions", StatusActivitate.values()); // Trimite enum către frontend
        progress = serviceActivitati.calculateProgress();
        model.addAttribute("progress", Math.round(progress * 10000.0) / 100.0);
        model.addAttribute("listButtonText", listButtonText);


        List<Activitati> activitati = serviceActivitati.getActivitati();

        // Creează o hartă între ID și textul de afișat
         textDeFacut = new HashMap<>();
        for (Activitati activitate : activitati) {
            textDeFacut.put((long)activitate.getIdActivitati(), activitate.isDeFacut() ? "Elimină" : "De făcut");
        }

        // Adaugă lista și harta în model
        model.addAttribute("activitati", activitati);
        model.addAttribute("textDeFacut", textDeFacut);
        System.out.println(textDeFacut);


        model.addAttribute("showDeleteConfirmation", false);
        model.addAttribute("deleteId", null);

        model.addAttribute("showSuccessOverlay", showSuccessOverlay);
        showSuccessOverlay = false; // Resetează variabila după afișare

        return "index";
    }

    @PostMapping("/toggleList")
    public String toggleList() {
        showList = (showList+1)%2; // Inversăm starea listei
        return "redirect:/"; // Redirecționăm către pagina principală
    }



    @GetMapping("/search")
    public String searchActivitati(@RequestParam("query") String query, Model model) {
        List<Activitati> rezultate = serviceActivitati.searchActivitati(query);
        model.addAttribute("activitati", rezultate);
       model.addAttribute("lucruriDeFacut", serviceActivitati.getLucruriDeFacutWithBreaks());

        listButtonText = showList == 1? "Ascunde Lista" : "Lista Activități";

        model.addAttribute("progress", Math.round(progress * 10000.0) / 100.0);

        model.addAttribute("textDeFacut", textDeFacut);

        model.addAttribute("query", query); // Textul de căutare pentru afișare
        model.addAttribute("showList", 1); // Activează lista automat
        if(showList==0) {
            showList = (showList + 1) % 2;
            listButtonText = showList == 1? "Ascunde Lista" : "Lista Activități";

        }
        model.addAttribute("listButtonText", listButtonText);

        // Verifică dacă rezultatele sunt goale
        if (rezultate.isEmpty()) {
            model.addAttribute("noResults", true); // Afișează mesajul de eroare
            model.addAttribute("errorMessage", "Nu s-a găsit nicio activitate care să corespundă textului introdus.");

        } else {
            model.addAttribute("noResults", false); // Nu afișa mesajul
        }

//        showList=0;
        return "index";
    }



    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("showForm", true);
        model.addAttribute("formTitle", "Adaugă Activitate");
        model.addAttribute("formAction", "/add");
        model.addAttribute("activitate", new Activitati());
        return "index";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam int id, Model model) {
        Activitati activitate = serviceActivitati.getActivitateById(id);
        model.addAttribute("showForm", true);
        model.addAttribute("formTitle", "Editare Activitate");
        model.addAttribute("formAction", "/update");
        model.addAttribute("activitate", activitate);
        return "index";
    }


@PostMapping("/add")
public String addActivitate(@ModelAttribute Activitati activitate, Model model) {
    // Validare activitate
    List<String> errors = serviceActivitati.validateActivitate(activitate);

    // Verifică dacă există o activitate similară
    List<Activitati> toateActivitatile = serviceActivitati.getActivitati();
    for (Activitati a : toateActivitatile) {
        if (a.equals(activitate) && a.getIdActivitati() != activitate.getIdActivitati()) {
            errors.add("O activitate similară există deja în baza de date.");
            break;
        }
    }

    if (!errors.isEmpty()) {
        model.addAttribute("errors", errors); // Adăugăm toate erorile în model
        model.addAttribute("showForm", true);
        model.addAttribute("activitate", activitate); // Păstrăm datele introduse
        model.addAttribute("formTitle", "Adaugă Activitate");
        model.addAttribute("formAction", "/add");
        model.addAttribute("showList", 0); // Ascundem lista

        return "index";
    }

    // Salvare activitate
    serviceActivitati.saveActivitate(activitate);

    // Activează overlay-ul de succes
    showSuccessOverlay = true;
    model.addAttribute("showSuccessOverlay", showSuccessOverlay);

    return "redirect:/";
}




    @PostMapping("/update")
    public String updateActivitate(@ModelAttribute Activitati activitate, Model model) {
    // Validare activitate
    List<String> errors = serviceActivitati.validateActivitate(activitate);

    // Verifică dacă există o activitate similară
    List<Activitati> toateActivitatile = serviceActivitati.getActivitati();
        for (Activitati a : toateActivitatile) {
            if (a.equals(activitate) && a.getIdActivitati() != activitate.getIdActivitati()) {
                errors.add("O activitate similară există deja în baza de date.");
                break;
            }
        }

    if (!errors.isEmpty()) {
        model.addAttribute("errors", errors);
        model.addAttribute("showForm", true);
        model.addAttribute("activitate", activitate);
        model.addAttribute("formTitle", "Editare Activitate");
        model.addAttribute("formAction", "/update");
        model.addAttribute("showList", 0); // Ascundem lista



        return "index";
    }

    // Găsire și actualizare activitate
    Activitati activitateExistenta = serviceActivitati.getActivitateById(activitate.getIdActivitati());
    if (activitateExistenta == null) {
        throw new IllegalArgumentException("Activitatea cu acest ID nu există.");
    }
    activitateExistenta.setNume(activitate.getNume());
    activitateExistenta.setDescriere(activitate.getDescriere());
    activitateExistenta.setOraInceput(activitate.getOraInceput());
    activitateExistenta.setDurata(activitate.getDurata());

    serviceActivitati.saveActivitate(activitateExistenta);

    // Activează overlay-ul de succes
    showSuccessOverlay = true;
    model.addAttribute("showSuccessOverlay", showSuccessOverlay);

    return "redirect:/";
    }

    @GetMapping("/confirmDelete")
    public String confirmDelete(@RequestParam("id") Long id, Model model) {
        model.addAttribute("showDeleteConfirmation", true);
        model.addAttribute("deleteId", id);
        return "index"; // Sau pagina în care vrei să afișezi overlay-ul
    }

    @PostMapping("/showDeleteConfirmation")
    public String showDeleteConfirmation(@RequestParam("id") Long id, Model model) {
        model.addAttribute("showDeleteConfirmation", true);
        model.addAttribute("deleteId", id);
        return "index";
    }



    @PostMapping("/delete")
    public String deleteActivitate(@RequestParam("id") int id) {
        serviceActivitati.deleteActivitate(id);
        return "redirect:/";
    }



    @PostMapping("/toggleDeFacut")
    public String toggleDeFacut(@RequestParam("id") int id, Model model) {
        try {
            Activitati activitate = serviceActivitati.getActivitateById(id);
            if (activitate == null) {
                throw new IllegalArgumentException("Activitatea cu acest ID nu există.");
            }

            // Dacă activitatea este marcată pentru "de făcut "
            if (!activitate.isDeFacut()) {
                List<Activitati> activitatiDeFacut = serviceActivitati.getActivitatiDeFacut();

                // Verifică suprapunerea
                for (Activitati a : activitatiDeFacut) {
                    if (seSuprapun(activitate, a)) {
                        model.addAttribute("errorMessage", "Activitatea se suprapune cu alta deja adăugată.");
                        model.addAttribute("showErrorOverlay", true);
                        return "index"; // Reîncarcă pagina cu mesajul de eroare
                    }
                }
            }

            // Toggle pentru câmpul `deFacut`
            activitate.setDeFacut(!activitate.isDeFacut());
            activitate.setStatus(StatusActivitate.neinceputa);
            activitate.setProgres("0");
            serviceActivitati.saveActivitate(activitate);

            return "redirect:/"; // Redirecționează către pagina principală
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "index";
        }
    }

    // Variabilă pentru afișarea confirmării
    private boolean showDeleteAllConfirmation = false;

    @GetMapping("/confirmRemoveAll")
    public String confirmRemoveAll(Model model) {
        showDeleteAllConfirmation = true; // Activează overlay-ul
        model.addAttribute("showDeleteAllConfirmation", showDeleteAllConfirmation);
        return "index"; // Întoarce pagina principală
    }

    @PostMapping("/removeAllDeFacut")
    public String removeAllDeFacut(Model model) {
        try {
            serviceActivitati.removeAllDeFacut();
            showDeleteAllConfirmation = false; // Dezactivează overlay-ul după ștergere
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "A apărut o eroare: " + e.getMessage());
            return "index";
        }
    }



    // Metodă pentru a verifica suprapunerea
    private boolean seSuprapun(Activitati a1, Activitati a2) {
        int start1 = parseOraInMinutes(a1.getOraInceput());
        int end1 = start1 + Integer.parseInt(a1.getDurata()); // Adăugăm durata în minute
        int start2 = parseOraInMinutes(a2.getOraInceput());
        int end2 = start2 + Integer.parseInt(a2.getDurata()); // Adăugăm durata în minute

        return (start1 < end2 && end1 > start2); // Suprapunere
    }


    private int parseOraInMinutes(String ora) {
        try {
            String[] parts = ora.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Ora nu este într-un format valid (HH:MM): " + ora);
            }
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes; // Convertim totul în minute
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ora nu este numerică: " + ora, e);
        }
    }



    @GetMapping("/showToggleConfirmation")
    public String showToggleConfirmation(@RequestParam int id, Model model) {
        model.addAttribute("showToggleConfirmation", true);
        model.addAttribute("toggleId", id);
        return "index"; // Înlocuiește cu numele șablonului tău
    }

    @GetMapping("/showToggleConfirmation2")
    public String showToggleConfirmation2(@RequestParam int id, @RequestParam String action, Model model) {
        model.addAttribute("showToggleConfirmation2", true);
        model.addAttribute("toggleId", id);
        model.addAttribute("toggleAction", action);
        return "index"; // Înlocuiește cu numele șablonului tău
    }



    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("id") int id, @RequestParam("status") String status, Model model) {
        try {
            // Găsește activitatea după ID
            Activitati activitate = serviceActivitati.getActivitateById(id);
            if (activitate == null) {
                throw new IllegalArgumentException("Activitatea nu a fost găsită.");
            }

            // Verifică dacă toate activitățile anterioare sunt finalizate
            StatusActivitate newStatus = StatusActivitate.valueOf(status);
            if ((newStatus == StatusActivitate.in_progres || newStatus == StatusActivitate.finalizata) &&
                    !serviceActivitati.toateActivitatileAnterioareSuntFinalizate(activitate)) {
                // Afișează eroare în caz că activitățile anterioare nu sunt finalizate
                model.addAttribute("errorMessage", "Trebuie să finalizezi toate activitățile anterioare înainte să continui.");
                return "index"; // Redirecționează către pagina principală cu eroare
            }

            // Actualizează statusul
            activitate.setStatus(newStatus);
            if(newStatus == StatusActivitate.finalizata)
                activitate.setProgres("100");
            if(newStatus == StatusActivitate.neinceputa || newStatus == StatusActivitate.in_progres)
                activitate.setProgres("0");
            serviceActivitati.saveActivitate(activitate);

            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Eroare la actualizarea statusului: " + e.getMessage());
            return "index";
        }
    }

    @PostMapping("/showUpdateProgres")
    public String showUpdateProgres(@RequestParam("id") int id, Model model) {
        Activitati activitate = serviceActivitati.getActivitateById(id);
        if (activitate == null) {
            throw new IllegalArgumentException("Activitatea nu a fost găsită.");
        }

        model.addAttribute("activitate", activitate);
        model.addAttribute("showProgressForm", true);
        return "index";
    }

    @PostMapping("/updateProgres")
    public String updateProgres(@RequestParam("id") int id, @RequestParam("newProgress") String newProgress, Model model) {
        try {
            // Validare progres
            int progressValue;
            try {
                progressValue = Integer.parseInt(newProgress);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Progresul trebuie să fie un număr.");
            }

            if (progressValue < 0 || progressValue > 100) {
                throw new IllegalArgumentException("Progresul trebuie să fie între 0 și 100.");
            }

            // Găsire activitate
            Activitati activitate = serviceActivitati.getActivitateById(id);
            if (activitate == null) {
                throw new IllegalArgumentException("Activitatea nu a fost găsită.");
            }

            // Actualizare progres
            activitate.setProgres(newProgress);
            serviceActivitati.saveActivitate(activitate);

            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("showProgressForm", true);
            model.addAttribute("activitate", serviceActivitati.getActivitateById(id));
            return "index";
        }
    }


}
