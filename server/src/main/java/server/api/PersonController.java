package server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.PersonRepository;
import commons.Person;

import java.util.List;
import java.util.Optional;

@RestController
public class PersonController {
    private final PersonRepository repo;

    public PersonController(PersonRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/persons")
    public List<Person> getAll() {
        return repo.findAll();
    }

    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @PostMapping("/person/")
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
        Person personObj = repo.save(person);

        return ResponseEntity.ok(personObj);
    }

    @DeleteMapping("person/{id}")
    public ResponseEntity<Person> deletePerson(@PathVariable Long id) {
        Optional<Person> personToDelete = repo.findById(id);
        if (!personToDelete.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(id);
        return ResponseEntity.ok(personToDelete.get());
    }

}
