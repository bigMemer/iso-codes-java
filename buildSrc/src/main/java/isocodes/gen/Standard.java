package isocodes.gen;

import java.util.List;

/**
 * Describes how one iso-codes JSON file is turned into a Java type.
 *
 * @param code         the standard's number as used in the iso-codes file names, e.g. {@code 3166-1}
 * @param subPackage   package below the base package, e.g. {@code iso3166}
 * @param className    simple name of the generated type
 * @param kind         whether to generate an enum or a class with chunked data holders
 * @param keyField     JSON field whose value names enum constants (enums only)
 * @param lookupFields JSON fields that get a static {@code fromXxx(String)} lookup method
 * @param summary      one-line description used in the generated Javadoc
 */
public record Standard(
        String code,
        String subPackage,
        String className,
        Kind kind,
        String keyField,
        List<String> lookupFields,
        String summary) {

    public enum Kind {
        /** A Java enum; only possible when the data fits within JVM class-file limits. */
        ENUM,
        /** A final class whose instances are created by package-private data holder classes. */
        TABLE
    }

    public static final List<Standard> ALL = List.of(
            new Standard("3166-1", "iso3166", "Country", Kind.ENUM, "alpha_2",
                    List.of("alpha_2", "alpha_3", "numeric"),
                    "ISO 3166-1 codes for the representation of names of countries."),
            new Standard("3166-2", "iso3166", "Subdivision", Kind.TABLE, "code",
                    List.of("code"),
                    "ISO 3166-2 codes for the representation of country subdivisions."),
            new Standard("3166-3", "iso3166", "FormerCountry", Kind.ENUM, "alpha_4",
                    List.of("alpha_4"),
                    "ISO 3166-3 codes for formerly used names of countries."),
            new Standard("4217", "iso4217", "Currency", Kind.ENUM, "alpha_3",
                    List.of("alpha_3", "numeric"),
                    "ISO 4217 codes for the representation of currencies."),
            new Standard("15924", "iso15924", "Script", Kind.ENUM, "alpha_4",
                    List.of("alpha_4", "numeric"),
                    "ISO 15924 codes for the representation of names of scripts."),
            new Standard("639-2", "iso639", "LanguagePart2", Kind.ENUM, "alpha_3",
                    List.of("alpha_3", "alpha_2", "bibliographic"),
                    "ISO 639-2 alpha-3 codes for the representation of names of languages."),
            new Standard("639-3", "iso639", "Language", Kind.TABLE, "alpha_3",
                    List.of("alpha_3", "alpha_2", "bibliographic"),
                    "ISO 639-3 codes for comprehensive coverage of languages."),
            new Standard("639-5", "iso639", "LanguageFamily", Kind.ENUM, "alpha_3",
                    List.of("alpha_3"),
                    "ISO 639-5 codes for language families and groups."));

    public String dataFileName() {
        return "iso_" + code + ".json";
    }

    public String schemaFileName() {
        return "schema-" + code + ".json";
    }
}
