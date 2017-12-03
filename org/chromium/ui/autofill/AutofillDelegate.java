package org.chromium.ui.autofill;

public interface AutofillDelegate {
    void deleteSuggestion(int i);

    void dismissed();

    void suggestionSelected(int i);
}
