//used utilities
import java.io.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.io.Serializable;

public class LibraryManagementSystemFA3 {
    //main method
    public static void main(String[] args) throws IOException {
        //scanner name is scanner
        Scanner scanner = new Scanner(System.in);
        //set ExitProgram to false
        boolean exitProgram = false;

        //collection of methods in Library
        MethodsLibrary library = new MethodsLibrary();
        library.BooksToCheckout();

        System.out.println("\n....................................");
        System.out.print("     LIBRARY MANAGEMENT SYSTEM\n");
        System.out.println("....................................");

        //while the program runs
        while (!exitProgram) {
            System.out.println("\n MAIN MENU");
            System.out.println("-----------");
            System.out.println("1. Display List of Books");
            System.out.println("2. Add Book");
            System.out.println("3. Search Book");
            System.out.println("4. Add Member");
            System.out.println("5. Check out Books");
            System.out.println("6. Check Due Dates");
            System.out.println("7. View Fines");
            System.out.println("8. Manage Notifications");
            System.out.println("9. Exit");
            System.out.println("\nEnter your choice: ");
            int userChoice = scanner.nextInt(); // go to next option

            //switch cases for option that user chooses
            switch (userChoice) {
                //if user selects 1st option
                case 1:
                    library.DisplayAllBooks();
                    break;
                //if user selects 2nd option
                case 2:
                    library.AddBook();
                    break;
                //if user selects 3rd option
                case 3:
                    System.out.print("Enter Book Title to Search: ");
                    scanner.nextLine();
                    String searchTitle = scanner.nextLine();
                    library.SearchBook(searchTitle);
                    break;
                //if user selects 4th option
                case 4:
                    library.AddMember();
                    break;
                //if user selects 5th option
                case 5:
                    //system scans through the title and member's name
                    System.out.print("Enter the title of the book to check out: ");
                    scanner.nextLine();
                    String checkoutTitle = scanner.nextLine();
                    System.out.print("Enter the name of the member: ");
                    String checkoutMember = scanner.nextLine();
                    library.CheckoutBooks(checkoutTitle, checkoutMember);
                    library.MemberFines();
                    break;
                //if user selects 6th option
                case 6:
                    library.CheckDueDates();
                    break;
                //if user selects 7th option
                case 7:
                    library.ViewFines();
                    break;
                //if user selects 8th options
                case 8:
                    library.ManageNotifications();
                    break;
                //if user selects 9th option and wants to leave the program
                case 9:
                    exitProgram = true; //set ExitProgram to true, which stops the program from running further
                    break;
            }
        }
    }

    //Book class with properties
    static class Book implements Serializable {
        public String BookTitle;
        public String BookAuthor;
        public int ISBN;
        public boolean isAvailable;
        public LocalDate DueDate;

        public Book(String BookTitle, String BookAuthor, int ISBN, boolean isAvailable, LocalDate DueDate) {
            this.BookTitle = BookTitle;
            this.BookAuthor = BookAuthor;
            this.ISBN = ISBN;
            this.isAvailable = isAvailable;
            this.DueDate = DueDate;
        }

        //Get methods with return
        public String getBookTitle() {
            return BookTitle;
        }

        public String getBookAuthor() {
            return BookAuthor;
        }

        public int getISBN() {
            return ISBN;
        }

        public boolean isAvailable() {
            return isAvailable;
        }

        public void setAvailable(boolean available) {
            isAvailable = available;
        }

        public LocalDate getDueDate() {
            return DueDate;
        }

        public void setDueDate(LocalDate DueDate) {
            this.DueDate = DueDate;
        }
    }

    //Member class with properties
    static class Member implements Serializable{
        public String MemberName;
        public String MemberEmail;
        public List<Book> Borrowed;
        public LocalDate DueDate;
        public double fine;

