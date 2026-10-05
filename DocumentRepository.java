import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class DocumentRepository {
    private final String directory;
    private final List<LegalDocument> documents = new ArrayList<>();

    public DocumentRepository(String directory) {
        this.directory = directory;
    }

    public void loadDocuments() {
        documents.clear();
        Path dir = Paths.get(directory);
        try {
            Files.createDirectories(dir);
            int id = 1;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt")) {
                for (Path p : stream) {
                    String file = p.getFileName().toString();
                    String title = file.replace(".txt", "").replace("_", " ");
                    String category = inferCategory(file);
                    documents.add(new LegalDocument(id++, title, category, file));
                }
            }
        } catch (IOException e) {
            System.out.println("[!] Could not load documents: " + e.getMessage());
        }
    }

    private String inferCategory(String file) {
        String f = file.toLowerCase();
        if (f.contains("employment")) return "Employment Law";
        if (f.contains("nda") || f.contains("confidential")) return "Corporate Law";
        if (f.contains("software")) return "Intellectual Property";
        if (f.contains("court") || f.contains("judgment")) return "Civil Law";
        if (f.contains("service")) return "Commercial Law";
        return "Legal Document";
    }

    public void addDocument(String title, String category, String fileName) {
        int id = documents.size() + 1;
        documents.add(new LegalDocument(id, title, category, fileName));
    }

    public List<LegalDocument> getDocuments() { return documents; }

    public LegalDocument getById(int id) {
        for (LegalDocument d : documents) if (d.getId() == id) return d;
        return null;
    }

    public String readText(LegalDocument d) {
        try {
            return Files.readString(Paths.get(directory, d.getFileName()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
