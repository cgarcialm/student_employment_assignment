import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import java.util.stream.IntStream;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

/**
 * This class represents a Student Employment Assignment problem solver.
 */
public class StudentEmploymentAssignment {
   private final int numStudents;
   private final int[] allStudents;
   private final int numClasses;
   private final int[] allClasses;
   private final int[][] classesPreferences;
   MPVariable[][] assignments;
   MPSolver solver;
   MPObjective objective;

   /**
    * Constructor to initialize the StudentEmploymentAssignment instance with class preferences.
    *
    * @param classesPreferences The 2D array of student preferences for classes.
    */
   public StudentEmploymentAssignment(final int[][] classesPreferences) {
      this.numClasses = classesPreferences[0].length;
      this.numStudents = classesPreferences.length;
      this.classesPreferences = classesPreferences;
      this.allClasses = IntStream.range(0, this.numClasses).toArray();
      this.allStudents = IntStream.range(0, this.numStudents).toArray();
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
         final MPConstraint constraint = this.solver.makeConstraint(0.0, 1.0, "");
         for (final int s : this.allStudents) {
            constraint.setCoefficient(this.assignments[s][c], 1.0);
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
            this.objective.setCoefficient(this.assignments[s][c], (double)this.classesPreferences[s][c]);
         }
      }
      this.objective.setMaximization();
   }

   /**
    * Prints the solution to the problem.
    *
    * @param resultStatus The result status of the solver.
    */
   private void printSolution(final MPSolver.ResultStatus resultStatus) {
      if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
         System.out.printf("Total satisfaction: " + this.objective.value() + "\n\n");
         for (final int s : this.allStudents) {
            boolean assigned = false;
            for (final int c : this.allClasses) {
               if (this.assignments[s][c].solutionValue() > 0.5) {
                  assigned = true;
                  System.out.printf("Student %d was assigned to class %d.\n", s, c);
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
    */
   public void solve() {
      this.createSolver();
      this.createVariables();
      this.addExactlyOneStudentPerClass();
      this.createObjectiveFunction();
      final MPSolver.ResultStatus resultStatus = this.solver.solve();
      this.printSolution(resultStatus);
   }

   /**
    * Main method to run the Student Employment Assignment problem solver.
    *
    * @param args Command-line arguments (not used).
    */
   public static void main(final String[] args) {
      Loader.loadNativeLibraries();
      final int[][] preferences = { { 1, 2 }, { 2, 1 }, { 1, 1 }, { 3, 2 } };
      final StudentEmploymentAssignment problem = new StudentEmploymentAssignment(preferences);
      problem.solve();
   }
}
