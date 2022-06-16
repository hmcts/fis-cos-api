package uk.gov.hmcts.reform.cosapi.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.cosapi.model.idam.User;
import uk.gov.hmcts.reform.cosapi.services.idam.IdamManagementService;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class IdamManagementControllerTest {

    @MockBean
    private IdamManagementService idamManagementService;

    @Autowired
    private transient MockMvc mockMvc;

    @Test
    void userNotFoundForGivenAccessToken() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/idam/dss-orhestration/user/details")
                                                  .contentType(APPLICATION_JSON)
                                                  .header(HttpHeaders.AUTHORIZATION, "Authorization"))
            .andExpect(status().isNotFound()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertEquals(
            "User details not found for the given user access token",
            response.getContentAsString()
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void userFound() throws Exception {
        User user = User.builder()
            .firstName("hmcts")
            .lastName("gov")
            .email("gov@htcts.net")
            .build();
        Mockito.when(idamManagementService.getUserDetails("Authorization")).thenReturn(user);
        mockMvc.perform(get("/idam/dss-orhestration/user/details")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Authorization"))
            .andExpect(status().isOk()).andReturn();
    }

    @Test
    void userAcessTokenEmpty() throws Exception {
        mockMvc.perform(get("/idam/dss-orhestration/user/details")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, ""))
            .andExpect(status().isBadRequest()).andReturn();
    }
}
