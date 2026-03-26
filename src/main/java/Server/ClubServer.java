package Server;

import dataaccess.ActivityDAO;
import dataaccess.DBManager;
import dataaccess.InscriptionDAO;
import dataaccess.MemberDAO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static utilities.FormatValidationUtils.isValidEmail;

/**
 * ClubServer class sets up the server to handle client connections and process commands.
 */
public class ClubServer {
    public static void main(String[] args) {
        // Initialize database and DAOs
        DBManager.setDBMode(DBManager.DBMode.PROD);
        DBManager.initializeDatabase();
        MemberDAO mDAO = new MemberDAO();
        ActivityDAO aDAO = new ActivityDAO();
        InscriptionDAO iDAO = new InscriptionDAO();

        // Initialize Club
        Club clubService = new Club(mDAO, aDAO, iDAO);

        // Start server socket
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            while (true) {
                Socket socket = serverSocket.accept();

                new ClientHandler(socket, clubService).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}


/**
 * ClientHandler class handles communication with a connected client.
 */
class ClientHandler extends Thread {
    private final Socket socket;
    private final Club club;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Current logged-in member
    private ClubMember currentMember = null;
    private boolean loggedIn = false;


    /**
     * Constructor for ClientHandler
     * @param socket
     * @param club
     */
    public ClientHandler(Socket socket, Club club) {
        this.socket = socket;
        this.club = club;
    }

    /**
     * Main run method for handling client commands.
     */
    @Override
    public void run() {
        try {
            setupStreams();
            out.writeObject("Welcome to the Club Server!");
            Object obj;
            while ((obj = in.readObject()) != null) {
                processCommand((String) obj);
            }
        } catch (Exception e) {
            System.out.println("Conection closed with: " + socket.getInetAddress());
        } finally {
            closeResources();
        }
    }

    /**
     * Processes a command received from the client.
     * @param line The command line received.
     * @throws IOException if an I/O error occurs.
     */
    private void processCommand(String line) throws IOException {
        // Split command and parameters by '|'
        String[] parts = line.split("\\|");
        String cmd = parts[0].toUpperCase();

        // Handle commands
        switch (cmd) {
            case "LOGIN" -> handleLogin(parts);
            case "ADD_MEMBER" -> handleAddMember(parts);
            case "DELETE_PARTNER" -> handleDeletePartner(parts);
            case "MODIFY_MEMBER" -> handleModifyMember(parts);
            case "GET_MEMBERS" -> sendObject(club.getAllMembers());
            case "GET_MEMBER_BY_EMAIL" -> handleGetByEmail(parts);
            case "GET_PARTNERS" -> sendObject(club.getAllPartners());
            case "GET_ACTIVITIES" -> sendObject(club.getAllActivities());
            case "JOIN_ACTIVITY" -> handleJoin(parts);
            case "UNROLL_ME" -> handleUnrollMe(parts);
            case "UNROLL_PARTICIPANT" -> handleUnrollParticipant(parts);
            case "IS_ENROLLED_IN_ACTIVITY" -> handleIsEnrolled(parts);
            case "ADD_ACTIVITY" -> handleAddActivity(parts);
            case "MODIFY_ACTIVITY" -> handleModifyActivity(parts);
            case "DELETE_ACTIVITY" -> handleDeleteActivity(parts);

            case "GET_PARTICIPANTS" -> handleGetParticipants(parts);
            case "CHANGE_PASSWORD" -> handleChangePassword(parts);
            case "EXIT_MAIN" -> closeConnection();
            default -> sendResponse("UNKNOWN_COMMAND");
        }
    }

    /**
     * Handles the GET_MEMBER_BY_EMAIL command.
     * @param parts
     * @throws IOException
     */
    private void handleGetByEmail(String[] parts) throws IOException {
        ClubMember member = club.getMemberByEmail(parts[1]);
        sendObject(member);
    }

    /**
     * Handles the IS_ENROLLED_IN_ACTIVITY command.
     * @param parts
     * @throws IOException
     */
    private void handleIsEnrolled(String[] parts) throws IOException {
        if (!loggedIn) { sendResponse("LOGIN_REQUIRED");
            return;
        }
        int activityId = Integer.parseInt(parts[1]);
        boolean enrolled = club.isEnrolled(activityId, currentMember.getEmail());
        sendObject(enrolled);
    }

    /**
     * Handles the UNROLL_ME command.
     * @param parts
     * @throws IOException
     */
    private void handleUnrollMe(String[] parts) throws IOException {
        if (!loggedIn) { sendResponse("LOGIN_REQUIRED");
            return;
        }
        int activityId = Integer.parseInt(parts[1]);
        boolean success = currentMember.unrollFromActivity(activityId, club);
        if (success) {
            sendResponse("UNROLLED_FROM_ACTIVITY");
            logAction("USER: " + currentMember.getFullName() + " WAS UNROLLED FROM ACTIVITY: " + activityId);
        } else {
            sendResponse("UNROLL_ACTIVITY_FAILED");
            logAction("FAILED TO UNROLL USER: " + currentMember.getFullName() + " FROM ACTIVITY: " + activityId);
        }
    }

    /**
     * Handles the UNROLL_PARTICIPANT command.
     * @param parts
     * @throws IOException
     */
    private void handleUnrollParticipant(String[] parts) throws IOException {
        if (!isAdmin()) { sendResponse("ADMIN_ACCESS_DENIED"); return; }

        int activityId = Integer.parseInt(parts[1]);
        String memberEmail = parts[2];
        boolean success = club.unroll(activityId, memberEmail);
        if (success) {
            sendResponse("PARTICIPANT_UNROLLED");
            logAction("UNROLLED PARTICIPANT: " + currentMember.getFullName() + " from activity ID " + activityId);
        } else {
            sendResponse("UNROLL_PARTICIPANT_FAILED");
            logAction("FAILED TO UNROLL PARTICIPANT: " + currentMember.getFullName() + " from activity ID " + activityId);
        }
    }

    /**
     * Handles the LOGIN command.
     * @param parts
     * @throws IOException
     */
    private void handleLogin(String[] parts) throws IOException {
        if (parts.length != 3) {
            sendResponse("Usage: LOGIN email password");
            return;
        }
        LoginResult result = club.authenticate(parts[1], parts[2]);

        if (result.getStatus() == LoginResult.Status.SUCCESS) {
            completeLogin(result.getMember());
            logAction("LOGIN SUCCESSFUL");
        } else if( result.getStatus() == LoginResult.Status.FAIL_CREDENTIALS) {
            sendResponse("FAIL_CREDENTIALS");
            logAction("LOGIN FAILED for " + parts[1] + " (invalid credentials)");
        } else {
            sendResponse("LOGIN_ERROR");
            logAction("LOGIN ERROR for " + parts[1]);
        }
    }


    /**
     * Handles the MODIFY_MEMBER command.
     * @param parts
     * @throws IOException
     */
    private void handleModifyMember(String[] parts) throws IOException {
        if (!isAdmin()) { sendResponse("ADMIN_ACCESS_DENIED"); return; }
        Admin admin = (Admin) this.currentMember;
        boolean success = admin.modifyMember(parts[1], parts[2], parts[3], parts[4], club);

        if (success) {
            sendResponse("MEMBER_MODIFIED");
        } else {
            sendResponse("MODIFY_MEMBER_FAILED");
        }
    }

    /**
     * Handles the ADD_ACTIVITY command.
     * @param parts
     * @throws IOException
     */
    private void handleAddActivity(String[] parts) throws IOException {
        if (!isAdmin()) { sendResponse("ADMIN_ACCESS_DENIED"); return; }
        Admin admin = (Admin) this.currentMember;
        Date date = parseDate(parts[2], out);
        if (date != null && admin.addActivity(parts[1], date, parts[3], club)) {
            sendResponse("ACTIVITY_ADDED");
            logAction("ACTIVITY CREATED: " + parts[1]);
        } else {
            sendResponse("ADD_ACTIVITY_FAILED");
        }
    }

    /**
     * Handles the MODIFY_ACTIVITY command.
     * @param parts
     * @throws IOException
     */
    private void handleModifyActivity(String[] parts) throws IOException {
        if (!isAdmin()) { sendResponse("ADMIN_ACCESS_DENIED"); return; }
        Admin admin = (Admin) this.currentMember;
        int activityId = Integer.parseInt(parts[1]);
        Date date = parseDate(parts[3], out);
        if (date != null) {
            boolean success = admin.modifyActivity(activityId, parts[2], date, parts[4], club);
            if (success) {
                sendResponse("ACTIVITY_MODIFIED");
                logAction("ACTIVITY MODIFIED: " + activityId);
            } else {
                sendResponse("MODIFY_ACTIVITY_FAILED");
            }
        }
    }

    /**
     * Handles the DELETE_ACTIVITY command.
     * @param parts
     * @throws IOException
     */
    private void handleDeleteActivity(String[] parts) throws IOException {
        if (!isAdmin()) { sendResponse("ADMIN_ACCESS_DENIED"); return; }
        Admin admin = (Admin) this.currentMember;
        int activityId = Integer.parseInt(parts[1]);
        boolean success = admin.deleteActivity(activityId, club);
        if (success) {
            sendResponse("ACTIVITY_DELETED");
            logAction("ACTIVITY DELETED: " + activityId);
        } else {
            sendResponse("DELETE_ACTIVITY_FAILED");
        }
    }

    /**
     * Handles the ADD_MEMBER command.
     * @param parts
     * @throws IOException
     */
    private void handleAddMember(String[] parts) throws IOException {
        if(!isAdmin()) { sendResponse("ADMIN_ACCESS_DENIED"); return; }
        Admin admin = (Admin) this.currentMember;
        if(!isValidEmail(parts[3])) {
            sendResponse("INVALID_EMAIL_FORMAT");
            return;
        }

        boolean success = admin.addMember(parts[1], parts[2], parts[3], parts[4], parts[5], club);
        if (success) {
            sendResponse("MEMBER_ADDED");
            logAction("MEMBER ADDED: " + parts[3]);
        } else {
            sendResponse("ADD_MEMBER_FAILED");
        }
    }

    /**
     * Handles the DELETE_PARTNER command.
     * @param parts
     * @throws IOException
     */
    private void handleDeletePartner(String[] parts) throws IOException {
        if (!isAdmin()) { sendResponse("ADMIN_ACCESS_DENIED"); return; }
        Admin admin = (Admin) this.currentMember;
        boolean success = admin.deleteMember(parts[1], club);
        if (success) {
            sendResponse("PARTNER_DELETED");
            logAction("MEMBER DELETED: " + parts[1]);
        } else {
            sendResponse("DELETE_PARTNER_FAILED");
        }
    }

    /**
     * Handles the JOIN_ACTIVITY command.
     * @param parts
     * @throws IOException
     */
    private void handleJoin(String[] parts) throws IOException {
        if (!loggedIn) { sendResponse("LOGIN_REQUIRED"); return; }

        int activityId = Integer.parseInt(parts[1]);
        boolean success = currentMember.enrollInActivity(activityId, club);
        if (success) {
            sendResponse("JOINED_ACTIVITY");
            logAction("JOINED ACTIVITY ID: " + activityId);
        } else {
            sendResponse("JOIN_ACTIVITY_FAILED");
        }
    }

    /**
     * Handles the GET_PARTICIPANTS command.
     * @param parts
     * @throws IOException
     */
    private void handleGetParticipants(String[] parts) throws IOException {
        int activityId = Integer.parseInt(parts[1]);
        List<ClubMember> participants = club.getParticipants(activityId);
        sendObject(participants);
    }

    /**
     * Handles the CHANGE_PASSWORD command.
     * @param parts
     * @throws IOException
     */
    private void handleChangePassword(String[] parts) throws IOException {
        if (!loggedIn) { sendResponse("LOGIN_REQUIRED"); return; }

        String newPassword = parts[1];
        boolean success = club.modifyMember(
                currentMember.getName(),
                currentMember.getSurname(),
                newPassword,
                currentMember.getEmail()
        );
        if (success) {
            sendResponse("PASSWORD_CHANGED");
            logAction("PASSWORD CHANGED for member" + currentMember.getFullName());
        } else {
            sendResponse("CHANGE_PASSWORD_FAILED");
        }
    }

    /**
     * Completes the login process for a member.
     * @param member The ClubMember who has logged in.
     * @throws IOException if an I/O error occurs.
     */
    private void completeLogin(ClubMember member) throws IOException {
        this.currentMember = member;
        this.loggedIn = true;
        sendObject(member);
    }

    /**
     * Checks if the current member is an admin.
     * @return true if the current member is an admin, false otherwise.
     */
    private boolean isAdmin() {
        return loggedIn && currentMember instanceof Admin;
    }

    /**
     * Sends a response message to the client.
     * @param msg The message to send.
     * @throws IOException if an I/O error occurs.
     */
    private void sendResponse(String msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    /**
     * Sends an object to the client.
     * @param obj The object to send.
     * @throws IOException if an I/O error occurs.
     */
    private void sendObject(Object obj) throws IOException {
        out.reset();
        out.writeObject(obj);
        out.flush();
    }

    /**
     * Sets up the input and output streams for communication.
     * @throws IOException
     */
    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Parses a date string in the format "dd/MM/yyyy".
     * @param dateStr The date string to parse.
     * @param out The output stream to send error messages if needed.
     * @return A Date object if parsing is successful, null otherwise.
     * @throws IOException if an I/O error occurs.
     */
    private Date parseDate(String dateStr, ObjectOutputStream out) throws IOException {
        try {
            String[] dateParts = dateStr.split("/");
            if (dateParts.length != 3) throw new IllegalArgumentException();

            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            return new Date(day, month, year);
        } catch (Exception e) {
            sendResponse("INVALID_DATE_FORMAT");
            return null;
        }
    }

    /**
     * Logs an action to the server log file with a timestamp and user information.
     * @param action The action to log.
     */
    private void logAction(String action) {
        String logFileName = "server_log.txt";
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String user = (currentMember != null) ? currentMember.getEmail() : "Anonymous";
        String logEntry = String.format("%s User: %s Action: %s%n", timestamp, user, action);

        try (java.io.FileWriter fw = new java.io.FileWriter(logFileName, true);
             java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
            pw.print(logEntry);
        } catch (java.io.IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }

    /**
     * Closes the connection with the client.
     * @throws IOException
     */
    private void closeConnection() throws IOException {
        sendResponse("EXITING");
        socket.close();
    }

    /**
     * Closes the input and output streams and the socket.
     */
    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}