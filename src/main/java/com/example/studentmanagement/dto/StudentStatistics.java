package com.example.studentmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * DTO de statistiques renvoye par GET /students/statistics.
 */
@Schema(description = "Statistiques sur les etudiants : total et repartition par annee de naissance")
public class StudentStatistics {

    @Schema(description = "Nombre total d'etudiants", example = "3")
    private long total;

    @Schema(description = "Nombre d'etudiants par annee de naissance", example = "{\"1985\":2,\"1990\":1}")
    private Map<Integer, Long> parAnnee;

    public StudentStatistics() {
    }

    public StudentStatistics(long total, Map<Integer, Long> parAnnee) {
        this.total = total;
        this.parAnnee = parAnnee;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Map<Integer, Long> getParAnnee() {
        return parAnnee;
    }

    public void setParAnnee(Map<Integer, Long> parAnnee) {
        this.parAnnee = parAnnee;
    }
}
