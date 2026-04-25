import java.io.*;
import java.util.*;
public class introsort {
    private static long iterationCount;
    private static final int INSERTION_SORT_THRESHOLD = 16;
    public static void introSort(int[] arr, int left, int right, int depthLimit) {
        int length = right - left + 1;
        if (length <= INSERTION_SORT_THRESHOLD) {
            insertionSort(arr, left, right);
            return;
        }
        if (depthLimit == 0) {
            heapSort(arr, left, right);
            return;
        }
        int pivot = partition(arr, left, right);
        introSort(arr, left, pivot - 1, depthLimit - 1);
        introSort(arr, pivot + 1, right, depthLimit - 1);
    }
    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left - 1;
        for (int j = left; j < right; j++) {
            iterationCount++;
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, right);
        return i + 1;
    }
    private static void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                iterationCount++;
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
    private static void heapSort(int[] arr, int left, int right) {
        int n = right - left + 1;
        int[] temp = new int[n];
        System.arraycopy(arr, left, temp, 0, n);
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(temp, n, i);
        }
        for (int i = n - 1; i > 0; i--) {
            swap(temp, 0, i);
            heapify(temp, i, 0);
        }
        System.arraycopy(temp, 0, arr, left, n);
    }
    private static void heapify(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        iterationCount++;
        if (left < n && arr[left] > arr[largest]) {
            largest = left;
        }
        iterationCount++;
        if (right < n && arr[right] > arr[largest]) {
            largest = right;
        }
        if (largest != i) {
            swap(arr, i, largest);
            heapify(arr, n, largest);
        }
    }
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    public static void generateTestData() throws IOException {
        int[] sizes = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000,
                2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000};
        for (int size : sizes) {
            int[] randomArr = new int[size];
            for (int i = 0; i < size; i++) {
                randomArr[i] = (int) (Math.random() * size);
            }
            saveToFile(randomArr, "data/random_" + size + ".txt");
            int[] sortedArr = new int[size];
            for (int i = 0; i < size; i++) {
                sortedArr[i] = i;
            }
            saveToFile(sortedArr, "data/sorted_" + size + ".txt");
            int[] reverseArr = new int[size];
            for (int i = 0; i < size; i++) {
                reverseArr[i] = size - i;
            }
            saveToFile(reverseArr, "data/reverse_" + size + ".txt");
        }
        System.out.println("Тестовые данные сгенерированы");
    }
    private static void saveToFile(int[] arr, String filename) throws IOException {
        File file = new File(filename);
        file.getParentFile().mkdirs();
        try (PrintWriter writer = new PrintWriter(file)) {
            for (int num : arr) {
                writer.println(num);
            }
        }
    }
    private static int[] readFromFile(String filename) throws IOException {
        List<Integer> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(Integer.parseInt(line.trim()));
            }
        }
        return list.stream().mapToInt(i -> i).toArray();
    }
    public static void measureAll() throws IOException {
        int[] sizes = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000,
                2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000};
        try (PrintWriter csvRandom = new PrintWriter("results_random.csv");
             PrintWriter csvSorted = new PrintWriter("results_sorted.csv");
             PrintWriter csvReverse = new PrintWriter("results_reverse.csv")) {
            csvRandom.println("size,time_ms,iterations");
            csvSorted.println("size,time_ms,iterations");
            csvReverse.println("size,time_ms,iterations");
            System.out.println("\nИЗМЕРЕНИЯ");
            for (int size : sizes) {
                int[] randomArr = readFromFile("data/random_" + size + ".txt");
                Result r = measureSort(randomArr);
                csvRandom.printf("%d,%d,%d\n", size, r.time, r.iterations);
                int[] sortedArr = readFromFile("data/sorted_" + size + ".txt");
                Result s = measureSort(sortedArr);
                csvSorted.printf("%d,%d,%d\n", size, s.time, s.iterations);
                int[] reverseArr = readFromFile("data/reverse_" + size + ".txt");
                Result rev = measureSort(reverseArr);
                csvReverse.printf("%d,%d,%d\n", size, rev.time, rev.iterations);
                System.out.printf("Размер %d: random=%dмс (%d итер), sorted=%dмс, reverse=%dмс\n",
                        size, r.time, r.iterations, s.time, rev.time);
            }
        }
        System.out.println("\nРезультаты сохранены в CSV файлы");
        System.out.println("Импортируйте их в Excel для построения графиков.");
    }
    static class Result {
        long time;
        long iterations;
        Result(long time, long iterations) {
            this.time = time;
            this.iterations = iterations;
        }
    }
    static Result measureSort(int[] arr) {
        int[] copy = Arrays.copyOf(arr, arr.length);
        iterationCount = 0;
        int depthLimit = 2 * (int)(Math.log(copy.length) / Math.log(2));
        long start = System.nanoTime();
        introSort(copy, 0, copy.length - 1, depthLimit);
        long end = System.nanoTime();
        return new Result((end - start) / 1_000_000, iterationCount);
    }
    public static void main(String[] args) throws IOException {
        generateTestData();
        measureAll();
    }
}