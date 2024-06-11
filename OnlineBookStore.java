import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OnlineBookStore {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BookStore store = new BookStore();
        for (int i = 0; i < 100; i++) {
            String line = scanner.nextLine();
            if (line.equals("end")) break;
            String[] command = line.split(" ");
            if (command[0].equals("createBook")) {
                store.createBook(command[1], command[2], command[3]);
            } else if (command[0].equals("createUser")) {
                store.createUser(command[1], command[2]);
            } else if (command[0].equals("subscribe")) {
                store.subscribe(command[1]);
            } else if (command[0].equals("unsubscribe")) {
                store.unsubscribe(command[1]);
            } else if (command[0].equals("updatePrice")) {
                store.updatePrice(command[1], command[2]);
            } else if (command[0].equals("readBook")) {
                store.readBook(command[1], command[2]);
            } else if (command[0].equals("listenBook")) {
                store.listenBook(command[1], command[2]);
            }
        }
    }
}

//The class represents an online book store
class BookStore {
    UserSystem userSystem;
    LibrarySystem librarySystem;

    public BookStore() {
        this.userSystem = new UserSystem();
        this.librarySystem = new LibrarySystem();
    }

    //creates a new book
    public void createBook(String title, String author, String price) {
        librarySystem.createBook(title, author, price);
    }

    //creates a new user
    public void createUser(String type, String name) {
        userSystem.createUser(type, name);
    }

    //subscribes user
    public void subscribe(String userName) {
        librarySystem.subscribe(userSystem.getUser(userName));
    }

    //Unsubscribes user
    public void unsubscribe(String userName) {
        librarySystem.unsubscribe(userSystem.getUser(userName));
    }


    //Updates the price of the book
    public void updatePrice(String title, String new_price) {
        librarySystem.updatePrice(title, new_price);
    }

    //user reads the book
    public void readBook(String userName, String title) {
        userSystem.readBook(userName, title, librarySystem.getLibrary());
    }

    //user listens the book
    public void listenBook(String userName, String title) {
        userSystem.listenBook(userName, title, librarySystem.getLibrary());
    }

}

class UserSystem {
    private Map<String, User> users;
    private UserCreator userCreator;

    public UserSystem() {
        this.users = new HashMap<>();
        this.userCreator = new UserCreator();
    }

    //creates a new user if it does not exist
    public void createUser(String type, String name) {
        if (this.users.containsKey(name)) {
            System.out.println("User already exists");
            return;
        }
        this.users.put(name, userCreator.create(type, name));
    }

    //user reads the book
    public void readBook(String userName, String title, Map<String, Book> library) {
        this.users.get(userName).readBook(library.get(title));
    }

    //user listens the book
    public void listenBook(String userName, String title, Map<String, Book> library) {
        this.users.get(userName).listenBook(library.get(title));
    }

    //returns the user with the given name
    public User getUser(String name) {
        return users.get(name);
    }
}

interface Publisher {
    void subscribe(Subscriber subscriber);
    void unsubscribe(Subscriber subscriber);
    void notifySubscribers(String message);
}


class LibrarySystem implements Publisher {
    private Map<String, Book> books;
    private ArrayList<Subscriber> subscribers;

    public LibrarySystem() {
        this.books = new HashMap<>();
        this.subscribers = new ArrayList<>();
    }

    //creates a new book if it does not exist
    public void createBook(String title, String author, String price) {
        if (this.books.containsKey(title)) {
            System.out.println("Book already exists");
            return;
        }
        this.books.put(title, new Book(title, author, price));
    }

    //subscribes user if it is unsubscribed
    @Override
    public void subscribe(Subscriber subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        } else {
            System.out.println("User already subscribed");
        }
    }

    //unsubscribes user if it is subscribed
    @Override
    public void unsubscribe(Subscriber subscriber) {
        if (subscribers.contains(subscriber)) {
            subscribers.remove(subscriber);
        } else {
            System.out.println("User is not subscribed");
        }
    }

    //sets a new price for the book and notifies subscribers
    public void updatePrice(String title, String newPrice) {
        books.get(title).updatePrice(newPrice);
        String message = "price update for " + title + " to " + newPrice;
        this.notifySubscribers(message);
    }    

    //sends a message to subscribers
    @Override
    public void notifySubscribers(String message) {
        for (Subscriber subscriber: subscribers) {
            subscriber.update(message);
        }
    }

    //returns library
    public Map<String, Book> getLibrary() {
        return this.books;
    }
}

interface Subscriber {
    public void update(String message);
}

abstract class User implements Subscriber {
    public User(String name) {
        this.name = name;
    }

    //the user reads a book
    public void readBook(Book book) {
        System.out.println(name + " reading " + book.getTitle() + " by " + book.getAuthor());
    }

    //the user get a message
    public void update(String message) {
        System.out.println(name + " notified about " + message);
    }

    //returns the users name
    public String getUserName() {
        return name;
    }

    public abstract void listenBook(Book book);

    private String name;
}

class StandardUser extends User {
    public StandardUser(String name) {
        super(name);
    }

    //prints error access message
    @Override
    public void listenBook(Book book) {
        System.out.println("No access");
    }
}

class PremiumUser extends User {
    public PremiumUser(String name) {
        super(name);
    }

    //the user listens a book
    @Override
    public void listenBook(Book book) {
        System.out.println(getUserName() + " listening " + book.getTitle() + " by " + book.getAuthor());
    }
}

interface UserFactory {
    User createUser(String name);
}

class StandardUserFactory implements UserFactory {
    //creates a new standard user
    @Override
    public StandardUser createUser(String name) {
        return new StandardUser(name);
    }
}

class PremiumUserFactory implements UserFactory {
    //creates a new premium user
    @Override
    public PremiumUser createUser(String name) {
        return new PremiumUser(name);
    }
}

class UserCreator {
    public UserFactory factory;
    //creates a new user based on type
    public User create(String type, String name) {
        if (type.equals("standard")) {
            factory = new StandardUserFactory();
        } else if (type.equals("premium")) {
            factory = new PremiumUserFactory();
        } else {
            return null;
        }
        return factory.createUser(name);
    }
}

class Book {
    private String title;
    private String author;
    private String price;

    public Book(String title, String author, String price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    //updates the price
    public void updatePrice(String newPrice) {
        this.price = newPrice;
    }

    //returns the title
    public String getTitle() {
        return this.title;
    }

    //returns the author
    public String getAuthor() {
        return this.author;
    }
}