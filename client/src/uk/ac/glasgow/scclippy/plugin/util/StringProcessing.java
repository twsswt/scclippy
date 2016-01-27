package uk.ac.glasgow.scclippy.plugin.util;

/**
 * String processing (static functions)
 */
public class StringProcessing {

    /**
     * Converts text to HTML by adding breaks and nbsp
     *
     * @param snippetText the text
     * @return the html variant of the text
     */
    public static String textToHTML(String snippetText) {
        snippetText = replaceAll(snippetText, '\n', "<br/>");
        snippetText = replaceAll(snippetText, ' ', "&nbsp ");

        return snippetText;
    }

    /**
     * Replaces all occurences of a character in a string with some string
     *
     * @param s           the initial string
     * @param c           the character
     * @param replacement the replacement string
     * @return the modified string
     */
    private static String replaceAll(String s, char c, String replacement) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = s.length() - 2; i >= 0; i--) {
            if (sb.charAt(i) == c) {
                sb.replace(i, i + 1, replacement);
            }
        }
        return sb.toString();
    }
}
