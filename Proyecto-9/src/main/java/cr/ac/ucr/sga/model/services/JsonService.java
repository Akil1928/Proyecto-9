package cr.ac.ucr.sga.model.services;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import cr.ac.ucr.sga.model.entities.AcademicRecordEntry;
import cr.ac.ucr.sga.model.entities.CourseBuilder;
import cr.ac.ucr.sga.model.entities.Tramit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonService {

    private static final Path DATA_DIR     = Paths.get(System.getProperty("user.home"), "sga-data");
    private static final Path TRAMITS_FILE = DATA_DIR.resolve("tramits.json");
    private static final Path COURSES_FILE = DATA_DIR.resolve("courses.json");

    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // -------------------------------------------------------------------------
    // DTOs planos (sin propiedades JavaFX) para serialización segura con Gson
    // -------------------------------------------------------------------------

    /** DTO plano para Course. */
    private static class CourseDto {
        String code, name;
        int credits;
        CourseDto(String code, String name, int credits) {
            this.code = code; this.name = name; this.credits = credits;
        }
    }

    /** DTO plano para AcademicRecordEntry. */
    private static class RecordDto {
        CourseDto course;
        String period, status;
        double grade;
        RecordDto(CourseDto course, String period, double grade, String status) {
            this.course = course; this.period = period;
            this.grade = grade; this.status = status;
        }
    }

    /** DTO plano para Tramit (evita serializar LocalDateTime directamente). */
    private static class TramitDto {
        String id, type, description, studentId, studentName, stateName, createdAt;
        TramitDto(Tramit t) {
            this.id          = t.getId();
            this.type        = t.getType();
            this.description = t.getDescription();
            this.studentId   = t.getStudentId();
            this.studentName = t.getStudentName();
            this.stateName   = t.getStateName();
            this.createdAt   = t.getCreatedAt() != null ? t.getCreatedAt().format(DTF) : null;
        }
    }

    // -------------------------------------------------------------------------

    private static Gson buildGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    private JsonService() {}

    private static void ensureDir() {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            System.err.println("No se pudo crear el directorio de datos: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Trámites
    // -------------------------------------------------------------------------

    public static void saveTramits(Tramit[] tramits) {
        ensureDir();
        TramitDto[] dtos = new TramitDto[tramits.length];
        for (int i = 0; i < tramits.length; i++) {
            if (tramits[i] != null) dtos[i] = new TramitDto(tramits[i]);
        }
        try (Writer writer = new FileWriter(TRAMITS_FILE.toFile())) {
            buildGson().toJson(dtos, writer);
        } catch (IOException e) {
            System.err.println("No se pudo guardar tramits.json: " + e.getMessage());
        }
    }

    public static Tramit[] loadTramits() {
        if (!Files.exists(TRAMITS_FILE)) return new Tramit[0];
        try (Reader reader = new FileReader(TRAMITS_FILE.toFile())) {
            TramitDto[] dtos = buildGson().fromJson(reader, TramitDto[].class);
            if (dtos == null) return new Tramit[0];
            Tramit[] result = new Tramit[dtos.length];
            for (int i = 0; i < dtos.length; i++) {
                if (dtos[i] == null) continue;
                Tramit t = new Tramit(dtos[i].type, dtos[i].description, dtos[i].studentId, dtos[i].studentName);
                //Restaurar fecha si existe
                if (dtos[i].createdAt != null) {
                    t.setCreatedAt(LocalDateTime.parse(dtos[i].createdAt, DTF));
                }
                result[i] = t;
            }
            return result;
        } catch (Exception e) {
            System.err.println("tramits.json incompatible o corrupto, se reinicia: " + e.getMessage());
            try { Files.deleteIfExists(TRAMITS_FILE); } catch (IOException ignored) {}
            return new Tramit[0];
        }
    }

    // -------------------------------------------------------------------------
    // Expediente académico
    // -------------------------------------------------------------------------

    public static void saveAcademicRecord(AcademicRecordEntry[] entries, String path) {
        ensureDir();
        RecordDto[] dtos = new RecordDto[entries.length];
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] == null) continue;
            var c = entries[i].getCourse();
            dtos[i] = new RecordDto(
                    new CourseDto(c.getCode(), c.getName(), c.getCredits()),
                    entries[i].getPeriod(),
                    entries[i].getGrade(),
                    entries[i].getStatus()
            );
        }
        try (Writer writer = new FileWriter(COURSES_FILE.toFile())) {
            buildGson().toJson(dtos, writer);
        } catch (IOException e) {
            System.err.println("No se pudo guardar courses.json: " + e.getMessage());
        }
    }

    public static AcademicRecordEntry[] loadAcademicRecord() {
        if (!Files.exists(COURSES_FILE)) return new AcademicRecordEntry[0];
        try (Reader reader = new FileReader(COURSES_FILE.toFile())) {
            RecordDto[] dtos = buildGson().fromJson(reader, RecordDto[].class);
            if (dtos == null) return new AcademicRecordEntry[0];
            AcademicRecordEntry[] result = new AcademicRecordEntry[dtos.length];
            for (int i = 0; i < dtos.length; i++) {
                if (dtos[i] == null || dtos[i].course == null) continue;
                var course = new CourseBuilder()
                        .setCode(dtos[i].course.code)
                        .setName(dtos[i].course.name)
                        .setCredits(dtos[i].course.credits)
                        .build();
                result[i] = new AcademicRecordEntry(course, dtos[i].period, dtos[i].grade, dtos[i].status);
            }
            return result;
        } catch (Exception e) {
            // El archivo puede estar en un formato antiguo o corrupto — se descarta y se empieza limpio
            System.err.println("courses.json incompatible o corrupto, se reinicia: " + e.getMessage());
            try { Files.deleteIfExists(COURSES_FILE); } catch (IOException ignored) {}
            return new AcademicRecordEntry[0];
        }
    }

    public static String getDataDir() {
        return DATA_DIR.toAbsolutePath().toString();
    }
}