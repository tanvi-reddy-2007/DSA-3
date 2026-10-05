# LexVault - Legal Document Repository

A Java terminal application demonstrating Advanced Algorithms through legal document search and analysis.

## Features

- Document repository using `.txt` files
- KMP exact phrase search
- Levenshtein fuzzy search using Dynamic Programming
- Aho-Corasick multi-clause scanning
- Document comparison
- Similar document detection
- Suffix Array / LCP implementation
- Algorithm Lab

## Requirements

- Java 17 or later
- VS Code with Java Extension Pack recommended

## Run in VS Code Terminal

From the LexVault folder:

```bash
javac -d out Main.java LegalDocument.java DocumentRepository.java algorithms/*.java
java -cp out Main
```

## Algorithm Mapping

| Feature | Algorithm | Module |
|---|---|---|
| Exact phrase search | KMP | Module 2 |
| Multi-clause search | Aho-Corasick | Module 2 |
| Fuzzy search | Levenshtein / Wagner-Fischer | Module 3 |
| Document comparison | Dynamic Programming | Module 3 |
| Similarity / common substrings | Suffix Array + LCP | Module 2 |
| Algorithm explanations | TextHack system design | Module 1 |

The repository intentionally focuses on algorithms that have a natural relationship to legal document processing rather than artificially forcing unrelated algorithms into the application.
