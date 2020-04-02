package life.tannineo.cs7is3.group4;

import java.util.ArrayList;

public enum FieldName {

    DOCNO("DOCNO"),
    TEXT("TEXT"),
    META("META"),

    DOCID("DOCID"),
    DATE("DATE"),
    SECTION("SECTION"),
    LENGTH("LENGTH"),
    HEADLINE("HEADLINE"),
    GRAPHIC("GRAPHIC"),
    TYPE("TYPE"),
    SUBJECT("SUBJECT"),
    BYLINE("BYLINE"),
    PROFILE("PROFILE"),
    PUB("PUB"),
    PAGE("PAGE"),
    PARENT("PARENT"),
    HT("HT"),
    HEADER("HEADER");

    private String name;

    FieldName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ArrayList<String> getAllNames() {
        ArrayList<String> arr = new ArrayList<>();
        for (FieldName fn : FieldName.values()) {
            arr.add(fn.getName());
        }
        return arr;
    }
}
