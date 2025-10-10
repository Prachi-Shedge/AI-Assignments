
import java.util.*;

public class BackwardChaining {

    static class Rule {
        List<String> conditions;
        String conclusion;

        Rule(List<String> conditions, String conclusion) {
            this.conditions = conditions;
            this.conclusion = conclusion;
        }
    }

    // Recursive backward chaining function
    static boolean backwardChain(String goal, Set<String> facts, List<Rule> rules, Set<String> visited) {
        if (facts.contains(goal)) {
            return true; // Goal already known
        }

        if (visited.contains(goal)) {
            return false; // Avoid infinite loop
        }
        visited.add(goal);

        // Check rules whose conclusion matches the goal
        for (Rule rule : rules) {
            if (rule.conclusion.equals(goal)) {
                boolean allTrue = true;
                for (String condition : rule.conditions) {
                    if (!backwardChain(condition, facts, rules, visited)) {
                        allTrue = false;
                        break;
                    }
                }
                if (allTrue) {
                    facts.add(goal); // inferred fact
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        // Knowledge Base: Rules
        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule(Arrays.asList("A"), "B"));
        rules.add(new Rule(Arrays.asList("B"), "C"));
        rules.add(new Rule(Arrays.asList("C", "D"), "E"));
        rules.add(new Rule(Arrays.asList("E"), "Goal"));

        // Initial facts
        Set<String> facts = new HashSet<>();
        facts.add("A");
        facts.add("D");

        String query = "Goal";
        System.out.println("Initial Facts: " + facts);
        System.out.println("Trying to prove: " + query);

        boolean result = backwardChain(query, facts, rules, new HashSet<>());

        if (result) {
            System.out.println("✅ Goal proven! Final Facts: " + facts);
        } else {
            System.out.println("❌ Goal cannot be proven.");
        }
    }
}
