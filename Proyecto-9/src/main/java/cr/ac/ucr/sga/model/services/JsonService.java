package cr.ac.ucr.sga.model.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cr.ac.ucr.sga.model.entities.AcademicRecordEntry;
import cr.ac.ucr.sga.model.entities.Tramit;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.Reader;

public class JsonService {

    private static final String TRAMITS_PATH = "src/main/resources/data/tramits.json";

    private JsonService() {
    }

    public static void saveAcademicRecord(AcademicRecordEntry[] entries, String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = new FileWriter(path)) {
            gson.toJson(entries, writer);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el archivo JSON: " + e.getMessage());
        }
    }

    public static void saveTramits(Tramit[] tramits) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = new FileWriter(TRAMITS_PATH)) {
            gson.toJson(tramits, writer);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el archivo de trámites JSON: " + e.getMessage());
        }
    }

    public static Tramit[] loadTramits() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Reader reader = new FileReader(TRAMITS_PATH)) {
            Tramit[] tramits = gson.fromJson(reader, Tramit[].class);
            return tramits == null ? new Tramit[0] : tramits;
        } catch (IOException e) {
            return new Tramit[0];
        }
    }
}
