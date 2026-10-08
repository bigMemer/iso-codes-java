// SPDX-License-Identifier: LGPL-2.1-or-later
// Generated from Debian iso-codes 4.20.1. Do not edit.
package com.wwwdottheinternetdotcom.isocodes.iso3166;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ISO 3166-3 codes for formerly used names of countries.
 *
 * <p>Generated from Debian iso-codes 4.20.1 (31 entries). Do not edit.
 */
public enum FormerCountry {
  /**
   * French Afars and Issas.
   */
  AIDJ("AI", "AFI", "AIDJ", "French Afars and Issas", "262", null, "1977"),

  /**
   * Netherlands Antilles.
   */
  ANHH("AN", "ANT", "ANHH", "Netherlands Antilles", "530", "had numeric code 532 until Aruba split away in 1986", "2010-12-15"),

  /**
   * British Antarctic Territory.
   */
  BQAQ("BQ", "ATB", "BQAQ", "British Antarctic Territory", null, null, "1979"),

  /**
   * Burma, Socialist Republic of the Union of.
   */
  BUMM("BU", "BUR", "BUMM", "Burma, Socialist Republic of the Union of", "104", null, "1989-12-05"),

  /**
   * Byelorussian SSR Soviet Socialist Republic.
   */
  BYAA("BY", "BYS", "BYAA", "Byelorussian SSR Soviet Socialist Republic", "112", null, "1992-06-15"),

  /**
   * Czechoslovakia, Czechoslovak Socialist Republic.
   */
  CSHH("CS", "CSK", "CSHH", "Czechoslovakia, Czechoslovak Socialist Republic", "200", null, "1993-06-15"),

  /**
   * Serbia and Montenegro.
   */
  CSXX("CS", "SCG", "CSXX", "Serbia and Montenegro", "891", null, "2006-09-26"),

  /**
   * Canton and Enderbury Islands.
   */
  CTKI("CT", "CTE", "CTKI", "Canton and Enderbury Islands", "128", null, "1984"),

  /**
   * German Democratic Republic.
   */
  DDDE("DD", "DDR", "DDDE", "German Democratic Republic", "278", null, "1990-10-30"),

  /**
   * Dahomey.
   */
  DYBJ("DY", "DHY", "DYBJ", "Dahomey", "204", null, "1977"),

  /**
   * French Southern and Antarctic Territories.
   */
  FQHH("FQ", "ATF", "FQHH", "French Southern and Antarctic Territories", null, "now split between AQ and TF", "1979"),

  /**
   * France, Metropolitan.
   */
  FXFR("FX", "FXX", "FXFR", "France, Metropolitan", "249", null, "1997-07-14"),

  /**
   * Gilbert and Ellice Islands.
   */
  GEHH("GE", "GEL", "GEHH", "Gilbert and Ellice Islands", "296", "now split into Kiribati and Tuvalu", "1979"),

  /**
   * Upper Volta, Republic of.
   */
  HVBF("HV", "HVO", "HVBF", "Upper Volta, Republic of", "854", null, "1984"),

  /**
   * Johnston Island.
   */
  JTUM("JT", "JTN", "JTUM", "Johnston Island", "396", null, "1986"),

  /**
   * Midway Islands.
   */
  MIUM("MI", "MID", "MIUM", "Midway Islands", "488", null, "1986"),

  /**
   * New Hebrides.
   */
  NHVU("NH", "NHB", "NHVU", "New Hebrides", "548", null, "1980"),

  /**
   * Dronning Maud Land.
   */
  NQAQ("NQ", "ATN", "NQAQ", "Dronning Maud Land", "216", null, "1983"),

  /**
   * Neutral Zone.
   */
  NTHH("NT", "NTZ", "NTHH", "Neutral Zone", "536", "formerly between Saudi Arabia and Iraq", "1993-07-12"),

  /**
   * Pacific Islands (trust territory).
   */
  PCHH("PC", "PCI", "PCHH", "Pacific Islands (trust territory)", "582", "divided into FM, MH, MP, and PW", "1986"),

  /**
   * US Miscellaneous Pacific Islands.
   */
  PUUM("PU", "PUS", "PUUM", "US Miscellaneous Pacific Islands", "849", null, "1986"),

