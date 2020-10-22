package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        ImList<Clause> clauses = formula.getClauses();
        Environment env = new Environment();
        return solve(clauses, env);
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        // DPLL Algorithm: Find the smallest clause, check T and F values, recursively call solve()

        // If there are no clauses, the formula is trivially satisfiable;
        if (clauses.isEmpty()) return env;


        Clause smallest = clauses.first();
        for (Clause clause : clauses) {
            // If there is an empty clause, the clause list is unsatisfiable -- fail and backtrack.
            // Use an empty clause evaluated to FALSE based on the variable binding in the environment.
            if (clause.isEmpty()) {
                return null;
            }

            // Otherwise, find the smallest clause (by number of literals)
            // If the clause has only one literal, bind its variable in the environment so that the
            // clause is satisfied.
            if (clause.size() < smallest.size()) {
                smallest = clause;
            }
            if (smallest.isUnit()) {
                smallest = clause;
                break;
            }
        }

        // Pick an arbitrary literal from the smallest clause
        Literal literal = smallest.chooseLiteral();
        Variable var = literal.getVariable();

        // If the clause has only one literal,
        if (smallest.isUnit()) {
            Environment newEnv;
            // bind its variable in the environment so that the clause is Satisfied,
            // substitute for the variable in all the other clauses and recursively call solve()
            if (literal instanceof PosLiteral)
                newEnv = solve(substitute(clauses, literal), env.putTrue(var));
            else newEnv = solve(substitute(clauses, literal), env.putFalse(var));

            return newEnv;
        }
        else {
            Environment newEnv;
            env = env.putTrue(var);
            // Set literal to TRUE, substitute for it in all the clauses, then solve recursively
            newEnv = solve(substitute(clauses, literal), env);
            if (newEnv == null) return solve(substitute(clauses, literal.getNegation()), env);
            else return newEnv;
        }
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {
        ImList<Clause> list = new EmptyImList<>();
        Clause clause = new Clause();

        for (Clause c : clauses) {
            if (c != null) clause = c.reduce(l);
            if (clause != null) list = list.add(clause);
        }

        return list;
    }

}
