/**
 * Copyright 2017 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.component.history;

import com.intellij.openapi.ui.ex.MultiLineLabel;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.swing.ui.SamebugBundle;
import com.samebug.clients.idea.ui.controller.history.HistoryCardListener;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

final public class HistoryTabView extends JPanel {
    @NotNull
    final HistoryPanel historyPanel;
    @NotNull
    final JComponent warningPanel;
    @NotNull
    final MessageBus messageBus;

    public HistoryTabView(MessageBus messageBus) {
        historyPanel = new HistoryPanel(messageBus, Collections.<Card.Model>emptyList());
        warningPanel = new JPanel();
        this.messageBus = messageBus;

        setLayout(new BorderLayout());
        setWarningLoading();
    }


    public void setWarningLoading() {
        warningPanel.removeAll();
        warningPanel.add(new JLabel(SamebugBundle.message("samebug.toolwindow.history.content.loading")));
        removeAll();
        add(warningPanel);
    }

    public void update(@NotNull java.util.List<Card.Model> groups) {
        historyPanel.update(groups);
        removeAll();
        add(historyPanel);
    }

    public static final class HistoryPanel extends JPanel {
        final JScrollPane scrollPane;
        final JPanel contentPanel;
        final MessageBus messageBus;

        public HistoryPanel(MessageBus messageBus, java.util.List<Card.Model> groups) {
            scrollPane = new JScrollPane();
            contentPanel = new JPanel();
            this.messageBus = messageBus;

            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
            contentPanel.setBackground(Color.blue);
            scrollPane.setViewportView(contentPanel);

            setLayout(new BorderLayout());
            add(scrollPane);
            update(groups);
        }

        public void update(java.util.List<Card.Model> groups) {
            contentPanel.removeAll();
            for (Card.Model model : groups) {
                // NOTE to be able to access a single card for update, we might have to save the cards to a collection (keeping the references)
                contentPanel.add(new Card(messageBus, model));
            }
        }
    }

    public static final class Card extends JPanel {

        Model model;
        final MultiLineLabel title;
        final MessageBus messageBus;

        public Card(final MessageBus messageBus, Model model) {
            this.messageBus = messageBus;
            title = new MultiLineLabel("");

            title.addMouseListener(new T());

            setLayout(new MigLayout("fillx", "0[fill]0", "10[fill]10"));
            add(title, "wmin 0, hmax 56");

            update(model);
        }

        public void update(Model model) {
            this.model = new Model(model);

            title.setText(model.title);
        }


        public static final class Model {
            public String title;
            public int lastSearchId;

            public Model(Model rhs) {
                this(rhs.title, rhs.lastSearchId);
            }

            public Model(String title, int lastSearchId) {
                this.title = title;
                this.lastSearchId = lastSearchId;
            }
        }

        final private class T extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent e) {
                messageBus.syncPublisher(HistoryCardListener.TOPIC).titleClick(model.lastSearchId);
            }
        }
    }
}
