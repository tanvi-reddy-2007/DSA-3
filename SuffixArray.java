package algorithms;

import java.util.*;

public class SuffixArray {
    public static int[] build(String text) {
        int n = text.length();
        Integer[] sa = new Integer[n];
        for (int i = 0; i < n; i++) sa[i] = i;

        Arrays.sort(sa, (a, b) -> {
            int i = a, j = b;
            while (i < n && j < n) {
                if (text.charAt(i) != text.charAt(j))
                    return Character.compare(text.charAt(i), text.charAt(j));
                i++; j++;
            }
            return Integer.compare(n - a, n - b);
        });

        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = sa[i];
        return result;
    }

    public static int lcp(String text, int a, int b) {
        int count = 0;
        while (a < text.length() && b < text.length() &&
               text.charAt(a) == text.charAt(b)) {
            count++; a++; b++;
        }
        return count;
    }
}
