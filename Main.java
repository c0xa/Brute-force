package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 @brief Класс для чтения из файла и создания потоков
 @detailed Считывает построчно из файла и создает threadCount потоков с помощью ExecutorService
 */
public class Main {
    /**
     * CHAR_SEQUENCE_LENGTH - длина искомой буквенной комбинации
     * CHAR_PREFIX_LENGTH - длина префикса, созданного для оптимизации поиска
     */
    private static final int CHAR_SEQUENCE_LENGTH = 5;
    private static final int CHAR_PREFIX_LENGTH = 3;

    /**
     @brief метод для считывания имени файла и количества потоков с клавиатуры
     @detailed Передает данные в метод fileStream
     */
    public static void main(String[] args) {
        String fileName = "";
        int threadCount = 1;

        boolean needInfo = true;
        while (needInfo) {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter file name");
                fileName = scanner.next();
                System.out.println("Enter count thread");
                threadCount = scanner.nextInt();
                needInfo = false;
            } catch (Exception e) {
                System.out.println("Error. Enter the numbers in the correct range. Try again");
            }
        }
        fileStream(fileName, threadCount);
    }

    /**
     @brief метод для создания потоков
     @param fileName имя файла
     @param threadCount количество потоков
     @detailed Передает данные в метод fileStream
     */
    private static void fileStream(String fileName, int threadCount) {
        long startTime;
        startTime = System.currentTimeMillis();
        try (FileInputStream fin = new FileInputStream(fileName)) {
            Scanner scan = new Scanner(fin);

            while (scan.hasNextLine()) {
                String hash = scan.nextLine();
                ExecutorService executor = Executors.newFixedThreadPool(threadCount);
                //resultRef - строковая переменная, доступная во всех потоков
                AtomicReference<String> resultRef = new AtomicReference<>();

                CharSequenceGenerator prefixGenerator = new CharSequenceGenerator(
                        CHAR_PREFIX_LENGTH,
                        'a',
                        'z',
                        (prefix) -> executor.submit(new Sha256Calculator(
                                prefix,
                                CHAR_SEQUENCE_LENGTH - CHAR_PREFIX_LENGTH,
                                'a',
                                'z',
                                hash,
                                newValue -> { resultRef.set(newValue); executor.shutdownNow(); }
                        )),
                        (str) -> resultRef.get() == null
                );
                prefixGenerator.generate();
                executor.shutdown();
                executor.awaitTermination(1, TimeUnit.DAYS);
                System.out.println("Result: hash \"" + hash + "\" -> " + resultRef);
            }
            System.out.println("Time " + (System.currentTimeMillis() - startTime));
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
