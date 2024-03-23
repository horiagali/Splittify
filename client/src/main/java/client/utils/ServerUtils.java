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
package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.*;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {
	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;
	public static String server = "http://localhost:8080/";

	/**
	 * Constructor
	 * @throws IOException IOException
	 * @throws InterruptedException InterruptedException
	 */
	public ServerUtils() throws IOException, InterruptedException {
		this.objectMapper = new ObjectMapper();
		this.restTemplate = new RestTemplate();
	}

	/**
	 * 
	 * @param server
	 */
	public static void setServer(String server) {
		ServerUtils.server = server;
	}

	/**
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void getQuotesTheHardWay() throws IOException {
		String url = server + "/api/quotes";
		var connection = new URL(url).openConnection();
		var is = connection.getInputStream();
		var br = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}


	/**
	 * 
	 * @return list of quotes
	 */
	public List<Quote> getQuotes() {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(server).path("api/quotes") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
	}

	

	/**
	 * 
	 * @return list of events
	 */
	public List<Event> getEvents() {
		List<Event> events = ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/events")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<List<Event>>() {});
		return events;
	}

	/**
	 * Get event by event ID
	 * @param id id of the event
	 * @return event
	 */
	public Event getEvent(Long id) {
		Event event = ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/events/"+id)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<Event>() {});
		return event;
	}
	private StompSession session = connect("ws://localhost:8080/websocket");
	private StompSession connect(String url) {
		var client = new StandardWebSocketClient();
		var stomp = new WebSocketStompClient(client);
		stomp.setMessageConverter(new MappingJackson2MessageConverter());
		try {
			return stomp.connect(url, new StompSessionHandlerAdapter() {}).get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		throw new IllegalStateException();
	}
	public void registerForEvents(String dest, Consumer<Event> consumer) {
		session.subscribe(dest, new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return Event.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				consumer.accept((Event) payload);
			}
		});
	}

	public String getServer() {
		return server;
	}

	/**
	 * 
	 * @param quote quote
	 * @return quote that is added
	 */
	public Quote addQuote(Quote quote) {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(server).path("api/quotes") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
	}

	/**
	 * 
	 * @param event
	 * @return event that is added (as quote for now)
	 */
	public Event addEvent(Event event) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/events")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.post(Entity.entity(event, APPLICATION_JSON), Event.class);
	}

	/**
	 * Retrieves all nicknames of participants of an event.
	 *
	 * @param eventId ID of the event to retrieve participants for
	 * @return list of participants belonging to the event
	 */
	public List<String> getParticipantNicknamesByEventId(long eventId) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId + "/participants/nicknames")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<List<String>>() {});
	}

	/**
	 * get participants by eventId
	 * @param eventId the eventId
	 * @return the participants
	 */
	public List<Participant> getParticipantsByEventId(long eventId) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId + "/participants")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<List<Participant>>() {});
	}

	/**
	 * return a list of all tags related to 1 event
	 * @param eventId
	 * @return list of tags
	 */
	public List<Tag> getTags(long eventId) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId + "/tags")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<List<Tag>>() {});
	}

	/**
	 *
	 * @param selectedEvent
	 *   deletes an event from the database
	 */
	public void deleteEvent(Event selectedEvent) {
		ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + selectedEvent.getId())
				.request()
				.delete();
	}

	/**
<<<<<<< HEAD
<<<<<<< HEAD
	 * Add an expense to an event
	 * @param eventId id of the event to add the expense to
	 * @param expense expense to be added
	 */
	public void addExpenseToEvent(long eventId, Expense expense) {
		// Construct the URL for the specific event's expenses endpoint
		String url = String.format("%s/events/%d/expenses", server, eventId);

		// Create HttpHeaders with JSON content type
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Create HttpEntity with expense object and headers
		HttpEntity<Expense> requestEntity = new HttpEntity<>(expense, headers);
		restTemplate.postForObject(url, requestEntity, Void.class);
	}

	/**
	 * Get participant by nickname
	 * @param eventID id of the event the participant is a part of
	 * @param nickname nickname
	 * @return participant
	 */
	public Participant getParticipantByNickname(Long eventID, String nickname) {
		String url = server + "api/events/" + eventID + "/participants/" + nickname;

		// Make the HTTP GET request and directly retrieve the participant
		Participant participant = restTemplate.getForObject(url, Participant.class);

		return participant;
	}

	 /**
	  * Update the event in the DB.
	  *
	  * @param updatedEvent   the event
	  */
	 public void updateEvent(Event updatedEvent) {
		ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + updatedEvent.getId())
				.request()
				.put(Entity.entity(updatedEvent, APPLICATION_JSON));
	}

	/**
	 * Adds a participant to an event.
	 *
	 * @param eventId     The ID of the event to which the participant will be added.
	 * @param participant The participant to be added.
	 * @return The added participant.
	 */
	public Participant addParticipant(long eventId, Participant participant) {
		String url = server + "api/events/" + eventId + "/participants";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Participant> requestEntity = new HttpEntity<>(participant, headers);

		return restTemplate.postForObject(url, requestEntity, Participant.class);
	}
	/**
	 * Retrieves all participants of an event by event ID.
	 *
	 * @param eventId ID of the event to retrieve participants for
	 * @return list of participants belonging to the event
	 */
	public List<Participant> getParticipants(long eventId) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId + "/participants")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<List<Participant>>() {});
	}


	/**
	 * sends a mail
	 * @param mail the mail
	 * @return the sent mail
	 */
	public Mail sendEmail(Mail mail) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/mail")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.post(Entity.entity(mail, APPLICATION_JSON), Mail.class);
	}

	/**
	 * get expense by eventId
	 * @param eventId the eventId
	 * @return the expenses
	 */
	public List<Expense> getExpensesByEventId(Long eventId) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId + "/expenses")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<List<Expense>>() {});
	}
	/**
	 * Update a participant in the database.
	 *
	 * @param eventId     The ID of the event to which the participant belongs.
	 * @param participant The updated participant information.
	 */
	public void updateParticipant(long eventId, Participant participant) {
		ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId +
						"/participants/" + participant.getParticipantID())
				.request()
				.put(Entity.entity(participant, APPLICATION_JSON));
	}

	/**
	 * Deletes a participant from an event.
	 *
	 * @param eventId     The ID of the event from which the participant will be deleted.
	 * @param participant The participant to be deleted.
	 */
	public void deleteParticipant(long eventId, Participant participant) {
		ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId +
						"/participants/" + participant.getParticipantID())
				.request()
				.delete();
	}


}