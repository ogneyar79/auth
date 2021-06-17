package ru.job4j.auth.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.net.URL;
import java.security.CodeSource;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private final PersonRepository persons;
    private static Logger log = Logger.getLogger(PersonController.class.getName());

    public PersonController(final PersonRepository persons) {
        this.persons = persons;
    }

    @GetMapping("/")
    public List<Person> findAll() {
        log.info("findAll31");
        return StreamSupport.stream(this.persons.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        log.info("id");
        var person = this.persons.findById(id);
        return new ResponseEntity<>(person.orElse(new Person()), person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping("/") public ResponseEntity<Person> create(@RequestBody Person person) {
        return new ResponseEntity<>(
                this.persons.save(person),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        this.persons.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        this.persons.delete(person);
        return ResponseEntity.ok().build();
    }


    public static void main(String...args)  {
        CodeSource src = ReflectionUtils.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            System.out.println(jar.toString());
        }
    }
}
