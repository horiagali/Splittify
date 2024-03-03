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

import commons.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    /**
     * Constructor for the eventController
     * @param eventService an EventService
     */
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Retrieves all events.
     * @return List of all events.
     */
    @GetMapping
    @ResponseBody
    public List<Event> getAllEvents() {
        return eventService.getEvents();
    }

    /**
     * Retrieves an event by its ID.
     * @param id The ID of the event to retrieve.
     * @return ResponseEntity containing the retrieved event,
     * or a bad request response if the ID is invalid.
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Event> getById(@PathVariable("id") Integer id) {
        return eventService.getEventById(id);
    }

    /**
     * Adds a new event.
     * @param event The event to add.
     * @return ResponseEntity containing the added event,
     * or a bad request response if the event is invalid.
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        System.out.println(event);
        return eventService.createEvent(event);
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
     * delete an event by id
     * @param id the id of the deleted event
     * @return an Event
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Event> deleteEvent(@PathVariable("id") Integer id){
        return eventService.deleteEvent(id);
    }

    /**
     * Update an existing event
     * @param event the updated event
     * @param id the id of the event to update
     * @return the updated event
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Event> updateEvent(Event event, @PathVariable("id") Integer id){
        return eventService.updateEvent(event, id);
    }
}