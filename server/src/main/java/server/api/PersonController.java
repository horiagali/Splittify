package server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.PersonRepository;
import commons.Person;

import java.util.List;
import java.util.Optional;

/**
 * Basic REST API for Person
 */
@RestController
public class PersonController {
    private final PersonRepository repo;

    /**
     * Constructor for controller
     * @param repo Person repository
     */
    public PersonController(PersonRepository repo) {
        this.repo = repo;
    }

    /**
     * GET request -> returns all persons in database
     * @return List with persons in database
     */
    @GetMapping("/persons")
    public List<Person> getAll() {
        return repo.findAll();
    }

    /**
     * GET request for specific person
     * @param id Person ID
     * @return badRequest if person does not exist, else ok
     */
    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * POST request to create person
     * @param person Parsed from request body
     * @return Ok response
     */
    @PostMapping("/person/")
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
        Person personObj = repo.save(person);

        return ResponseEntity.ok(personObj);
    }

    /**
     * DELETE for deleting a person by ID
     * @param id ID of person to delete
     * @return badRequest if person not found, else ok
     */
    @DeleteMapping("person/{id}")
    public ResponseEntity<Person> deletePerson(@PathVariable Long id) {
        Optional<Person> personToDelete = repo.findById(id);
        if (personToDelete.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(id);
        return ResponseEntity.ok(personToDelete.get());
    }

}
