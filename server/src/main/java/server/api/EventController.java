/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import java.util.List;
import java.util.Random;

import commons.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import commons.Event;
import server.database.EventRepository;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final Random random;
    private final EventRepository repo;

    /**
     * Constructs a new EventController.
     * @param random Random object for generating random numbers.
     * @param repo EventRepository for accessing event data.
     */
    public EventController(Random random, EventRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * Retrieves all events.
     * @return List of all events.
     */
    @GetMapping(path = {"", "/"})
    public List<Event> getAll() {
        return repo.findAll();
    }

    /**
     * Retrieves an event by its ID.
     * @param id The ID of the event to retrieve.
     * @return ResponseEntity containing the retrieved event,
     * or a bad request response if the ID is invalid.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }   //// this is a random change

    /**
     * Adds a new event.
     * @param event The event to add.
     * @return ResponseEntity containing the added event,
     * or a bad request response if the event is invalid.
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Event> add(@RequestBody Event event) {

        if (event.getTitle() == null || isNullOrEmpty(event.getTitle())) {
            return ResponseEntity.badRequest().build();
        }

        Event saved = repo.save(event);
        return ResponseEntity.ok(saved);
    }

    /**
     * Checks if a string is null or empty.
     * @param s The string to check.
     * @return True if the string is null or empty, false otherwise.
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Retrieves a random event.
     * @return ResponseEntity containing a random event.
     */
    @GetMapping("rnd")
    public ResponseEntity<Event> getRandom() {
        var events = repo.findAll();
        var idx = random.nextInt((int) repo.count());
        return ResponseEntity.ok(events.get(idx));
    }
}