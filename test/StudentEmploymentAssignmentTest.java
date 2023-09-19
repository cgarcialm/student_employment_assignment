import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the StudentEmploymentAssignment class.
 */
class StudentEmploymentAssignmentTest {

    /**
     * Retrieves the student-class assignments from the solver based on the
     * given preferences.
     *
     * @param preferences The 2D array of student preferences for classes.
     * @return A 2D array representing the student-class assignments, where 1
     * indicates assignment and 0 indicates no assignment.
     */
    private int[][] getSolverAssignments(int[][] preferences) {
        // Load native libraries for OR-Tools.
        Loader.loadNativeLibraries();

        // Create an instance of the StudentEmploymentAssignment class with the
        // specified preferences.
        StudentEmploymentAssignment problem = new
                StudentEmploymentAssignment(preferences);

        // Solve the problem to obtain the student-class assignments.
        problem.solve();

        // Retrieve the assignments as MPVariable 2D array from the solver.
        MPVariable[][] probAssignments = problem.getAssignments();

        // Convert MPVariable assignments to a 2D integer array.
        int[][] assignments = new
                int[probAssignments.length][probAssignments[0].length];
        int studentIndex = 0;
        for (MPVariable[] studentAssignments : probAssignments) {
            int classIndex = 0;
            for (MPVariable sol : studentAssignments) {
                if (sol.solutionValue() > 0.5) {
                    assignments[studentIndex][classIndex] = 1;
                }
                classIndex++;
            }
            studentIndex++;
        }

        return assignments;
    }

    /**
     * Provides test cases with preferences and expected assignments for the
     * testSolveCase method.
     *
     * @return A stream of argument sets for the testSolveCase parameterized
     * test.
     */
    private static Stream<Arguments> providesPreferencesForTestSolve() {
        return Stream.of(
                Arguments.of(new int[][] {{1, 2}, {2, 1}, {1, 1}, {3, 2}},
                        new int[][] {{0, 1}, {0, 0}, {0, 0}, {1, 0}})
        );
    }

    /**
     * Parameterized test to validate the solve method with different
     * preferences and expected assignments.
     *
     * @param preferences The 2D array of student preferences for classes.
     * @param expected    The expected student-class assignments.
     */
    @ParameterizedTest
    @MethodSource("providesPreferencesForTestSolve")
    public void testSolveCase(int[][] preferences, int[][] expected) {
        // Get assignments from the solver based on the provided preferences.
        int[][] assignments = getSolverAssignments(preferences);

        // Verify that the obtained assignments match the expected assignments.
        assertTrue(Arrays.deepEquals(assignments, expected));
    }
}
