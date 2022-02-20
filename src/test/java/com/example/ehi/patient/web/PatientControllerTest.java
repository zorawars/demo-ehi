package com.example.ehi.patient.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PatientControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Test retrieving all patients")
    public void testGetAllPatients() throws Exception {
        this.mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty", is(false)))
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content.length()", is(6)))
                .andExpect(jsonPath("$.size", is(50)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(6)))
                .andExpect(jsonPath("$.numberOfElements", is(6)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(true)));
    }

    @ParameterizedTest
    @MethodSource("patientProvider")
    @DisplayName("Test retrieving individual patients")
    public void testGetPatient(int id, String firstName, String lastName, String dateOfBirth) throws Exception {
        String uri = "/patients/" + id;
        this.mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.firstName", is(firstName)))
                .andExpect(jsonPath("$.lastName", is(lastName)))
                .andExpect(jsonPath("$.dateOfBirth", is(dateOfBirth)));
    }

    static Stream<Arguments> patientProvider() {
        return Stream.of(
                Arguments.of(1, "Steve", "Rogers", "1918-07-04"),
                Arguments.of(2, "Carol", "Danvers", "1965-08-17"),
                Arguments.of(3, "Caroline", "Diaz", "1975-09-18"),
                Arguments.of(4, "Bruce", "Banner", "1969-12-18"),
                Arguments.of(5, "Anita", "Kahn", "1971-01-20"),
                Arguments.of(6, "Stephen", "Strange", "1930-11-18")
        );
    }

    @Test
    @DisplayName("Test adding a new patient")
    public void testAddPatient() throws Exception {
        String patient = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"dateOfBirth\":\"1988-01-01\"}";
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/ehi/patients")
                                .contextPath("/ehi")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(patient)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", endsWith("/ehi/patients/7")))
                .andExpect(header().string("Location", is("http://localhost/ehi/patients/7")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"firstName\":\"\",\"lastName\":\"Doe\",\"dateOfBirth\":\"1988-01-01\"}",
            "{\"lastName\":\"Doe\",\"dateOfBirth\":\"1988-01-01\"}"
    })
    @DisplayName("Bad Request when adding a new patient without first name")
    public void testAddPatientWithoutFirstName(String jsonPatient) throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/ehi/patients")
                                .contextPath("/ehi")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPatient)
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("First name is required"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"firstName\":\"John\",\"lastName\":\"\",\"dateOfBirth\":\"1988-01-01\"}",
            "{\"firstName\":\"John\",\"dateOfBirth\":\"1988-01-01\"}"
    })
    @DisplayName("Bad Request when adding a new patient without last name")
    public void testAddPatientWithoutLastName(String jsonPatient) throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/ehi/patients")
                                .contextPath("/ehi")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPatient)
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Last name is required"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"dateOfBirth\":\"\"}",
            "{\"firstName\":\"John\",\"lastName\":\"Doe\"}"
    })
    @DisplayName("Bad Request when adding a new patient without date of birth")
    public void testAddPatientWithoutDateOfBirth(String jsonPatient) throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/ehi/patients")
                                .contextPath("/ehi")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPatient)
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Date of Birth is required"));
    }

}
