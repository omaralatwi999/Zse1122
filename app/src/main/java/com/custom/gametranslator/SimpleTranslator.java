package com.custom.gametranslator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SimpleTranslator {

    private static final Map<String, String> DICTIONARY = new HashMap<>();

    static {
        DICTIONARY.put("press start", "اضغط للبدء");
        DICTIONARY.put("new game", "لعبة جديدة");
        DICTIONARY.put("continue", "متابعة");
        DICTIONARY.put("settings", "الإعدادات");
        DICTIONARY.put("exit", "خروج");
        DICTIONARY.put("game over", "انتهت اللعبة");
        DICTIONARY.put("loading", "جاري التحميل");
        DICTIONARY.put("hello", "أهلاً");
        DICTIONARY.put("thank you", "شكراً لك");
        DICTIONARY.put("help", "مساعدة");
    }

    public static String translateToArabic(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "لا يوجد نص لترجمته";
        }

        String normalized = input.trim().toLowerCase(Locale.ROOT);
        if (DICTIONARY.containsKey(normalized)) {
            return DICTIONARY.get(normalized);
        }

        String result = normalized;
        for (Map.Entry<String, String> entry : DICTIONARY.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        if (result.equals(normalized)) {
            return "ترجمة النص: " + input.trim();
        }
        return result;
    }
}