        public Member(String MemberName, String MemberEmail, List<Book> Borrowed, double fine) {
            this.MemberName = MemberName;
            this.fine = 0.00;
            // validate user email
            if (EmailValidation.isValidEmail(MemberEmail)) {
                this.MemberEmail = MemberEmail;
            } else {
                // throw error when email doesn't fit the pattern in EmailValidation
                throw new IllegalArgumentException("Invalid email address: " + MemberEmail);
            }
            this.Borrowed = new ArrayList<>();
        }

        // Get methods with return
        public String getMemberName() {
            return MemberName;
        }

        public String getMemberEmail() {
            return MemberEmail;
        }

        public List<Book> getBorrowed() {
            return Borrowed;
        }

        public LocalDate getDueDate() {
            return DueDate;
        }

        public double getFine() {
            return fine;
        }
    }

    // EmailValidation class
    public static class EmailValidation {
        //Email Regex pattern
        private static final String EmailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        private static final Pattern pattern = Pattern.compile(EmailRegex);

        // Method to validate email address
        public static boolean isValidEmail(String MemberEmail) {
            Matcher matcher = pattern.matcher(MemberEmail);
            return matcher.matches();
        }
    }

    // MethodsLibrary class with properties
    static class MethodsLibrary {
        public List<Book> books = new ArrayList<>(); //Book list
        public List<Member> members = new ArrayList<>(); //Members list

        // Predetermined loan period of 14 days
        public int LoanPeriod = 14;
        public final ScheduledExecutorService scheduler;

        public MethodsLibrary() {
            scheduler = Executors.newScheduledThreadPool(2);
        }

        // Books in library
        public void BooksToCheckout() {
            LocalDate today = LocalDate.now();
            LocalDate DueDate = today.plusDays(LoanPeriod);

            //Initialize predefined books to display, add to list
            books.add(new Book("Mexican Gothic", "Silvia Moreno-Garcia", 103485, true, DueDate));
            books.add(new Book("The Song of Achilles", "Madeline Miller", 208756, true, DueDate));
            books.add(new Book("Credence", "Penelope Douglas", 303014, true, DueDate));
            books.add(new Book("The Silent Patient", "Alex Michaelides", 405524, true, DueDate));
            books.add(new Book("Punk 57", "Penelope Douglas", 501149, true, DueDate));
            books.add(new Book("A Little Life", "Hanya Yanagihara", 603640, true, DueDate));
            books.add(new Book("They Both Die at the End", "Adam Silvera", 702183, true, DueDate));
        }

        //Method to display all predefined books AND books users have added in option 2
        public void DisplayAllBooks() {
            System.out.println("\n BOOKS IN LIBRARY: ");
            System.out.println(":::::::::::::::::::");
            //Get books by title and author to display them
            for (Book book : books) {
                System.out.println(book.getBookTitle() + ", by " + book.getBookAuthor() + ", ISBN: " + book.getISBN());

            }
        }

        //Method to add a book
        public void AddBook() {
            try {
                //Users enter the book title, author and ISBN
                Scanner scanner = new Scanner(System.in);
                System.out.print("\nEnter Book Title: ");
                String bookName = scanner.nextLine();
                System.out.print("Enter Book Author: ");
                String bookAuthor = scanner.nextLine();
                System.out.print("Enter Book ISBN: ");

                int bookISBN = scanner.nextInt();
                boolean isAvailable = true; //Set book to available
                LocalDate DueDate = null; //Set due date to null

                //Adds book according to user input
                Book newBook = new Book(bookName, bookAuthor, bookISBN, isAvailable, DueDate);

                //Text file
                String path = "Saved Added Books.txt";

                //Create objects to write to the text file
                FileWriter W = new FileWriter(path, true);
                BufferedWriter BW = new BufferedWriter(W);
                PrintWriter PW = new PrintWriter(BW);

                //Write the book details to the text file
                PW.println("Title: " + newBook.getBookTitle() + ", Author: " + newBook.getBookAuthor() + ", ISBN: " + newBook.getISBN());

                //Close PrintWriter, BufferedWriter, FileWriter
                PW.close();
                BW.close();
                W.close();

                //Adds new book to the list of Saved Added Books
                books.add(newBook);

                // Print a success message
                System.out.println("\nBOOK ADDED");
            } catch(IOException e){
                // Handle file IO exceptions
                System.out.println("\nError writing to file: " + e.getMessage());
            } catch(Exception e){
                // Handle any other exceptions that occur during the process
                System.out.println("An error occurred ");
                e.printStackTrace();
            }
        }

