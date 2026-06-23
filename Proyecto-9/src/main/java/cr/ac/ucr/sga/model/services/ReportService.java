package cr.ac.ucr.sga.model.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import cr.ac.ucr.sga.model.entities.AcademicRecordEntry;
import cr.ac.ucr.sga.model.entities.Student;
import cr.ac.ucr.sga.model.entities.User;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    private ReportService() {}

    // ─────────────────────────────────────────────────────────────────────────
    // MÉTRICAS (reutilizable para preview en pantalla y para PDF/CSV/Excel)
    // ─────────────────────────────────────────────────────────────────────────

    public static ReportData collectMetrics(String filterPeriod, String filterCourse, String filterProfessor) {
        StudentDirectoryService sds       = StudentDirectoryService.getInstance();
        AcademicRecordService   ars       = AcademicRecordService.getInstance();
        CurriculumService       curriculum = CurriculumService.getInstance();
        UserService             userService = UserService.getInstance();

        Student[]             students = sds.getStudents();
        AcademicRecordEntry[] records  = ars.toArray();

        // ── Estudiantes ──────────────────────────────────────────────────────
        int totalStudents    = students.length;
        int activeStudents   = 0;
        int inactiveStudents = 0;
        for (Student s : students) {
            if (s != null && s.getApprovedCredits() > 0) activeStudents++;
            else inactiveStudents++;
        }

        // ── Notas y cursos ────────────────────────────────────────────────────
        int    approvedCourses = 0;
        int    failedCourses   = 0;
        double sumGrades       = 0;
        double maxGrade        = -1;
        double minGrade        = 101;
        String bestCourse      = "—";
        String worstCourse     = "—";

        // Para contar matrículas por curso
        Map<String, Integer> enrollmentCount = new HashMap<>();

        for (AcademicRecordEntry e : records) {
            if (e == null) continue;

            // Aplicar filtros
            if (filterPeriod != null && !filterPeriod.isEmpty()
                    && !e.getPeriod().equalsIgnoreCase(filterPeriod)) continue;
            if (filterCourse != null && !filterCourse.isEmpty()
                    && !e.getCourse().getCode().equalsIgnoreCase(filterCourse)) continue;

            double grade      = e.getGrade();
            String courseCode = e.getCourse().getCode();
            String courseName = e.getCourse().getName();

            enrollmentCount.merge(courseName, 1, Integer::sum);

            if ("Aprobado".equalsIgnoreCase(e.getStatus())) {
                approvedCourses++;
                sumGrades += grade;
                if (grade > maxGrade) { maxGrade = grade; bestCourse = courseName; }
                if (grade < minGrade) { minGrade = grade; worstCourse = courseName; }
            } else if ("Reprobado".equalsIgnoreCase(e.getStatus())) {
                failedCourses++;
            }
        }

        double avg = approvedCourses > 0 ? sumGrades / approvedCourses : 0;

        // Curso con mayor / menor matrícula
        String maxEnrollCourse = "—";
        String minEnrollCourse = "—";
        int    maxEnroll       = 0;
        int    minEnroll       = Integer.MAX_VALUE;
        for (Map.Entry<String, Integer> entry : enrollmentCount.entrySet()) {
            if (entry.getValue() > maxEnroll) { maxEnroll = entry.getValue(); maxEnrollCourse = entry.getKey() + " (" + entry.getValue() + ")"; }
            if (entry.getValue() < minEnroll) { minEnroll = entry.getValue(); minEnrollCourse = entry.getKey() + " (" + entry.getValue() + ")"; }
        }
        if (enrollmentCount.isEmpty()) { maxEnrollCourse = "—"; minEnrollCourse = "—"; }

        // ── Cursos ────────────────────────────────────────────────────────────
        int totalCourses  = curriculum.getAllCourseCodes().size();
        int activeCourses = enrollmentCount.size();  // cursos con al menos 1 inscripción

        // ── Profesores ────────────────────────────────────────────────────────
        int    professors     = 0;
        String topProfessor   = "—";
        List<User> allUsers   = userService.getAllUsers();
        for (User u : allUsers) {
            if (u.getRole() == User.Role.PROFESOR) {
                professors++;
                if (topProfessor.equals("—")) topProfessor = u.getDisplayName();
            }
        }

        // ── Período detectado ─────────────────────────────────────────────────
        String detectedPeriod = "I Ciclo " + LocalDate.now().getYear();
        if (filterPeriod != null && !filterPeriod.isEmpty()) detectedPeriod = filterPeriod;

        ReportData data = new ReportData();
        data.date            = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        data.period          = detectedPeriod;
        data.totalStudents   = totalStudents;
        data.activeStudents  = activeStudents;
        data.inactiveStudents= inactiveStudents;
        data.totalCourses    = totalCourses;
        data.activeCourses   = activeCourses;
        data.approvedCourses = approvedCourses;
        data.failedCourses   = failedCourses;
        data.avg             = avg;
        data.maxGrade        = maxGrade < 0 ? 0 : maxGrade;
        data.minGrade        = minGrade > 100 ? 0 : minGrade;
        data.bestCourse      = bestCourse;
        data.worstCourse     = worstCourse;
        data.maxEnrollCourse = maxEnrollCourse;
        data.minEnrollCourse = minEnrollCourse;
        data.professors      = professors;
        data.topProfessor    = topProfessor;
        return data;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GENERAR PDF  (formato igual al ejemplo del profesor)
    // ─────────────────────────────────────────────────────────────────────────

    public static boolean generateSystemMetricsPdf(String outputPath) {
        return generateSystemMetricsPdf(outputPath, null, null, null);
    }

    public static boolean generateSystemMetricsPdf(
            String outputPath,
            String filterPeriod,
            String filterCourse,
            String filterProfessor) {

        try {
            ReportData d = collectMetrics(filterPeriod, filterCourse, filterProfessor);

            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(doc, new FileOutputStream(outputPath));
            doc.open();

            // ── Paleta ────────────────────────────────────────────────────────
            BaseColor NAVY      = new BaseColor(0x10, 0x22, 0x4A);
            BaseColor GOLD      = new BaseColor(0xC9, 0xA0, 0x2C);
            BaseColor LIGHT_BG  = new BaseColor(0xF4, 0xF6, 0xFA);
            BaseColor DARK_TEXT = new BaseColor(0x1A, 0x1A, 0x2E);
            BaseColor MID_GRAY  = new BaseColor(0x64, 0x74, 0x8B);

            // ── Fuentes ───────────────────────────────────────────────────────
            Font fTitle   = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD,   BaseColor.WHITE);
            Font fSection = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,   NAVY);
            Font fLabel   = new Font(Font.FontFamily.HELVETICA,  9, Font.NORMAL, MID_GRAY);
            Font fValue   = new Font(Font.FontFamily.HELVETICA,  9, Font.BOLD,   DARK_TEXT);
            Font fSmall   = new Font(Font.FontFamily.HELVETICA,  8, Font.NORMAL, MID_GRAY);

            // ── Cabecera ──────────────────────────────────────────────────────
            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            PdfPCell titleCell = new PdfPCell(new Phrase("INFORME DE MÉTRICAS DEL SISTEMA ACADÉMICO", fTitle));
            titleCell.setBackgroundColor(NAVY);
            titleCell.setPadding(10);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setBorder(Rectangle.NO_BORDER);
            header.addCell(titleCell);
            doc.add(header);
            doc.add(Chunk.NEWLINE);

            // ── Fecha y período ───────────────────────────────────────────────
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            metaTable.setWidths(new float[]{1, 1});
            addMeta(metaTable, "Fecha:", d.date, fLabel, fValue, LIGHT_BG);
            addMeta(metaTable, "Período:", d.period, fLabel, fValue, LIGHT_BG);
            doc.add(metaTable);
            doc.add(Chunk.NEWLINE);

            // ── Separador dorado ──────────────────────────────────────────────
            addSeparator(doc, GOLD);

            // ── Layout 2 columnas ─────────────────────────────────────────────
            PdfPTable body = new PdfPTable(2);
            body.setWidthPercentage(100);
            body.setWidths(new float[]{1, 1});
            body.setSpacingBefore(8);

            // Columna izquierda
            PdfPCell leftCol  = buildSection("ESTUDIANTES", new String[][]{
                    {"Registrados:", String.valueOf(d.totalStudents)},
                    {"Activos:",     String.valueOf(d.activeStudents)},
                    {"Inactivos:",   String.valueOf(d.inactiveStudents)}
            }, fSection, fLabel, fValue, LIGHT_BG, GOLD);

            // Columna derecha — Cursos con mayor/menor matrícula
            PdfPCell rightCol = buildSection("CURSOS (MATRÍCULA)", new String[][]{
                    {"Curso con mayor matrícula:", d.maxEnrollCourse},
                    {"Curso con menor matrícula:", d.minEnrollCourse}
            }, fSection, fLabel, fValue, LIGHT_BG, GOLD);

            body.addCell(leftCol);
            body.addCell(rightCol);

            // Segunda fila
            PdfPCell cursosCell = buildSection("CURSOS", new String[][]{
                    {"Cursos registrados:", String.valueOf(d.totalCourses)},
                    {"Cursos activos:",     String.valueOf(d.activeCourses)}
            }, fSection, fLabel, fValue, LIGHT_BG, GOLD);

            PdfPCell notasCell = buildSection("CALIFICACIONES", new String[][]{
                    {"Promedio institucional:", String.format("%.2f", d.avg)},
                    {"Nota máxima:",            String.valueOf((int) d.maxGrade)},
                    {"Nota mínima:",            String.valueOf((int) d.minGrade)},
                    {"Aprobados:",              String.valueOf(d.approvedCourses)},
                    {"Reprobados:",             String.valueOf(d.failedCourses)}
            }, fSection, fLabel, fValue, LIGHT_BG, GOLD);

            body.addCell(cursosCell);
            body.addCell(notasCell);

            // Tercera fila
            PdfPCell profCell = buildSection("PROFESORES", new String[][]{
                    {"Profesores activos:",          String.valueOf(d.professors)},
                    {"Profesor con mayor carga:", d.topProfessor}
            }, fSection, fLabel, fValue, LIGHT_BG, GOLD);

            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setBorder(Rectangle.NO_BORDER);

            body.addCell(profCell);
            body.addCell(emptyCell);

            doc.add(body);

            // ── Pie de página ─────────────────────────────────────────────────
            doc.add(Chunk.NEWLINE);
            addSeparator(doc, GOLD);
            Paragraph footer = new Paragraph("Sistema de Gestión Académica — UCR  |  Generado el " + d.date, fSmall);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

            doc.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GENERAR CSV
    // ─────────────────────────────────────────────────────────────────────────

    public static boolean generateSystemMetricsCsv(String outputPath) {
        return generateSystemMetricsCsv(outputPath, null, null, null);
    }

    public static boolean generateSystemMetricsCsv(
            String outputPath,
            String filterPeriod,
            String filterCourse,
            String filterProfessor) {
        try {
            ReportData d = collectMetrics(filterPeriod, filterCourse, filterProfessor);
            StringBuilder sb = new StringBuilder();
            sb.append("Métrica,Valor\n");
            sb.append("Fecha,").append(d.date).append("\n");
            sb.append("Período,").append(d.period).append("\n");
            sb.append("Estudiantes registrados,").append(d.totalStudents).append("\n");
            sb.append("Estudiantes activos,").append(d.activeStudents).append("\n");
            sb.append("Estudiantes inactivos,").append(d.inactiveStudents).append("\n");
            sb.append("Cursos registrados,").append(d.totalCourses).append("\n");
            sb.append("Cursos activos,").append(d.activeCourses).append("\n");
            sb.append("Promedio institucional,").append(String.format("%.2f", d.avg)).append("\n");
            sb.append("Nota máxima,").append((int) d.maxGrade).append("\n");
            sb.append("Nota mínima,").append((int) d.minGrade).append("\n");
            sb.append("Aprobados,").append(d.approvedCourses).append("\n");
            sb.append("Reprobados,").append(d.failedCourses).append("\n");
            sb.append("Curso con mayor matrícula,").append(d.maxEnrollCourse).append("\n");
            sb.append("Curso con menor matrícula,").append(d.minEnrollCourse).append("\n");
            sb.append("Profesores activos,").append(d.professors).append("\n");
            sb.append("Profesor con mayor carga,").append(d.topProfessor).append("\n");

            java.nio.file.Files.writeString(java.nio.file.Path.of(outputPath), sb.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS PDF
    // ─────────────────────────────────────────────────────────────────────────

    private static void addSeparator(Document doc, BaseColor color) throws DocumentException {
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(color);
        cell.setFixedHeight(2f);
        cell.setBorder(Rectangle.NO_BORDER);
        line.addCell(cell);
        doc.add(line);
        doc.add(new Paragraph(" "));
    }

    private static void addMeta(PdfPTable table, String label, String value,
                                Font fLabel, Font fValue, BaseColor bg) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, fLabel));
        lCell.setBackgroundColor(bg);
        lCell.setBorder(Rectangle.NO_BORDER);
        lCell.setPadding(4);
        table.addCell(lCell);

        PdfPCell vCell = new PdfPCell(new Phrase(value, fValue));
        vCell.setBackgroundColor(bg);
        vCell.setBorder(Rectangle.NO_BORDER);
        vCell.setPadding(4);
        table.addCell(vCell);
    }

    private static PdfPCell buildSection(String title, String[][] rows,
                                         Font fSection, Font fLabel, Font fValue,
                                         BaseColor bg, BaseColor accent) {
        PdfPTable inner = new PdfPTable(2);
        try { inner.setWidths(new float[]{1.4f, 1f}); } catch (Exception ignored) {}
        inner.setWidthPercentage(100);

        // Título de sección
        PdfPCell hCell = new PdfPCell(new Phrase(title, fSection));
        hCell.setColspan(2);
        hCell.setBackgroundColor(bg);
        hCell.setBorderColor(accent);
        hCell.setBorderWidthBottom(2f);
        hCell.setBorderWidthTop(0);
        hCell.setBorderWidthLeft(0);
        hCell.setBorderWidthRight(0);
        hCell.setPadding(5);
        inner.addCell(hCell);

        // Filas de datos
        for (String[] row : rows) {
            PdfPCell lc = new PdfPCell(new Phrase(row[0], fLabel));
            lc.setBorder(Rectangle.NO_BORDER);
            lc.setBackgroundColor(BaseColor.WHITE);
            lc.setPadding(3);
            inner.addCell(lc);

            PdfPCell vc = new PdfPCell(new Phrase(row[1], fValue));
            vc.setBorder(Rectangle.NO_BORDER);
            vc.setBackgroundColor(BaseColor.WHITE);
            vc.setPadding(3);
            inner.addCell(vc);
        }

        PdfPCell wrapper = new PdfPCell(inner);
        wrapper.setPadding(6);
        wrapper.setBorderColor(new BaseColor(0xE2, 0xE8, 0xF0));
        return wrapper;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DATA OBJECT
    // ─────────────────────────────────────────────────────────────────────────

    public static class ReportData {
        public String date, period, bestCourse, worstCourse, maxEnrollCourse, minEnrollCourse, topProfessor;
        public int    totalStudents, activeStudents, inactiveStudents,
                totalCourses, activeCourses, approvedCourses, failedCourses, professors;
        public double avg, maxGrade, minGrade;
    }
}