package netclean.chat.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import netclean.chat.packets.servertoclient.MessageType;

@SuppressWarnings("serial")
public class ClientGUI extends JFrame
{
    private static Theme theme = new DefaultTheme();
    private JTextPane text;
    private JTextField txtCommand;
    private JList<String> list;

    public ClientGUI()
    {
        setTitle("NetClean Chat | Welcome!");
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));

        txtCommand = new JTextField();
        panel.add(txtCommand, BorderLayout.CENTER);
        txtCommand.setColumns(10);

        JButton btnSend = new JButton("Send");
        panel.add(btnSend, BorderLayout.EAST);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.9);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane();
        text = new JTextPane();
        text.setBackground(Color.DARK_GRAY);
        text.setForeground(Color.WHITE);
        text.setEditable(false);
        text.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));

        scrollPane.setViewportView(text);
        splitPane.setLeftComponent(scrollPane);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        list = new JList<String>();
        list.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "<html>Connected users <p>('/r users' to refresh)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        scrollPane_1.setViewportView(list);
        splitPane.setRightComponent(scrollPane_1);

        ActionListener send = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ChatClient.send(txtCommand.getText());
                txtCommand.setText("");
            }
        };

        btnSend.addActionListener(send);
        txtCommand.addActionListener(send);

        setLocationRelativeTo(null);
    }

    private void appendToPane(JTextPane tp, String msg, MessageType t)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = SimpleAttributeSet.EMPTY;

        MessageStyle ms = theme.getStyle(t);

        aset = sc.addAttribute(aset, StyleConstants.Foreground, ms.getForeground() == null ? tp.getForeground() : ms.getForeground());
        aset = sc.addAttribute(aset, StyleConstants.Background, ms.getBackground() == null ? tp.getBackground() : ms.getBackground());
        aset = sc.addAttribute(aset, StyleConstants.Bold, ms.isBold());
        aset = sc.addAttribute(aset, StyleConstants.Italic, ms.isItalic());
        aset = sc.addAttribute(aset, StyleConstants.Underline, ms.isUnderlined());

        StyledDocument sd = tp.getStyledDocument();
        try
        {
            sd.insertString(sd.getLength(), msg, aset);
        }
        catch(BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    private class ArrayListModel extends AbstractListModel<String>
    {
        private ArrayList<String> listData;

        private ArrayListModel(ArrayList<String> listData)
        {
            this.listData = listData;
        }

        public int getSize()
        {
            return listData.size();
        }

        public String getElementAt(int i)
        {
            return listData.get(i);
        }
    }

    public void updateList(ArrayList<String> strings)
    {
        list.setModel(new ArrayListModel(strings));
    }

    public synchronized void append(String s, MessageType type)
    {
        SwingUtilities.invokeLater(() ->
        {
            try
            {
                appendToPane(text, s + "\n", type);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        });

    }
}
