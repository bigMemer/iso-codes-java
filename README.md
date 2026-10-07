# iso-codes-java

Java enums and classes generated from [Debian's iso-codes](https://salsa.debian.org/iso-codes-team/iso-codes),
published to Maven Central. Each artifact version is the iso-codes release it was generated from.

```kotlin
dependencies {
    implementation("com.wwwdottheinternetdotcom:iso-codes:4.20.1")
}
```

Types live under `com.wwwdottheinternetdotcom.isocodes`.

| Standard   | Type                     | Kind  | Lookups                                          |
|------------|--------------------------|-------|--------------------------------------------------|
| ISO 3166-1 | `iso3166.Country`        | enum  | `fromAlpha2`, `fromAlpha3`, `fromNumeric`        |
| ISO 3166-2 | `iso3166.Subdivision`    | class | `fromCode`, `all()`                              |
| ISO 3166-3 | `iso3166.FormerCountry`  | enum  | `fromAlpha4`                                     |
| ISO 4217   | `iso4217.Currency`       | enum  | `fromAlpha3`, `fromNumeric`                      |
| ISO 15924  | `iso15924.Script`        | enum  | `fromAlpha4`, `fromNumeric`                      |
| ISO 639-2  | `iso639.LanguagePart2`   | enum  | `fromAlpha3`, `fromAlpha2`, `fromBibliographic`  |
| ISO 639-3  | `iso639.Language`        | class | `fromAlpha3`, `fromAlpha2`, `fromBibliographic`, `all()` |
| ISO 639-5  | `iso639.LanguageFamily`  | enum  | `fromAlpha3`                                     |

```java
Country de = Country.DE;
de.alpha3();                          // "DEU"
de.officialName();                    // Optional[Federal Republic of Germany]
Country.fromNumeric("276");           // Optional[DE]
Subdivision.fromCode("US-CA");        // Optional[US-CA]
Language.fromAlpha3("eng").map(Language::englishName); // Optional[English]
```

ISO 3166-2 and ISO 639-3 have thousands of entries, which is more than a single JVM class can hold as enum
constants, so they are plain classes with lookup methods instead.

Accessors mirror the upstream JSON fields. A field present on every entry returns `String`; one that only some
entries have returns `Optional<String>`. The JSON `name` field is exposed as `englishName()` because `name()` is
taken by `Enum`. Fields appear and disappear across upstream releases (e.g. `Country.flag()` exists from 4.8.0 on),
so the API of each version follows its data.

Releases from 3.67 onward are published. 3.66 used an older JSON layout and earlier releases had no JSON.

## Building

```sh
./gradlew build                            # generates from the isoCodesVersion in gradle.properties
./gradlew build -PisoCodesVersion=4.7.0    # or any other release
```

Generated sources land in `build/generated/sources/iso-codes`. The generator lives in `buildSrc/`.

## Releasing

`.github/workflows/release.yml` runs weekly. It lists upstream tags, compares them with what's already on Maven
Central (`scripts/unpublished_versions.py`), and publishes each missing release, oldest first. Run it manually
from the Actions tab (with dry-run unticked) to publish immediately.

Required repository secrets: `MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_PASSWORD` (a Central Portal user token),
`SIGNING_KEY` (ASCII-armored GPG private key), `SIGNING_KEY_PASSWORD`.

## Licence

The data, and therefore the generated code, comes from iso-codes, which is licensed under the
[GNU LGPL 2.1 or later](LICENSE). This project uses the same licence.
