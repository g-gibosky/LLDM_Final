package org.chromium.base;

import java.util.Locale;
import org.chromium.base.annotations.CalledByNative;

public class LocaleUtils {
    private LocaleUtils() {
    }

    public static String getLocale(Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if ("iw".equals(language)) {
            language = "he";
        } else if ("in".equals(language)) {
            language = "id";
        } else if ("tl".equals(language)) {
            language = "fil";
        }
        return country.isEmpty() ? language : language + "-" + country;
    }

    @CalledByNative
    public static String getDefaultLocale() {
        return getLocale(Locale.getDefault());
    }

    @CalledByNative
    private static String getDefaultCountryCode() {
        CommandLine commandLine = CommandLine.getInstance();
        return commandLine.hasSwitch(BaseSwitches.DEFAULT_COUNTRY_CODE_AT_INSTALL) ? commandLine.getSwitchValue(BaseSwitches.DEFAULT_COUNTRY_CODE_AT_INSTALL) : Locale.getDefault().getCountry();
    }
}
