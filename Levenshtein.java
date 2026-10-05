package algorithms;

public class Levenshtein {
    public static int distance(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = Character.toLowerCase(a.charAt(i - 1)) ==
                           Character.toLowerCase(b.charAt(j - 1)) ? 0 : 1;

                int delete = dp[i - 1][j] + 1;
                int insert = dp[i][j - 1] + 1;
                int replace = dp[i - 1][j - 1] + cost;

                dp[i][j] = Math.min(delete, Math.min(insert, replace));
            }
        }
        return dp[n][m];
    }
}
