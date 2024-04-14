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
        - For AddEvent/EditEvent:   
          - Escape Key (KeyCode.ESCAPE):
            Triggers the cancel button. This action could be used to cancel creating a new event 
          - Control + N (KeyCode.N):
            Triggers the addEvent action. This action lets you create a new event.
          - Control + L (CTRL+L): Pressing CTRL + L displays the language menu, allowing users to switch the application's language.
          - Control + M (CTRL+M): Using CTRL + M displays the currency menu, enabling users to select a different currency.
        - For AddExpenses/EditExpenses/addPartialDebt/editPartialDebt: 
          - Escape Key (ESC): Pressing ESC navigates back to the overview screen (mainCtrl.goToOverview()). 
          - Control + E (Ctrl + E): Pressing Ctrl + E triggers the addition of a new expense 
          - Control + L (CTRL+L): Pressing CTRL + L displays the language menu, allowing users to switch the application's language.
          - Control + M (CTRL+M): Using CTRL + M displays the currency menu, enabling users to select a different currency.
          - Arrow Keys (UP and DOWN): The currency selection dropdown responds to UP and DOWN arrow key presses. This feature allows users to navigate through currency options without using the mouse.
        - For Adminpage: 
          - Escape Key (ESC): Pressing ESC key navigates back to the previous screen. 
          - Control + D (Ctrl + D): Holding down Ctrl key and pressing D triggers the deletion of the currently selected event (if any).
        - For AdminPass:
          - Escape Key (ESC): Pressing ESC key navigates back to the previous screen.
          - Enter Key (ENTER): Pressing ENTER key triggers an attempting to fill in the password.
        - For Balances:
          - Escape Key (ESC): Pressing ESC key triggers the back() method, navigating the user back to the previous screen.
          - Control + R (CTRL+R): Holding CTRL key and pressing R refreshes the current page.
          - Control + P (CTRL+P): Using CTRL + P takes you to add a partial debt.
          - Control + L (CTRL+L): Pressing CTRL + L displays the language menu, allowing users to switch the application's language.
          - Control + M (CTRL+M): Using CTRL + M displays the currency menu, enabling users to select a different currency.
          - Control + D (CTRL+D): Pressing CTRL + D initiates the process to settle debts.
        - For addParticipant/ContactDetails: 
          - Escape Key (ESC): Pressing ESC key triggers the back() method, navigating the user back to the previous screen.
          - Control + P (CTRL+P): Holding CTRL key and pressing P lets you add your participant.
          - Control + L (CTRL+L): Pressing CTRL + L displays the language menu, allowing users to switch the application's language.
          - Control + M (CTRL+M): Using CTRL + M displays the currency menu, enabling users to select a different currency.
        - For Invite:
          - Escape Key (ESC): Pressing ESC navigates the user back to the previous screen.
          - Alt + C (ALT+Q): Using ALT + C performs an action to copy an invite code.
          - Control + N (CTRL+N): Pressing CTRL + N triggers an action to add an email.
          - Control + S (CTRL+S): Using CTRL + S initiates an action to send invitations by email.
          - Control + L (CTRL+L): Pressing CTRL + L displays the language menu, allowing users to switch the application's language.
          - Control + M (CTRL+M): Using CTRL + M displays the currency menu, enabling users to select a different currency.
        - For the MainPage:
          - Control + D (CTRL+D): Mimics pressing the disconnect button.
          - Control + L (CTRL+L): Pressing CTRL + L displays the language menu, allowing users to switch the application's language.
          - Control + M (CTRL+M): Using CTRL + M displays the currency menu, enabling users to select a different currency. 
          - Control + R (CTRL+R): Using CTRL + R triggers an action to refresh the page. 
        - For the overview page:
          - Escape Key (ESC): Pressing ESC key triggers the back() method, navigating the user back to the previous screen.
          - Control + L (CTRL+L): Pressing CTRL + L displays the language menu, allowing users to switch the application's language.
          - Control + M (CTRL+M): Using CTRL + M displays the currency menu, enabling users to select a different currency.
          - Control + E (CTRL+E): Using CTRL + E takes you to the addExpenses page. 
          - Control + S (CTRL+S): Takes you to the invite page. 
          - Control + B (CTRL+B): Takes you to the balances page. 
          - Control + D (CTRL+D): Triggers the action to delete an event, you first get a confirmation pop-up.
          - Alt + E (ALT+E): Prompts the user to pick the number (from the top) of the expense they want to edit. 
          - Alt + P (ALT+P): Prompts the user to pick the number (from the top) of the participant they want to edit.
          - Control + R (CTRL+R): Refreshes the current page. 
          - Control + P (CTRL+P): Navigates the user to the addParticipant page. 
          - Alt + S (ALT+S): Navigates the user to the statistics page. 
          - Alt + T (ALT+T): Navigates the user to the tagOverview page. 
      - For Statistics:
          - Escape Key (KeyCode.ESCAPE):
            Triggers the cancel button. This action could be used to cancel creating a new event
          - Control + R (KeyCode.N): Refreshes the current page. 
          - Control + L (CTRL+L): Pressing CTRL + L displays the language menu, allowing users to switch the application's language.
          - Control + M (CTRL+M): Using CTRL + M displays the currency menu, enabling users to select a different currency.
      - For TagOverview:
          - Escape Key (KeyCode.ESCAPE):
            Triggers the cancel button. This action could be used to cancel creating a new event
          - Control + R (KeyCode.N): Refreshes the current page.
          - Control + L (CTRL+L): Pressing CTRL + L displays the language menu, allowing users to switch the application's language.
          - Control + M (CTRL+M): Using CTRL + M displays the currency menu, enabling users to select a different currency.
          - Control + N (CTRL+N): Lets you add a new Tag. 
  - Undo actions for updating expenses
  - Error messages
  - Informative feedback
  - Confirmation for Key Actions
      - Influential ones that adjust a lot in the DB: deleting participants or tags, for example.
- Long polling in the admin page
- Web sockets in the main overview, event overview, tag overview, balance overview, statistics page and settling debts page.
- updating after every second in all other parts of the app.


We hope you have fun using our app.
