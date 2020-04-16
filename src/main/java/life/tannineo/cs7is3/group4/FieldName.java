package life.tannineo.cs7is3.group4;

public enum FieldName {

    DOCNO("DOCNO"),

    // basic
    TEXT("TEXT"),
    META("META"),

    // extra
    DATE("DATE"),
    HEADLINE("HEADLINE"),
    TYPE("TYPE"),
    SUBJECT("SUBJECT"),
    HEADER("HEADER"),
    HT("HT"),
    GRAPHIC("GRAPHIC"),

    // non-sense
    DOCID("DOCID"),
    SECTION("SECTION"),
    LENGTH("LENGTH"),
    BYLINE("BYLINE"),
    PROFILE("PROFILE"),
    PUB("PUB"),
    PAGE("PAGE"),
    PARENT("PARENT");

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

    public static String[] getAllNamesExceptNonSense() {
        FieldName[] allFieldNamesExceptNonSense = {
            // basic
            TEXT,
//            META,

            // extra
//            DATE,
            HEADLINE,
//            TYPE,
//            SUBJECT,
//            HEADER,
//            HT,
//            GRAPHIC,
        };
        int size = allFieldNamesExceptNonSense.length;
        String[] arr = new String[size];
        for (int i = 0; i < size; i++) {
            arr[i] = allFieldNamesExceptNonSense[i].name;
        }
        return arr;
    }
}
