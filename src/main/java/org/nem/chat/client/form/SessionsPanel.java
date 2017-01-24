package org.nem.chat.client.form;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.nem.chat.client.model.Session;
import org.nem.chat.client.service.Chat;

/**
 *
 * @author Nemanja Marjanović
 */
public class SessionsPanel extends JPanel {

    private final Chat chat;
    private final JTabbedPane chatSessionTabs;
    private final Map<Long, Component> tabs = new HashMap<>();

    public SessionsPanel(final Chat chat) {

        this.chat = chat;
        this.chatSessionTabs = new JTabbedPane();

        new Timer(100, (e) -> {

            Session openedSession = this.chat.getOpenedSession();
            if (openedSession != null) {
                ChatPanel csp = new ChatPanel(openedSession.getId(), this.chat);
                JScrollPane jScrollPane = new JScrollPane(csp);
                this.chatSessionTabs.add(openedSession.getId()
                        + openedSession.getBuddy().getName(),
                        jScrollPane);
                this.tabs.put(openedSession.getId(), jScrollPane);
            }

            Session closedSession = this.chat.getClosedSession();
            if (closedSession != null
                    && this.tabs.containsKey(closedSession.getId())) {
                this.chatSessionTabs.remove(this.tabs.get(closedSession.getId()));
            }

            if (!this.chat.isLogged()) {
                this.chatSessionTabs.removeAll();
            }

        }).start();

        this.initComponents();
    }

    private void initComponents() {
        setLayout(new MigLayout("", "[grow]", "[grow]"));
        add(chatSessionTabs, new CC().growX().growY());;
    }

}
