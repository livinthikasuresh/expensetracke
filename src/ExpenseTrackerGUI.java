import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ExpenseTrackerGUI extends JFrame {

    private JTextField expenseField;
    private JComboBox<String> categoryComboBox;
    private JTextArea expenseListArea;
    private Map<String, Map<String, Double>> usersExpensesMap; // Store usernames and their expenses
    private Map<String, String> usersMap; // Store usernames and passwords
    private String currentUser;

    public ExpenseTrackerGUI() {
        setTitle("Family Expense Tracker");
        setSize(500, 300); // Set the width to 500 pixels
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        usersExpensesMap = new HashMap<>();
        usersMap = new HashMap<>();

        // Dummy users
        usersMap.put("dad", "dad123");
        usersMap.put("mom", "mom123");
        usersMap.put("child1", "child1123");
        usersMap.put("child2", "child2123");

        for (String username : usersMap.keySet()) {
            usersExpensesMap.put(username, new HashMap<>()); // Initialize empty expenses map for each user
        }

        showLoginOrRegisterPanel();
    }

    private void showLoginOrRegisterPanel() {
        getContentPane().removeAll();
        JPanel loginOrRegisterPanel = new JPanel(new GridLayout(3, 2));
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
     
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginOrRegisterPanel.add(usernameLabel);
        loginOrRegisterPanel.add(usernameField);
        loginOrRegisterPanel.add(passwordLabel);
        loginOrRegisterPanel.add(passwordField);
        loginOrRegisterPanel.add(loginButton);
        loginOrRegisterPanel.add(registerButton);

        loginButton.setForeground(Color.BLACK); // Set button's foreground color to black
        registerButton.setForeground(Color.BLACK); // Set button's foreground color to black

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticate(username, password)) {
                    currentUser = username;
                    getContentPane().removeAll();
                    addComponents();
                    setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(ExpenseTrackerGUI.this,
                            "Invalid username or password. Please try again.");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(ExpenseTrackerGUI.this,
                            "Username and password cannot be empty.");
                    return;
                }
                if (usersMap.containsKey(username)) {
                    JOptionPane.showMessageDialog(ExpenseTrackerGUI.this,
                            "Username already exists. Please choose another one.");
                } else {
                    usersMap.put(username, password);
                    usersExpensesMap.put(username, new HashMap<>()); // Initialize empty expenses map for new user
                    JOptionPane.showMessageDialog(ExpenseTrackerGUI.this,
                            "Registration successful. You can now log in.");
                }
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(loginOrRegisterPanel, BorderLayout.CENTER);
    }

    private boolean authenticate(String username, String password) {
        // Dummy authentication for demonstration purposes
        return usersMap.containsKey(username) && usersMap.get(username).equals(password);
    }

    private void addComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));

        JLabel expenseLabel = new JLabel("Expense:");
        expenseField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        categoryComboBox = new JComboBox<>(new String[]{"Food", "Stationery", "Entertainment", "Transportation", "Cloths","Toys","Grocery"});
        JButton addButton = new JButton("Add Expense");
        addButton.addActionListener(new AddButtonListener());

        inputPanel.add(expenseLabel);
        inputPanel.add(expenseField);
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryComboBox);
        inputPanel.add(new JLabel());
        inputPanel.add(addButton);

        expenseListArea = new JTextArea();
        expenseListArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(expenseListArea);

        JButton viewRecordsButton = new JButton("View My Records");
        viewRecordsButton.addActionListener(new ViewRecordsButtonListener());
        viewRecordsButton.setForeground(Color.BLACK); // Set button's foreground color to black
        

        JButton viewAllExpensesButton = new JButton("View All Expenses");
        viewAllExpensesButton.addActionListener(new ViewAllExpensesButtonListener());
        viewAllExpensesButton.setForeground(Color.BLACK); // Set button's foreground color to black

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new LogoutButtonListener());
        logoutButton.setForeground(Color.BLACK); // Set button's foreground color to black
        logoutButton.setBackground(Color.RED);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewRecordsButton);
        buttonPanel.add(viewAllExpensesButton);
        buttonPanel.add(logoutButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        updateExpenseListArea();
    }

    private void updateExpenseListArea() {
        StringBuilder sb = new StringBuilder();
        sb.append("Category\tExpense\n");
        Map<String, Double> userExpenses = usersExpensesMap.get(currentUser);
        for (Map.Entry<String, Double> entry : userExpenses.entrySet()) {
            sb.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
        }
        expenseListArea.setText(sb.toString());
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String expense = expenseField.getText();
            String category = (String) categoryComboBox.getSelectedItem();
            try {
                double amount = Double.parseDouble(expense);
                if (amount < 0) {
                    JOptionPane.showMessageDialog(ExpenseTrackerGUI.this, "Expense amount cannot be negative!");
                    return;
                }
                Map<String, Double> userExpenses = usersExpensesMap.get(currentUser);
                if (userExpenses.containsKey(category)) {
                    userExpenses.put(category, userExpenses.get(category) + amount);
                } else {
                    userExpenses.put(category, amount);
                }
                updateExpenseListArea();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ExpenseTrackerGUI.this, "Please enter a valid number for expense!");
            }
            expenseField.setText("");
        }
    }

    private class ViewRecordsButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder records = new StringBuilder();
            records.append("Category\tExpense\n");
            Map<String, Double> userExpenses = usersExpensesMap.get(currentUser);
            for (Map.Entry<String, Double> entry : userExpenses.entrySet()) {
                records.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
            }
            JTextArea recordsArea = new JTextArea(records.toString());
            recordsArea.setEditable(false);
            JOptionPane.showMessageDialog(ExpenseTrackerGUI.this, new JScrollPane(recordsArea),
                    "My Expense Records", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private class ViewAllExpensesButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentUser.equals("dad")) { // Only dad can view all expenses
                StringBuilder records = new StringBuilder();
                records.append("User\tCategory\tExpense\n");
                for (Map.Entry<String, Map<String, Double>> userEntry : usersExpensesMap.entrySet()) {
                    String username = userEntry.getKey();
                    if (!username.equals(currentUser)) { // Skip current user's expenses
                        Map<String, Double> userExpenses = userEntry.getValue();
                        for (Map.Entry<String, Double> entry : userExpenses.entrySet()) {
                            records.append(username).append("\t").append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
                        }
                    }
                }
                JTextArea recordsArea = new JTextArea(records.toString());
                recordsArea.setEditable(false);
                JOptionPane.showMessageDialog(ExpenseTrackerGUI.this, new JScrollPane(recordsArea),
                        "All Expenses", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ExpenseTrackerGUI.this,
                        "You are not authorized to view all expenses.");
            }
        }
    }

    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentUser = null;
            getContentPane().removeAll();
            showLoginOrRegisterPanel();
            setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseTrackerGUI expenseTrackerGUI = new ExpenseTrackerGUI();
            expenseTrackerGUI.setVisible(true);
        });
    }
}