        // Method to add member
        public void AddMember() throws IOException {
            //Users enter their name and email address
            Scanner scanner = new Scanner(System.in);
            System.out.print("\nEnter your Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter your Email Address: ");
            String email = scanner.nextLine();
            List<Book> borrowed = new ArrayList<>();
            double fine = 0.00;

            Member newMember = new Member(name, email, borrowed, fine);

            //Text file
            String path = "Saved Added Members.txt";

            //Create objects to write to the text file
            FileWriter W = new FileWriter(path, true);
            BufferedWriter B = new BufferedWriter(W);
            PrintWriter P = new PrintWriter(B);

            //Write member details to the text file
            P.println("Name: " + newMember.getMemberName() + ", Email: " + newMember.getMemberEmail() + ", Outstanding Fines: R" + newMember.getFine());

            //ClosePrintWriter, BufferedWriter,FileWriter
            P.close();
            B.close();
            W.close();

            //Adds new member to the list of Saved Added Members
            members.add(newMember);

            //If the user's email fits the regex pattern, it can be added
            if (EmailValidation.isValidEmail(email)) {
                Member newMembers = new Member(name, email, borrowed, fine);
                members.add(newMember);
                System.out.println("\nMEMBER ADDED");

                //If user's email doesn't fit the regex pattern, it can't be added
            } else {
                System.out.println("\nINVALID EMAIL, Member not added.");
            }
        }

        //Method to search for books
        public void SearchBook(String bookTitle) {
            boolean found = false;
            for (Book book : books) {
                //When a user searches for a book, it is recognized in lowercase or uppercase
                if (book.getBookTitle().equalsIgnoreCase(bookTitle)) {
                    //If book is found in user's search, print Book Found
                    System.out.println("\nBOOK FOUND");
                    System.out.println("Title: " + book.getBookTitle());
                    System.out.println("Author: " + book.getBookAuthor());
                    System.out.println("ISBN: " + book.getISBN());
                    found = true;
                    break;
                }
            }
            //If book is not found, error message
            if (!found) {
                System.out.println("\nBOOK NOT FOUND");
            }
        }

        //Method to check out books
        public void CheckoutBooks(String bookTitle, String memberName) {
            //Set to null
            Book bookToCheckout = null;
            Member memberToCheckout = null;
            for (Book book : books) {
                //When member has to type the book name, it can be recognized in lowercase or uppercase
                if (book.getBookTitle().equalsIgnoreCase(bookTitle) && book.isAvailable()) {
                    bookToCheckout = book;
                    break;
                }
            }
            for (Member member : members) {
                //When member has to type their name, it can be recognized in lowercase or uppercase
                if (member.getMemberName().equalsIgnoreCase(memberName)) {
                    memberToCheckout = member;
                    break;
                }
            }

            //If book to be checked out AND the member who is checking it out are not null
            if (bookToCheckout != null && memberToCheckout != null) {

                bookToCheckout.setAvailable(false); //Set book to unavailable
                memberToCheckout.getBorrowed().add(bookToCheckout);
                bookToCheckout.setDueDate(LocalDate.now().plusDays(LoanPeriod)); //Set due date for the checked out book 14 days from today
                memberToCheckout.getBorrowed().add(bookToCheckout);

                System.out.println("\n"+bookToCheckout.getBookTitle() + " is CHECKED OUT by " + memberToCheckout.getMemberName());
                System.out.println("DUE DATE: " + bookToCheckout.getDueDate() + ", 14 days from today.");
                System.out.println("\nFor each day a book is overdue, R40 will be charged!");
                System.out.println("____________________________________________________");
            }
            //If book can't be checked out, error message
            else
            {
                System.out.println("\nBook is unavailable OR Book or Member cannot be found");
            }
        }

