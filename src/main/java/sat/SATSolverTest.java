package sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import sat.env.Environment;

import sat.formula.*;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();



	
    public static void main(String[] args) {
        String file = String.join("", args);
        try {
            // 1. Read file
            Scanner reader = new Scanner(new File(file));
            String ln;
            int clauses = 0;
            while ((ln = reader.nextLine()) != null) {
                String[] lnArr = ln.split("\\s+");
                if (ln.charAt(0) == 'p') {
                    clauses = Integer.parseInt(lnArr[3].trim());
                    break;
                }
            }

            // 2. Extract the values into clauses into a formula
            Formula formula = new Formula();
            while (formula.getSize() != clauses) {
                ln = reader.nextLine();
                if (ln.length() > 0) {
                    String[] lnArr = ln.trim().split("\\s+");
                    Clause clause = new Clause();

                    for (String str : lnArr) {
                        int lit = Integer.parseInt(str);

                        // skip an empty line
                        if (lit == 0) break;

                        // create a variable of the literal
                        Literal literal = PosLiteral.make(Integer.toString(Math.abs(lit)));

                        if (lit < 0) clause = clause.add(literal.getNegation());
                        else if (lit > 0) clause = clause.add(literal);
                    }
                    formula = formula.addClause(clause);
                }
            }

            reader.close();

            // 3. Run the solver
            //System.out.println(formula);
            long solveStart = System.nanoTime();
            Environment env = SATSolver.solve(formula);
            long solveEnd = System.nanoTime();
            printTime("Solving", solveEnd, solveStart);

            // 4. Export results into file
            String fileName = "BoolAssignment.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            if (env == null) System.out.println("Formula Unsatisfiable");
            else {
                System.out.println("Formula Satisfiable");
                String bindings = env.toString();
                bindings = bindings.substring(bindings.indexOf("[") + 1, bindings.indexOf("]"));
                bindings.replaceAll("->", ":");
                String[] bindingsArr = bindings.split(", ");

                for (String binding : bindingsArr) {
                    writer.append(binding + "\n");
                }
                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public static void printTime(String type, long endTime, long startTime) {
        System.out.println(type + " Time: " + ((endTime - startTime)/1000000.0) + "ms");
    }
	
    public void testSATSolver1(){
    	// (a v b)
    	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())  
    			|| Bool.TRUE == e.get(b.getVariable())	);
    	
*/    	
    }
    
    
    public void testSATSolver2(){
    	// (~a)
    	Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/    	
    }
    
    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
    
    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
    
    
    
}