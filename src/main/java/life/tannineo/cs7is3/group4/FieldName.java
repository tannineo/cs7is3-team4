package life.tannineo.cs7is3.group4;

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

    public static String[] getAllNames() {
        FieldName[] allFieldNames = FieldName.values();
        int size = allFieldNames.length;
        String[] arr = new String[size];
        for (int i = 0; i < size; i++) {
            arr[i] = allFieldNames[i].name;
        }
        return arr;
    }
}
