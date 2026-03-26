package Server;

import dataaccess.ActivityDAO;
import dataaccess.InscriptionDAO;
import dataaccess.MemberDAO;
import java.util.List;

/**
 * Club class encapsulates the business logic for managing club members, activities, and inscriptions.
 * It interacts with DAOs to perform CRUD operations.
 */
public class Club {
    // DAOs
    private final MemberDAO memberDAO;
    private final ActivityDAO activityDAO;
    private final InscriptionDAO inscriptionDAO;

    /**
     * Constructor with dependency injection of DAOs.
     * @param memberDAO
     * @param activityDAO
     * @param inscriptionDAO
     */
    public Club(MemberDAO memberDAO, ActivityDAO activityDAO, InscriptionDAO inscriptionDAO) {
        this.memberDAO = memberDAO;
        this.activityDAO = activityDAO;
        this.inscriptionDAO = inscriptionDAO;
    }

    /**
     * Authenticates a member using email and password.
     * @param email
     * @param password
     * @return LoginResult indicating success or failure and the member object if successfully.
     */
    public LoginResult authenticate(String email, String password) {
        return memberDAO.login(email, password);
    }

    /**
     * Registers a new member (Admin or Partner) in the club.
     * @param name The first name of the member.
     * @param surname The last name of the member.
     * @param email The email address of the member.
     * @param password The password of the member.
     * @param role The role of the member ("ADMIN" or "PARTNER").
     * @return true if registration is successful, false otherwise.
     */
    public boolean registerMember(String name, String surname, String email, String password, String role) {
        // Regla de Negocio: No permitir duplicados (el DAO ya lo gestiona, pero aquí podríamos añadir más filtros)
        if(role.equals("ADMIN")){
            Admin newAdmin = new Admin(name, surname, email, password);
            return memberDAO.save(newAdmin);
        }

        Partner newPartner = new Partner(name, surname, email, password);
        return memberDAO.save(newPartner);
    }

    /**
     * Retrieves all club members.
     * @return List of ClubMember objects.
     */
    public List<ClubMember> getAllMembers() {
        return memberDAO.findAll();
    }

    /**
     * Retrieves all partners from the club members.
     * @return List of Partner objects.
     */
    public List<Partner> getAllPartners() {
        List<ClubMember> allMembers = memberDAO.findAll();
        List<Partner> partners = new java.util.ArrayList<>();
        for (ClubMember member : allMembers) {
            if (member.getRole().equals("PARTNER")) {
                partners.add((Partner) member);
            }
        }
        return partners;
    }

    /**
     * Retrieves a club member by email.
     * @param email
     * @return ClubMember object or null if not found.
     */
    public ClubMember getMemberByEmail(String email) {
        return memberDAO.getMemberByEmail(email);
    }

    /**
     * Removes a member from the club by email.
     * @param email
     * @return true if removal is successful, false otherwise.
     */
    public boolean removeMember(String email) {
        return memberDAO.deleteMember(email);
    }

    /**
     * Modifies a member's details.
     * @param newName
     * @param newSurname
     * @param newPassword
     * @param email
     * @return true if modification is successful, false otherwise.
     */
    public boolean modifyMember(String newName, String newSurname, String newPassword, String email) {
        return memberDAO.updateMember(newName, newSurname, newPassword, email);
    }

    /**
     * Retrieves all activities in the club.
     * @return List of Activity objects.
     */
    public List<Activity> getAllActivities() {
        return activityDAO.findAll();
    }

    /**
     * Creates a new activity (Course or Competition).
     * @param name
     * @param date
     * @param type
     * @return true if creation is successful, false otherwise.
     */
    public boolean createActivity(String name, Date date, String type) {

        if(type.equals("Course")){
            return activityDAO.save(new Course(-1, name, date));
        }
        else if(type.equals("Competition")){
            return activityDAO.save(new Competition(-1, name, date));
        }
        else {
            return false;
        }
    }

    /**
     * Modifies an existing activity's details.
     * @param id
     * @param newName
     * @param newDate
     * @param newType
     * @return true if modification is successful, false otherwise.
     */
    public boolean modifyActivity(int id, String newName, Date newDate, String newType) {
        String dateStr = newDate.getDay() + "/" + newDate.getMonth() + "/" + newDate.getYear();
        return activityDAO.update(id, newName, dateStr, newType);
    }

    /**
     * Removes an activity from the club by ID.
     * @param id The id of the activity to be removed
     * @return true if removal is successful, false otherwise.
     */
    public boolean removeActivity(int id) {
        return activityDAO.delete(id);
    }

    /**
     * Enrolls a member in an activity.
     * @param activityId The activity ID
     * @param memberId The ID of the member to be enrolled
     * @return true if enrollment is successful, false otherwise.
     */
    public boolean enroll(int activityId, int memberId) {
        return inscriptionDAO.save(activityId, memberId);
    }

    /**
     * Unrolls a member from an activity.
     * @param activityId The activity ID
     * @param memberEmail The email of the member to be unrolled
     * @return true if unrollment is successful, false otherwise.
     */
    public boolean unroll(int activityId, String memberEmail) {
        return inscriptionDAO.delete(activityId, memberEmail);
    }

    /**
     * Checks if a member is enrolled in an activity.
     * @param activityId The activity ID
     * @param email The email of the member
     * @return true if the member is enrolled, false otherwise.
     */
    public boolean isEnrolled(int activityId, String email) {
        return inscriptionDAO.isEnrolled(activityId, email);
    }

    /**
     * Retrieves all participants enrolled in a specific activity.
     * @param activityId The activity ID
     * @return List of ClubMember objects who are participants in the activity.
     */
    public List<ClubMember> getParticipants(int activityId) {
        return memberDAO.getParticipantsByActivity(activityId);
    }
}

