package tn.esprit.interfaces;

// IEmploiService.java
import tn.esprit.entity.Emploi;

import java.util.List;

public interface IEmploiService {
    void addEmploi(Emploi emploi);
    void updateEmploi(Emploi emploi);
    void deleteEmploi(int id);
    List<Emploi> getAllEmplois();
    Emploi getEmploiById(int id);
}
