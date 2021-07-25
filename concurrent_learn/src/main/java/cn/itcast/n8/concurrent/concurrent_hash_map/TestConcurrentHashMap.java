package cn.itcast.n8.concurrent.concurrent_hash_map;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by John.Ma on 2021/7/25 0025 22:41
 */
public class TestConcurrentHashMap {

    public static void main(String[] args) {

        demo(
            () -> new ConcurrentHashMap<String, LongAdder>(),
            (map, words) -> {
                for (String word : words) {
                    // 注意不能使用 putIfAbsent，此方法返回的是上一次的 value，首次调用返回 null
                    map.computeIfAbsent(word, (key) -> new LongAdder()).increment();
                }
            }
        );

        demo(
            () -> new ConcurrentHashMap<String,Integer>(),
            (map,words) -> {
                for(String word:words){
                    // 函数式编程,无需原子变量
                    map.merge(word,1,Integer::sum);
                }
            }

        );
    }

    // 模版代码,模版代码中封装了多线程读取文件的代码
    private static <V> void demo(Supplier<Map<String, V>> supplier,
                                 BiConsumer<Map<String, V>, List<String>> consumer
    ) {
        Map<String, V> counterMap = supplier.get();
        List<Thread> ts = new ArrayList<>();
        for (int i = 1; i <= 26; i++) {
            int idx = i;
            Thread thread = new Thread(() -> {
                List<String> words = readFromFile(idx);
                consumer.accept(counterMap, words);
            });
            ts.add(thread);
        }

        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(counterMap);
    }

    public static List<String> readFromFile(int i) {
        ArrayList<String> words = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(new FileInputStream("D:\\fileImportant\\Learn_projects\\learn\\concurrent_learn\\tmp\\" + i + ".txt")))
        ) {
            while(true){
                String word = in.readLine();
                if(word == null){
                    break;
                }
                words.add(word);
            }
            return words;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

// 生成文件,可以不看
class GenerateFile {
    // 单词计数
    // 生成测试数据
    static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) {
        int length = ALPHA.length();
        int count = 200;
        List<String> list = new ArrayList<>(length * count);
        ;
        for (int i = 0; i < length; i++) {
            char ch = ALPHA.charAt(i);
            for (int j = 0; j < count; j++) {
                list.add(String.valueOf(ch));
            }
        }

        Collections.shuffle(list);
        for (int i = 0; i < 26; i++) {
            try (PrintWriter out = new PrintWriter(
                new OutputStreamWriter(
                    new FileOutputStream("D:/tmp/" + (i + 1) + ".txt")))) {
                String collect = list.subList(i * count, (i + 1) * count).stream()
                    .collect(Collectors.joining("\n"));
                out.print(collect);
            } catch (IOException e) {
            }
        }

    }
}
