package com.buguagaoshu.tiktube.filter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于深度学习的文本内容违规检测
 * 从 Filter4J (https://github.com/LL4J/Filter4J) 移植
 * 
 * 使用方法：
 * boolean illegal = TextFilter.isIllegal("要检测的文本");
 */
public class TextFilter {
    
    private static final Logger log = LoggerFactory.getLogger(TextFilter.class);
    
    private static final String[] script;
    private static final Tokenizer tokenizer;
    private static final boolean initialized;
    private static final String initError;

    static {
        String[] tempScript = null;
        Tokenizer tempTokenizer = null;
        boolean tempInitialized = false;
        String tempError = null;
        
        try {
            // 从 classpath 加载模型，使用 UTF-8 编码
            List<String> scriptList = new ArrayList<>();
            try (InputStream is = TextFilter.class.getClassLoader().getResourceAsStream("filter4j/judge.model");
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    scriptList.add(line);
                }
            }
            tempScript = scriptList.toArray(new String[0]);
            tempTokenizer = Tokenizer.loadFromResource("filter4j/tokenize.model");
            tempInitialized = true;
            log.info("Filter4J 模型加载成功，script行数: {}", tempScript.length);
        } catch (Exception ex) {
            tempError = ex.getMessage();
            tempInitialized = false;
            log.error("Filter4J 模型加载失败: {}", tempError);
        }
        
        script = tempScript;
        tokenizer = tempTokenizer;
        initialized = tempInitialized;
        initError = tempError;
    }

    private TextFilter() {
        throw new UnsupportedOperationException("This is a static class");
    }

    /**
     * 检查文本是否违规
     * @param text 要检测的文本
     * @return true 表示违规，false 表示正常
     */
    public static boolean isIllegal(String text) {
        if (!initialized) {
            // 模型未加载时，返回 false（不阻止内容）
            log.warn("Filter4J 未初始化，跳过检测，文本: {}", text);
            return false;
        }
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        int result = MinRt.doAi(tokenizer.tokenize(text), script);
        boolean illegal = (result == 1);
        log.info("Filter4J 检测结果 - 文本: '{}', 结果: {}, 违规: {}", text, result, illegal);
        return illegal;
    }

    /**
     * 检查模型是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * 获取初始化错误信息
     */
    public static String getInitError() {
        return initError;
    }
}
