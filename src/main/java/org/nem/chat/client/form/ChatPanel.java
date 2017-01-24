package org.nem.chat.client.form;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.nem.chat.client.service.Chat;

/**
 *
 * @author Nemanja Marjanović
 */
public class ChatPanel extends JPanel {

    private final Chat chat;
    private final Long id;
    private final JTextArea messageHistory;
    private final JTextField input;
    private final JButton close;

    public ChatPanel(final Long id, final Chat chat) {

        this.chat = chat;
        this.id = id;
        this.messageHistory = new JTextArea();
        this.input = new JTextField();
        this.close = new JButton("Zatvori");

        this.close.addActionListener((e) -> {
            this.chat.closeChatSession(this.id);
            this.setEnabled(false);
            this.messageHistory.append("CLOSED");
        });

        this.input.addActionListener((e) -> {
            String msg = ChatPanel.this.input.getText();
            ChatPanel.this.input.setText("");
            ChatPanel.this.messageHistory.append("Ja: " + msg + "\n");
            this.chat.sendChatMessage(this.id, msg);
        });

        new Timer(100, (e) -> {
            String incoming = this.chat.receiveChatMessage(this.id);
            if (incoming != null) {
                this.messageHistory.append(incoming + "\n");
            }
        }).start();

        this.initComponents();
    }

    private void initComponents() {
        this.messageHistory.setEditable(false);
        setLayout(new MigLayout("", "[grow]", "[][grow][]"));
        add(this.close, new CC().alignX("right").wrap());
        add(new JScrollPane(this.messageHistory), new CC().growX().growY().wrap());
        add(this.input, new CC().growX().wrap());
    }

}
