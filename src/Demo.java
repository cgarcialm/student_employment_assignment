import com.google.ortools.Loader;

public class Demo {
    /**
     * Main method to run the Student Employment Assignment problem solver.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(final String[] args) {
        Loader.loadNativeLibraries();
        final int[][] profPreferences = {{1, 2}, {2, 1}, {1, 1}, {3, 2}};
        final int[][] studPreferences = {{1, 2}, {2, 1}, {1, 1}, {3, 2}};
        final int[] hoursPerClass = {10, 10, 10};
        final StudentEmploymentAssignment problem = new
                StudentEmploymentAssignment(profPreferences, studPreferences,
                hoursPerClass);
        problem.solve();
        problem.printSolution();
    }
}
