package ru.job4j.auth.controller;


import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.auth.AuthApplication;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
public class PersonControllerTest {

    private static Logger log = Logger.getLogger(PersonControllerTest.class.getName());

    @MockBean
    private PersonRepository repository;

    @Autowired
    PersonController personController;

    @Autowired
    private MockMvc mockMvc;

    Person person1;

    @BeforeEach
    void setUp() {
        person1 = new Person(1, "personOne", "ONE");
    }

    @Test
    @DisplayName("Checking that test work")
    public void contextLoads() throws Exception {
        assertThat(personController).isNotNull();
    }

    @Test
    @DisplayName("test find All elements of base")
    public void shouldReturnAllPersons() throws Exception {
        log.info("test find All");
        when(repository.findAll()).thenReturn(List.of(person1, new Person(2, "two", "12345")));
        List<Person> personList = personController.findAll();
        assertThat(personList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Try Getting All With JSONArray")
    public void givenPerson_whenFindAllPersonss_thenReturnJsonArray() throws Exception {
        List<Person> allPerson = Arrays.asList(person1);
        given(repository.findAll()).willReturn(allPerson);
        mockMvc.perform(get("/person/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(person1.getId())));
    }

    @Test
    @DisplayName("test status OK and Getting What wanted And our GET")
    public void shouldAndStatusOk() throws Exception {
        log.info("test find All");
        when(repository.findAll()).thenReturn(List.of(person1));
        mockMvc.perform(get("/person/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"login\":\"personOne\",\"password\":\"ONE\"}]"));
    }

    @Test
    @DisplayName("Getting Person by Id get status")
    public void findByIdAndGetStatus() throws Exception {
        Person person = null;
        when(repository.findById(1)).thenReturn(Optional.ofNullable(person1));
        assertAll("Checking return object of Person and person null object",
                () -> assertThat(personController.findById(1)).isEqualTo(new ResponseEntity<>(person1, HttpStatus.OK)),
                () -> log.info(" Try get object that NO have " + personController.findById(2).toString()),
                () -> assertThat(personController.findById(2)).isEqualTo(new ResponseEntity<>(new Person(), HttpStatus.NOT_FOUND)),
                () -> assertThat(personController.findById(2)).isNotEqualTo(new ResponseEntity<>(new Person(), HttpStatus.OK)));
    }

    @Test
    @DisplayName("Getting Person by Id")
    public void findById() throws Exception {
        given(repository.findById(1)).willReturn(java.util.Optional.of(new Person(1, "test", "P")));
        mockMvc.perform(get("/person/{id}", "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.login", is("test")))
                .andExpect(jsonPath("$.password", is("P")));
    }

    @Test
    @DisplayName("Create new Person Object")
    public void createNewPersonAndStatus() throws Exception {
        given(repository.save(person1)).willReturn(person1);
        assertAll(() -> mockMvc.perform(post("/person/")
                        .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(person1)))
                        .andDo(print()).andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id", is(1)))
                        .andExpect(jsonPath("$.login", is("personOne")))
                , () -> log.info(" test creation new  new ResponseEntity< Person> ")
                , () -> assertThat(personController.create(person1)).isEqualTo(new ResponseEntity<>(person1, HttpStatus.CREATED))
        );
    }

    @Test
    @DisplayName("Delete object with id")
    public void deletePerson() throws Exception {
        mockMvc.perform(delete("/person/{id}", "1"))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(personController.delete(1)).isEqualTo(ResponseEntity.ok().build());
    }
}
