// SPDX-License-Identifier: LGPL-2.1-or-later
// Generated from Debian iso-codes 4.20.1. Do not edit.
package com.wwwdottheinternetdotcom.isocodes.iso3166;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ISO 3166-2 codes for the representation of country subdivisions.
 *
 * <p>Generated from Debian iso-codes 4.20.1 (5046 entries). Do not edit.
 */
public final class Subdivision {
  private static final List<Subdivision> ALL;

  private static final Map<String, Subdivision> BY_CODE;

  static {
    List<Subdivision> list = new ArrayList<>(5046);
    SubdivisionData0.addTo(list);
    SubdivisionData1.addTo(list);
    SubdivisionData2.addTo(list);
    SubdivisionData3.addTo(list);
    SubdivisionData4.addTo(list);
    SubdivisionData5.addTo(list);
    SubdivisionData6.addTo(list);
    SubdivisionData7.addTo(list);
    SubdivisionData8.addTo(list);
    SubdivisionData9.addTo(list);
    SubdivisionData10.addTo(list);
    ALL = List.copyOf(list);
  }
  static {
    Map<String, Subdivision> by_code = new HashMap<>();
    for (Subdivision entry : ALL) {
      by_code.put(entry.code, entry);
    }
    BY_CODE = Map.copyOf(by_code);
  }

  private final String code;

  private final String englishName;

  private final String parent;

  private final String type;

  Subdivision(String code, String englishName, String parent, String type) {
    this.code = code;
    this.englishName = englishName;
    this.parent = parent;
    this.type = type;
  }

  /**
   * Code of the country subset item.
   *
   * @return the value, never null
   */
  public String code() {
    return code;
  }

  /**
   * Name of the country subset item.
   *
   * @return the value, never null
   */
  public String englishName() {
    return englishName;
  }

  /**
   * Parent of the country subset item.
   *
   * @return the value, or empty if this entry has none
   */
  public Optional<String> parent() {
    return Optional.ofNullable(parent);
  }

  /**
   * Type of subset of the country.
   *
   * @return the value, never null
   */
  public String type() {
    return type;
  }

  /**
   * Returns every entry, in upstream order.
   *
   * @return an unmodifiable list of all entries
   */
  public static List<Subdivision> all() {
    return ALL;
  }

  /**
   * Finds the entry whose {@code code} is exactly the given value.
   *
   * @param code the value to look up (case-sensitive)
   * @return the matching entry, or empty if there is none
   */
  public static Optional<Subdivision> fromCode(String code) {
    return Optional.ofNullable(BY_CODE.get(code));
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Subdivision that && code.equals(that.code);
  }

  @Override
  public int hashCode() {
    return code.hashCode();
  }

  @Override
  public String toString() {
    return code;
  }
}
