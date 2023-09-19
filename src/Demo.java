import com.google.ortools.Loader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Demo {
    private static final String PROF_PREF_PATH =
            "input/professors_preferences.txt";
    private static final String STUD_PREF_PATH =
            "input/students_preferences.txt";
    private static final String HOURS_PER_CLASS_PATH =
            "input/class_hours.txt";

    /**
     * Parses professor/student preferences from a text file and returns them as
     * a 2D integer array.
     *
     * @param filePath The path to the input text file containing
     *                 professor/student preferences.
     * @param nClasses The number of classes
     * @return A 2D integer array representing professor/student preferences,
     * where each row corresponds
     *         to a professor/student and each column to a preference value.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static int[][] getPreferences(String filePath, int nClasses) throws
            IOException {
        List<int[]> preferencesList = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filePath))) {

            while (scanner.hasNextLine()) {
                Scanner line = new Scanner(scanner.nextLine());
                line.useDelimiter(",|\\s+"); // Use comma, spaces, or newline as
                // delimiters
                int[] preferencesRow = new int[nClasses];
                int classNum = 0;
                while(line.hasNextInt()) {
                    preferencesRow[classNum] = line.nextInt(); // First
                    // preference value
                    classNum++;
                }
                preferencesList.add(preferencesRow);
            }
        } catch (IOException e) {
            // Handle the IOException by rethrowing it
            throw e;
        }

        // Convert the List to a 2D array
        int[][] preferences = new int[preferencesList.size()][nClasses];
        for (int i = 0; i < preferencesList.size(); i++) {
            preferences[i] = preferencesList.get(i);
        }

        return preferences;
    }

    /**
     * Parses class hours from a text file and returns them as an array of
     * integers.
     *
     * @return An array of integers representing the hours for each class.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static int[] getHoursPerClass() throws IOException {
        List<Integer> hoursList = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(HOURS_PER_CLASS_PATH))) {

            while (scanner.hasNextLine()) {
                Scanner line = new Scanner(scanner.nextLine());
                line.useDelimiter(",|\\s+"); // Use comma, spaces, or newline as
                // delimiters
                line.nextInt(); // Skip the first integer (professor ID)
                line.next();
                int hours = line.nextInt();
                hoursList.add(hours);
            }
        } catch (IOException e) {
            // Handle the IOException by rethrowing it
            throw e;
        }

        // Convert the List to an array
        int[] hoursPerClass = new int[hoursList.size()];
        for (int i = 0; i < hoursList.size(); i++) {
            hoursPerClass[i] = hoursList.get(i);
        }

        return hoursPerClass;
    }


    /**
     * Main method to run the Student Employment Assignment problem solver.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static void main(final String[] args) throws IOException {
        Loader.loadNativeLibraries();
        final int[] hoursPerClass = getHoursPerClass();
        final int[][] profPreferences = getPreferences(PROF_PREF_PATH,
                hoursPerClass.length);
        final int[][] studPreferences = getPreferences(STUD_PREF_PATH,
                hoursPerClass.length);
        final StudentEmploymentAssignment problem = new
                StudentEmploymentAssignment(profPreferences, studPreferences,
                hoursPerClass);
        problem.solve();
        problem.printSolution();
    }
}
