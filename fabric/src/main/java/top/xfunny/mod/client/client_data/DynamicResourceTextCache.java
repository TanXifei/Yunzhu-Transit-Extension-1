package top.xfunny.mod.client.client_data;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import java.util.ArrayList;
import java.util.List;



public class DynamicResourceTextCache {
    public static DynamicResourceTextCache instance = new DynamicResourceTextCache();

    //两层 1、坐标；2（存两个）<当前文本><上一个文本>
    private final Object2ObjectLinkedOpenHashMap<Long, List<String>> dynamicResourceKeyCache = new Object2ObjectLinkedOpenHashMap<>();

    public String getCurrentText(Long location){
        String currentText = dynamicResourceKeyCache.get(location).get(1);
        if(currentText != null){
            return currentText;
        }
        return "";
    }

    public String getPreviousText(Long location){
        return dynamicResourceKeyCache.get(location).get(0);
    }

    public void updateText(Long location, String text) {
        List<String> details = dynamicResourceKeyCache.computeIfAbsent(location, k -> new ArrayList<>());

        if (!details.isEmpty() && details.get(details.size() - 1).equals(text)) {
            return;
        }

        if (details.size() >= 2) {
            details.remove(0);
        }

        details.add(text);
    }

    public void reload() {
        dynamicResourceKeyCache.clear();
    }
}
