package Server;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class representing a generic activity in the club.
 * This class implements the common logic for both Races and Courses.
 */
public abstract class Activity implements Serializable {


    private int id; // Unique identifier for the activity
    private String name; // Name of the activity
    private Date date; // Date of the activity
    private final Set<ClubMember> participants = Collections.synchronizedSet(new HashSet<>()); // Set of enrolled members

    /**
     * Constructor for Activity.
     * @param id
     * @param name
     * @param date
     */
    public Activity(int id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    /**
     * Abstract method to get the type of activity.
     * @return String representing the type of activity (Competition/Course)
     */
    public abstract String getType();

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public Date getDate() { return date; }

    /**
     * NOT USED IN THE CURRENT IMPLEMENTATION
     * Participants are managed directly through Inscriptions table on database
     * Adds a participant to the activity.
     * @param member ClubMember to be added as a participant
     * @return true if the member was added, false if they were already enrolled
     */
    public boolean addParticipant(ClubMember member) {
        return participants.add(member);
    }

    /**
     * NOT USED IN THE CURRENT IMPLEMENTATION
     * Participants are managed directly through Inscriptions table on database
     * Removes a participant from the activity.
     * @param member ClubMember to be removed from participants
     * @return true if the member was removed, false if they were not enrolled
     */
    public boolean removeParticipant(ClubMember member) {
        return participants.remove(member);
    }

    /**
     * NOT USED IN THE CURRENT IMPLEMENTATION
     * Participants are managed directly through Inscriptions table on database
     * Gets an unmodifiable view of the participants set.
     * @return Set of ClubMember participants
     */
    public Set<ClubMember> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    @Override
    public String toString() {
        return "[" + getType() + "] ID: " + id + ", Name: " + name +
                ", Date: " + date;
    }
}

