package algorithms;
import java.util.*;

public class KMP {
    public static List<Integer> search(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        if (pattern.length() == 0) return result;

        int[] lps = buildLPS(pattern);
        int i = 0, j = 0;

        while (i < text.length()) {
            if (Character.toLowerCase(text.charAt(i)) == Character.toLowerCase(pattern.charAt(j))) {
                i++; j++;
                if (j == pattern.length()) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return result;
    }

    private static int[] buildLPS(String p) {
        int[] lps = new int[p.length()];
        int len = 0, i = 1;
        while (i < p.length()) {
            if (Character.toLowerCase(p.charAt(i)) == Character.toLowerCase(p.charAt(len))) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }
}
