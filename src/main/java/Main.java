import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final int size = 100;
    private static final int lengthText  = 100_000;

    private static final BlockingQueue<String> texts1 = new ArrayBlockingQueue<>(size);
    private static final BlockingQueue<String> texts2 = new ArrayBlockingQueue<>(size);
    private static final BlockingQueue<String> texts3 = new ArrayBlockingQueue<>(size);

    public static void main(String[] args) throws InterruptedException {


        Thread threadGenerateText = new Thread(() -> {
            for (int i = 0; i < size; i++) {
                try {
                    texts1.put(generateText("abc", lengthText));
                    texts2.put(generateText("abc", lengthText));
                    texts3.put(generateText("abc", lengthText));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        threadGenerateText.start();
        threadGenerateText.join();

        Thread threadA = new Thread(() -> {
            char letter = 'a';
            int countLetters = getLongTextWithOneLetter(texts1, letter);
            System.out.println("Text with Letter \"" + letter + "\" has max count - " + countLetters);
        });

        Thread threadB = new Thread(() -> {
            char letter = 'b';
            int countLetters = getLongTextWithOneLetter(texts2, letter);
            System.out.println("Text with Letter \"" + letter + "\" has max count - " + countLetters);
        });

        Thread threadC = new Thread(() -> {
            char letter = 'c';
            int countLetters = getLongTextWithOneLetter(texts3, letter);
            System.out.println("Text with Letter \"" + letter + "\" has max count - " + countLetters);
        });

        threadA.start();
        threadB.start();
        threadC.start();

        threadA.join();
        threadB.join();
        threadC.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int getLongTextWithOneLetter(BlockingQueue<String> queue, char letter) {
        int maxLength = 0;
        int length;

        try {
            for (int i = 0; i < size; i++) {
                length = countSameLetters(queue.take(), letter);

                if (maxLength < length)
                    maxLength = length;
            }
        } catch (InterruptedException e) {
            return -1;
        }
        return maxLength;
    }

    public static int countSameLetters(String text, char letter) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == letter)
                count++;
        }
        return count;
    }
}