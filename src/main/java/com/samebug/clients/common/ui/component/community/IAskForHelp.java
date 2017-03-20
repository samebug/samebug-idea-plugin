package com.samebug.clients.common.ui.component.community;

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.form.FormMismatchException;

import java.util.List;

public interface IAskForHelp {
    void startRequestTip();

    void failRequestTip(List<FieldError> errors) throws FormMismatchException;

    void successRequestTip(/*TODO param*/);

    final class Model {
        public final int numberOfBugmates;
        public final String exceptionTitle;

        public Model(Model rhs) {
            this(rhs.numberOfBugmates, rhs.exceptionTitle);
        }

        public Model(int numberOfBugmates, String exceptionTitle) {
            this.numberOfBugmates = numberOfBugmates;
            this.exceptionTitle = exceptionTitle;
        }
    }

    interface Listener {
        void askBugmates(IAskForHelp source, String description);
    }
}