  /**
   * Panama Canal Zone.
   */
  PZPA("PZ", "PCZ", "PZPA", "Panama Canal Zone", null, null, "1980"),

  /**
   * Southern Rhodesia.
   */
  RHZW("RH", "RHO", "RHZW", "Southern Rhodesia", "716", null, "1980"),

  /**
   * Sikkim.
   */
  SKIN("SK", "SKM", "SKIN", "Sikkim", null, null, "1975"),

  /**
   * USSR, Union of Soviet Socialist Republics.
   */
  SUHH("SU", "SUN", "SUHH", "USSR, Union of Soviet Socialist Republics", "810", null, "1992-08-30"),

  /**
   * East Timor.
   */
  TPTL("TP", "TMP", "TPTL", "East Timor", "626", "was Portuguese Timor", "2002-05-20"),

  /**
   * Viet-Nam, Democratic Republic of.
   */
  VDVN("VD", "VDR", "VDVN", "Viet-Nam, Democratic Republic of", null, null, "1977"),

  /**
   * Wake Island.
   */
  WKUM("WK", "WAK", "WKUM", "Wake Island", "872", null, "1986"),

  /**
   * Yemen, Democratic, People's Democratic Republic of.
   */
  YDYE("YD", "YMD", "YDYE", "Yemen, Democratic, People's Democratic Republic of", "720", null, "1990-08-14"),

  /**
   * Yugoslavia, (Socialist) Federal Republic of.
   */
  YUCS("YU", "YUG", "YUCS", "Yugoslavia, (Socialist) Federal Republic of", "891", "had numeric code 890 until the 'Socialist Federal Republic of Yugoslavia' formerly broke apart on 27 April 1992 and the 'Federal Republic of Yugoslavia' was founded", "2003-07-23"),

  /**
   * Zaire, Republic of.
   */
  ZRCD("ZR", "ZAR", "ZRCD", "Zaire, Republic of", "180", null, "1997-07-14");

  private static final Map<String, FormerCountry> BY_ALPHA_4;

  static {
    Map<String, FormerCountry> by_alpha_4 = new HashMap<>();
    for (FormerCountry entry : values()) {
      by_alpha_4.put(entry.alpha4, entry);
    }
    BY_ALPHA_4 = Map.copyOf(by_alpha_4);
  }

  private final String alpha2;

  private final String alpha3;

  private final String alpha4;

  private final String englishName;

  private final String numeric;

  private final String comment;

  private final String withdrawalDate;

  FormerCountry(String alpha2, String alpha3, String alpha4, String englishName, String numeric,
      String comment, String withdrawalDate) {
    this.alpha2 = alpha2;
    this.alpha3 = alpha3;
    this.alpha4 = alpha4;
    this.englishName = englishName;
    this.numeric = numeric;
    this.comment = comment;
    this.withdrawalDate = withdrawalDate;
  }

  /**
   * Two letter alphabetic code of the item.
   *
   * @return the value, never null
   */
  public String alpha2() {
    return alpha2;
  }

  /**
   * Three letter alphabetic code of the item.
   *
   * @return the value, never null
   */
  public String alpha3() {
    return alpha3;
  }

  /**
   * Four letter alphabetic code of the item.
   *
   * @return the value, never null
   */
  public String alpha4() {
    return alpha4;
  }

  /**
   * Name of the item.
   *
   * @return the value, never null
   */
  public String englishName() {
    return englishName;
  }

  /**
   * Three digit numeric code of the item, including leading zeros.
   *
   * @return the value, or empty if this entry has none
   */
  public Optional<String> numeric() {
    return Optional.ofNullable(numeric);
  }

  /**
   * Comment for the item.
   *
   * @return the value, or empty if this entry has none
   */
  public Optional<String> comment() {
    return Optional.ofNullable(comment);
  }

  /**
   * Date of withdrawal from ISO 3166-1.
   *
   * @return the value, never null
   */
  public String withdrawalDate() {
    return withdrawalDate;
  }

  /**
   * Finds the entry whose {@code alpha_4} is exactly the given value.
   *
   * @param alpha4 the value to look up (case-sensitive)
   * @return the matching entry, or empty if there is none
   */
  public static Optional<FormerCountry> fromAlpha4(String alpha4) {
    return Optional.ofNullable(BY_ALPHA_4.get(alpha4));
  }
}
