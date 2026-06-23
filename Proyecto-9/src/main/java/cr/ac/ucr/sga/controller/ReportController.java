package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.services.ReportService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;

public class ReportController {

    @FXML private Label lblStatus;

    @FXML
    private void onDownloadPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar informe");
        fileChooser.setInitialFileName("InformeSistemaAcademico.pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        File file = fileChooser.showSaveDialog(lblStatus.getScene().getWindow());
        if (file == null) return;

        boolean ok = ReportService.generateSystemMetricsPdf(file.getAbsolutePath());
        lblStatus.setText(ok
                ? "✔ Informe generado correctamente."
                : "⚠ Error al generar el informe.");
    }
}