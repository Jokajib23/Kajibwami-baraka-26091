import java.util.*;

class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

abstract class User {
    private String username;
    private String password;
    private double balance;

    public User(String username, String password, double balance) {
        if (password.length() < 8 || !password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain a number.");
        }
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        this.balance += amount;
    }

    public double getBalance() {
        return balance;
    }

    public void deductBalance(double amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }
        balance -= amount;
    }
}

class RegularUser extends User {
    private List<Bet> betHistory;

    public RegularUser(String username, String password, double balance) {
        super(username, password, balance);
        this.betHistory = new ArrayList<>();
    }

    public void placeBet(Bet bet) throws InsufficientBalanceException {
        deductBalance(bet.getAmount());
        betHistory.add(bet);
        System.out.println("Bet placed successfully!");
    }

    public List<Bet> viewBetHistory() {
        return betHistory;
    }
}

class AdminUser extends User {
    private Map<String, Game> games;

    public AdminUser(String username, String password, double balance) {
        super(username, password, balance);
        this.games = new HashMap<>();
    }

    public void addGame(Game game) {
        if (games.containsKey(game.getGameId())) {
            throw new IllegalArgumentException("Game ID already exists.");
        }
        games.put(game.getGameId(), game);
        System.out.println("Game added successfully!");
    }

    public void removeGame(String gameId) {
        if (!games.containsKey(gameId)) {
            throw new IllegalArgumentException("Game ID does not exist.");
        }
        games.remove(gameId);
        System.out.println("Game removed successfully!");
    }

    public Map<String, Game> getGames() {
        return games;
    }
}

class Bet {
    private String gameId;
    private double amount;

    public Bet(String gameId, double amount) {
        this.gameId = gameId;
        this.amount = amount;
    }

    public String getGameId() {
        return gameId;
    }

    public double getAmount() {
        return amount;
    }
}

class Game {
    private String gameId;
    private String gameName;

    public Game(String gameId, String gameName) {
        this.gameId = gameId;
        this.gameName = gameName;
    }

    public String getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }
}

public class OnlineBettingManagementSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, RegularUser> users = new HashMap<>();
        AdminUser admin = new AdminUser("admin", "admin123", 0);

        System.out.println("Welcome to the Online Betting Management System!");

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Admin - Add Game");
            System.out.println("2. Admin - Remove Game");
            System.out.println("3. User - Register");
            System.out.println("4. User - Place Bet");
            System.out.println("5. User - View Bet History");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1: // Add Game
                        System.out.print("Enter Game ID: ");
                        String gameId = scanner.nextLine();
                        System.out.print("Enter Game Name: ");
                        String gameName = scanner.nextLine();

                        Game game = new Game(gameId, gameName);
                        admin.addGame(game);
                        break;

                    case 2: // Remove Game
                        System.out.print("Enter Game ID to Remove: ");
                        gameId = scanner.nextLine();
                        admin.removeGame(gameId);
                        break;

                    case 3: // Register User
                        System.out.print("Enter Username: ");
                        String username = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        String password = scanner.nextLine();
                        System.out.print("Enter Initial Balance: ");
                        double balance = scanner.nextDouble();

                        RegularUser user = new RegularUser(username, password, balance);
                        users.put(username, user);
                        System.out.println("User registered successfully!");
                        break;

                    case 4: // Place Bet
                        System.out.print("Enter Username: ");
                        username = scanner.nextLine();
                        if (!users.containsKey(username)) {
                            throw new IllegalArgumentException("User not found.");
                        }

                        RegularUser bettingUser = users.get(username);
                        System.out.print("Enter Game ID to Bet On: ");
                        gameId = scanner.nextLine();
                        if (!admin.getGames().containsKey(gameId)) {
                            throw new IllegalArgumentException("Game ID not found.");
                        }

                        System.out.print("Enter Bet Amount: ");
                        double betAmount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline

                        Bet bet = new Bet(gameId, betAmount);
                        bettingUser.placeBet(bet);
                        break;

                    case 5: // View Bet History
                        System.out.print("Enter Username: ");
                        username = scanner.nextLine();
                        if (!users.containsKey(username)) {
                            throw new IllegalArgumentException("User not found.");
                        }

                        List<Bet> bets = users.get(username).viewBetHistory();
                        System.out.println("Bet History:");
                        for (Bet b : bets) {
                            System.out.println("Game ID: " + b.getGameId() + ", Bet Amount: $" + b.getAmount());
                        }
                        break;

                    case 6: // Exit
                        System.out.println("Exiting the system. Goodbye!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
