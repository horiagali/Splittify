# OOPP Project: Splittify
Made by Amanda Andree, Horia Găliţianu, Martijn van Leest, Iulia Slănină, Fayaz Wagid Hosain and Mihnea Nedelcu.

Welcome to Splittify! Here are a few things to note when using this project:

**Executing the project** <br>
- 
You can run the client at any time by executing "command line name" in the console. When starting the client, it looks for the serverURL that is configured in the config file of the client. If it cannot find said server, it will show a scene to connect to a running server.

You can run the server by executing "command line name" in the console. This will start a server at localhost:8080.
When this url is set as the serverURL in the config file of the client, the client will connect to this server.

**All the features, so you do not miss them** <br>
-
- You can switch between Spittify servers by clicking on the disconnect button in the mainPage of the app. Entering a serverURL of a server that is active will allow you to connect to the corresponding server.
- You can edit or remove expenses and participants by clicking on them in the Event Overview.
- The sum of all expenses can be found in the Statistics tab. The share per person can also be found here. How much each person owes can be found in Balances.
- You can delete events as an Admin by right-clicking on an event.
- Giving money from Participant A to B can be done in the balances tab.
- On the right of the event overview, Filters can be found to filter for certain owers, payers or tags.
- If you want payment instructions, an event has to be closed first. This can be done in the balances tab and makes the event not accept any more changes to expenses.
- An overview of tags, adding, updating and deleting them can all be found through the 'Manage tags' button.
    - Here, the share per person can be seen by a switch on top of the screen which will change the pieChart accordingly.
- If email credentails are absent or not valid, all email functions are grayed out.
- When inviting participants through email, they automatically get added as participant with their email as username.

**Implemented Rubric-features and where to find them**
- 
- For HCI, the app is developed with the following aspects in mind:
    - Color contrast
    - Keyboard shortcuts, namely:
        - (insert keyboard shortcuts here)
    - Undo actions for updating expenses
    - Error messages
    - Informative feedback
    - Confirmation for Key Actions
        - Influential ones that adjust a lot in the DB: deleting participants or tags, for example.
- Long polling in (location long polling)
- Web sockets in (location web sockets)
- updating after every second in all other parts of the app.


We hope you have fun using our app.