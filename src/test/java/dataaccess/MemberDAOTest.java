package dataaccess;

import dataaccess.DBManager;
import dataaccess.MemberDAO;
import Server.ClubMember;
import Server.Partner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;

class MemberDAOTest {
    private MemberDAO dao;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        Files.deleteIfExists(Paths.get("target/test.db"));
        DBManager.setDBMode(DBManager.DBMode.TEST);
        DBManager.getConnection();      // fuerza a crear la conexión
        DBManager.initializeDatabase(); // crea tablas
        dao = new MemberDAO();
    }

    @Test
    void testSaveAndRetrieveMember() {
        // Creamos un miembro de prueba
        // Asumiendo que tu Partner extiende de ClubMember
        Partner p = new Partner("Raul", "Gomez", "raul@test.com", "hash123");

        // Guardamos
        boolean result = dao.save(p);
        assertTrue(result, "El guardado debería ser exitoso");

        // Recuperamos para verificar que el SQL escribió bien las columnas
        ClubMember recuperado = dao.getMemberByEmail("raul@test.com");

        assertNotNull(recuperado);
        assertEquals("Raul", recuperado.getName());
        assertEquals("PARTNER", recuperado.getRole());
    }

    @Test
    @DisplayName("It must not allow duplicate member emails")
    void testSaveDuplicateMember() {
        Partner p = new Partner("Raul", "Rosado", "raul@test.com", "hash123");
        boolean firstSave = dao.save(p);
        assertTrue(firstSave, "The first save should succeed");
        Partner pDuplicated = new Partner("Raul", "Rosado", "raul@test.com", "hash123");
        boolean secondSave = dao.save(pDuplicated);
        assertFalse(secondSave, "The second save with duplicate email should fail");
    }

    @Test
    @DisplayName("A member should be deleted")
    void testDeleteMember() {
        Partner p = new Partner("Raul", "Rosado", "raul@test.com", "hash123");
        boolean save = dao.save(p);
        assertTrue(save, "The first save should succeed");
        boolean deleted = dao.deleteMember("raul@test.com");
        assertTrue(deleted, "The member should be deleted successfully");
        ClubMember m = dao.getMemberByEmail("raul@test.com");
        assertNull(m, "The member should not exist");
    }

    @Test
    @DisplayName("Deleting a non-existing member should return false")
    void testDeleteMemberThatNotExists() {
        boolean deleted = dao.deleteMember("raul@test.com");
        assertFalse(deleted, "The deletion of a non-existing member should return fase");
    }

    @Test
    @DisplayName("A member should be initialized successfully")
    void testUpdateMember() {
        Partner p = new Partner("Raul", "Rosado", "raul@test.com", "hash123");
        boolean save = dao.save(p);
        assertTrue(save, "The first save should succeed");
        dao.updateMember("Juan", "Perez", "newpass", "raul@test.com");
        ClubMember m = dao.getMemberByEmail("raul@test.com");
        assertEquals("Juan", m.getName());
        assertEquals("Perez", m.getSurname());
        assertEquals("newpass", m.getPassword());
    }


    @AfterEach
    void tearDown() throws SQLException {
        DBManager.closeTestConnection();
        DBManager.setDBMode(DBManager.DBMode.PROD);
    }
}
