import java.util.*;

class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }
}

class CartException extends Exception {
    public CartException(String message) {
        super(message);
    }
}

class PaymentException extends Exception {
    public PaymentException(String message) {
        super(message);
    }
}

abstract class User {
    private int userId;
    private String name;
    private String email;
    private String password;

    public User(int userId, String name, String email, String password) throws RegistrationException {
        if (userId <= 0) {
            throw new RegistrationException("User ID must be positive.");
        }
        if (name.isEmpty()) {
            throw new RegistrationException("Name cannot be empty.");
        }
        if (!email.contains("@")) {
            throw new RegistrationException("Invalid email format.");
        }
        if (password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[!@#$%^&*].*")) {
            throw new RegistrationException("Password must be at least 8 characters long and include a number and a special character.");
        }
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
}

class Customer extends User {
    private String address;
    private List<Product> cart;

    public Customer(int userId, String name, String email, String password, String address) throws RegistrationException {
        super(userId, name, email, password);
        if (address.isEmpty()) {
            throw new RegistrationException("Address cannot be empty.");
        }
        this.address = address;
        this.cart = new ArrayList<>();
    }

    public void addToCart(Product product) throws CartException {
        if (cart.contains(product)) {
            throw new CartException("Product already in the cart.");
        }
        cart.add(product);
        System.out.println("Product added to cart!");
    }

    public void removeFromCart(int productId) throws CartException {
        Product toRemove = null;
        for (Product product : cart) {
            if (product.getProductId() == productId) {
                toRemove = product;
                break;
            }
        }
        if (toRemove == null) {
            throw new CartException("Product not found in the cart.");
        }
        cart.remove(toRemove);
        System.out.println("Product removed from cart!");
    }

    public List<Product> viewCart() {
        return cart;
    }
}

abstract class Product {
    private int productId;
    private String name;
    private double price;

    public Product(int productId, String name, double price) {
        if (productId <= 0 || name.isEmpty() || price <= 0) {
            throw new IllegalArgumentException("Invalid product details.");
        }
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public abstract String getDetails();
}

class Electronics extends Product {
    private int warrantyPeriod;

    public Electronics(int productId, String name, double price, int warrantyPeriod) {
        super(productId, name, price);
        this.warrantyPeriod = warrantyPeriod;
    }

    @Override
    public String getDetails() {
        return "Electronics [ID: " + getProductId() + ", Name: " + getName() + ", Price: $" + getPrice() +
               ", Warranty: " + warrantyPeriod + " months]";
    }
}

class PaymentProcessor {
    public void pay(double amount) throws PaymentException {
        if (amount <= 0) {
            throw new PaymentException("Payment amount must be positive.");
        }
        System.out.println("Payment of $" + amount + " processed successfully!");
    }
}

public class OnlineShoppingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<Integer, Customer> customers = new HashMap<>();

        System.out.println("Welcome to the Online Shopping System!");

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Register Customer");
            System.out.println("2. Add Product to Cart");
            System.out.println("3. Remove Product from Cart");
            System.out.println("4. View Cart");
            System.out.println("5. Make Payment");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1: // Register Customer
                        System.out.print("Enter User ID: ");
                        int userId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        String password = scanner.nextLine();
                        System.out.print("Enter Address: ");
                        String address = scanner.nextLine();

                        Customer customer = new Customer(userId, name, email, password, address);
                        customers.put(userId, customer);
                        System.out.println("Customer registered successfully!");
                        break;

                    case 2: // Add Product to Cart
                        System.out.print("Enter User ID: ");
                        userId = scanner.nextInt();
                        if (!customers.containsKey(userId)) {
                            throw new IllegalArgumentException("Customer not found.");
                        }

                        System.out.print("Enter Product ID: ");
                        int productId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter Product Name: ");
                        String productName = scanner.nextLine();
                        System.out.print("Enter Product Price: ");
                        double price = scanner.nextDouble();
                        System.out.print("Enter Warranty Period (in months): ");
                        int warranty = scanner.nextInt();

                        Product product = new Electronics(productId, productName, price, warranty);
                        customers.get(userId).addToCart(product);
                        break;

                    case 3: // Remove Product from Cart
                        System.out.print("Enter User ID: ");
                        userId = scanner.nextInt();
                        if (!customers.containsKey(userId)) {
                            throw new IllegalArgumentException("Customer not found.");
                        }

                        System.out.print("Enter Product ID to Remove: ");
                        productId = scanner.nextInt();
                        customers.get(userId).removeFromCart(productId);
                        break;

                    case 4: // View Cart
                        System.out.print("Enter User ID: ");
                        userId = scanner.nextInt();
                        if (!customers.containsKey(userId)) {
                            throw new IllegalArgumentException("Customer not found.");
                        }

                        System.out.println("Cart Items:");
                        for (Product prod : customers.get(userId).viewCart()) {
                            System.out.println(prod.getDetails());
                        }
                        break;

                    case 5: // Make Payment
                        System.out.print("Enter User ID: ");
                        userId = scanner.nextInt();
                        if (!customers.containsKey(userId)) {
                            throw new IllegalArgumentException("Customer not found.");
                        }

                        double totalAmount = customers.get(userId).viewCart()
                                                       .stream()
                                                       .mapToDouble(Product::getPrice)
                                                       .sum();
                        PaymentProcessor processor = new PaymentProcessor();
                        processor.pay(totalAmount);
                        System.out.println("Payment completed!");
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
