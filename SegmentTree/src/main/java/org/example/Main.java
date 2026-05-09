package org.example;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class Main {
    private static long operationCount = 0;
    static class SegmentTree {
        private int[] tree;
        private int n;
        public SegmentTree(int size) {
            this.n = size;
            this.tree = new int[4 * size];
        }
        public void build(int[] arr) {
            buildHelper(arr, 1, 0, n - 1);
        }
        private void buildHelper(int[] arr, int node, int left, int right) {
            operationCount++;
            if (left == right) {
                tree[node] = arr[left];
                operationCount++;
            } else {
                int mid = left + (right - left) / 2;
                buildHelper(arr, 2 * node, left, mid);
                buildHelper(arr, 2 * node + 1, mid + 1, right);
                tree[node] = tree[2 * node] + tree[2 * node + 1];
                operationCount++;
            }
        }
        public void add(int index, int value) {
            addHelper(1, 0, n - 1, index, value);
        }
        private void addHelper(int node, int left, int right, int index, int value) {
            operationCount++;
            if (left == right) {
                tree[node] = value;
                operationCount++;
            } else {
                int mid = left + (right - left) / 2;
                operationCount++;
                if (index <= mid) {
                    addHelper(2 * node, left, mid, index, value);
                } else {
                    addHelper(2 * node + 1, mid + 1, right, index, value);
                }
                tree[node] = tree[2 * node] + tree[2 * node + 1];
                operationCount++;
            }
        }
        public void remove(int index) {
            add(index, 0);
        }
        public int search(int index) {
            return searchHelper(1, 0, n - 1, index);
        }
        private int searchHelper(int node, int left, int right, int index) {
            operationCount++;
            if (left == right) {
                operationCount++;
                return tree[node];
            }
            int mid = left + (right - left) / 2;
            operationCount++;
            if (index <= mid) {
                return searchHelper(2 * node, left, mid, index);
            } else {
                return searchHelper(2 * node + 1, mid + 1, right, index);
            }
        }
        public int rangeSum(int left, int right) {
            return rangeSumHelper(1, 0, n - 1, left, right);
        }
        private int rangeSumHelper(int node, int nodeLeft, int nodeRight, int left, int right) {
            if (left > right) return 0;
            if (nodeLeft == left && nodeRight == right) {
                return tree[node];
            }
            int mid = nodeLeft + (nodeRight - nodeLeft) / 2;
            return rangeSumHelper(2 * node, nodeLeft, mid, left, Math.min(right, mid)) +
                    rangeSumHelper(2 * node + 1, mid + 1, nodeRight, Math.max(left, mid + 1), right);
        }
    }
    static class MeasurementResult {
        List<Long> times = new ArrayList<>();
        List<Long> operations = new ArrayList<>();
        double averageTime() {
            return times.stream().mapToLong(Long::longValue).average().orElse(0);
        }
        double averageOperations() {
            return operations.stream().mapToLong(Long::longValue).average().orElse(0);
        }
    }
    public static void main(String[] args) {
        final int N = 10000;
        final int SEARCH_COUNT = 100;
        final int REMOVE_COUNT = 1000;
        Random random = new Random();
        int[] array = new int[N];
        for (int i = 0; i < N; i++) {
            array[i] = random.nextInt(100000);
        }
        SegmentTree segmentTree = new SegmentTree(N);
        int[] initialArray = new int[N];
        segmentTree.build(initialArray);
        MeasurementResult addResults = new MeasurementResult();
        MeasurementResult searchResults = new MeasurementResult();
        MeasurementResult removeResults = new MeasurementResult();
        System.out.println("Добавление элементов...");
        for (int i = 0; i < N; i++) {
            operationCount = 0;
            long startTime = System.nanoTime();
            segmentTree.add(i, array[i]);
            long endTime = System.nanoTime();
            addResults.times.add(endTime - startTime);
            addResults.operations.add(operationCount);
        }
        System.out.println("Поиск элементов...");
        for (int i = 0; i < SEARCH_COUNT; i++) {
            int index = random.nextInt(N);
            operationCount = 0;
            long startTime = System.nanoTime();
            segmentTree.search(index);
            long endTime = System.nanoTime();
            searchResults.times.add(endTime - startTime);
            searchResults.operations.add(operationCount);
        }
        System.out.println("Удаление элементов...");
        List<Integer> removedIndices = new ArrayList<>();
        for (int i = 0; i < REMOVE_COUNT; i++) {
            int index = random.nextInt(N);
            removedIndices.add(index);
            operationCount = 0;
            long startTime = System.nanoTime();
            segmentTree.remove(index);
            long endTime = System.nanoTime();
            removeResults.times.add(endTime - startTime);
            removeResults.operations.add(operationCount);
        }
        try (FileWriter writer = new FileWriter("segment_tree_results.txt")) {
            writer.write("Дерево отрезков - Результаты измерений\n");
            writer.write("Общее количество элементов: " + N + "\n");
            writer.write("Количество поисков: " + SEARCH_COUNT + "\n");
            writer.write("Количество удалений: " + REMOVE_COUNT + "\n\n");
            writer.write("СРЕДНИЕ ЗНАЧЕНИЯ:\n");
            writer.write(String.format("Добавление - Среднее время: %.2f нс, Среднее количество операций: %.2f\n",
                    addResults.averageTime(), addResults.averageOperations()));
            writer.write(String.format("Поиск - Среднее время: %.2f нс, Среднее количество операций: %.2f\n",
                    searchResults.averageTime(), searchResults.averageOperations()));
            writer.write(String.format("Удаление - Среднее время: %.2f нс, Среднее количество операций: %.2f\n",
                    removeResults.averageTime(), removeResults.averageOperations()));
            writer.write("\nДЕТАЛЬНЫЕ ДАННЫЕ ПО ДОБАВЛЕНИЯМ (первые 20):\n");
            for (int i = 0; i < Math.min(20, N); i++) {
                writer.write(String.format("Добавление %d: время=%d нс, операций=%d\n",
                        i + 1, addResults.times.get(i), addResults.operations.get(i)));
            }
            writer.write("\nДЕТАЛЬНЫЕ ДАННЫЕ ПО ПОИСКАМ (первые 20):\n");
            for (int i = 0; i < Math.min(20, SEARCH_COUNT); i++) {
                writer.write(String.format("Поиск %d: время=%d нс, операций=%d\n",
                        i + 1, searchResults.times.get(i), searchResults.operations.get(i)));
            }
            writer.write("\nДЕТАЛЬНЫЕ ДАННЫЕ ПО УДАЛЕНИЯМ (первые 20):\n");
            for (int i = 0; i < Math.min(20, REMOVE_COUNT); i++) {
                writer.write(String.format("Удаление %d: время=%d нс, операций=%d\n",
                        i + 1, removeResults.times.get(i), removeResults.operations.get(i)));
            }
            System.out.println("Результаты сохранены в файл segment_tree_results.txt");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении результатов: " + e.getMessage());
        }
        System.out.println("\nИТОГОВЫЕ РЕЗУЛЬТАТЫ");
        System.out.printf("Добавление - среднее время: %.2f нс, среднее операций: %.2f%n",
                addResults.averageTime(), addResults.averageOperations());
        System.out.printf("Поиск - среднее время: %.2f нс, среднее операций: %.2f%n",
                searchResults.averageTime(), searchResults.averageOperations());
        System.out.printf("Удаление - среднее время: %.2f нс, среднее операций: %.2f%n",
                removeResults.averageTime(), removeResults.averageOperations());
        System.out.println("\nОценка сложности O(log N):");
        System.out.println("Добавление: O(log N) = O(log " + N + ") ≈ " + (Math.log(N) / Math.log(2)));
        System.out.println("Поиск: O(log N) = O(log " + N + ") ≈ " + (Math.log(N) / Math.log(2)));
        System.out.println("Удаление: O(log N) = O(log " + N + ") ≈ " + (Math.log(N) / Math.log(2)));
    }
}