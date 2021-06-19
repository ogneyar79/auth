package ru.job4j.auth.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.auth.domain.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
}
