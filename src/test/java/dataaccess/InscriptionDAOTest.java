package dataaccess;
import Server.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;


public class InscriptionDAOTest {

    InscriptionDAO dao;
    MemberDAO memberDAO;
    ActivityDAO activityDAO;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        Files.deleteIfExists(Paths.get("target/test.db"));
        DBManager.setDBMode(DBManager.DBMode.TEST);
        DBManager.getConnection();      // fuerza a crear la conexión
        DBManager.initializeDatabase(); // crea tablas
        dao = new InscriptionDAO();
        memberDAO = new MemberDAO();
        activityDAO = new ActivityDAO();
    }

    @Test
    void testSaveInscription() {
        ClubMember member = new Partner("Member", "Enrolled", "inscription@test.com", "pass");
        memberDAO.save(member);
        member = memberDAO.findAll().get(0);

        Activity activity = new Competition(1, "Football", new Date(1, 1, 2026));
        activityDAO.save(activity);
        activity = activityDAO.findAll().get(0);

        boolean result = dao.save(activity.getId(), member.getId());
        assertTrue(result, "The inscription must be saved successfully");

        boolean duplicateResult = dao.save(activity.getId(), member.getId());
        assertFalse(duplicateResult, "A duplicated inscription must not be allowed");
    }

    @Test
    void testDeleteInscription() {
        ClubMember member = new Partner("Member", "Enrolled", "inscription@test.com", "pass");
        memberDAO.save(member);
        member = memberDAO.findAll().get(0);

        Activity activity = new Course(1, "Test", new Date(1, 1, 2026));
        activityDAO.save(activity);
        activity = activityDAO.findAll().get(0);

        boolean result = dao.save(activity.getId(), member.getId());
        assertTrue(result, "The inscription must be saved successfully");

        boolean deleteResult = dao.delete(activity.getId(), member.getEmail());
        assertTrue(deleteResult, "The inscription must be deleted successfully");

    }


    @AfterEach
    void tearDown() throws SQLException {
        DBManager.closeTestConnection();
        DBManager.setDBMode(DBManager.DBMode.PROD);
    }
}
