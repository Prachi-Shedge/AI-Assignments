import java.util.*;
import java.util.stream.Collectors;

public class Assignment_2 {

    public interface Constraint<V, D> {
        Set<V> scope();
        
        boolean isSatisfied(Map<V, D> assignment);
    }


    public static class NotEqualConstraint<V, D> implements Constraint<V, D> {
        private final V a, b;
        public NotEqualConstraint(V a, V b) { this.a = a; this.b = b; }
        @Override public Set<V> scope() { return Set.of(a, b); }
        @Override public boolean isSatisfied(Map<V, D> asg) {
            if (!asg.containsKey(a) || !asg.containsKey(b)) return true; // not yet conflicting
            return !Objects.equals(asg.get(a), asg.get(b));
        }
        public V other(V x) { return x.equals(a) ? b : a; }
        public V left() { return a; }
        public V right() { return b; }
    }

    /** CSP model: variables, domains, and constraints. */
    public static class CSP<V, D> {
        private final List<V> variables;
        private final Map<V, List<D>> domains;
        private final Map<V, List<Constraint<V, D>>> constraintsByVar;

        public CSP(List<V> variables, Map<V, List<D>> domains) {
            this.variables = new ArrayList<>(variables);
            this.domains = new HashMap<>();
            for (V v : variables) {
                List<D> dom = domains.get(v);
                if (dom == null) throw new IllegalArgumentException("Missing domain for: " + v);
                this.domains.put(v, new ArrayList<>(dom));
            }
            this.constraintsByVar = new HashMap<>();
            for (V v : variables) constraintsByVar.put(v, new ArrayList<>());
        }

        public void addConstraint(Constraint<V, D> c) {
            for (V v : c.scope()) {
                if (!constraintsByVar.containsKey(v))
                    throw new IllegalArgumentException("Constraint on unknown var: " + v);
                constraintsByVar.get(v).add(c);
            }
        }

        public List<V> getVariables() { return variables; }
        public List<D> getDomain(V v) { return domains.get(v); }
        public void setDomain(V v, List<D> domain) { domains.put(v, new ArrayList<>(domain)); }
        public List<Constraint<V, D>> constraintsOf(V v) { return constraintsByVar.get(v); }

        /** All neighbors of v (vars sharing a constraint). */
        public Set<V> neighbors(V v) {
            Set<V> n = new HashSet<>();
            for (Constraint<V, D> c : constraintsByVar.get(v)) {
                for (V u : c.scope()) if (!u.equals(v)) n.add(u);
            }
            return n;
        }

        /** Check if current partial assignment is consistent for a particular var. */
        public boolean isConsistent(V v, Map<V, D> assignment) {
            for (Constraint<V, D> c : constraintsByVar.get(v)) {
                if (!c.isSatisfied(assignment)) return false;
            }
            return true;
        }
    }


    public static class BacktrackingSolver<V, D> {

        public Optional<Map<V, D>> solve(CSP<V, D> csp) {
            // Make a deep copy of domains for in-place AC-3 pruning.
            Map<V, List<D>> localDomains = deepCopyDomains(csp);
            if (!ac3(csp, localDomains)) return Optional.empty();
            return backtrack(new HashMap<>(), csp, localDomains);
        }

        private Optional<Map<V, D>> backtrack(Map<V, D> assignment, CSP<V, D> csp, Map<V, List<D>> domains) {
            if (assignment.size() == csp.getVariables().size()) return Optional.of(assignment);

            V var = selectUnassignedVariable(assignment, csp, domains); // MRV
            for (D value : orderDomainValues(var, csp, domains, assignment)) { // LCV
                assignment.put(var, value);
                if (csp.isConsistent(var, assignment)) {
                    // Save domains snapshot
                    Map<V, List<D>> saved = deepCopy(domains);
                    // Forward inference: AC-3 with var assigned
                    domains.put(var, List.of(value));
                    if (ac3(csp, domains)) {
                        Optional<Map<V, D>> result = backtrack(assignment, csp, domains);
                        if (result.isPresent()) return result;
                    }
                    // Restore on failure
                    domains.clear(); domains.putAll(saved);
                }
                assignment.remove(var);
            }
            return Optional.empty();
        }

    
        private V selectUnassignedVariable(Map<V, D> assignment, CSP<V, D> csp, Map<V, List<D>> domains) {
            return csp.getVariables().stream()
                    .filter(v -> !assignment.containsKey(v))
                    .min(Comparator.comparingInt(v -> domains.get(v).size()))
                    .orElseThrow();
        }

        private List<D> orderDomainValues(V var, CSP<V, D> csp, Map<V, List<D>> domains, Map<V, D> assignment) {
            List<D> values = new ArrayList<>(domains.get(var));
            return values.stream()
                    .sorted(Comparator.comparingInt(val -> countConflicts(var, val, csp, domains, assignment)))
                    .collect(Collectors.toList());
        }

