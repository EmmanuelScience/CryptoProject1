package com.ticketswap.cryptoproject1;

import com.ticketswap.cryptoproject1.config.DigitalSignature;
import com.ticketswap.cryptoproject1.config.OTP;
import com.ticketswap.cryptoproject1.config.RSA;
import com.ticketswap.cryptoproject1.entities.*;
import com.ticketswap.cryptoproject1.repository.*;
import com.ticketswap.cryptoproject1.utils.EmailUtility;
import com.ticketswap.cryptoproject1.utils.InputHelper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private TicketRequestRepository ticketRequestRepository;

    private Users currentUser;
    private boolean isAdministrator = false;

    public static String keyPassword;

    RSA rsa = new RSA();

    @SneakyThrows
    public void mainMenu() {
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
                    uploadTicket(selectEvent());
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

    /**
     * Allows a user to upload a ticket
     * @param event the event the ticket is for
     */
    @SneakyThrows
    public void uploadTicket(Event event) {
        if (currentUser == null) {
            System.out.println("Please login or Register to sell tickets");
            mainMenu();
            return;
        }
        double ticketPrice = InputHelper.getDoubleInput("Enter ticket price: ");
        String ticketCode = InputHelper.getStringInput("Enter ticket code: ");
        boolean verified = verifyTicketCode(ticketCode);
        if (verified) {
            int ticketQuantity = InputHelper.getIntInput("Enter ticket quantity: ");
            requestBankCard();
            Ticket ticket = new Ticket();
            ticket.setEvent(event);
            ticket.setCanBeSold(true);
            ticket.setTicketPrice(ticketPrice);
            ticket.setTicketOwner(currentUser);
            ticket.setQuantity(ticketQuantity);
            ticketRepository.save(ticket);
            System.out.println("Ticket added successfully");
            myTickets();
        } else {
            System.out.println("Ticket code is not valid");
        }
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

    private boolean verifyTicketCode(String ticketCode) {
        return ticketCode.length() > 0;
    }

    @SneakyThrows
    public void buyTicket(Event event) {
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
                if (ticket.isCanBeSold())
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
                    EmailUtility emailUtility = new EmailUtility();
                    //Ticket request
                    String publicKey = currentUser.getPublicKey();
                    byte[] signature = DigitalSignature.generateSignatureForMessage("C:\\chomsky\\Academics\\Fall-2022\\crypto\\CryptoProject1\\src\\A\\top.key", publicKey, keyPassword);
                    byte[] certificate = DigitalSignature.readCertFromFile("C:\\chomsky\\Academics\\Fall-2022\\crypto\\CryptoProject1\\src\\A\\top.crt").getEncoded();
                    TicketRequest ticketRequest = new TicketRequest();
                    ticketRequest.setCertificate(certificate);
                    ticketRequest.setSignature(signature);
                    ticketRequest.setAliasBuyer(currentUser.getUserName());
                    ticketRequest.setAliasSeller(ticket.getTicketOwner().getUserName());
                    ticketRequest.setTicketId(ticketId);
                    ticketRequest.setQuantity(quantity);
                    ticketRequest.setEmailBuyer(currentUser.getEmail());
                    ticketRequest.setEmailSeller(ticket.getTicketOwner().getEmail());
                    ticketRequest.setPublicKey(publicKey);
                    ticketRequestRepository.save(ticketRequest);
                    String  subject =currentUser.getUserName() + " wants to buy your ticket";
                    String  body = "Hi " + ticket.getTicketOwner().getFirstName() + ", " + currentUser.getUserName() +
                            ". Please log in and accept the request";
                    emailUtility.sendMail(subject,ticket.getTicketOwner().getEmail(), body);
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

    @SneakyThrows
    public void sellTicket() {
        if (currentUser == null) {
            System.out.println("Please login or Register to sell tickets");
            mainMenu();
            return;
        }
        List<TicketRequest> ticketRequests = ticketRequestRepository.findByEmailSeller(currentUser.getEmail());
        if (ticketRequests.size() == 0) {
            System.out.println("No ticket requests found");
        } else {
            for (TicketRequest ticketRequest : ticketRequests) {
                Ticket ticket = ticketRepository.findById(ticketRequest.getTicketId()).isPresent() ?
                        ticketRepository.findById(ticketRequest.getTicketId()).get() : null;
                //Verifying public key
                boolean isVerified = DigitalSignature.verifySignature(ticketRequest.getCertificate(), ticketRequest.getSignature(), ticketRequest.getPublicKey().getBytes());
                //verify certificate chain
                X509Certificate rootCert = DigitalSignature.readCertFromFile("C:\\chomsky\\Academics\\Fall-2022\\crypto\\CryptoProject1\\src\\A\\root.crt");
                X509Certificate intermediateCert = DigitalSignature.readCertFromFile("C:\\chomsky\\Academics\\Fall-2022\\crypto\\CryptoProject1\\src\\A\\middle.crt");
                CertificateFactory fac = CertificateFactory.getInstance("X509");
                X509Certificate endCert = (X509Certificate) fac.generateCertificate(new ByteArrayInputStream(ticketRequest.getCertificate()));
                List<X509Certificate> certList = new ArrayList<>();
                certList.add(rootCert);
                certList.add(intermediateCert);
                certList.add(endCert);
                try {
                    DigitalSignature.verifyCertificateChain(certList);
                } catch (Exception e) {
                    System.out.println("Certificate chain is not valid");
                    isVerified = false;
                }
                if (isVerified) {
                    System.out.println("Ticket request from " + ticketRequest.getAliasBuyer());
                    System.out.println("Ticket details: " + ticket);
                    System.out.println("Quantity: " + ticketRequest.getQuantity());
                    System.out.println("Price: " + ticket.getTicketPrice() * ticketRequest.getQuantity());
                    String accept = InputHelper.getStringInput("Accept? (y/n): ");
                    if (accept.equalsIgnoreCase("y")) {
                        String ticketCode = InputHelper.getStringInput("Enter ticket code: ");
                        rsa.setPeerPublicKey(ticketRequest.getPublicKey());
                        //Encrypting ticket code
                        String ticketCodeEncrypted = rsa.encrypt(ticketCode);
                        ticket.setTicketCode(ticketCodeEncrypted);
                        if (simulatePayment(ticket.getTicketPrice() * ticketRequest.getQuantity(), ticket.getTicketOwner())) {
                            ticket.setQuantity(ticket.getQuantity() - ticketRequest.getQuantity());
                            ticket.setTicketOwner(currentUser);
                            ticketRepository.save(ticket);
                            ticketRequestRepository.delete(ticketRequest);
                            System.out.println("Ticket sold successfully");
                        }
                    } else {
                        ticketRequestRepository.delete(ticketRequest);
                    }
                } else {
                    System.out.println("Invalid public key");
                    ticketRequestRepository.delete(ticketRequest);
                }
            }
        }
        goToMenu();
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
        System.out.println("**************************Register User***************************");
        currentUser = new Users();
        /*currentUser.setFirstName(InputHelper.getStringInput("Enter first name: "));
        currentUser.setLastName(InputHelper.getStringInput("Enter last name: "));
         */
        String email = InputHelper.getStringInput("Enter email: ");
        if (userRepository.findByEmail(email).size() > 0 || !InputHelper.validateEmail(email)) {
            System.out.println("Email already exists or invalid email");
            registerUser();
            return;
        }
        currentUser.setEmail(email);
        currentUser.setPassword(InputHelper.getStringInput("Enter password: "));
        keyPassword = currentUser.getPassword() + "111111111111";
        /*currentUser.setAddress(InputHelper.getStringInput("Enter address: "));
        currentUser.setCity(InputHelper.getStringInput("Enter city: "));
        currentUser.setCountry(InputHelper.getStringInput("Enter country: "));
        currentUser.setPostalCode(InputHelper.getStringInput("Enter zip code: "));
        currentUser.setPhoneNumber(InputHelper.getStringInput("Enter phone number: "));
         */
        currentUser.setSalt("salt");
        currentUser.setPassword(hashPassword(currentUser.getPassword() + currentUser.getSalt()));
        setAdministrator(currentUser.getEmail());
        if (!isAdministrator) {
            rsa.generateKeys();
            currentUser.setPublicKey(rsa.getPublicKeyString());
            currentUser.setPrivateKey(rsa.getPrivateKeyString());
        }
        userRepository.save(currentUser);
        Users user = userRepository.findByEmail(currentUser.getEmail()).get(0);
        System.out.println("The private key is: " + user.getPrivateKey());
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

    public  void loginUser() throws GeneralSecurityException, MessagingException {
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
                    checkIfAdmin(user);
                    if (isAdministrator) {
                        currentUser = user;
                        System.out.println("Login successful");
                        adminMenu();
                    } else {
                        System.out.println("Wait for OTP");
                        OTP otp = new OTP();
                        String code = otp.generate(6);
                        EmailUtility emailUtility = new EmailUtility();
                        LocalDateTime now = LocalDateTime.now();
                        emailUtility.sendMail("Login code", email, code);
                        String inputCode = InputHelper.getStringInput("Please enter code sent to your email: ");
                        if (LocalDateTime.now().isAfter(now.plusMinutes(5))) {
                            System.out.println("Code expired");
                            exit = false;
                        } else if (inputCode.equals(code)) {
                            currentUser = user;
                            System.out.println("Login successful");
                            rsa.setPrivateKeyString(currentUser.getPrivateKey());
                            rsa.setPublicKeyString(currentUser.getPublicKey());
                            userMenu();
                        } else {
                            System.out.println("Invalid code");
                            exit = false;
                        }
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
            isAdministrator = true;
        } else {
            currentUser.setUserType(UserType.CUSTOMER);
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
                        " 2. Upload Ticket \n" +
                        " 3. Buy Ticket \n" +
                        " 4. Logout User \n" +
                        " 5. Send Requested Ticket \n" +
                        " 6. My Tickets" +
                        " 7. Main Menu \n");
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
                    uploadTicket(selectEvent());
                    break;
                case 3:
                    displayEvents(searchEvent(false));
                    buyTicket(selectEvent());
                    break;
                case 4:
                    logOut();
                    break;
                case 5:
                    sellTicket();
                    break;
                case 6:
                    myTickets();
                    goToMenu();
                    break;
                case 7:
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
                        " 4. Remove Event \n" +
                        " 5. Main Menu \n");
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
                case 5:
                    goToMenu();
                    break;
                default:
                    System.out.println("Invalid choice");
                    exit = false;
                    break;
            }
        }

    }
}



