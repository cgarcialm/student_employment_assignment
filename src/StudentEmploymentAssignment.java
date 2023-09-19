import com.google.ortools.linearsolver.MPConstraint;

import java.util.Arrays;
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
    private static final int MAX_HOURS_PER_STUDENT = 20;  // Maximum hours per
                                                                    // student.
    private static final double REL_WEIGHT_PROF_PREFERENCES = 0.75;  // Relative
                                            // weight for professor preferences.
    private static final double REL_WEIGHT_STUD_PREFERENCES = 0.25;  // Relative
                                            // weight for student preferences.
    private final int numStudents;  // Total number of students.
    private final int[] allStudents;  // Array containing all student indices.
    private final int numClasses;  // Total number of classes.
    private final int[] allClasses;  // Array containing all class indices.
    private final int[][] profPreferences;  // Professor preferences for
    // classes.
    private final int[][] studPreferences;  // Student preferences for classes.
    private final int[] hoursPerClass;  // Hours per class.

    int[][][] registeredSlotsPerStudentDay; // Registered slots for each
                                            // student on each day
    int[][] slotsPerClass; // Assigned slots for each class
    MPVariable[][] assignments;  // Matrix of assignment variables.
    MPSolver solver;  // Solver for the optimization problem.
    MPObjective objective;  // Objective function for optimization.
    MPSolver.ResultStatus resultStatus;  // Result status of the optimization.

    /**
     * Constructor to initialize the StudentEmploymentAssignment instance with
     * class preferences and scheduling constraints.
     *
     * @param profPreferences The 2D array of professors' preferences for each
     *                        student for each class.
     * @param studPreferences The 2D array of students' preferences for each
     *                        class.
     * @param hoursPerClass The 1D array of required hours per class.
     * @param registeredSlotsPerStudentDay The 3D array representing registered
     *                                     slots for each student on each day.
     * @param slotsPerClass The 2D array representing assigned slots for each
     *                      class.
     */
    public StudentEmploymentAssignment(
            int[][] profPreferences,
            int[][] studPreferences,
            int[] hoursPerClass,
            int[][][] registeredSlotsPerStudentDay,
            int[][] slotsPerClass
    ) {
        this.numClasses = profPreferences[0].length;
        this.numStudents = profPreferences.length;
        this.profPreferences = profPreferences;
        this.studPreferences = studPreferences;
        this.hoursPerClass = hoursPerClass;
        this.registeredSlotsPerStudentDay = registeredSlotsPerStudentDay;
        this.slotsPerClass = slotsPerClass;
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

    /**
     * Adds the constraint that each student does not work more than 20 hours
     * per week.
     */
    private void addMaxHoursPerStudent() {
        for (final int s : this.allStudents) {
            final MPConstraint constraint = this.solver.makeConstraint(
                    0.0, MAX_HOURS_PER_STUDENT, ""
            );
            for (final int c : this.allClasses) {
                constraint.setCoefficient(
                        this.assignments[s][c], hoursPerClass[c]
                );
            }
        }
    }
    /**
     * Checks if a professor wants a specific student for a class.
     *
     * @param s The student index.
     * @param c The class index.
     * @return true if the professor wants the student for the class, false otherwise.
     */
    private boolean profWantsStudent(int s, int c) {
        return this.profPreferences[s][c] > 0;
    }

    /**
     * Checks if a student wants a specific class.
     *
     * @param s The student index.
     * @param c The class index.
     * @return true if the student wants the class, false otherwise.
     */
    private boolean studWantsClass(int s, int c) {
        return this.studPreferences[s][c] > 0;
    }

    /**
     * Checks if a student's slot is available for a class on every day the
     * class has slots assigned.
     *
     * @param s The student index.
     * @param c The class index.
     * @return true if the slots are available, false if student's already
     * registered.
     */
    private boolean isStudentSlotFree(int s, int c) {
        final int NUM_WEEKDAYS = 5;
        int[][] studentRegisteredSlots = registeredSlotsPerStudentDay[s];
        int[] classSlots = slotsPerClass[c];
        for (int d = 0; d < NUM_WEEKDAYS; d++) {
            if (classSlots[d] > 0) {
                if (Arrays.asList(studentRegisteredSlots[d]).contains(
                        classSlots[d])
                ) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates the objective function based on student preferences.
     */
    private void createObjectiveFunction() {
        this.objective = this.solver.objective();
        for (final int s : this.allStudents) {
            for (final int c : this.allClasses) {
                // If student is interested in the class
                boolean profWants = profWantsStudent(s, c);
                // If professor is interested in the student for this class
                boolean studWants = studWantsClass(s, c);
                // If student is free for class's assigned slots
                boolean studIsFree = isStudentSlotFree(s, c);
                if (profWants & studWants & studIsFree) {
                    this.objective.setCoefficient(
                            this.assignments[s][c],
                            this.profPreferences[s][c]
                                    * REL_WEIGHT_PROF_PREFERENCES
                    );
                    this.objective.setCoefficient(
                            this.assignments[s][c],
                            this.studPreferences[s][c]
                                    * REL_WEIGHT_STUD_PREFERENCES
                    );
                }
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
}
