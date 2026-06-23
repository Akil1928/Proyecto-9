package cr.ac.ucr.sga.model.services;

import java.util.*;


//CurriculumService — Singleton que contiene la malla curricular completa
// del Bachillerato en Informática Empresarial, código 600002, plan 07
//(Informe curricular CEA-46-2025).
//Permite validar si un estudiante cumple los requisitos para llevar
// un curso dado su expediente académico actual.
//Lógica de requisitos (según la malla):
// - "IF-0004 requiere IF-0001 o IF-2000" → basta con que haya aprobado UNO de los dos.
// - Cuando hay múltiples requisitos sin "o" entre ellos, se deben cumplir TODOS.
// - Un curso está "aprobado" si su estado en el expediente es "Aprobado".

public class CurriculumService {

    private static CurriculumService instance;

    /**
     * Mapa: código del curso → lista de grupos de requisitos.
     *
     * Cada "grupo" es un Set de códigos alternativos (OR entre ellos).
     * Entre grupos la relación es AND (todos los grupos deben satisfacerse).
     *
     * Ejemplo: IF-0010 requiere (IF-0007 o IF-4100)
     *   → [ {IF-0007, IF-4100} ]
     *
     * Ejemplo: IF-0022 requiere IF-0016 AND IF-0020 AND IF-7201 AND (IF-0025 o IF-0026 o IF-7100) AND (IF-0029 o IF-0030)
     *   → [ {IF-0016}, {IF-0020}, {IF-7201}, {IF-0025, IF-0026, IF-7100}, {IF-0029, IF-0030} ]
     */
    private final Map<String, List<Set<String>>> prerequisites = new LinkedHashMap<>();

    /**
     * Información descriptiva de cada curso: código → nombre oficial.
     */
    private final Map<String, String> courseNames = new LinkedHashMap<>();

    private CurriculumService() {
        buildCurriculum();
    }

