package hu.unideb.inf.printingservice.controllertests;

import hu.unideb.inf.printingservice.controller.UserController;
import hu.unideb.inf.printingservice.service.UserService;
import hu.unideb.inf.printingservice.service.dto.UserDisplayDto;
import hu.unideb.inf.printingservice.service.dto.UserSaveDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
// Csak a webes réteget és a UserControllert indítja el (adatbázis nélkül)
@WebMvcTest(UserController.class)
class UserControllerTest {

    // A "virtuális Postman", amivel HTTP kéréseket szimulálunk
    @Autowired
    private MockMvc mockMvc;

    // A valódi UserService helyett egy irányítható bábut adunk a Controllernek
    @MockitoBean
    private UserService userService;

    @Test
    void saveUser_ervenyesAdatokkal_200OkEsJsonValasz() throws Exception {
        // --- ARRANGE: Mit adjon vissza a hamis Service, ha meghívják? ---
        UserDisplayDto elvartValasz = new UserDisplayDto();
        elvartValasz.setId(1L);
        elvartValasz.setFelhasznalonev("tesztuser");
        elvartValasz.setEmail("teszt@unideb.hu");

        when(userService.save(any(UserSaveDto.class))).thenReturn(elvartValasz);

        String kuldottJson = """
                {
                    "felhasznalonev": "tesztuser",
                    "jelszo": "titkos123",
                    "email": "teszt@unideb.hu"
                }
                """;

        // --- ACT & ASSERT: Elküldjük a POST kérést és ellenőrizzük a HTTP választ ---
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(kuldottJson))
                .andExpect(status().isOk()) // 200-as státuszkódot várunk
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.felhasznalonev").value("tesztuser"))
                .andExpect(jsonPath("$.jelszo").doesNotExist()); // Ellenőrizzük, hogy a jelszó tényleg nincs benne!
    }

    @Test
    void saveUser_hibasBemenetEseten_400BadRequest() throws Exception {
        // Üres felhasználónevet és hibás emailt küldünk -> a @Valid-nak meg kell fognia!
        String hibasJson = """
                {
                    "felhasznalonev": "",
                    "jelszo": "12",
                    "email": "nem-email-cim"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hibasJson))
                .andExpect(status().isBadRequest()); // 400-as validációs hibát várunk

        // Mivel a Controller már az ajtóban megfogta a kérést, a Service-nek le sem szabad futnia
        verify(userService, never()).save(any());
    }
}