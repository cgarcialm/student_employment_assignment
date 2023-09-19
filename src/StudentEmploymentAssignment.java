import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;

import java.util.stream.IntStream;

import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

/**
 * This class represents a Student Employment Assignment problem solver.
 *
 * @author cgarcialm
 * @version 1.0
 */
public class StudentEmploymentAssignment {
    private static final int MAX_HOURS = 20;
    private static final int HOURS_PER_CLASS = 10;
    private final int numStudents;
    private final int[] allStudents;
    private final int numClasses;
    private final int[] allClasses;
    private final int[][] classesPreferences;
    MPVariable[][] assignments;
    MPSolver solver;
    MPObjective objective;
    MPSolver.ResultStatus resultStatus;

    /**
     * Constructor to initialize the StudentEmploymentAssignment instance with
     * class preferences.
     *
     * @param classesPreferences The 2D array of student preferences for
     *                           classes.
     */
    public StudentEmploymentAssignment(final int[][] classesPreferences) {
        this.numClasses = classesPreferences[0].length;
        this.numStudents = classesPreferences.length;
        this.classesPreferences = classesPreferences;
        this.allClasses = IntStream.range(0, this.numClasses).toArray();
        this.allStudents = IntStream.range(0, this.numStudents).toArray();
    }

    /**
     * Getter of the assignments array
     *
     * @return 2D array of assignments for each student, class
     */
    public MPVariable[][] getAssignments() {
        return assignments;
    }

    /**
     * Creates the solver instance for the problem.
     */
    private void createSolver() {
        this.solver = MPSolver.createSolver("SCIP");
        if (this.solver == null) {
            System.out.println("Could not create solver SCIP");
        }
    }

    /**
     * Creates the binary decision variables for student-class assignments.
     */
    private void createVariables() {
        this.assignments = new MPVariable[this.numStudents][this.numClasses];
        for (final int s : this.allStudents) {
            for (final int c : this.allClasses) {
                this.assignments[s][c] = this.solver.makeIntVar(0.0, 1.0, "");
            }
        }
    }

    /**
     * Adds the constraint that each class is assigned to exactly one student.
     */
    private void addExactlyOneStudentPerClass() {
        for (final int c : this.allClasses) {
            final MPConstraint constraint = this.solver.makeConstraint(
                    0.0, 1.0, ""
            );
            for (final int s : this.allStudents) {
                constraint.setCoefficient(this.assignments[s][c], 1.0);
            }
        }
    }

    private void addMaxHoursPerStudent() {
        for (final int s : this.allStudents) {
            final MPConstraint constraint = this.solver.makeConstraint(
                    0.0, MAX_HOURS, ""
            );
            for (final int c : this.allClasses) {
                constraint.setCoefficient(
                        this.assignments[s][c], HOURS_PER_CLASS
                );
            }
        }
    }

    /**
     * Creates the objective function based on student preferences.
     */
    private void createObjectiveFunction() {
        this.objective = this.solver.objective();
        for (final int s : this.allStudents) {
            for (final int c : this.allClasses) {
                this.objective.setCoefficient(
                        this.assignments[s][c],
                        this.classesPreferences[s][c]
                );
            }
        }
        this.objective.setMaximization();
    }

    /**
     * Prints the solution to the problem.
     */
    public void printSolution() {
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL ||
                resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            System.out.printf("Total satisfaction: " + this.objective.value()
                    + "\n\n");
            for (final int s : this.allStudents) {
                boolean assigned = false;
                for (final int c : this.allClasses) {
                    if (this.assignments[s][c].solutionValue() > 0.5) {
                        assigned = true;
                        System.out.printf("Student %d was assigned to " +
                                "class %d.\n", s, c);
                    }
                }
                if (!assigned) {
                    System.out.printf("Student %d was not assigned.\n", s);
                }
            }
        } else {
            System.err.println("No solution found.");
        }
    }

    /**
     * Solves the Student Employment Assignment problem.
     *
     * @return The result status of the solver.
     */
    public MPSolver.ResultStatus solve() {
        this.createSolver();
        this.createVariables();
        this.addExactlyOneStudentPerClass();
        this.addMaxHoursPerStudent();
        this.createObjectiveFunction();
        resultStatus = this.solver.solve();

        return resultStatus;
    }

    /**
     * Main method to run the Student Employment Assignment problem solver.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(final String[] args) {
        Loader.loadNativeLibraries();
        final int[][] preferences = {{1, 2}, {2, 1}, {1, 1}, {3, 2}};
        final StudentEmploymentAssignment problem = new
                StudentEmploymentAssignment(preferences);
        problem.solve();
        problem.printSolution();
    }
}
