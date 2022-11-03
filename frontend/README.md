## Frontend documentation

## Classes and their functionalities

### LoginActivity
```
  This is the first activity that the app starts on. User has two buttons; "Login" creates a 
  post request to the backend /api/users/login with the login details with the
  inputted username and password. If the response code is equal to 200 the current activity
  changes to MainActivity. The response contains the sessionId that is later used for GET
  and POST request that require it.
  If the code isn't 200 it stays on the current activity and shows the user an error msg.
  "Register" button changes the current activity to RegisterActivity.
```

### RegisterActivity
```
  "Register" button sends a post request to the backend /api/users/register/ if it returns code
  200 it means that the post request was successful and the activity changes back to LoginActivity
  else it shows the user an error msg.
  "Cancel" button simply changes the activity back to LoginActivity.
```

### MainActivity
```
  Gets the sessionId from the headers that LoginActivity sends via the POST request and sends
  a GET request to /api/restaurants/all/ .
  The GET request returns restaurant names, -descriptions and -items. These
  are then used to create corresponding restaurant buttons.

  Clicking on one of the buttons sends the corresponding restaurants item informations and
  changes the current activity to RestaurantActivity.
  
  Sidepanel has options "Order", "About", "LogOut", "Dark mode", "Account", each of which
  will change the current activity to the corresponding <name>Activity.
```

### RestaurantActivity
```
  Gets the restaurant items information from MainActivity and uses these to create buttons
  for each item that the restaurant has.

  Clicking on one of the buttons sends a POST request to /api/restaurants/order/.
```

### OkHttpPostRequest
```
  Sends the POST requests depending on which activity calls it.
  In case the activity that calls it is LoginActivity it sends a
  JSON in format.
  {
    "username": <user_input>,
    "password: <user_input>
  }
  If it is MainActivity it is instead:
  {
    "rest_id": <restaurant_id>,
    "item_id": <item_id>,
    "amount": <amount>,
    "description": <location>
  }
  In addition to these it will also send a sessionId.
```

### OkHttpGetRequest
```
  Currently only used for getting restaurant information from the backend. Requires
  the current sessionId that is provided with successful Login.

  The response contains:
    -Restaurant names
    -Restaurant descriptions
    -Restaurant products
```