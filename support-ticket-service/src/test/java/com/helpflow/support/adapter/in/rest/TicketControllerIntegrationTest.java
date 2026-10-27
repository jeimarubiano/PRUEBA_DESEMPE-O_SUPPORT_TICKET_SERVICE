package com.helpflow.support.adapter.in.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpflow.support.application.port.out.TechnicianDispatchPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean TechnicianDispatchPort dispatch;

    @Test
    void userCreatesAnAutomaticallyAssignedTicketAndCanReadOnlyTheirOwnTickets() throws Exception {
        when(dispatch.request(anyLong(), any(), any(), anyString()))
                .thenReturn(new TechnicianDispatchPort.DispatchSuggestion("TEC-RED", "ALTA", 2));
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String email = "user" + suffix + "@helpflow.com";
        String register = "{\"document\":\"DOC-" + suffix + "\",\"name\":\"Usuario Prueba\",\"email\":\"" + email + "\",\"site\":\"Bogota\",\"area\":\"TI\",\"password\":\"Password123\"}";
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(register)).andExpect(status().isCreated());
        var login = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"Password123\"}"))
                .andExpect(status().isOk()).andReturn();
        JsonNode body = objectMapper.readTree(login.getResponse().getContentAsString());
        String token = body.get("accessToken").asText();
        String ticket = "{\"title\":\"Sin red\",\"description\":\"No hay conexión a Internet\",\"category\":\"RED\",\"priority\":\"ALTA\",\"site\":\"Bogota\",\"affectedAsset\":\"PC-01\"}";

        mockMvc.perform(post("/tickets").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(ticket))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("ASIGNADO"));
        mockMvc.perform(get("/tickets").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Sin red"));
    }
}
