package life.tannineo.cs7is3.group4.helper;

public class WeirdEntityParser {

    public static String parse(String input) {
        return input.replaceAll("&hyph;", "-");
    }
}
