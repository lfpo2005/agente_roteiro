package br.com.devluisoliveira.agenteroteiro.core.application.service.enums;

import lombok.Getter;

@Getter
public enum PhilosopherType {
    ANTIPATRO_DE_TARSO("Antipatro de Tarso", "Filósofo estoico do período médio, discípulo de Diógenes da Babilônia"),
    ARISTO_DE_QUIOS("Aristo de Quios", "Filósofo estoico que enfatizava a indiferença às coisas externas"),
    ARQUESILAU_DE_PITANE("Arquesilau de Pitane", "Filósofo que combinou elementos estoicos com ceticismo"),
    ATENODORO_DE_TARSUS("Atenodoro de Tarsus", "Filósofo estoico e tutor de Augusto"),
    CLEANTHES("Cleanthes", "Segundo líder da escola estoica, conhecido por seu Hino a Zeus"),
    CRISIPO_DE_SOLIS("Crisipo de Solis", "Terceiro líder da escola estoica, sistematizador da doutrina"),
    DIOGENES_DE_BABILONIA("Diogenes de Babilônia", "Estoico que liderou a famosa embaixada de filósofos a Roma"),
    DIODORO_CRONOS("Diodoro Cronos", "Dialético que influenciou o desenvolvimento da lógica estoica"),
    EPICTETO("Epicteto", "Ex-escravo e filósofo estoico conhecido pelo Encheiridion (Manual)"),
    EUDORO_DE_ALEXANDRIA("Eudoro de Alexandria", "Filósofo que combinou elementos estoicos com platonismo médio"),
    HERACLITO_DE_EFESO("Heraclito de Éfeso", "Filósofo pré-socrático que influenciou o desenvolvimento do estoicismo"),
    HERMOTIMO_DE_ATENAS("Hermótimo de Atenas", "Filósofo estoico menor do período médio"),
    HIEROCLES("Hierocles", "Estoico conhecido por sua teoria dos círculos concêntricos de preocupação"),
    MARCO_AURELIO("Marco Aurélio", "Imperador romano e filósofo estoico, autor das Meditações"),
    MUSONIO_RUFO("Musônio Rufo", "Filósofo estoico romano e professor de Epicteto"),
    PANECIO_DE_RODES("Panécio de Rodes", "Estoico do período médio que adaptou a filosofia para os romanos"),
    POSIDONIO_DE_APAMEIA("Posidônio de Apameia", "Filósofo, historiador e cientista estoico do período médio"),
    SENECA("Sêneca", "Filósofo estoico romano, tutor de Nero e prolífico escritor"),
    ZENAO_DE_CITIO("Zenão de Cítio", "Fundador da escola estoica em Atenas");

    private final String name;
    private final String description;

    PhilosopherType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Busca um filósofo pelo nome exato
     */
    public static PhilosopherType findByName(String name) {
        for (PhilosopherType philosopher : values()) {
            if (philosopher.getName().equals(name)) {
                return philosopher;
            }
        }
        return null;
    }

    /**
     * Busca um filósofo por nome aproximado (ignorando acentos e case)
     */
    public static PhilosopherType findByApproximateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        String normalized = name.toLowerCase()
                .replaceAll("[áàâã]", "a")
                .replaceAll("[éèê]", "e")
                .replaceAll("[íì]", "i")
                .replaceAll("[óòôõ]", "o")
                .replaceAll("[úù]", "u")
                .replaceAll("[ç]", "c");

        for (PhilosopherType philosopher : values()) {
            String normalizedPhilosopher = philosopher.getName().toLowerCase()
                    .replaceAll("[áàâã]", "a")
                    .replaceAll("[éèê]", "e")
                    .replaceAll("[íì]", "i")
                    .replaceAll("[óòôõ]", "o")
                    .replaceAll("[úù]", "u")
                    .replaceAll("[ç]", "c");

            if (normalizedPhilosopher.contains(normalized) || normalized.contains(normalizedPhilosopher)) {
                return philosopher;
            }
        }

        return null;
    }
}