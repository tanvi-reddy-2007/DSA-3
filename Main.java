import algorithms.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    static final String DOC_DIR = "documents";
    static final Scanner sc = new Scanner(System.in);
    static final DocumentRepository repo = new DocumentRepository(DOC_DIR);

    public static void main(String[] args) {
        repo.loadDocuments();
        while (true) {
            printHeader();
            System.out.println("1. View All Legal Documents");
            System.out.println("2. Add Legal Document");
            System.out.println("3. Search Exact Phrase (KMP)");
            System.out.println("4. Fuzzy Search (Levenshtein DP)");
            System.out.println("5. Scan Legal Clauses (Aho-Corasick)");
            System.out.println("6. Compare Two Documents");
            System.out.println("7. Find Similar Documents");
            System.out.println("8. View Document");
            System.out.println("9. Algorithm Information");
            System.out.println("0. Exit");
            System.out.print("\nEnter your choice: ");

            String choice = sc.nextLine().trim();
            System.out.println();

            try {
                switch (choice) {
                    case "1" -> viewAll();
                    case "2" -> addDocument();
                    case "3" -> exactSearch();
                    case "4" -> fuzzySearch();
                    case "5" -> clauseScan();
                    case "6" -> compareDocuments();
                    case "7" -> similarDocuments();
                    case "8" -> viewDocument();
                    case "9" -> algorithmLab();
                    case "0" -> {
                        System.out.println("Goodbye from LexVault. ⚖");
                        return;
                    }
                    default -> System.out.println("[!] Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("[!] Operation failed: " + e.getMessage());
            }
            pause();
        }
    }

    static void printHeader() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║                 LEXVAULT                 ║");
        System.out.println("║         LEGAL DOCUMENT REPOSITORY        ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    static void viewAll() {
        List<LegalDocument> docs = repo.getDocuments();
        if (docs.isEmpty()) {
            System.out.println("[!] No documents found.");
            return;
        }
        System.out.println("AVAILABLE LEGAL DOCUMENTS");
        System.out.println("------------------------------------------");
        for (LegalDocument d : docs) {
            System.out.printf("[%d] %s%n    Category: %s%n    File: %s%n%n",
                    d.getId(), d.getTitle(), d.getCategory(), d.getFileName());
        }
    }

    static void addDocument() {
        System.out.print("Enter document title: ");
        String title = sc.nextLine().trim();
        System.out.print("Enter category: ");
        String category = sc.nextLine().trim();
        System.out.print("Enter path to .txt file: ");
        String path = sc.nextLine().trim();

        if (title.isEmpty() || category.isEmpty() || path.isEmpty()) {
            System.out.println("[!] All fields are required.");
            return;
        }
        Path source = Paths.get(path);
        if (!Files.exists(source)) {
            System.out.println("[!] File not found.");
            return;
        }
        if (!source.toString().toLowerCase().endsWith(".txt")) {
            System.out.println("[!] Only .txt documents are supported.");
            return;
        }

        try {
            String safeName = source.getFileName().toString().replaceAll("[^A-Za-z0-9._-]", "_");
            Path target = Paths.get(DOC_DIR, safeName);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            repo.addDocument(title, category, safeName);
            System.out.println("[✓] Document added successfully.");
        } catch (IOException e) {
            System.out.println("[!] Could not add document: " + e.getMessage());
        }
    }

    static LegalDocument chooseDocument(String prompt) {
        viewAll();
        System.out.print("\n" + prompt);
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            return repo.getById(id);
        } catch (Exception e) {
            return null;
        }
    }

    static void exactSearch() {
        System.out.print("Enter exact phrase: ");
        String pattern = sc.nextLine();
        if (pattern.trim().isEmpty()) {
            System.out.println("[!] Search phrase cannot be empty.");
            return;
        }

        System.out.println("\nSearching repository...");
        System.out.println("Algorithm: Knuth-Morris-Pratt (KMP)\n");
        int total = 0;

        for (LegalDocument d : repo.getDocuments()) {
            String text = repo.readText(d);
            List<Integer> matches = KMP.search(text, pattern);
            if (!matches.isEmpty()) {
                System.out.println("[FOUND] " + d.getFileName());
                for (int pos : matches) System.out.println("        Position: " + pos);
                total += matches.size();
            }
        }

        System.out.println("\nTotal Matches: " + total);
        System.out.println("Time Complexity: O(n + m) per document");
    }

    static void fuzzySearch() {
        System.out.print("Enter legal phrase/term: ");
        String query = sc.nextLine().trim().toLowerCase();
        if (query.isEmpty()) {
            System.out.println("[!] Query cannot be empty.");
            return;
        }

        int best = Integer.MAX_VALUE;
        String bestTerm = null;
        LegalDocument bestDoc = null;

        for (LegalDocument d : repo.getDocuments()) {
            String text = repo.readText(d).toLowerCase();
            String[] words = text.split("[^a-z0-9-]+");
            for (String word : words) {
                if (word.isEmpty()) continue;
                int dist = Levenshtein.distance(query, word);
                if (dist < best) {
                    best = dist;
                    bestTerm = word;
                    bestDoc = d;
                }
            }
        }

        System.out.println("\nFUZZY SEARCH RESULT");
        System.out.println("------------------------------------------");
        if (bestDoc == null) {
            System.out.println("[NOT FOUND] No searchable content.");
            return;
        }
        System.out.println("Query: " + query);
        System.out.println("Closest Match: " + bestTerm);
        System.out.println("Document: " + bestDoc.getFileName());
        System.out.println("Edit Distance: " + best);
        System.out.println("Algorithm: Wagner-Fischer Dynamic Programming");
        System.out.println("Complexity: O(nm)");
    }

    static void clauseScan() {
        LegalDocument d = chooseDocument("Select document ID: ");
        if (d == null) {
            System.out.println("[!] Invalid document.");
            return;
        }

        String[] patterns = {
            "termination", "confidentiality", "arbitration", "indemnity",
            "liability", "force majeure", "governing law", "intellectual property"
        };

        AhoCorasick ac = new AhoCorasick();
        for (String p : patterns) ac.addPattern(p);
        ac.buildFailureLinks();

        List<String> found = ac.search(repo.readText(d).toLowerCase());

        System.out.println("\nScanning " + d.getFileName() + "...");
        System.out.println("------------------------------------------");
        Set<String> unique = new LinkedHashSet<>(found);
        for (String p : patterns) {
            if (unique.contains(p)) System.out.println("[FOUND] " + p);
            else System.out.println("[NOT FOUND] " + p);
        }
        System.out.println("\nAlgorithm: Aho-Corasick");
        System.out.println("Purpose: Multi-pattern string matching");
    }

    static void compareDocuments() {
        LegalDocument a = chooseDocument("Select first document ID: ");
        if (a == null) { System.out.println("[!] Invalid document."); return; }
        LegalDocument b = chooseDocument("Select second document ID: ");
        if (b == null) { System.out.println("[!] Invalid document."); return; }

        String x = repo.readText(a);
        String y = repo.readText(b);
        int dist = Levenshtein.distance(x, y);
        int max = Math.max(x.length(), y.length());
        double similarity = max == 0 ? 100.0 : (1.0 - (double) dist / max) * 100.0;
        if (similarity < 0) similarity = 0;

        System.out.println("\nDOCUMENT COMPARISON");
        System.out.println("==========================================");
        System.out.println("Document A: " + a.getFileName());
        System.out.println("Document B: " + b.getFileName());
        System.out.println("Edit Distance: " + dist);
        System.out.printf("Similarity: %.2f%%%n", similarity);
        System.out.println("Algorithm: Dynamic Programming / Sequence Alignment");
        System.out.println("Related concepts: Wagner-Fischer, Needleman-Wunsch");
    }

    static void similarDocuments() {
        LegalDocument selected = chooseDocument("Select document ID: ");
        if (selected == null) {
            System.out.println("[!] Invalid document.");
            return;
        }

        String base = repo.readText(selected);
        List<Result> results = new ArrayList<>();

        for (LegalDocument d : repo.getDocuments()) {
            if (d.getId() == selected.getId()) continue;
            String other = repo.readText(d);
            int dist = Levenshtein.distance(base, other);
            int max = Math.max(base.length(), other.length());
            double score = max == 0 ? 100 : Math.max(0, (1.0 - (double) dist / max) * 100);
            results.add(new Result(d, score));
        }

        results.sort((r1, r2) -> Double.compare(r2.score, r1.score));

        System.out.println("\nSIMILAR DOCUMENTS");
        System.out.println("------------------------------------------");
        for (Result r : results) {
            System.out.printf("%-32s %.2f%%%n", r.doc.getFileName(), r.score);
        }
        System.out.println("\nComparison uses Dynamic Programming text distance.");
        System.out.println("Suffix Array/LCP is provided in Algorithm Lab as the advanced similarity technique.");
    }

    static void viewDocument() {
        LegalDocument d = chooseDocument("Select document ID: ");
        if (d == null) {
            System.out.println("[!] Invalid document.");
            return;
        }

        System.out.println("\n==========================================");
        System.out.println(d.getTitle().toUpperCase());
        System.out.println("==========================================");
        System.out.println(repo.readText(d));
        System.out.println("------------------------------------------");
        System.out.println("Category: " + d.getCategory());
        System.out.println("Document ID: " + d.getId());
    }

    static void algorithmLab() {
        while (true) {
            System.out.println("\n========== ALGORITHM LAB ==========");
            System.out.println("1. KMP");
            System.out.println("2. Levenshtein Distance");
            System.out.println("3. Aho-Corasick");
            System.out.println("4. Suffix Array / LCP");
            System.out.println("5. Dynamic Programming");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> info("KMP", "Exact phrase search", "O(n + m)", "O(m)",
                        "Uses the LPS/failure function to avoid rechecking characters.");
                case "2" -> info("Levenshtein Distance", "Fuzzy search and comparison", "O(nm)", "O(nm)",
                        "Computes the minimum insertions, deletions and substitutions needed.");
                case "3" -> info("Aho-Corasick", "Scanning many legal clauses at once", "O(n + matches + total pattern length)", "O(total pattern length)",
                        "Builds a trie with failure links for multi-pattern matching.");
                case "4" -> info("Suffix Array + LCP", "Finding repeated/common substrings", "O(n log n) practical construction", "O(n)",
                        "Sorts suffixes and uses LCP values to reveal common prefixes.");
                case "5" -> info("Dynamic Programming", "Edit distance and document alignment", "Problem dependent", "Problem dependent",
                        "Stores smaller subproblem results to avoid recomputation.");
                case "0" -> { return; }
                default -> System.out.println("[!] Invalid choice.");
            }
        }
    }

    static void info(String name, String use, String time, String space, String explanation) {
        System.out.println("\n" + name.toUpperCase());
        System.out.println("------------------------------------------");
        System.out.println("Purpose: " + use);
        System.out.println("Time Complexity: " + time);
        System.out.println("Space Complexity: " + space);
        System.out.println("Explanation: " + explanation);
    }

    static void pause() {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    static class Result {
        LegalDocument doc; double score;
        Result(LegalDocument d, double s) { doc = d; score = s; }
    }
}
