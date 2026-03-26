package Server;

import dataaccess.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import static Server.SimulationLogger.clearLog;
import static Server.SimulationLogger.log;

public class SimulatonRunner {

    public static void main(String[] args) {

        clearLog();
        // DB de simulación
        DBManager.setDBMode(DBManager.DBMode.SIMULATION);
        DBManager.initializeDatabase();
        DBManager.resetDatabase();
        Club club = new Club(
                new MemberDAO(),
                new ActivityDAO(),
                new InscriptionDAO()
        );


        club.registerMember("Raul", "Rosado", "admin@club.com", "admin", "ADMIN");

        club.registerMember("Agostino", "Poggi", "agostino@club.com", "pwd", "PARTNER");
        club.registerMember("Alicia", "Rosado", "alicia@club.com", "pwd", "PARTNER");

        club.createActivity("Corso Yoga", new Date(10, 5, 2026), "Course");
        club.createActivity("Gara Nuoto", new Date(20, 5, 2026), "Competition");

        ClubMember member = club.getMemberByEmail("admin@club.com");

        Admin admin = null;
        if (member instanceof Admin) {
            admin = (Admin) member;
        }
        if(admin != null) {
            if(admin.addMember("David", "Rosado", "david@club.com", "pwd", "PARTNER", club)) {
                log("[" + admin.getRole() + " " + admin.getFullName() + "]" + "Member David added successfully.");
            } else {
                System.out.println("[" + admin.getRole() + " " + admin.getFullName() + "]" + "Failed to add member David.");
            }

            if(admin.modifyMember( "DavidMod", "Rosado", "newpwd", "david@club.com", club)) {
                log("[" + admin.getRole() + " " + admin.getFullName() + "]" + "Member David modified successfully.");
            } else {
                System.out.println("[" + admin.getRole() + " " + admin.getFullName() + "]" + "Failed to modify member David.");
            }

            if(admin.deleteMember("david@club.com", club)) {
                log("[" + admin.getRole() + " " + admin.getFullName() + "]" + "Member David deleted successfully.");
            } else {
                log("[" + admin.getRole() + " " + admin.getFullName() + "]" + "Failed to delete member David.");
            }
        }


        Partner chosenPartner = club.getAllPartners().get(0);

        List<Activity> activities = club.getAllActivities();

        int courseId = activities.stream()
                .filter(a -> a.getType().equals("Course"))
                .findFirst().get().getId();

        int competitionId = activities.stream()
                .filter(a -> a.getType().equals("Competition"))
                .findFirst().get().getId();

        if(chosenPartner.enrollInActivity(competitionId, club)) {
            log("[" + chosenPartner.getRole() + " " + chosenPartner.getFullName() + "]" +
                    " enrolled in activity ID " + competitionId + " successfully.");
        } else {
            log("Failed to enroll in activity ID " + competitionId + ".");
        }

        if(chosenPartner.enrollInActivity(courseId, club)) {
            log("[" + chosenPartner.getRole() + " " + chosenPartner.getFullName() + "]" +
                    " enrolled in activity ID " + courseId + " successfully.");
        } else {
            log("Failed to enroll in activity ID " + courseId + ".");
        }

        if(chosenPartner.unrollFromActivity(courseId, club)) {
            log("[" + chosenPartner.getRole() + " " + chosenPartner.getFullName() + "]" +
                   " unrolled from activity ID " + courseId + " successfully.");
        } else {
            log("Failed to unroll from activity ID " + courseId + ".");
        }


        log("MEMBERS: ");
        for (ClubMember m : club.getAllMembers()) {
            log("    - " + m.toString());
        }

        log("ACTIVITIES: ");
        for (Activity a : club.getAllActivities()) {
            log("    - " + a.toString());
            log("        Participants: ");
            for (ClubMember p : club.getParticipants(a.getId())) {
                log("          · " + p.toString());
            }
        }

    }
}


class SimulationLogger {

    private static final String FILE_NAME = "simulation.txt";

    public static void clearLog() {
        try (FileWriter writer = new FileWriter(FILE_NAME, false)) {
        } catch (IOException e) {
            System.err.println("Error clearing simulation log: " + e.getMessage());
        }
    }

    public static void log(String message) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(message + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }
}
