package org.nem.chat.client.form;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.nem.chat.client.service.Chat;
import org.nem.chat.protocol.model.User;

/**
 *
 * @author Nemanja Marjanović
 */
public class UsersPanel extends JPanel {

    private final Chat chat;
    private final JList<User> chaterList;
    private final DefaultListModel<User> chaterListModel;

    public UsersPanel(final Chat chat) {

        this.chat = chat;
        this.chaterListModel = new DefaultListModel<>();
        this.chaterList = new JList<>(chaterListModel);

        this.chaterList.addListSelectionListener((e) -> {
            this.chaterList.setEnabled(false);
            if (!this.chaterList.isSelectionEmpty()) {
                this.chat.openChatSession(this.chaterList.getSelectedValue());
            }
            this.chaterList.clearSelection();
            this.chaterList.setEnabled(true);
        });

        new Timer(1000, (e) -> {
            this.chaterListModel.removeAllElements();
            if (this.chat.isLogged()) {
                this.chat.getAvailableBuddies().forEach(this.chaterListModel::addElement);
            }
        }).start();

        this.initComponents();
    }

    private void initComponents() {
        setLayout(new MigLayout("", "[grow]", "[grow]"));
        add(new JScrollPane(this.chaterList), new CC().growX().growY());;
    }

}
