package dataaccess;

import Server.Activity;
import Server.Competition;
import Server.Course;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityDAOTest {

    private ActivityDAO dao;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        Files.deleteIfExists(Paths.get("target/test.db"));
        DBManager.setDBMode(DBManager.DBMode.TEST);
        DBManager.getConnection();      // fuerza a crear la conexión
        DBManager.initializeDatabase(); // crea tablas
        dao = new ActivityDAO();
    }

    @Test
    void testSaveAndRetrieverActivity() {
        Activity activity = new Course(1, "Test", new Server.Date(15, 8, 2024));
        boolean saved = dao.save(activity);
        assertTrue(saved, "Activity should be saved successfully");
        Activity retrieved = dao.findAll().stream()
                .filter(a -> a.getName().equals("Test"))
                .findFirst()
                .orElse(null);
        assertNotNull(retrieved, "Activity should be retrievable");
    }

    @Test
    void testDeleteActivity() {
        Activity activity = new Competition(1, "ToDelete", new Server.Date(20, 9, 2024));
        dao.save(activity);
        Activity toDelete = dao.findAll().stream()
                .filter(a -> a.getName().equals("ToDelete"))
                .findFirst()
                .orElse(null);
        assertNotNull(toDelete, "Activity to delete should exist");
        boolean deleted = dao.delete(toDelete.getId());
        assertTrue(deleted, "Activity should be deleted successfully");
        Activity shouldBeNull = dao.findAll().stream()
                .filter(a -> a.getName().equals("ToDelete"))
                .findFirst()
                .orElse(null);
        assertNull(shouldBeNull, "Deleted activity should not be retrievable");
    }

    @Test
    void testModifyActivity() {
        Activity activity = new Course(1, "ToModify", new Server.Date(10, 10, 2024));
        dao.save(activity);
        Activity toModify = dao.findAll().stream()
                .filter(a -> a.getName().equals("ToModify"))
                .findFirst()
                .orElse(null);

        assertNotNull(toModify, "Activity to modify should exist");
        int id = toModify.getId();
        boolean modified = dao.update(id, "ModifiedName", "1/1/2026", "Competition");
        assertTrue(modified, "Activity should be modified successfully");
        Activity modifiedActivity = dao.findAll().stream()
                .filter(a -> a.getId() == toModify.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(modifiedActivity, "Modified activity should be retrievable");
        assertEquals("ModifiedName", modifiedActivity.getName(), "Name should be updated");
        assertEquals("Competition", modifiedActivity.getType(), "Type should be updated");
        assertEquals(1, modifiedActivity.getDate().getDay(), "Day should be updated");
        assertEquals(1, modifiedActivity.getDate().getMonth(), "Month should be updated");
        assertEquals(2026, modifiedActivity.getDate().getYear(), "Year should be updated");
    }


    @AfterEach
    void tearDown() throws SQLException, IOException {
        DBManager.closeTestConnection();
        Files.deleteIfExists(Paths.get("target/test.db"));
    }
}
