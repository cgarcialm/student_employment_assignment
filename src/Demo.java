import com.google.ortools.Loader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class implements a demo for the Student Employment Assignment solver.
 *
 * @author cgarcialm
 * @version 1.0
 */
public class Demo {
    private static final String PROF_PREF_PATH =
            "input/professors_preferences.txt";  // Path to the file containing
                                                 // professors' preferences.
    private static final String STUD_PREF_PATH =
            "input/students_preferences.txt";  // Path to the file containing
                                               // students' preferences.
    private static final String HOURS_PER_CLASS_PATH =
            "input/required_class_hours.txt";  // Path to the file containing
                                               // class hours.
   private static final String CLASS_SLOTS_PATH =
           "input/class_slots.txt";  // Path to the file containing class
    private static final String REGISTERED_SLOTS_PATH =
            "input/students_registered_slots.txt";  // Path to the file
                                                    // containing registered
                                                    // slots.


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
        Scanner scanner = new Scanner(new File(filePath));
        Scanner line;

        while (scanner.hasNextLine()) {
            line = new Scanner(scanner.nextLine());
            line.useDelimiter(",|\\s+"); // Use comma, spaces, or newline as
            // delimiters
            line.nextInt();
            line.next();
            int[] preferencesRow = new int[nClasses];
            int classNum = 0;
            while(line.hasNextInt()) {
                preferencesRow[classNum] = line.nextInt(); // First
                // preference value
                classNum++;
            }
            preferencesList.add(preferencesRow);
            line.close();
        }

        // Convert the List to a 2D array
        int[][] preferences = new int[preferencesList.size()][nClasses];
        for (int i = 0; i < preferencesList.size(); i++) {
            preferences[i] = preferencesList.get(i);
        }

        scanner.close();
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
        Scanner scanner = new Scanner(new File(HOURS_PER_CLASS_PATH));
        Scanner line;

        while (scanner.hasNextLine()) {
            line = new Scanner(scanner.nextLine());
            line.useDelimiter(",|\\s+"); // Use comma, spaces, or newline as
            // delimiters
            line.nextInt(); // Skip the first integer (class ID)
            line.next();
            int hours = line.nextInt();
            hoursList.add(hours);
            line.close();
        }

        // Convert the List to an array
        int[] hoursPerClass = new int[hoursList.size()];
        for (int i = 0; i < hoursList.size(); i++) {
            hoursPerClass[i] = hoursList.get(i);
        }

        scanner.close();
        return hoursPerClass;
    }

    /**
     * Parses classes' slots from a text file and returns them as a 2D array of
     * integers arrays.
     *
     * @return A 2D array of integers representing the slot in each weekday for
     * each class.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static int[][] getSlotsPerClass() throws IOException {
        final int NUM_WEEKDAYS = 5;
        List<int[]> slotsPerClass = new ArrayList<>();
        Scanner scanner = new Scanner(new File(CLASS_SLOTS_PATH));
        Scanner line;

        while (scanner.hasNextLine()) {
            line = new Scanner(scanner.nextLine());
            line.useDelimiter(",|\\s+"); // Use comma, spaces, or newline as
            // delimiters
            line.nextInt(); // Skip the first integer (class ID)
            line.next();
            int[] slots = new int[NUM_WEEKDAYS];
            for (int d = 0; d < NUM_WEEKDAYS; d++) {
                slots[d] = line.nextInt();
            }
            slotsPerClass.add(slots);
            line.close();
        }

        // Convert the List to an array
        int[][] hoursPerClass = new int[slotsPerClass.size()][NUM_WEEKDAYS];
        for (int i = 0; i < slotsPerClass.size(); i++) {
            hoursPerClass[i] = slotsPerClass.get(i);
        }

        scanner.close();
        return hoursPerClass;
    }

    /**
     * Reads and parses registered slots from a text file and returns them as a 3D integer array.
     *
     * @return A 3D integer array representing registered slots where the first dimension corresponds
     *         to rows, the second dimension corresponds to weekdays, and the third dimension contains
     *         slot values.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static int[][][] getRegisteredSlots() throws IOException {
        final int NUM_WEEKDAYS = 5;
        List<List<List<Integer>>> registeredSlotsList = new ArrayList<>();

        Scanner scanner = new Scanner(new File(REGISTERED_SLOTS_PATH));
        int numRows = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            int row = Integer.parseInt(parts[0].trim());
            int col = Integer.parseInt(parts[1].trim());
            registeredSlotsList.add(new ArrayList<>());
            List<Integer> slots = new ArrayList<>();

            if (parts.length == 3) {

                if (col >= 0 && col < NUM_WEEKDAYS) {
                    String[] slotValues = parts[2].split("\\s+");

                    for (String slotValue : slotValues) {
                        if (slotValue.length() > 0)
                            slots.add(Integer.parseInt(slotValue.trim()));
                    }

                }
            }
            registeredSlotsList.get(row).add(slots);
            numRows = row;
        }
        numRows++;
        scanner.close();

        // Convert registeredSlotsList to a 3D array
        int[][][] registeredSlots = new int[numRows][NUM_WEEKDAYS][];

        for (int row = 0; row < numRows; row++) {
            List<List<Integer>> rowList = registeredSlotsList.get(row);

            for (int col = 0; col < NUM_WEEKDAYS; col++) {
                if (!rowList.isEmpty()) {
                    List<Integer> slotsList = rowList.get(col);

                    registeredSlots[row][col] = new int[slotsList.size()];

                    for (int i = 0; i < slotsList.size(); i++) {
                        registeredSlots[row][col][i] = slotsList.get(i);
                    }
                }
            }
        }

        return registeredSlots;
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
        int[][][] registeredSlotsPerStudentDay = getRegisteredSlots();
        int[][] slotsPerClass = getSlotsPerClass();

        final StudentEmploymentAssignment problem = new
                StudentEmploymentAssignment(
                        profPreferences, studPreferences,
                        hoursPerClass,
                        registeredSlotsPerStudentDay, slotsPerClass
        );
        problem.solve();
        problem.printSolution();
    }
}
