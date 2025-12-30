package com.buguagaoshu.tiktube.filter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 分词器 - 从 Filter4J 移植
 */
public class Tokenizer {
    private final String[] vocab;

    public Tokenizer(String[] vocab) {
        this.vocab = vocab;
    }

    /**
     * 从 classpath 资源加载分词器模型
     * 使用 UTF-8 编码避免乱码
     */
    public static Tokenizer loadFromResource(String resourcePath) {
        try (InputStream is = Tokenizer.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            int size = Integer.parseInt(reader.readLine());
            String[] vocab = new String[size];
            for (int i = 0; i < size; i++) {
                vocab[i] = reader.readLine();
            }
            return new Tokenizer(vocab);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load tokenizer model: " + resourcePath, e);
        }
    }

    public double[] tokenize(String text) {
        double[] values = new double[vocab.length];
        for (int i = 0; i < values.length; i++) {
            if (text.contains(vocab[i])) {
                values[i] = 1;
            }
        }
        return values;
    }
}