    public static CurriculumService getInstance() {
        if (instance == null) {
            instance = new CurriculumService();
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Construcción de la malla
    // ─────────────────────────────────────────────────────────────────────────

    private void buildCurriculum() {

        // ── PRIMER AÑO ────────────────────

        //I Ciclo
        addCourse("EG-I",   "Curso Integrado de Humanidades I");           // sin requisitos
        addCourse("IF-0001","Desarrollo de Software I");                    // sin requisitos
        addCourse("IF-0002","Introducción a la Informática Empresarial");   // sin requisitos
        addCourse("IF-0003","Matemática Básica para Informática Empresarial"); // sin requisitos

        //II Ciclo
        addCourse("EG-II",  "Curso Integrado de Humanidades II",  or("EG-I"));
        addCourse("EF-",    "Actividad Deportiva");                         // sin requisitos
        addCourse("IF-0004","Desarrollo de Software II",          or("IF-0001","IF-2000"));
        addCourse("IF-0005","Matemáticas Discretas para Informática Empresarial",
                or("IF-0003","IF-1400"));

        // ── SEGUNDO AÑO ───────────────────────────

        //III Ciclo
        addCourse("SR-I",   "Seminario de Realidad Nacional I",   or("EG-II"));
        addCourse("IF-0006","Desarrollo de Software III",         or("IF-0004"));
        addCourse("IF-0007","Bases de Datos I",                   or("IF-0004"));
        addCourse("IF-0008","Cálculo I para Informática Empresarial",
                or("IF-0003","IF-1400"));
        addCourse("IF-3001","Algoritmos y Estructuras de Datos",  or("IF-0004","IF-2000"));

        //IV Ciclo
        addCourse("SR-II",  "Seminario de Realidad Nacional II",  or("SR-I"));
        addCourse("IF-0009","Desarrollo de Software IV",          or("IF-0006","IF-3000"));
        addCourse("IF-0010","Bases de Datos II",                  or("IF-0007","IF-4100"));
        addCourse("IF-0011","Redes de Computadoras",              or("IF-0006","IF-3000"));
        addCourse("IF-0012","Álgebra Lineal para Informática Empresarial",
                or("IF-0008","MA-0321"));

        // ── TERCER AÑO ──────────────────

        //V Ciclo (Verano)
        addCourse("IF-0013","Inglés I para Informática Empresarial");       // sin requisitos

        //VI Ciclo
        addCourse("IF-0014","Inglés II para Informática Empresarial",       or("IF-0013"));
        addCourse("IF-0015","Introducción a la Administración de Negocios", or("IF-0002","IF-1300"));
        addCourse("IF-0016","Introducción a la Estadística y Análisis de Datos",
                or("IF-0010","IF-5100"));
        addCourse("IF-0017","Métodos Numéricos y Análisis Computacional",   or("IF-0012","MA-0322"));
        //OPT Arquitectura e Infraestructura (VI): sin requisitos formales en malla

        //VII Ciclo
        addCourse("IF-0018","Inglés III para Informática Empresarial",      or("IF-0014"));
        addCourse("IF-0019","Seguridad en Sistemas Informáticos",           or("IF-0029","IF-0030"));
        //OPT Desarrollo de Software: sin requisito de malla con código fijo
        //OPT Ingeniería de Datos: sin requisito de malla con código fijo

        //VIII Ciclo (Verano)
        addCourse("IF-0020","Inglés IV para Informática Empresarial",       or("IF-0018"));

        // ── CUARTO AÑO ─────────────

        //IX Ciclo
        addCourse("IF-7201","Gestión de Proyectos",                         or("IF-0015","IF-6200"));
        //OPT Gestión de la Informática IX: sin requisito fijo

        //X Ciclo
        addCourse("IF-0021","Ética y Responsabilidad Profesional",          or("IF-0023","IF-0024","IF-6201"));
        //IF-0022 tiene múltiples grupos de requisitos independientes (AND entre grupos)
        addCourse("IF-0022","Práctica Empresarial Supervisada",
                or("IF-0016"),
                or("IF-0020"),
                or("IF-7201"),
                or("IF-0025","IF-0026","IF-7100"),
                or("IF-0029","IF-0030"));

        // ── OPTATIVOS ────────

        //Área Gestión de la Informática en las Organizaciones
        addCourse("IF-0023","Gobernanza de Tecnologías de Información",     or("IF-0015","IF-5200"));
        addCourse("IF-0024","Emprendimiento y Desarrollo de Negocios",      or("IF-7201"));
        addCourse("IF-6201","Informática Aplicada a los Negocios",          or("IF-0015","IF-5200"));

        //Área Tendencias de Desarrollo de Software
        addCourse("IF-0025","Aseguramiento de la Calidad en la Ingeniería del Software",
                or("IF-0009","IF-4101"));
        addCourse("IF-0026","Interacción Humano Computador",               or("IF-0009","IF-4101"));
        addCourse("IF-7100","Ingeniería de Software",                       or("IF-0009","IF-6100"));

        //Área Ingeniería de Datos
        addCourse("IF-0027","Inteligencia de Negocios",                     or("IF-0016","XS-0105"));
        addCourse("IF-0028","Minería de Datos",                             or("IF-0016","XS-0105"));

        // Área Arquitectura e Infraestructura
        addCourse("IF-0029","Sistemas Operativos y Distribuidos",           or("IF-0011","IF-6000"));
        addCourse("IF-0030","Diseño de Sistemas Automatizados",             or("IF-0019"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers de construcción
    // ─────────────────────────────────────────────────────────────────────────

    /** Registra un curso sin requisitos. */
    private void addCourse(String code, String name) {
        courseNames.put(code.toUpperCase(), name);
        prerequisites.put(code.toUpperCase(), new ArrayList<>());
    }

    /**
     * Registra un curso con uno o más grupos de requisitos.
     * Uso: addCourse("IF-0022", "Nombre", or("A","B"), or("C"), or("D","E"))
     */
    @SafeVarargs
    private final void addCourse(String code, String name, Set<String>... groups) {
        courseNames.put(code.toUpperCase(), name);
        List<Set<String>> groupList = new ArrayList<>();
        for (Set<String> g : groups) {
            groupList.add(g);
        }
        prerequisites.put(code.toUpperCase(), groupList);
    }

    /** Crea un grupo OR de requisitos (basta con cumplir uno). */
    private Set<String> or(String... codes) {
        Set<String> group = new LinkedHashSet<>();
        for (String c : codes) group.add(c.toUpperCase());
        return group;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devuelve el nombre oficial del curso, o null si no está en la malla.
     */
    public String getCourseName(String code) {
        return courseNames.get(code.toUpperCase());
    }

    /**
     * Devuelve true si el código está registrado en la malla curricular.
     */
    public boolean isInCurriculum(String code) {
        return courseNames.containsKey(code.toUpperCase());
    }

    /**
     * Devuelve los grupos de requisitos del curso.
     * Cada Set representa un grupo OR (con cualquiera basta).
     * Entre grupos la relación es AND (todos deben satisfacerse).
     * Lista vacía = sin requisitos.
     */
    public List<Set<String>> getPrerequisiteGroups(String code) {
        List<Set<String>> groups = prerequisites.get(code.toUpperCase());
        return groups != null ? groups : new ArrayList<>();
    }

    /**
     * Valida si el estudiante puede matricular el curso indicado.
     *
     * @param courseCode   Código del curso a verificar (ej. "IF-0010").
     * @param approvedCodes Conjunto de códigos que el estudiante ya aprobó.
     * @return {@code ValidationResult} con el resultado y los requisitos faltantes.
     */
    public ValidationResult canEnroll(String courseCode, Set<String> approvedCodes) {
        List<Set<String>> groups = getPrerequisiteGroups(courseCode);

        if (groups.isEmpty()) {
            return ValidationResult.ok();
        }

        List<String> missing = new ArrayList<>();

        for (Set<String> group : groups) {
            //¿El estudiante aprobó al menos UNO del grupo?
            boolean groupSatisfied = false;
            for (String req : group) {
                if (approvedCodes.contains(req.toUpperCase())) {
                    groupSatisfied = true;
                    break;
                }
            }
            if (!groupSatisfied) {
                //construir mensaje legible: "IF-0007 o IF-4100"
                StringBuilder sb = new StringBuilder();
                String[] arr = group.toArray(new String[0]);
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) sb.append(" o ");
                    String name = getCourseName(arr[i]);
                    sb.append(arr[i]);
                    if (name != null) sb.append(" (").append(name).append(")");
                }
                missing.add(sb.toString());
            }
        }

        if (missing.isEmpty()) {
            return ValidationResult.ok();
        }
        return ValidationResult.fail(missing);
    }

    /**
     * Resultado de la validación de requisitos.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> missingGroups;

        private ValidationResult(boolean valid, List<String> missingGroups) {
            this.valid = valid;
            this.missingGroups = missingGroups;
        }

        public static ValidationResult ok() {
            return new ValidationResult(true, Collections.emptyList());
        }

        public static ValidationResult fail(List<String> missing) {
            return new ValidationResult(false, missing);
        }

        public boolean isValid() { return valid; }

        public List<String> getMissingGroups() { return missingGroups; }

        /**
         * Mensaje formateado listo para mostrar en un Alert.
         */
        public String buildErrorMessage(String courseCode) {
            StringBuilder sb = new StringBuilder();
            sb.append("No podés matricular \"").append(courseCode).append("\" porque te faltan los siguientes requisitos:\n\n");
            for (int i = 0; i < missingGroups.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(missingGroups.get(i)).append("\n");
            }
            return sb.toString();
        }
    }

    public Set<String> getAllCourseCodes() {
        return Collections.unmodifiableSet(courseNames.keySet());
    }

}