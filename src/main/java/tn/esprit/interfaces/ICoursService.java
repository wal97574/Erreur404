package tn.esprit.interfaces;

import tn.esprit.entity.Cours;

import java.util.List;

public interface ICoursService {
    void addCours(Cours cours);
    void updateCours(Cours cours);
    void deleteCours(int id);
    List<Cours> getAllCours();
    Cours getCoursById(int id);
}
