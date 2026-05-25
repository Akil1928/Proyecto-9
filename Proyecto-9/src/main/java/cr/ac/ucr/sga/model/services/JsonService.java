package cr.ac.ucr.sga.model.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cr.ac.ucr.sga.model.entities.AcademicRecordEntry;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class JsonService {

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
}
