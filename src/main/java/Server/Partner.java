package Server;

import java.io.Serial;
import java.io.Serializable;

/**
 * Partner class representing a partner member in the club system.
 */
public class Partner extends ClubMember implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Partner(int id, String name, String surname, String email, String passwordHash) {
        super(id, name, surname, email, passwordHash, "PARTNER");
    }

    public Partner(String name, String surname, String email, String passwordHash) {
        super(-1, name, surname, email, passwordHash, "PARTNER");
    }

    @Override
    public String toString() {
        return "PARTNER: " + getFullName() + " (" + getEmail() + ")";
    }
}

