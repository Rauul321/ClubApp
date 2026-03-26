package com.example.clubapp.communication;

import Server.Activity;
import Server.ClubMember;
import Server.Partner;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * ClubServiceClient manages the connection and communication with the club server.
 */
public class ClubServiceClient {


    private final String host; // Server host
    private final int port; // Server port
    private Socket socket; // Socket for communication
    private ObjectInputStream in; // Input stream for receiving data
    private ObjectOutputStream out; // Output stream for sending data

    /**
     * Constructor to initialize communication service
     * @param host
     * @param port
     */
    public ClubServiceClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Establishes connection to the server
     * @return
     */
    public boolean connect() {
        try {
            // Verify if already connected
            if (socket != null && !socket.isClosed() && socket.isConnected()) {
                return true;
            }

            // socket and streams
            this.socket = new Socket(host, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());

            Object welcome = in.readObject();
            System.out.println("Server Welcome: " + welcome);

            return true;
        } catch (Exception e) {
            System.err.println("Cannot connect to the server: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the client is connected to the server
     * @return
     */
    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    /**
     * Sends a command to the server and waits for a response
     * @param command
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object sendCommand(String command) throws IOException, ClassNotFoundException {
        out.writeObject(command);
        return in.readObject();
    }

    /**
     * Retrieves the list of activities from the server
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<Activity> getActivities() throws IOException, ClassNotFoundException {
        return (List<Activity>) getObjects("GET_ACTIVITIES");
    }

    /**
     * Retrieves the list of partners from the server
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<Partner> getPartners() throws IOException, ClassNotFoundException {
        return (List<Partner>) getObjects("GET_PARTNERS");
    }

    /**
     * Generic method to get a list of objects from the server
     * @param command
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<?> getObjects(String command) throws IOException, ClassNotFoundException {
        try {
            out.writeObject(command);

            Object receivedObject = in.readObject();
            if (receivedObject instanceof List) {
                return (List<?>) receivedObject;
            }
            else if (receivedObject instanceof Set<?>) {
                return new ArrayList<>((Set<?>) receivedObject);
            } else {
                System.err.println("Error: Server did not send a Set. Received type: " + receivedObject.getClass().getName());
                System.out.println("Objet received: " + receivedObject);
                return new ArrayList<>();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Serialization error when receiving objects" + e.getMessage());
        }
    }

    /**
     * Retrieves the current logged-in club member from the server
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ClubMember getCurrentClubMember() throws IOException, ClassNotFoundException {
        out.writeObject("GET_CURRENT_MEMBER");
        return (ClubMember) in.readObject();
    }


    /**
     * Send a message and closes the connection with the server
     * @throws IOException
     */
    public void close() throws IOException {
        out.writeObject("EXIT_MAIN");
    }
}
