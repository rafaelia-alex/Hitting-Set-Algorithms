
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class hw5 {

    public static int[][] generateRandomArray(int n, int m, int c) {

        System.out.println("Generating random array");
        System.out.println("");

        int[][] array = new int[m][c];

        Random rand = new Random();

        for (int i = 0; i < m; i++) {
            int num_of_elements = rand.nextInt(c) + 1; // from 1 to c
            for (int j = 0; j < num_of_elements; j++) {
                array[i][j] = rand.nextInt(n) + 1; // from 1 to n
            }

        }
        printArray(array, m, c);
        return array;
    }

    public static int[][] generateRandomArray2(int n, int m, int c) {

        System.out.println("Generating random array");
        System.out.println("");

        int[][] array = new int[m][c];

        Random rand = new Random();

        for (int i = 0; i < m; i++) {
            int num_of_elements = rand.nextInt(c-4) + 5; // from 5 to c
            for (int j = 0; j < num_of_elements; j++) {
                array[i][j] = rand.nextInt(n) + 1; // from 1 to n
            }

        }
        printArray(array, m, c);
        return array;
    }
    
    private static void printArray(int[][] array, int m, int c) {
        for (int i = 0; i < m; i++) {
            System.out.print("B" + i + "= { ");
            for (int j = 0; (j < c && array[i][j] != 0); j++) {

                System.out.print(array[i][j] + " ");
            }
            System.out.println("}");
        }
    }

    private static void printHittingSet(int[] hs, int k) {
        if (hs == null) {
            System.out.println("Hitting set was not found for k= " + k);
            return;
        }

        System.out.println("Hitting set was found for max k= " + k);
        System.out.print("{ ");
        for (int i = 0; i < hs.length; i++) {
            if (hs[i] != 0) {
                System.out.print(hs[i] + " ");
            }
        }
        System.out.println("}");
    }

    private static boolean timeout(long startTime) {
        if ((System.nanoTime() - startTime) > (60L * 60 * 1_000_000_000)) {
            return true;
        }
        return false;
    }

    public static int[] countOccurance(int[][] array, int n, int m, int c) {

        int[] newArray = new int[n + 1];

        for (int idx = 0; idx < n; idx++) {
            newArray[idx] = 0;
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < c; j++) {
                if (array[i][j] == 0) {
                    break;
                }
                newArray[array[i][j]]++;
            }
        }

        return newArray;
    }

    public static int[][] sortByOccurance(int[][] array, int n, int m, int c, int random) {
        int[] occurance = countOccurance(array, n, m, c);

        //sort row based on occurance - decreasing sort
        int[] row = new int[c];

        int pos = 0;
        for (int j = 0; j < c; j++) {
            if (array[random][j] != 0) {
                row[pos++] = array[random][j];
            }
        }

        for (int i = 1; i < pos - 1; i++) {
            for (int j = i; j < pos; j++) {
                if (occurance[row[j]] > occurance[row[i]]) {
                    int temp = row[i];
                    row[i] = row[j];
                    row[j] = temp;
                }
            }
        }

        for (int i = 0; i < c; i++) {
            if (row[i] == 0) {
                array[random][i] = 0;
            } else {
                array[random][i] = row[i];
            }
        }
        return array;
    }

    public static int sortBySize(int[][] array, int m, int c) {

        int[] sizes = new int[m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; (j < c && array[i][j] != 0); j++) {
                sizes[i]++;
            }
        }

        int min = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < m; i++) {
            if (sizes[i] < min) {
                min = sizes[i];
                index = i;
            }
        }
        return index;
    }

    public static int[][] deleteSets(int[][] array, int m, int c, int element) {
        int count = 0;
        boolean[] toDelete = new boolean[m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < c; j++) {
                if (array[i][j] != 0 && array[i][j] == element && !toDelete[i]) {
                    toDelete[i] = true;
                    count++;
                    break;
                }
            }
        }
        int[][] new_array = new int[m - count][c];
        int k = 0;

        for (int i = 0; i < m; i++) {           //for each set check if it is to be removed
            if (toDelete[i] == false) {                //if not to be removed add all of its elements in a new array
                for (int j = 0; j < c; j++) {
                    new_array[k][j] = array[i][j];
                }
                k++;
            }
        }

        return new_array;
    }

    public static int[] solve1(int[][] array, int n, int m, int c, int k, int[] hs, long startTime, int flag, int alg, Random r) {
        //if i am running first experinment and reached timeout
        if (timeout(startTime) && flag == 1) {
            return null;
        }

        if (m == 0 && k >= 0) {
            return hs;
        }
        if (k <= 0) {
            return null;
        }

        int random = r.nextInt(m);

        if (alg == 1) {
            for (int i = 0; i < c; i++) {
                if (array[random][i] != 0) {
                    hs[k - 1] = array[random][i];
                    int[][] new_array = deleteSets(array, m, c, array[random][i]);
                    int[] new_hs = hs.clone();
                    int[] result = solve1(new_array, n, new_array.length, c, k - 1, new_hs, startTime, flag, alg, r);

                    if (result != null) {
                        return result;
                    }

                }
            }
        }

        if (alg == 2) {
            array = sortByOccurance(array, n, m, c, random);
            for (int i = 0; i < c; i++) {
                if (array[random][i] != 0) {
                    hs[k - 1] = array[random][i];
                    int[][] new_array = deleteSets(array, m, c, array[random][i]);
                    int[] new_hs = hs.clone();
                    int[] result = solve1(new_array, n, new_array.length, c, k - 1, new_hs, startTime, flag, alg, r);

                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;

    }

    private static int[] solve3(int[][] array, int n, int m, int c, int k, int[] hs, long startTime, int flag, int alg) {
        if (timeout(startTime) && flag == 1) {
            return null;
        }

        if (m == 0 && k >= 0) {
            return hs;
        }
        if (k <= 0) {
            return null;
        }

        int min_index = sortBySize(array, m, c);
        if (alg == 4) {
            array = sortByOccurance(array, n, m, c, min_index);
        }

        for (int i = 0; i < c; i++) {
            if (array[min_index][i] != 0) {
                hs[k - 1] = array[min_index][i];
                int[][] new_array = deleteSets(array, array.length, c, array[min_index][i]);
                int[] new_hs = hs.clone();
                int[] result = solve3(new_array, n, new_array.length, c, k - 1, new_hs, startTime, flag, alg);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    public static void main(String[] args) throws IOException {

        int n, m, c, k;
        int[][] A;
        int flag;

        try {
            flag = Integer.parseInt(args[0]);
        } catch (Exception e) {
            flag = 0;
            System.out.println("Default run");
            System.out.println("");
        }

        switch (flag) {
            case 0 -> {
                try (BufferedReader buffer = new BufferedReader(new FileReader("sets.dat"))) {
                    String[] array = buffer.readLine().trim().split("\\s+");
                    n = Integer.parseInt(array[0]);
                    m = Integer.parseInt(array[1]);
                    c = Integer.parseInt(array[2]);
                    k = Integer.parseInt(array[3]);

                    int[] hs = new int[k];
                    

                    A = new int[m][c];

                    for (int i = 0; i < m; i++) {
                        String line = buffer.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] parts = line.trim().split("\\s+");
                        for (int j = 0; j < parts.length; j++) {
                            A[i][j] = Integer.parseInt(parts[j]);
                        }
                    }

                    System.out.println("Trying to find hitting set using algorithm 1:");
                    Random r = new Random(m);
                    long startTime = System.nanoTime();
                    printHittingSet(solve1(A, n, m, c, k, hs, startTime, flag, 1, r), k);
                    long endTime = System.nanoTime();

                    System.out.println("Execution time for Algorithm 1 : " + ((endTime - startTime) / 1_000_000.0) + " ms");
                    System.out.println();

                    r = new Random(m);
                    System.out.println("Trying to find hitting set using algorithm 2:");
                    startTime = System.nanoTime();
                    printHittingSet(solve1(A, n, m, c, k, hs, startTime, flag, 2, r), k);
                    endTime = System.nanoTime();

                    System.out.println("Execution time for Algorithm 2:  " + ((endTime - startTime) / 1_000_000.0) + " ms");
                    System.out.println();

                    System.out.println("Trying to find hitting set using algorithm 3:");
                    startTime = System.nanoTime();
                    printHittingSet(solve3(A, n, m, c, k, hs, startTime, flag, 3), k);
                    endTime = System.nanoTime();

                    System.out.println("Execution time for Algorithm 3:  " + ((endTime - startTime) / 1_000_000.0) + " ms");
                    System.out.println();

                    System.out.println("Trying to find hitting set using algorithm 4:");
                    startTime = System.nanoTime();
                    printHittingSet(solve3(A, n, m, c, k, hs, startTime, flag, 4), k);
                    endTime = System.nanoTime();

                    System.out.println("Execution time for Algorithm 4:  " + ((endTime - startTime) / 1_000_000.0) + " ms");
                    System.out.println();

                } catch (IOException error) {
                    System.err.println("Error reading the file: " + error.getMessage());
                }
            }

            case 1 -> {
                PrintStream out = new PrintStream(new FileOutputStream("out.txt"));
                System.setOut(out);
                System.setErr(out);
                n = 1000;
                m = 40;
                c = 10;
                k = 1;
                A = generateRandomArray(n, m, c);
                

                long mo = 0;
                long startTime, endTime;
                boolean f1 = true, f2 = true, f3 = true, f4 = true;
                while (f1 || f2 || f3 || f4) {
                    int[] hs = new int[k];
                    for (int i = 0; (i < 3 && f1); i++) {

                        Random r = new Random(m);

                        startTime = System.nanoTime();
                        printHittingSet(solve1(A, n, m, c, k, hs.clone(), startTime, flag, 1, r), k);
                        endTime = System.nanoTime();

                        if (timeout(startTime)) {
                            f1 = false;
                            System.out.println("Algorithm 1 took more than 1 hour and stopped at k= " + k);
                            System.out.println();
                            break;
                        }

                        mo += endTime - startTime;

                    }

                    if (f1) {
                        mo = (long) (mo / 3);

                        System.out.println("Execution time for Algorithm 1 : " + ((mo) / 1_000_000.0) + " ms");
                        System.out.println();
                    }

                    mo = 0;

                    for (int i = 0; (i < 3 && f2); i++) {
                        Random r = new Random(m);

                        startTime = System.nanoTime();
                        printHittingSet(solve1(A, n, m, c, k, hs.clone(), startTime, flag, 2, r), k);
                        endTime = System.nanoTime();

                        if (timeout(startTime)) {

                            f2 = false;
                            System.out.println("Algorithm 2 took more than 1 hour and stopped at k= " + k);
                            System.out.println();
                            break;
                        }

                        mo += endTime - startTime;
                    }

                    if (f2) {
                        mo = (long) (mo / 3);

                        System.out.println("Execution time for Algorithm 2:  " + ((mo) / 1_000_000.0) + " ms");
                        System.out.println();
                    }
                    mo = 0;

                    for (int i = 0; (i < 3 && f3); i++) {
                        startTime = System.nanoTime();
                        printHittingSet(solve3(A, n, m, c, k, hs, startTime, flag, 3), k);
                        endTime = System.nanoTime();

                        if (timeout(startTime)) {
                            f3 = false;
                            System.out.println("Algorithm 3 took more than 1 hour and stopped at k= " + k);
                            System.out.println();
                            break;
                        }

                        mo += endTime - startTime;
                    }

                    if (f3) {
                        mo = (long) (mo / 3);
                        System.out.println("Execution time for Algorithm 3:  " + ((mo) / 1_000_000.0) + " ms");
                        System.out.println();
                    }

                    mo = 0;
                    for (int i = 0; i < 3 && f4; i++) {
                        startTime = System.nanoTime();
                        printHittingSet(solve3(A, n, m, c, k, hs, startTime, flag, 4), k);
                        endTime = System.nanoTime();

                        if (timeout(startTime)) {
                            f4 = false;
                            System.out.println("Algorithm 4 took more than 1 hour and stopped at k= " + k);
                            System.out.println();
                            break;
                        }
                        mo += endTime - startTime;
                    }

                    if (f4) {
                        mo = (long) (mo / 3);
                        System.out.println("Execution time for Algorithm 4:  " + ((mo) / 1_000_000.0) + " ms");
                        System.out.println();
                    }
                    k++;
                }
                System.out.println("All algorithms finished at k= " + k);
            }
            case 2 -> {
                PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
                System.setOut(out);
                System.setErr(out);
                n = 110;
                m = 60;
                c = 8;
                k = 15;
                A = new int[m][c];
                A = generateRandomArray2(n, m, c);

                int[] hs = new int[k];
                

                long startTime;
                long endTime;
                for (int i = 0; i < 10; i++) {
                    System.out.println("Run " + (i + 1) + ":");

                    Random r = new Random(m);

                    startTime = System.nanoTime();
                    printHittingSet(solve1(A, n, m, c, k, hs.clone(), startTime, flag, 1, r), k);
                    endTime = System.nanoTime();

                    System.out.println("Execution time for Algorithm 1 : " + ((endTime - startTime) / 1_000_000.0) + " ms");
                    System.out.println();
                }
                for (int i = 0; i < 10; i++) {
                    System.out.println("Run " + (i + 1) + ":");

                    Random r = new Random(m);

                    startTime = System.nanoTime();
                    printHittingSet(solve1(A, n, m, c, k, hs.clone(), startTime, flag, 2, r), k);
                    endTime = System.nanoTime();

                    System.out.println("Execution time for Algorithm 2:  " + ((endTime - startTime) / 1_000_000.0) + " ms");
                    System.out.println();
                }
                for (int i = 0; i < 10; i++) {
                    System.out.println("Run " + (i + 1) + ":");
                    startTime = System.nanoTime();
                    printHittingSet(solve3(A, n, m, c, k, hs, startTime, flag, 3), k);
                    endTime = System.nanoTime();

                    System.out.println("Execution time for Algorithm 3:  " + ((endTime - startTime) / 1_000_000.0) + " ms");
                    System.out.println();
                }
                for (int i = 0; i < 10; i++) {
                    System.out.println("Run " + (i + 1) + ":");
                    startTime = System.nanoTime();
                    printHittingSet(solve3(A, n, m, c, k, hs, startTime, flag, 4), k);
                    endTime = System.nanoTime();

                    System.out.println("Execution time for Algorithm 4:  " + ((endTime - startTime) / 1_000_000.0) + " ms");
                    System.out.println();
                }
                System.out.println("End of execution");
            }
            default -> {
            }
        }

    }
}