        private int countConflicts(V var, D val, CSP<V, D> csp, Map<V, List<D>> domains, Map<V, D> assignment) {
            int conflicts = 0;
            Map<V, D> trial = new HashMap<>(assignment);
            trial.put(var, val);
            for (V n : csp.neighbors(var)) {
                if (assignment.containsKey(n)) continue;
                for (D nVal : domains.get(n)) {
                    trial.put(n, nVal);
                    if (!csp.isConsistent(n, trial)) conflicts++;
                }
                trial.remove(n);
            }
            return conflicts;
        }

        /** AC-3 algorithm: enforce arc consistency on all binary constraints. */
        private boolean ac3(CSP<V, D> csp, Map<V, List<D>> domains) {
            Deque<AbstractMap.SimpleEntry<V, V>> queue = new ArrayDeque<>();

            // Initialize with all arcs (Xi, Xj) for constraints involving pairs.
            for (V xi : csp.getVariables()) {
                for (V xj : csp.neighbors(xi)) queue.add(new AbstractMap.SimpleEntry<>(xi, xj));
            }

            while (!queue.isEmpty()) {
                var arc = queue.poll();
                V xi = arc.getKey(), xj = arc.getValue();
                if (revise(xi, xj, csp, domains)) {
                    if (domains.get(xi).isEmpty()) return false;
                    for (V xk : csp.neighbors(xi)) {
                        if (!xk.equals(xj)) queue.add(new AbstractMap.SimpleEntry<>(xk, xi));
                    }
                }
            }
            return true;
        }

        /** Remove values from domain(Xi) that have no support in domain(Xj). */
        private boolean revise(V xi, V xj, CSP<V, D> csp, Map<V, List<D>> domains) {
            boolean revised = false;
            List<D> di = new ArrayList<>(domains.get(xi));
            List<D> dj = domains.get(xj);

            // Only consider constraints that include both xi and xj.
            List<Constraint<V, D>> both = csp.constraintsOf(xi).stream()
                    .filter(c -> c.scope().contains(xj))
                    .collect(Collectors.toList());

            for (D vi : di) {
                boolean supported = false;
                for (D vj : dj) {
                    Map<V, D> asg = Map.of(xi, vi, xj, vj);
                    boolean ok = true;
                    for (Constraint<V, D> c : both) {
                        if (!c.isSatisfied(asg)) { ok = false; break; }
                    }
                    if (ok) { supported = true; break; }
                }
                if (!supported) {
                    domains.get(xi).remove(vi);
                    revised = true;
                }
            }
            return revised;
        }

        private Map<V, List<D>> deepCopyDomains(CSP<V, D> csp) {
            Map<V, List<D>> copy = new HashMap<>();
            for (V v : csp.getVariables()) copy.put(v, new ArrayList<>(csp.getDomain(v)));
            return copy;
        }

        private Map<V, List<D>> deepCopy(Map<V, List<D>> src) {
            Map<V, List<D>> out = new HashMap<>();
            for (var e : src.entrySet()) out.put(e.getKey(), new ArrayList<>(e.getValue()));
            return out;
        }
    }

    /*  Example: Australia Map-Coloring   */

    public static void main(String[] args) {
        // Variables (regions)
        List<String> vars = List.of("WA","NT","SA","Q","NSW","V","T");
        // Domain: 3 colors
        List<String> colors = List.of("Red","Green","Blue");

        Map<String, List<String>> domains = new HashMap<>();
        for (String v : vars) domains.put(v, colors);

        CSP<String, String> csp = new CSP<>(vars, domains);

        // Adjacency (undirected), add NotEqual constraints for each edge
        addDiff(csp, "WA","NT");
        addDiff(csp, "WA","SA");
        addDiff(csp, "NT","SA");
        addDiff(csp, "NT","Q");
        addDiff(csp, "SA","Q");
        addDiff(csp, "SA","NSW");
        addDiff(csp, "SA","V");
        addDiff(csp, "Q","NSW");
        addDiff(csp, "NSW","V");
        // Tasmania (T) has no land neighbors

        BacktrackingSolver<String, String> solver = new BacktrackingSolver<>();
        Optional<Map<String, String>> sol = solver.solve(csp);

        if (sol.isPresent()) {
            System.out.println("Solution:");
            sol.get().forEach((k,v) -> System.out.println("  " + k + " -> " + v));
        } else {
            System.out.println("No solution.");
        }
    }

    private static void addDiff(CSP<String, String> csp, String a, String b) {
        csp.addConstraint(new NotEqualConstraint<>(a, b));
        csp.addConstraint(new NotEqualConstraint<>(b, a)); 
    }
}
