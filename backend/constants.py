"""
Constants for application, to be used globally
"""

DB_NAME = "development.db"
DB_LOCATION = "instance/development.db"
JSON = "application/json"
HIDDEN_RESTAURANT = "VIP Lounge"

users = {
    "u_1": {
        "name": "Admin",
        "password": "supersecurepassword123456",
        "is_admin": True,
    },
    "u_2": {
        "name": "Bob",
        "password": "password",
        "is_admin": False,
    },
}

restaurants = {
    "r_1": {
        "name": "Bob's Burgers",
        "description": "Best blocky burgers by big burger builder Bob!",
        "icon": "bobs_burgers",
    },
    "r_2": {
        "name": "Alice's Apples",
        "description": "My apples bring all boys to the yard",
        "icon": "alice_apple",
    },
    "r_3": {
        "name": "Peter's Pies",
        "description": "Mmm...pies",
        "icon": "peter_pie",
    },
    "r_4": {
        "name": "DjRonald's",
        "description": "Who's McDonald?",
        "icon": "djronald",
    },
    "r_5": {
        "name": "HazBurger",
        "description": "Can I haz cheezburger?",
        "icon": "hasburger",
    },
    "r_6": {"name": "Segway", "description": "Eat fast", "icon": "segway"},
    "r_7": {
        "name": "Taco Ball",
        "description": "You can't resist our balls",
        "icon": "taco_ball",
    },
}
