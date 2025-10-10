import java.util.*;

public class ForwardChaining {

    static class Rule {
        List<String> conditions;
        String conclusion;

        Rule(List<String> conditions, String conclusion) {
            this.conditions = conditions;
            this.conclusion = conclusion;
        }
    }

    public static void main(String[] args) {
        // Knowledge Base: Rules
        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule(Arrays.asList("A"), "B"));
        rules.add(new Rule(Arrays.asList("B"), "C"));
        rules.add(new Rule(Arrays.asList("C", "D"), "E"));
        rules.add(new Rule(Arrays.asList("E"), "Goal"));

        // Initial Facts
        Set<String> facts = new HashSet<>();
        facts.add("A");
        facts.add("D");

        System.out.println("Initial Facts: " + facts);

        // Forward Chaining process
        boolean newFactAdded = true;
        while (newFactAdded) {
            newFactAdded = false;

            for (Rule rule : rules) {
                if (facts.containsAll(rule.conditions) && !facts.contains(rule.conclusion)) {
                    System.out.println("Applying Rule: " + rule.conditions + " -> " + rule.conclusion);
                    facts.add(rule.conclusion);
                    newFactAdded = true;
                }
            }
        }

        System.out.println("Final Facts: " + facts);

        if (facts.contains("Goal")) {
            System.out.println("✅ Goal reached!");
        } else {
            System.out.println("❌ Goal not reached.");
        }
    }
}
