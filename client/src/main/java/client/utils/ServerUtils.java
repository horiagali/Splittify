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

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import client.Main;
import commons.Event;
import commons.Expense;
import commons.Mail;
import commons.Participant;
import commons.Quote;
import commons.Tag;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

public class ServerUtils {
	private final ObjectMapper objectMapper;
	private RestTemplate restTemplate;
	public static String server = "http://localhost:8080/";
	public static String serverPort = server.replace("http://", "");
	private StompSession session;
	private Client client;
	private static final ExecutorService EXEC = Executors.newSingleThreadExecutor();

	/**
	 * getter for url
	 * @return url
	 */
	public static String getServer() {
		return server;
	}

	/**
	 * Constructor
	 * @throws IOException IOException
	 * @throws InterruptedException InterruptedException
	 */
	public ServerUtils() throws IOException, InterruptedException {
		this.objectMapper = new ObjectMapper();
		this.restTemplate = new RestTemplate();
		checkConnectionForWebsockets();
	}

	/**
	 * 
	 * @param server
	 */
	public static void setServer(String server) {
		ServerUtils.server = server;
		serverPort = server.replace("http://", "");
	}

	/**
	 * 
	 */
	public void checkConnectionForWebsockets() {
		if(Main.checkConnection())
		session = connect("ws://"+ serverPort + "websocket");
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
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/events")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<List<Event>>() {});
	}

	/**
	 * Get event by event ID
	 * @param id id of the event
	 * @return event
	 */
	public Event getEvent(Long id) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/events/"+id)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<Event>() {});
	}


	/**
	 * Implementation for long polling
	 * @param consumer consumer
	 */
	public void registerForUpdates(Consumer<Event> consumer) {
		EXEC.submit(() -> {
			while (!Thread.interrupted()) {
				Response res = ClientBuilder.newClient(new ClientConfig())
						.target(server).path("api/events/updates")
						.request(APPLICATION_JSON)
						.accept(APPLICATION_JSON)
						.get(Response.class);

				if (res.getStatus() == 204) {
					continue;
				}
				Event e = res.readEntity(Event.class);
				consumer.accept(e);
			}
		});

	}

	/**
	 * Stops long polling thread
	 */
	public void stop() {
		EXEC.shutdownNow();
	}

	/**
	 * Connect to a stomp session with url to websocket
	 * @param url websocket url
	 * @return stomp session
	 */
	private StompSession connect(String url) {
		var client = new StandardWebSocketClient();
		var stomp = new WebSocketStompClient(client);
		stomp.setMessageConverter(new MappingJackson2MessageConverter());
		try {
			return stomp.connect(url, new StompSessionHandlerAdapter() {}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		throw new IllegalStateException();
	}

	/**
	 * Subscribe to topic
	 * @param dest url to topic endpoint
	 * @param consumer consumer
	 */
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

	/**
	 * Use this to add an event to the database using websockets
	 * @param dest destination of the app endpoint
	 * @param e event to be added
	 */
	public void sendEvent(String dest, Event e) {
		session.send(dest, e);
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
	 * Add an event to the database
	 * @param event The event to be added
	 * @return The added event
	 */
	public Event addEvent(Event event) {
		String url = server + "api/events";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Event> requestEntity = new HttpEntity<>(event, headers);

		ResponseEntity<Event> responseEntity =
				restTemplate.postForEntity(url, requestEntity, Event.class);

		if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
			return responseEntity.getBody();
		} else {
			throw new RuntimeException("Failed to add event. Status code: "
					+ responseEntity.getStatusCodeValue());
		}
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
	 * Add an expense to an event
	 * @param eventId id of the event to add the expense to
	 * @param expense expense to be added
	 * @return Expense expense
	 */
	public Expense addExpenseToEvent(long eventId, Expense expense) {
		String url = server + "api/events/" + eventId + "/expenses";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Expense> requestEntity = new HttpEntity<>(expense, headers);

		return restTemplate.postForObject(url, requestEntity, Expense.class);
	}

	/**
	 * Get participant by nickname
	 * @param eventID id of the event the participant is a part of
	 * @param nickname nickname
	 * @return participant
	 */
	public Participant getParticipantByNickname(Long eventID, String nickname) {
		List<Participant> list = getParticipants(eventID);
		List<String> names = list.stream().map(x -> x.getNickname()).toList();
		int index = names.indexOf(nickname);
		if(index == -1) {
			throw new BadRequestException
			("there is no participant with nickname " + nickname + " in this event!");
		}
		else {
			return list.get(index);
		}
		
		

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
	 * Adds a tag to an event.
	 *
	 * @param eventId     The ID of the event to which the tag will be added.
	 * @param tag The tag to be added.
	 * @return The added tag.
	 */
	public Tag addTag(long eventId, Tag tag) {
		String url = server + "api/events/" + eventId + "/tags";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Tag> requestEntity = new HttpEntity<>(tag, headers);
		Tag tagCheck = restTemplate.postForObject(url, requestEntity, Tag.class);
		System.out.println(tagCheck);
		return tagCheck;
	}

	/**
	  * Update the tag in the DB.
	  *
	  * @param updatedTag the tag
	  * @param eventId current event
	  */
	  public void updateTag(Tag updatedTag, long eventId) {
		ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId + "/tags/" + updatedTag.getId())
				.request()
				.put(Entity.entity(updatedTag, APPLICATION_JSON));
	}

	/**
	 * Deletes a participant from an event.
	 *
	 * @param eventId     The ID of the event from which the participant will be deleted.
	 * @param tagId 	  The tag to be deleted.
	 */
	public void deleteTag(long eventId, long tagId) {
		ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId +
						"/tags/" + tagId)
				.request()
				.delete();
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
	 * Update a expense in the database.
	 *
	 * @param eventId     The ID of the event to which the expense belongs.
	 * @param expense The updated expense information.
	 */
	public void updateExpense(long eventId, Expense expense) {
		expense.setEvent(getEvent(eventId));
		ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId +
						"/expenses/" + expense.getId())
				.request()
				.put(Entity.entity(expense, APPLICATION_JSON));
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

	/**
	 * Setter
	 * @param restTemplate restTemplate
	 */
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Deletes an expense.
	 * @param eventId the event id.
	 * @param expense the expense.
	 */
	public void deleteExpense(long eventId, Expense expense) {
		ClientBuilder.newClient(new ClientConfig())
				.target(server)
				.path("api/events/" + eventId +
						"/expenses/" + expense.getId())
				.request()
				.delete();
	}

	/**
	 * Sets the JAX-RS client to be used by this utility.
	 *
	 * @param client the JAX-RS client to be set
	 */
	public void setClient(Client client) {
		this.client = client;
	}
}