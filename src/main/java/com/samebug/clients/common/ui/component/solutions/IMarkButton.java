package com.samebug.clients.common.ui.component.solutions;

import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.Nullable;

public interface IMarkButton {

    void setLoading();
    void setError();
    void update(Model model);

    final class Model {
        public final int marks;
        @Nullable
        public final Integer userMarkId;
        public final boolean userCanMark;

        public Model(Model rhs) {
            this(rhs.marks, rhs.userMarkId, rhs.userCanMark);
        }

        public Model(int marks, @Nullable Integer userMarkId, boolean userCanMark) {
            this.marks = marks;
            this.userMarkId = userMarkId;
            this.userCanMark = userCanMark;
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("MarkPanel", Listener.class);

        void markClicked(IMarkButton markButton, Integer solutionId, Integer currentMarkId);
    }

}
