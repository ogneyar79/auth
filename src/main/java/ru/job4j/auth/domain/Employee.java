package ru.job4j.auth.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;


@Entity
@Table(name = "employee")
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String surname;
    private String codeI;

    private Timestamp hiringDate;


    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "employee_person",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private ArrayList<Person> accounts = new ArrayList<>();

    public Employee(int id, String name, String surname, String codeI, Timestamp hiringDate) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.codeI = codeI;
        this.hiringDate = hiringDate;
    }

    public Employee() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCodeI() {
        return codeI;
    }

    public void setCodeI(String codeI) {
        this.codeI = codeI;
    }

    public Timestamp getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(Timestamp hiringDate) {
        this.hiringDate = hiringDate;
    }

    public ArrayList<Person> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Person> accounts) {
        this.accounts = accounts;
    }
}
