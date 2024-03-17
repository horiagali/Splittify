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
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

	private static final String SERVER = "http://localhost:8080/";
	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;

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
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void getQuotesTheHardWay() throws IOException, URISyntaxException {
		var url = new URI("http://localhost:8080/api/quotes").toURL();
		var is = url.openConnection().getInputStream();
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
				.target(SERVER).path("api/quotes") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
	}

	/**
	 * 
	 * @return list of events
	 */
	public List<Event> getEvents() {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/events") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.get(new GenericType<List<Event>>() {});
	}

	/**
	 * 
	 * @param quote quote
	 * @return quote that is added
	 */
	public Quote addQuote(Quote quote) {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/quotes") //
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
				.target(SERVER).path("api/events")
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
				.target(SERVER)
				.path("api/events/" + eventId + "/participants/nicknames")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<List<String>>() {});
	}

	/**
	 *
	 * @param selectedEvent
	 *   deletes an event from the database
	 */
	public void deleteEvent(Event selectedEvent) {
		ClientBuilder.newClient(new ClientConfig())
				.target(SERVER)
				.path("api/events/" + selectedEvent.getId())
				.request()
				.delete();
	}

	/**
	 * Add an expense to an event
	 * @param eventId id of the event to add the expense to
	 * @param expense expense to be added
	 */
	public void addExpenseToEvent(long eventId, Expense expense) {
		// Construct the URL for the specific event's expenses endpoint
		String url = String.format("%s/events/%d/expenses", SERVER, eventId);

		// Serialize the expense object to JSON string
		String expenseJson = serializeExpenseToJson(expense);

		// Create HttpClient
		HttpClient client = HttpClient.newHttpClient();

		// Create HttpRequest with POST method and JSON body
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(expenseJson))
				.build();

		try {
			// Send the request and receive the response
			HttpResponse<String> response = client.send(request,
					HttpResponse.BodyHandlers.ofString());

			// Check the response status code
			if (response.statusCode() == 200) {
				System.out.println("Expense added successfully!");
			} else {
				System.out.println("Failed to add expense. " +
						"Status code: " + response.statusCode());
				// Print error response if needed: response.body()
			}
		} catch (Exception e) {
			System.err.println("An error occurred: " + e.getMessage());
		}
	}


	private String serializeExpenseToJson(Expense expense) {
		try {
			// Use Jackson ObjectMapper to serialize Expense object to JSON string
			return objectMapper.writeValueAsString(expense);
		} catch (Exception e) {
			System.err.println("Error serializing " +
					"Expense object to JSON: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Get participant by nickname
	 * @param nickname nickname
	 * @return participant
	 */
	public Participant getParticipantByNickname(Long eventID, String nickname) {
		String url = SERVER + "api/events/" + eventID + "/participants/" + nickname;

		// Make the HTTP GET request and directly retrieve the participant
		Participant participant = restTemplate.getForObject(url, Participant.class);

		return participant;
	}
}