        //Method for users to check due dates on books
        public void CheckDueDates() {
            boolean DueDateFound = false;
            for (Book book : books) {
                //If book is checked out, print due date 14 days from date checked out
                if (!book.isAvailable) {
                    System.out.println("\n" + book.getBookTitle() + " has a due date for " + book.getDueDate());
                    DueDateFound = true;
                    break;
                }
            }
            //If no due date is found, it means a book has not been checked out
            if (!DueDateFound) {
                System.out.println("\nNo books have been checked out");
            }
        }

        //Method for users to view their fines
        public void ViewFines() {
            LocalDate today = LocalDate.now();

            //R40 for each day a book is overdue
            double DailyOverdueFine = 40.00;
            boolean hasfines = false;

            for (Book book : books) {
                //If a book is checked out and the due date is before today
                if (!book.isAvailable() && book.getDueDate().isBefore(today)) {
                    long daysOverdue = ChronoUnit.DAYS.between(book.getDueDate(), today);
                    //days overdue x R40
                    double fines = daysOverdue * DailyOverdueFine;
                    System.out.println("\nYour Overdue Fines for: " + book.BookTitle + " is R: " + fines);

                    hasfines = true;
                }
            }
            //Book is not overdue
            if (!hasfines) {
                System.out.println("\nNo Outstanding Fines.");
            }
        }

        //Method to enable users to see notifications for overdue books and fines
        public void ManageNotifications() {
            System.out.println("\n1. Manage Overdue Book Notifications");
            System.out.println("2. Manage Fines Notifications");
            Scanner notifyChoice = new Scanner(System.in);
            //User choice between 1 and 2
            System.out.println("\nEnter your choice: ");
            String NotificationInput = notifyChoice.nextLine();

            //If user chooses 1 - Overdue book notifications
            if (Objects.equals(NotificationInput, "1")) {
                for (Book book : books) {
                    if (!book.isAvailable() && book.getDueDate() != null) {
                        //If a book is overdue
                        System.out.println("\nNotification: " + book.getBookTitle() + " Must be handed in before " + book.getDueDate());
                    }
                    //If books are not overdue
                    else if (!book.isAvailable() && book.getDueDate() == null) {
                        System.out.println("\nNotification: No books are due.");
                    }
                }

            }
            //If user chooses 2 - Fines notifications
            else if (Objects.equals(NotificationInput, "2")) {
                for (Member member : members) {
                    //If fines are R0, reminder that there are no overdue fines
                    if (member.getFine() == 0.00) {
                        System.out.println("\nNotification: No overdue fines yet.");
                        System.out.println("Reminder: Return your book before the due date.");
                        break;
                    }
                    //If fines are more than R0, reminder to pay and return book
                    else if (member.getFine() > 0.00) {
                        System.out.println(member.getMemberName() + " You have an outstanding fine of R " + member.getFine());
                        System.out.println("\nPay all outstanding fines and return book!");
                        break;
                    }
                }
            }
            //Notifications every hour
            scheduler.schedule(this::ManageNotifications, 1, TimeUnit.HOURS);
        }

        //Method to calculate members' fines
        public void MemberFines(){
            LocalDate today = LocalDate.now();
            for (Book book : books) {
                for (Member member : members) {
                    double DailyOverdueFine = 40.00;
                    long daysOverdue = ChronoUnit.DAYS.between(book.getDueDate(), today);

                    //days overdue x R40
                    member.fine = daysOverdue * DailyOverdueFine;
                }
            }
        }
    }
}