package ru.job4j.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.auth.domain.Employee;

import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.EmployeeRepository;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static Logger log = Logger.getLogger(EmployeeController.class.getName());
    private static final String API = "http://localhost:8080/person/";
    private static final String API_ID = "http://localhost:8080/person/{id}";
    @Autowired
   private RestTemplate rest;

    @Autowired
    private final EmployeeRepository repositoryEmp;


    public EmployeeController(EmployeeRepository repositoryEmp) {
        this.repositoryEmp = repositoryEmp;
    }

    @GetMapping("/")
    public List<Employee> findAll() {
        log.info("findAll31");
        return StreamSupport.stream(this.repositoryEmp.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(@PathVariable int id) {
        log.info("findById get PC");
        var employee = this.repositoryEmp.findById(id);
        return new ResponseEntity<>(employee.orElse(new Employee())
                , employee.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<Employee> create(@RequestBody Employee emp) {
        return new ResponseEntity<>(this.repositoryEmp.save(emp), HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Employee e) {
        this.repositoryEmp.save(e);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Employee employee = new Employee();
        employee.setId(id);
        this.repositoryEmp.delete(employee);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addAccount/{id}")
    public ResponseEntity<Person> createAccount(@RequestBody Person p, @PathVariable int id) {
        var employee = repositoryEmp.findById(id);
        employee.ifPresent(valueE -> {
            valueE.getAccounts().add(p);
            rest.postForObject(API, p, Person.class);
            repositoryEmp.save(valueE);
        });
        return new ResponseEntity<>(p, employee.isPresent() ? HttpStatus.CREATED : HttpStatus.NOT_FOUND);
    }


    @PutMapping("/updateAccount")
    public ResponseEntity<String> updateAccount(@RequestBody Person person) {
        this.findAll().forEach(e -> e.getAccounts().forEach(p -> {
            if (p.getId() == person.getId()) {
                rest.put(API, person);
                p.setLogin(person.getLogin());
                p.setPassword(person.getPassword());
                ResponseEntity.ok().build();
            }
        }));
        return new ResponseEntity<String>(" No Such Account: " + person.toString(), HttpStatus.BAD_REQUEST);
    }

    /**
     * function delete Account (Person) using Rest Template from Employee
     *
     * @param id is id Person object
     */
    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable int id) {
        var employee = this.findAll().stream().
                filter(e -> e.getAccounts().removeIf(p -> p.getId() == id)).findAny();
        employee.ifPresent(value -> {
            rest.delete(API_ID, id);
            repositoryEmp.save(value);
        });
        return employee.isPresent() ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
