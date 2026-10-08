// SPDX-License-Identifier: LGPL-2.1-or-later
// Generated from Debian iso-codes 4.20.1. Do not edit.
package com.wwwdottheinternetdotcom.isocodes.iso639;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ISO 639-3 codes for comprehensive coverage of languages.
 *
 * <p>Generated from Debian iso-codes 4.20.1 (7923 entries). Do not edit.
 */
public final class Language {
  private static final List<Language> ALL;

  private static final Map<String, Language> BY_ALPHA_3;

  private static final Map<String, Language> BY_ALPHA_2;

  private static final Map<String, Language> BY_BIBLIOGRAPHIC;

  static {
    List<Language> list = new ArrayList<>(7923);
    LanguageData0.addTo(list);
    LanguageData1.addTo(list);
    LanguageData2.addTo(list);
    LanguageData3.addTo(list);
    LanguageData4.addTo(list);
    LanguageData5.addTo(list);
    LanguageData6.addTo(list);
    LanguageData7.addTo(list);
    LanguageData8.addTo(list);
    LanguageData9.addTo(list);
    LanguageData10.addTo(list);
    LanguageData11.addTo(list);
    LanguageData12.addTo(list);
    LanguageData13.addTo(list);
    LanguageData14.addTo(list);
    LanguageData15.addTo(list);
    ALL = List.copyOf(list);
  }
  static {
    Map<String, Language> by_alpha_3 = new HashMap<>();
    Map<String, Language> by_alpha_2 = new HashMap<>();
    Map<String, Language> by_bibliographic = new HashMap<>();
    for (Language entry : ALL) {
      by_alpha_3.put(entry.alpha3, entry);
      if (entry.alpha2 != null) {
        by_alpha_2.put(entry.alpha2, entry);
      }
      if (entry.bibliographic != null) {
        by_bibliographic.put(entry.bibliographic, entry);
      }
    }
    BY_ALPHA_3 = Map.copyOf(by_alpha_3);
    BY_ALPHA_2 = Map.copyOf(by_alpha_2);
    BY_BIBLIOGRAPHIC = Map.copyOf(by_bibliographic);
  }

  private final String alpha3;

  private final String englishName;

  private final String scope;

  private final String type;

  private final String alpha2;

  private final String commonName;

  private final String invertedName;

  private final String bibliographic;

  Language(String alpha3, String englishName, String scope, String type, String alpha2,
      String commonName, String invertedName, String bibliographic) {
    this.alpha3 = alpha3;
    this.englishName = englishName;
    this.scope = scope;
    this.type = type;
    this.alpha2 = alpha2;
    this.commonName = commonName;
    this.invertedName = invertedName;
    this.bibliographic = bibliographic;
  }

  /**
   * Three letter terminology code of the language.
   *
   * @return the value, never null
   */
  public String alpha3() {
    return alpha3;
  }

  /**
   * Reference name of the language.
   *
   * @return the value, never null
   */
  public String englishName() {
    return englishName;
  }

  /**
   * Scope of the language: I(ndividual), M(acrolanguage), S(pecial).
   *
   * @return the value, never null
   */
  public String scope() {
    return scope;
  }

  /**
   * Type of the language: A(ncient), C(onstructed), E(xtinct), H(istorical), L(iving), S(pecial).
   *
   * @return the value, never null
   */
  public String type() {
    return type;
  }

  /**
   * Two letter alphabetic code of the language from part 1.
   *
   * @return the value, or empty if this entry has none
   */
  public Optional<String> alpha2() {
    return Optional.ofNullable(alpha2);
  }

  /**
   * Common name of the language.
   *
   * @return the value, or empty if this entry has none
   */
  public Optional<String> commonName() {
    return Optional.ofNullable(commonName);
  }

  /**
   * Inverted name of the language.
   *
   * @return the value, or empty if this entry has none
   */
  public Optional<String> invertedName() {
    return Optional.ofNullable(invertedName);
  }

  /**
   * Three letter bibliographic code of the language from part 2.
   *
   * @return the value, or empty if this entry has none
   */
  public Optional<String> bibliographic() {
    return Optional.ofNullable(bibliographic);
  }

  /**
   * Returns every entry, in upstream order.
   *
   * @return an unmodifiable list of all entries
   */
  public static List<Language> all() {
    return ALL;
  }

  /**
   * Finds the entry whose {@code alpha_3} is exactly the given value.
   *
   * @param alpha3 the value to look up (case-sensitive)
   * @return the matching entry, or empty if there is none
   */
  public static Optional<Language> fromAlpha3(String alpha3) {
    return Optional.ofNullable(BY_ALPHA_3.get(alpha3));
  }

  /**
   * Finds the entry whose {@code alpha_2} is exactly the given value.
   *
   * @param alpha2 the value to look up (case-sensitive)
   * @return the matching entry, or empty if there is none
   */
  public static Optional<Language> fromAlpha2(String alpha2) {
    return Optional.ofNullable(BY_ALPHA_2.get(alpha2));
  }

  /**
   * Finds the entry whose {@code bibliographic} is exactly the given value.
   *
   * @param bibliographic the value to look up (case-sensitive)
   * @return the matching entry, or empty if there is none
   */
  public static Optional<Language> fromBibliographic(String bibliographic) {
    return Optional.ofNullable(BY_BIBLIOGRAPHIC.get(bibliographic));
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Language that && alpha3.equals(that.alpha3);
  }

  @Override
  public int hashCode() {
    return alpha3.hashCode();
  }

  @Override
  public String toString() {
    return alpha3;
  }
}
