package uk.gov.justice.laa.dstew.access.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.dstew.access.AccessApp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AccessApp.class, properties = "feature.disable-security=true")
@AutoConfigureMockMvc
@Transactional
public class DraftApplicationIntegrationTest extends BaseIntegrationTest{
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateEmptyItem() throws Exception {
        mockMvc
                .perform(
                        post("/api/v2/draft-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
