package com.xyt;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

public class WebCrawler {
    private static Set<String> sensitiveWords = new HashSet<>();

    public static void main(String[] args) {
        // 读取敏感词库
        loadSensitiveWords();
        //sensitiveWords.add("123");

        // 创建主界面
        JFrame frame = new JFrame("Web Crawler");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JTextField urlField = new JTextField(20);
        JTextPane textPane = new JTextPane();
        //textPane.setEditable(false); // 设置文本框为只读
        textPane.setMargin(new Insets(100, 100, 110, 110)); // 设置文本边距
        textPane.setFont(new Font("Arial", Font.PLAIN, 12)); // 设置字体和大小
        //textPane.setContentType("text/html"); // 设置内容类型为 HTML
        textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true); // 允许使用字体属性

        JButton crawlButton = getjButton(urlField, textPane);

        panel.add(new JLabel("URL:"));
        panel.add(urlField);
        panel.add(crawlButton);

        JScrollPane scrollPane = new JScrollPane(textPane);
        panel.add(scrollPane);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static JButton getjButton(JTextField urlField, JTextPane textPane) {
        JButton crawlButton = new JButton("Crawl");
        crawlButton.addActionListener(e -> {
            String url = urlField.getText();
            String htmlContent = crawlWebsite(url);
            textPane.setText(htmlContent);
            highlightSensitiveWords(textPane);
            saveSensitiveWords(url, htmlContent);
        });
        return crawlButton;
    }

    private static String crawlWebsite(String url) {
        try {
            URL website = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadSensitiveWords() {
        try (BufferedReader reader = new BufferedReader(new FileReader("E://temp//sensitive.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sensitiveWords.add(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void highlightSensitiveWords(JTextPane textPane) {
        String text = textPane.getText();
        for (String word : sensitiveWords) {
            int index = text.indexOf(word);
            while (index >= 0) {
                Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
                try {
                    textPane.getHighlighter().addHighlight(index, index + word.length(), painter);
                    index = text.indexOf(word, index + word.length());
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void saveSensitiveWords(String url, String htmlContent) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("E://temp//sensitive_output.txt", true))) {
            writer.println("URL: " + url);
            for (String word : sensitiveWords) {
                if (htmlContent.contains(word)) {
                    writer.println("Sensitive Word: " + word);
                }
            }
            writer.println("-------------------------------------------");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
