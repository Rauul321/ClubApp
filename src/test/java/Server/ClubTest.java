package Server;

import dataaccess.ActivityDAO;
import dataaccess.InscriptionDAO;
import dataaccess.MemberDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import Server.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClubTest {

    private Club club;

    // Mocks de los DAOs
    @Mock private MemberDAO memberDAO;
    @Mock private ActivityDAO activityDAO;
    @Mock private InscriptionDAO inscriptionDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        club = new Club(memberDAO, activityDAO, inscriptionDAO);
    }

    @Test
    @DisplayName("Should return only Partners from getAllPartners")
    void testGetAllPartners() {
        ClubMember socio = new Partner("Juan", "Perez", "juan@test.com", "123");
        ClubMember admin = new Admin("Ana", "Admin", "ana@test.com", "456");

        when(memberDAO.findAll()).thenReturn(Arrays.asList(socio, admin));

        List<Partner> resultado = club.getAllPartners();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0) instanceof Partner);
        assertEquals("juan@test.com", resultado.get(0).getEmail());
    }

    @Test
    @DisplayName("It should save an Admin when role is ADMIN")
    void testRegisterAdmin() {
        when(memberDAO.save(any())).thenReturn(true);
        boolean creado = club.registerMember("Raul", "Gomez", "raul@test.com", "pass", "ADMIN");

        assertTrue(creado);
        verify(memberDAO).save(argThat(m -> m instanceof Admin));
    }

    @Test
    @DisplayName("Should save a Partner when rol is PARTNER")
    void testRegisterPartner() {
        when(memberDAO.save(any())).thenReturn(true);
        boolean created = club.registerMember("Raul", "Rosado", "raul@test.com", "pass", "PARTNER");
        assertTrue(created);
        verify(memberDAO).save(argThat(m -> m instanceof Partner));
    }

    @Test
    @DisplayName("A member should be deleted")
    void testRemoveMember() {
        when(memberDAO.deleteMember(any())).thenReturn(true);
        boolean deleted = club.removeMember("removed@test.com");
        assertTrue(deleted);
    }

    @Test
    @DisplayName("It should return false when member to remove is not found")
    void testRemoveMemberNotFound() {
        when(memberDAO.deleteMember("noexiste@test.com")).thenReturn(false);

        boolean resultado = club.removeMember("noexiste@test.com");

        assertFalse(resultado, "Debería ser false si el miembro no existe en la DB");
    }

    @Test
    @DisplayName("it should register successfully an enrollment")
    void testEnrollSuccess() {
        when(inscriptionDAO.save(10, 50)).thenReturn(true);

        boolean resultado = club.enroll(10, 50);

        assertTrue(resultado);
        verify(inscriptionDAO, times(1)).save(10, 50);
    }

    @Test
    @DisplayName("It should return false when enrollment")
    void testEnrollFailure() {
        when(inscriptionDAO.save(999, 888)).thenReturn(false);

        boolean resultado = club.enroll(999, 888);

        assertFalse(resultado, "No se debería poder inscribir si el DAO falla");
    }

    @Test
    @DisplayName("Debería autenticar correctamente a un usuario con credenciales válidas")
    void testLogin() {
        ClubMember member = new Partner("Raul", "Rosado", "login@test.com", "mypassword");
        when(memberDAO.login("login@test.com", "mypassword")).thenReturn(new LoginResult(LoginResult.Status.SUCCESS, member));
        LoginResult lr = club.authenticate("login@test.com", "mypassword");
        assertTrue(lr.isSuccessful());
    }

    @Test
    @DisplayName("Should Return login result FAIL CREDENTIALS when the credentials are wrong")
    void testFailedLogin() {
        when(memberDAO.login("notexists@club.com", "password")).thenReturn(new LoginResult(LoginResult.Status.FAIL_CREDENTIALS, null));
        LoginResult lr = club.authenticate("notexists@club.com", "password");
        assertEquals(LoginResult.Status.FAIL_CREDENTIALS, lr.getStatus());
    }

}
