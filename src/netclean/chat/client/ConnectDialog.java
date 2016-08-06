package netclean.chat.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConnectDialog extends JDialog
{
    private final JPanel contentPanel = new JPanel();
    JTextField txtLocalhost;
    JSpinner textField_1;

    /**
     * Create the dialog.
     */
    public ConnectDialog(JFrame parent)
    {
        super(parent);
        setModalityType(ModalityType.APPLICATION_MODAL);
        
        setTitle("NetClean Chat Connect");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][]"));

        JLabel lblNetcleanChat = new JLabel("NetClean Chat");
        lblNetcleanChat.setFont(lblNetcleanChat.getFont().deriveFont(22F));
        contentPanel.add(lblNetcleanChat, "cell 0 0 2 1,alignx center");

        JLabel lblServerAddress = new JLabel("Server address :");
        contentPanel.add(lblServerAddress, "cell 0 1,alignx trailing");

        txtLocalhost = new JTextField();
        txtLocalhost.setText("localhost");
        contentPanel.add(txtLocalhost, "cell 1 1,growx");
        txtLocalhost.setColumns(10);

        JLabel lblPort = new JLabel("Port :");
        contentPanel.add(lblPort, "cell 0 2,alignx trailing");

        textField_1 = new JSpinner();
        textField_1.setModel(new SpinnerNumberModel(new Integer(7566), new Integer(0), new Integer(65535), new Integer(1)));
        contentPanel.add(textField_1, "cell 1 2,growx");

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        buttonPane.add(cancelButton);

        JButton okButton = new JButton("Connect!~");
        okButton.addActionListener(e -> dispose());
        okButton.setBackground(Color.ORANGE);
        okButton.setForeground(new Color(255, 69, 0));
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        pack();
        
        setLocationRelativeTo(parent);
    }

}
