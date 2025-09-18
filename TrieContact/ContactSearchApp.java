import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class ContactSearchApp extends JFrame {
    private final ContactManager manager;
    private final JTextField searchField = new JTextField(20);
    private final DefaultListModel<Contact> suggestionModel = new DefaultListModel<>();
    private final JList<Contact> suggestionList = new JList<>(suggestionModel);

    private final JTextField nameField = new JTextField(15);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private final JButton addButton = new JButton("Add Contact");

    public ContactSearchApp() {
        super("Contact Search (Trie + CSV persistence)");
        manager = new ContactManager("contacts.csv");
        initUI();
    }

    private void initUI() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        setLayout(new BorderLayout(10,10));

        // Top: Search and suggestions
        JPanel topPanel = new JPanel(new BorderLayout(1,5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Search Contacts"));
        searchField.setFont(searchField.getFont().deriveFont(Font.PLAIN, 16f));
        topPanel.add(searchField, BorderLayout.NORTH);

        suggestionList.setVisibleRowCount(8);
        suggestionList.setCellRenderer(new ContactCellRenderer());
        JScrollPane scroll = new JScrollPane(suggestionList);
        topPanel.add(scroll, BorderLayout.CENTER);

        add(topPanel, BorderLayout.CENTER);

        // Right: Add contact form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add New Contact"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx=0; gbc.gridy=0; form.add(new JLabel("Name:"), gbc);
        gbc.gridx=1; form.add(nameField, gbc);
        gbc.gridx=0; gbc.gridy=1; form.add(new JLabel("Phone:"), gbc);
        gbc.gridx=1; form.add(phoneField, gbc);
        gbc.gridx=0; gbc.gridy=2; form.add(new JLabel("Email:"), gbc);
        gbc.gridx=1; form.add(emailField, gbc);
        gbc.gridx=1; gbc.gridy=3; gbc.anchor = GridBagConstraints.EAST; form.add(addButton, gbc);

        add(form, BorderLayout.EAST);

        // Listeners
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Contact c = suggestionList.getSelectedValue();
                    if (c != null) populateForm(c);
                }
            }
        });

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Name is required."); return; }
            Contact c = new Contact(name, phone, email);
            boolean added = manager.addContact(c);
            if (!added) {
                JOptionPane.showMessageDialog(this, "Contact already exists (same name + phone).");
            } else {
                JOptionPane.showMessageDialog(this, "Contact added!");
                clearForm();
                updateSuggestions();
            }
        });

        pack();
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void populateForm(Contact c) {
        nameField.setText(c.getName());
        phoneField.setText(c.getPhone());
        emailField.setText(c.getEmail());
    }
    private void clearForm() { nameField.setText(""); phoneField.setText(""); emailField.setText(""); }

    private void updateSuggestions() {
        String text = searchField.getText().trim();
        SwingUtilities.invokeLater(() -> {
            suggestionModel.clear();
            if (text.isEmpty()) return;
            java.util.List<Contact> results = manager.search(text);
            int limit = Math.min(results.size(), 30); 
            for (int i = 0; i < limit; i++) suggestionModel.addElement(results.get(i));
        });
    }

    private static class ContactCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Contact) {
                Contact c = (Contact) value;
                setText("<html><b>" + escape(c.getName()) + "</b> &nbsp; " + escape(c.getPhone()) +
                        " &nbsp; <i>" + escape(c.getEmail()) + "</i></html>");
            }
            return this;
        }
        private static String escape(String s) { return s == null ? "" : s; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ContactSearchApp().setVisible(true));
    }
}