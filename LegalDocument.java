public class LegalDocument {
    private final int id;
    private final String title;
    private final String category;
    private final String fileName;

    public LegalDocument(int id, String title, String category, String fileName) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.fileName = fileName;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getFileName() { return fileName; }
}
