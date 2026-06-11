import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InlineDDLPathFinder {

    static class CompositeRelationship {
        String targetTable;
        List<String> sourceColumns;
        List<String> targetColumns;

        public CompositeRelationship(String targetTable, List<String> sourceColumns, List<String> targetColumns) {
            this.targetTable = targetTable;
            this.sourceColumns = new ArrayList<>(sourceColumns);
            this.targetColumns = new ArrayList<>(targetColumns);
        }
    }

    private final Map<String, List<CompositeRelationship>> schemaGraph = new HashMap<>();

    public void addRelationship(String tableA, List<String> colsA, String tableB, List<String> colsB) {
        if (colsA.size() != colsB.size()) return;

        schemaGraph.computeIfAbsent(tableA, k -> new ArrayList<CompositeRelationship>()).add(new CompositeRelationship(tableB, colsA, colsB));
        schemaGraph.computeIfAbsent(tableB, k -> new ArrayList<CompositeRelationship>()).add(new CompositeRelationship(tableA, colsB, colsA));
    }

    /**
     * Parses the file by locating each CREATE TABLE statement block 
     * and reading the inline FOREIGN KEY declarations within it.
     */
    public void loadInlineDDLsFromFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Error: Valid directory not found at " + folderPath);
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".sql"));
        if (files == null || files.length == 0) {
            System.out.println("No .sql files found.");
            return;
        }

        // Regex 1: Captures the Table name and the entire content block between the outer parentheses
        Pattern tablePattern = Pattern.compile("(?i)CREATE\\s+TABLE\\s+(\\w+)\\s*\\((.+?)\\);");
        
        // Regex 2: Captures inline Foreign Key components within a table's block context
        Pattern inlineFkPattern = Pattern.compile(
            "(?i)FOREIGN\\s+KEY\\s*\\(([^)]+)\\)\\s+REFERENCES\\s+(\\w+)\\s*\\(([^)]+)\\)"
        );

        for (File file : files) {
            try {
                // Read the whole file into a continuous single string removing heavy formatting breaks
                String fileContent = readFileToNormalizedString(file);
                Matcher tableMatcher = tablePattern.matcher(fileContent);

                while (tableMatcher.find()) {
                    String currentTable = tableMatcher.group(1).toUpperCase();
                    String tableBody = tableMatcher.group(2);

                    // Scan inside the current table's block for foreign keys
                    Matcher fkMatcher = inlineFkPattern.matcher(tableBody);
                    while (fkMatcher.find()) {
                        List<String> sourceCols = parseColumns(fkMatcher.group(1));
                        String targetTable = fkMatcher.group(2).toUpperCase();
                        List<String> targetCols = parseColumns(fkMatcher.group(3));

                        addRelationship(currentTable, sourceCols, targetTable, targetCols);
                        System.out.println("Loaded Link (Inline): " + currentTable + " ➔ " + targetTable);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error processing file " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private String readFileToNormalizedString(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Strip SQL comments out so they don't break regex lookups
                if (line.trim().startsWith("--")) continue; 
                sb.append(line).append(" ");
            }
        }
        // Normalize multiple spaces into single spaces to make pattern matching safe
        return sb.toString().replaceAll("\\s+", " ");
    }

    private List<String> parseColumns(String csvColumns) {
        List<String> cols = new ArrayList<>();
        for (String col : csvColumns.split(",")) {
            cols.add(col.trim().replace("\"", "").replace("`", ""));
        }
        return cols;
    }

    /**
     * BFS Path Finder Algorithm
     */
    public List<String> findSimplestPath(String startTable, String endTable) {
        startTable = startTable.toUpperCase();
        endTable = endTable.toUpperCase();

        if (!schemaGraph.containsKey(startTable) || !schemaGraph.containsKey(endTable)) {
            return Collections.emptyList();
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(startTable);
        visited.add(startTable);

        boolean pathFound = false;

        while (!queue.isEmpty()) {
            String currentTable = queue.poll();

            if (currentTable.equals(endTable)) {
                pathFound = true;
                break;
            }

            List<CompositeRelationship> relationships = schemaGraph.getOrDefault(currentTable, Collections.<CompositeRelationship>emptyList());
            for (CompositeRelationship rel : relationships) {
                if (!visited.contains(rel.targetTable)) {
                    visited.add(rel.targetTable);
                    parentMap.put(rel.targetTable, currentTable);
                    queue.add(rel.targetTable);
                }
            }
        }

        if (!pathFound) return Collections.emptyList();

        List<String> path = new LinkedList<>();
        String step = endTable;
        while (step != null) {
            path.add(0, step);
            step = parentMap.get(step);
        }
        return path;
    }

    public static void main(String[] args) {
        InlineDDLPathFinder finder = new InlineDDLPathFinder();

        // 1. Point this to your target Windows Directory
        String windowsFolderPath = "C:\\Users\\srini\\Documents\\DatabaseDDLs"; 

        System.out.println("Scanning folder for Inline DDLs: " + windowsFolderPath + "\n");
        finder.loadInlineDDLsFromFolder(windowsFolderPath);
        System.out.println("\nSchema Graph built successfully.\n");

        String start = "USERS";
        String end = "CATEGORIES";

        System.out.println("Finding simplest path from " + start + " to " + end + "...");
        List<String> path = finder.findSimplestPath(start, end);

        if (path.isEmpty()) {
            System.out.println("No relationship path found.");
        } else {
            System.out.println("\nPath Found (" + (path.size() - 1) + " hops):");
            System.out.println(String.join(" ➔ ", path));
        }
    }
}