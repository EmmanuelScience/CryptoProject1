package com.ticketswap.cryptoproject1;

import com.ticketswap.cryptoproject1.entities.*;
import com.ticketswap.cryptoproject1.repository.*;
import com.ticketswap.cryptoproject1.utils.InputHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;

import static com.ticketswap.cryptoproject1.utils.HashFunction.hashPassword;

@Service
@Configurable
public class AppSession {
    @Autowired
    private BankCardRepository bankCardRepository;
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  TicketRepository ticketRepository;

    @Autowired
    private  EventRepository eventRepository;

    private Users currentUser;
    private boolean isAdministrator = false;

    public static String keyPassword;

    public void mainMenu() throws GeneralSecurityException {
        System.out.println(
                " 1. Search Events \n" +
                " 2. Register User \n" +
                " 3. Login User");
        boolean exit = false;
        while (!exit) {
            exit = true;
            int choice = InputHelper.getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    searchEvent(true);
                    break;
                case 2:
                    registerUser();
                    break;
                case 3:
                    loginUser();
                    break;
                default:
                    System.out.println("Invalid choice");
                    exit = false;
                    break;
            }
        }
    }


    public void searchMenu(List<Event> events) throws GeneralSecurityException {
        displayEvents(events);
        System.out.println(
                        "1 - Buy Ticket \n" +
                        "2 - Sell Ticket \n" +
                        "3 - Back to Main Menu");
        boolean exit = false;
        while (!exit) {
            exit = true;
            int choice = InputHelper.getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    buyTicket(selectEvent());
                    break;
                case 2:
                    sellTicket(selectEvent());
                    break;
                case 3:
                    goToMenu();
                    break;
                default:
                    System.out.println("Invalid choice");
                    exit = false;
                    break;
            }
        }
    }

    public void goToMenu() throws GeneralSecurityException  {
        if (currentUser != null) {
            if (isAdministrator) {
                adminMenu();
            } else {
                userMenu();
            }
        } else {
            mainMenu();
        }
    }

    public List<Event> searchEvent(boolean fromMain) throws GeneralSecurityException {
        System.out.println("**************************Search Events***************************");
        String eventName = InputHelper.getStringInput("Enter event name: ");
        List<Event> events = eventRepository.findByName(eventName);
        if (events.size() == 0) {
            System.out.println("Searched event not found");
            events = eventRepository.findAll();
        }
        if (fromMain) {
            if (!isAdministrator) searchMenu(events);
            else {
                displayEvents(events);
                adminMenu();
            }
        }
        return events;
    }

    public void sellTicket(Event event) throws GeneralSecurityException {
        if (currentUser == null) {
            System.out.println("Please login or Register to sell tickets");
            mainMenu();
            return;
        }
        double ticketPrice = InputHelper.getDoubleInput("Enter ticket price: ");
        String ticketCode = InputHelper.getStringInput("Enter ticket code: ");
        int ticketQuantity = InputHelper.getIntInput("Enter ticket quantity: ");
        requestBankCard();
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setTicketPrice(ticketPrice);
        ticket.setTicketCode(ticketCode);
        ticket.setTicketOwner(currentUser);
        ticket.setQuantity(ticketQuantity);
        ticketRepository.save(ticket);
        System.out.println("Ticket added successfully");
        myTickets();
        goToMenu();
    }

    private void requestBankCard() {
        if(bankCardRepository.findByUser(currentUser.getUserId()).size() == 0){
            System.out.println("***********************Please enter your bank card details***********************");
            addBankCard();
        }else {
            System.out.println("Do you want to add a new bank card? (y/n)");
            String choice = InputHelper.getStringInput("Enter your choice: ");
            if (choice.equals("y")) {
                addBankCard();
            }
        }
    }


    public void buyTicket(Event event) throws GeneralSecurityException {
        if (currentUser == null) {
            System.out.println("Please login or Register to buy tickets");
            mainMenu();
            return;
        }
        List<Ticket> tickets = ticketRepository.findByEvent(event.getEventId());
        if (tickets.size() == 0) {
            System.out.println("No tickets found");
        } else {
            for (Ticket ticket : tickets) {
                System.out.println(
                        " ticket id: " + ticket.getTicketId() +
                        " ticket price: " + ticket.getTicketPrice() +
                        " ticket code: " + ticket.getTicketCode() +
                        " ticket quantity: " + ticket.getQuantity());
            }
            int ticketId = InputHelper.getIntInput("Enter ticket id: ");
            Ticket ticket = ticketRepository.findById(ticketId).isPresent() ? ticketRepository.findById(ticketId).get() : null;
            if (ticket == null) {
                System.out.println("Invalid ticket id");
                buyTicket(event);
                return;
            }
            if (ticket.getTicketOwner().getUserId() == currentUser.getUserId()) {
                System.out.println("You cannot buy your own ticket");
            } else {
                int quantity = InputHelper.getIntInput("Enter quantity: ");
                if (quantity > ticket.getQuantity()) {
                    System.out.println("Not enough tickets");
                } else {
                    requestBankCard();
                    if (simulatePayment(quantity * ticket.getTicketPrice(), ticket.getTicketOwner())) {
                        ticket.setQuantity(ticket.getQuantity() - quantity);
                        if(ticket.getQuantity() == 0) {
                            ticketRepository.delete(ticket);
                        } else {
                            ticketRepository.save(ticket);
                        }
                        Ticket newTicket = new Ticket();
                        newTicket.setEvent(event);
                        newTicket.setTicketPrice(ticket.getTicketPrice());
                        newTicket.setTicketCode(ticket.getTicketCode());
                        newTicket.setTicketOwner(currentUser);
                        newTicket.setQuantity(quantity);
                        ticketRepository.save(newTicket);
                        myTickets();
                    }
                }
            }
        }
        goToMenu();
    }

    public boolean simulatePayment(double amount, Users user) {
        System.out.println("**************************Simulate Payment***************************");
        List<BankCard> bankCards = bankCardRepository.findByUser(currentUser.getUserId());
        BankCard bankCardSeller = bankCardRepository.findByUser(user.getUserId()).get(0);
        if (bankCards.size() == 0) {
            System.out.println("No bank cards found");
            return false;
        } else {
            for (BankCard bankCard : bankCards) {
                System.out.println(bankCard);
            }
            int bankCardId = InputHelper.getIntInput("Enter bank card id: ");
            BankCard bankCard = bankCardRepository.findById(bankCardId).isPresent() ? bankCardRepository.findById(bankCardId).get() : null;
            assert bankCard != null;
            if (amount > bankCard.getBalance()) {
                System.out.println("Not enough balance");
                return false;
            } else {
                bankCard.setBalance(bankCard.getBalance() - amount);
                bankCardSeller.setBalance(bankCardSeller.getBalance() + amount);
                bankCardRepository.save(bankCardSeller);
                bankCardRepository.save(bankCard);
                System.out.println("Payment successful");
                return true;
            }
        }
    }

    private void myTickets() {
        List<Ticket> tickets = ticketRepository.findByTicketOwner(currentUser.getUserId());
        if (tickets.size() == 0) {
            System.out.println("No tickets found");
        } else {
            for (Ticket ticket : tickets) {
                System.out.println(
                        "ticket id: " + ticket.getTicketId() +
                        " ticket price: " + ticket.getTicketPrice() +
                        " ticket code: " + ticket.getTicketCode() +
                        " ticket quantity: " + ticket.getQuantity());
            }
        }
    }

    private void displayEvents(List<Event> events) {
        for (Event event : events) {
            System.out.println(event);
        }
    }

    private Event selectEvent() {
        while (true) {
            int eventId = InputHelper.getIntInput("Enter event id: ");
            Event event = eventRepository.findById(eventId).isPresent() ? eventRepository.findById(eventId).get() : null;
            if (event == null) {
                System.out.println("Invalid event id");
            } else {
                return event;
            }
        }
    }

    public void addEvent() throws GeneralSecurityException {
        System.out.println("**************************Add Event***************************");
        String eventName = InputHelper.getStringInput("Enter event name: ");
        String eventLocation = InputHelper.getStringInput("Enter event location: ");
        LocalDate eventDate = InputHelper.getLocalDateInput("Enter event date: ");
        String eventVenue = InputHelper.getStringInput("Enter event venue: ");

        Event event = new Event();
        event.setEventName(eventName);
        event.setCity(eventLocation);
        event.setVenue(eventVenue);
        event.setDate(eventDate);
        eventRepository.save(event);
        System.out.println("Event added successfully");
        goToMenu();
    }

    public void removeEvent() throws GeneralSecurityException {
        System.out.println("**************************Remove Event***************************");
        String eventName = InputHelper.getStringInput("Enter event name: ");
        List<Event> events = eventRepository.findByName(eventName);
        if (events.size() == 0) {
            System.out.println("No events found");
        } else {
            for (Event event : events) {
                System.out.println(event);
            }
            int eventId = InputHelper.getIntInput("Enter event id: ");
            if (ticketRepository.findByEvent(eventId).size() > 0) {
                System.out.println("Cannot delete event with tickets");
            } else {
                eventRepository.deleteById(eventId);
                System.out.println("Event deleted successfully");
            }
        }
        goToMenu();
    }


    public void registerUser() throws GeneralSecurityException {
        currentUser = new Users();
        currentUser.setFirstName(InputHelper.getStringInput("Enter first name: "));
        currentUser.setLastName(InputHelper.getStringInput("Enter last name: "));
        String email = InputHelper.getStringInput("Enter email: ");
        if (userRepository.findByEmail(email).size() > 0 || !InputHelper.validateEmail(email)) {
            System.out.println("Email already exists or invalid email");
            registerUser();
            return;
        }
        currentUser.setEmail(email);
        currentUser.setPassword(InputHelper.getStringInput("Enter password: "));
        currentUser.setAddress(InputHelper.getStringInput("Enter address: "));
        currentUser.setCity(InputHelper.getStringInput("Enter city: "));
        currentUser.setCountry(InputHelper.getStringInput("Enter country: "));
        currentUser.setPostalCode(InputHelper.getStringInput("Enter zip code: "));
        currentUser.setPhoneNumber(InputHelper.getStringInput("Enter phone number: "));
        currentUser.setSalt("salt");
        currentUser.setPassword(hashPassword(currentUser.getPassword() + currentUser.getSalt()));
        setAdministrator(currentUser.getEmail());
        System.out.println("User registered successfully");
        userMenu();
    }

    public void addBankCard() {
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber(InputHelper.getIntInput("Enter card number: "));
        bankCard.setCardHolderName(InputHelper.getStringInput("Enter card holder name: "));
        bankCard.setExpiryDate(LocalDate.parse(InputHelper.getStringInput("Enter expiry date: ")));
        bankCard.setCvv(InputHelper.getIntInput("Enter cvv: "));
        bankCard.setBalance(10000000);
        bankCard.setUser(currentUser);
        bankCardRepository.save(bankCard);
    }

    public  void loginUser() throws GeneralSecurityException {
        boolean exit = false;
        while (!exit) {
            exit = true;
            String email = InputHelper.getStringInput("Enter email: ");
            String password = InputHelper.getStringInput("Enter password: ");
            List<Users> users = userRepository.findByEmail(email);
            if (users.size() > 0) {
                Users user = users.get(0);
                String hashedPassword = hashPassword(password + user.getSalt());
                if (user.getPassword().equals(hashedPassword)) {
                    System.out.println("Login successful");
                    currentUser = user;
                    checkIfAdmin(user);
                    if (isAdministrator) {
                        adminMenu();
                    } else {
                        userMenu();
                    }
                } else {
                    System.out.println("Invalid password");
                    exit = false;
                }
            } else {
                System.out.println("Invalid email");
                exit = false;
            }
        }

    }

    public void checkIfAdmin(Users user) {
        isAdministrator = user.getUserType() == UserType.ADMIN;
    }

    public void setAdministrator(String email) {
        if (email.endsWith("@ticketswap.com")) {
            currentUser.setUserType(UserType.ADMIN);
            userRepository.save(currentUser);
        } else {
            currentUser.setUserType(UserType.CUSTOMER);
            userRepository.save(currentUser);
        }
    }

    public void logOut() throws GeneralSecurityException {
        currentUser = null;
        isAdministrator = false;
        System.out.println("Logged out successfully");
        keyPassword = null;
        goToMenu();
    }

    public void userMenu() throws GeneralSecurityException {
        System.out.println(
                        " 1. Search Events \n" +
                        " 2. Sell Ticket \n" +
                        " 3. Buy Ticket \n" +
                        " 4. Logout User \n" +
                        " 5. My Tickets" );
        boolean exit = false;
        while (!exit) {
            exit = true;
            int choice = InputHelper.getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    searchEvent(true);
                    break;
                case 2:
                    displayEvents(searchEvent(false));
                    sellTicket(selectEvent());
                    break;
                case 3:
                    displayEvents(searchEvent(false));
                    buyTicket(selectEvent());
                    break;
                case 4:
                    logOut();
                    break;
                case 5:
                    myTickets();
                    goToMenu();
                    break;
                default:
                    System.out.println("Invalid choice");
                    exit = false;
                    break;
            }
        }

    }

    public void adminMenu() throws GeneralSecurityException {
        System.out.println(
                        " 1. Search Events \n" +
                        " 2. Logout User \n" +
                        " 3. Add Event \n" +
                        " 4. Remove Event \n");
        boolean exit = false;
        while (!exit) {
            exit = true;
            int choice = InputHelper.getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    searchEvent(true);
                    break;
                case 2:
                    logOut();
                    break;
                case 3:
                    addEvent();
                    break;
                case 4:
                    removeEvent();
                    break;
                default:
                    System.out.println("Invalid choice");
                    exit = false;
                    break;
            }
        }

    }
}



