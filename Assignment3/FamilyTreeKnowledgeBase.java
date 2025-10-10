import java.util.*;

public class FamilyTreeKnowledgeBase {
     // Knowledge Base: father and mother relations
    static Map<String, List<String>> father = new HashMap<>();
    static Map<String, List<String>> mother = new HashMap<>();

    // Add Facts
    public static void addFather(String f, String c) {
        father.computeIfAbsent(f, k -> new ArrayList<>()).add(c);
    }

    public static void addMother(String m, String c) {
        mother.computeIfAbsent(m, k -> new ArrayList<>()).add(c);
    }

    // Rule 1: Get Children
    public static List<String> getChildren(String p) {
        List<String> result = new ArrayList<>();
        if (father.containsKey(p)) result.addAll(father.get(p));
        if (mother.containsKey(p)) result.addAll(mother.get(p));
        return result;
    }

    // Rule 2: Get Siblings
    public static List<String> getSiblings(String name) {
        Set<String> result = new HashSet<>();

        // Check in father map
        for (Map.Entry<String, List<String>> entry : father.entrySet()) {
            List<String> children = entry.getValue();
            if (children.contains(name)) {
                for (String c : children) {
                    if (!c.equals(name)) result.add(c);
                }
            }
        }

        // Check in mother map
        for (Map.Entry<String, List<String>> entry : mother.entrySet()) {
            List<String> children = entry.getValue();
            if (children.contains(name)) {
                for (String c : children) {
                    if (!c.equals(name)) result.add(c);
                }
            }
        }

        return new ArrayList<>(result);
    }

    // Rule 3: Get Grandchildren
    public static List<String> getGrandchildren(String gp) {
        List<String> result = new ArrayList<>();
        for (String child : father.getOrDefault(gp, new ArrayList<>())) {
            result.addAll(getChildren(child));
        }
        for (String child : mother.getOrDefault(gp, new ArrayList<>())) {
            result.addAll(getChildren(child));
        }
        return result;
}


    // Main Program: Query Parser
    public static void main(String[] args) {
        // Adding Facts
        addFather("Mohan", "Ram");
        addFather("Mohan", "Shyam");
        addFather("Mohan", "Gita");

        addMother("Sita", "Ram");
        addMother("Sita", "Shyam");
        addMother("Sita", "Gita");

        addFather("Shyam", "Anu");
        addFather("Shyam", "Raju");

        System.out.println("Family Tree Knowledge Base Parser");
        System.out.println("----------------------------------");
        System.out.println("Queries Supported:");
        System.out.println("1. children <name>");
        System.out.println("2. siblings <name>");
        System.out.println("3. grandchildren <name>");
        System.out.println("Type 'exit' to quit.\n");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter query: ");
            String query = scanner.next();
            if (query.equalsIgnoreCase("exit")) break;

            String name = scanner.next();

            if (query.equalsIgnoreCase("children")) {
                List<String> res = getChildren(name);
                if (res.isEmpty())
                    System.out.println(name + " has no children.");
                else {
                    System.out.print("Children of " + name + ": ");
                    for (String c : res) System.out.print(c + " ");
                    System.out.println();
                }
            }
            else if (query.equalsIgnoreCase("siblings")) {
                List<String> res = getSiblings(name);
                if (res.isEmpty())
                    System.out.println(name + " has no siblings.");
                else {
                    System.out.print("Siblings of " + name + ": ");
                    for (String s : res) System.out.print(s + " ");
                    System.out.println();
                }
            }
            else if (query.equalsIgnoreCase("grandchildren")) {
                List<String> res = getGrandchildren(name);
                if (res.isEmpty())
                    System.out.println(name + " has no grandchildren.");
                else {
                    System.out.print("Grandchildren of " + name + ": ");
                    for (String g : res) System.out.print(g + " ");
                    System.out.println();
                }
            }
            else {
                System.out.println("Unknown query type!");
            }
        }

        scanner.close();
    }
}