import java.util.*;

class InvalidViolationException extends Exception {
    public InvalidViolationException(String message) {
        super(message);
    }
}

abstract class Person {
    private String name;
    private String licenseNumber;

    public Person(String name, String licenseNumber) throws IllegalArgumentException {
        if (!licenseNumber.matches("[A-Za-z0-9]{8,12}")) {
            throw new IllegalArgumentException("License number must be alphanumeric and 8-12 characters long.");
        }
        this.name = name;
        this.licenseNumber = licenseNumber;
    }

    public String getName() {
        return name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public abstract void displayDetails();
}

class Driver extends Person {
    private double totalFines;
    private int violationCount;

    public Driver(String name, String licenseNumber) {
        super(name, licenseNumber);
        this.totalFines = 0.0;
        this.violationCount = 0;
    }

    public void addViolation(double fineAmount) throws InvalidViolationException {
        if (fineAmount <= 0) {
            throw new InvalidViolationException("Fine amount must be positive.");
        }
        this.totalFines += fineAmount;
        this.violationCount++;
    }

    public void resetViolations() {
        this.totalFines = 0.0;
        this.violationCount = 0;
    }

    @Override
    public void displayDetails() {
        System.out.println("Driver Name: " + getName());
        System.out.println("License Number: " + getLicenseNumber());
        System.out.println("Total Fines: $" + totalFines);
        System.out.println("Violation Count: " + violationCount);
    }
}

interface TrafficViolation {
    void validateViolation() throws InvalidViolationException;
}

class SpecificViolation implements TrafficViolation {
    private String violationType;
    private double fineAmount;
    private static final List<String> validViolations = Arrays.asList("Speeding", "Parking", "Signal Violation");

    public SpecificViolation(String violationType, double fineAmount) throws InvalidViolationException {
        this.violationType = violationType;
        this.fineAmount = fineAmount;
        validateViolation();
    }

    @Override
    public void validateViolation() throws InvalidViolationException {
        if (!validViolations.contains(violationType)) {
            throw new InvalidViolationException("Invalid violation type.");
        }
        if (fineAmount <= 0) {
            throw new InvalidViolationException("Fine amount must be positive.");
        }
    }

    public String getViolationDetails() {
        return "Violation Type: " + violationType + ", Fine Amount: $" + fineAmount;
    }

    public double getFineAmount() {
        return fineAmount;
    }
}

public class TrafficFineManagement {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Driver> drivers = new HashMap<>();

        System.out.println("Welcome to the Traffic Fine Management System!");

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Add Driver");
            System.out.println("2. Add Traffic Violation");
            System.out.println("3. Reset Violations for Driver");
            System.out.println("4. Display Driver Details");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter Driver Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter License Number (8-12 alphanumeric characters): ");
                        String licenseNumber = scanner.nextLine();

                        Driver driver = new Driver(name, licenseNumber);
                        drivers.put(licenseNumber, driver);
                        System.out.println("Driver added successfully!");
                        break;

                    case 2:
                        System.out.print("Enter Driver's License Number: ");
                        licenseNumber = scanner.nextLine();
                        if (!drivers.containsKey(licenseNumber)) {
                            throw new IllegalArgumentException("Driver not found.");
                        }
                        System.out.print("Enter Violation Type (Speeding, Parking, Signal Violation): ");
                        String violationType = scanner.nextLine();
                        System.out.print("Enter Fine Amount: ");
                        double fineAmount = scanner.nextDouble();

                        SpecificViolation violation = new SpecificViolation(violationType, fineAmount);
                        drivers.get(licenseNumber).addViolation(violation.getFineAmount());
                        System.out.println("Violation added successfully!");
                        break;

                    case 3:
                        System.out.print("Enter Driver's License Number: ");
                        licenseNumber = scanner.nextLine();
                        if (!drivers.containsKey(licenseNumber)) {
                            throw new IllegalArgumentException("Driver not found.");
                        }
                        drivers.get(licenseNumber).resetViolations();
                        System.out.println("Driver's violations have been reset.");
                        break;


                    case 4:
                        System.out.print("Enter Driver's License Number: ");
                        licenseNumber = scanner.nextLine();
                        if (!drivers.containsKey(licenseNumber)) {
                            throw new IllegalArgumentException("Driver not found.");
                        }
                        drivers.get(licenseNumber).displayDetails();
                        break;

                    case 5:
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
