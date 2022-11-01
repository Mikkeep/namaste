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
    "r_8": {
        "name": "VIP Lounge",
        "description": "Restaurant reserved for high profile customers.",
        "icon": "vip_lounge",
    },
}

items = {
    "i_1": {
        "name": "Burger",
        "res_id": "1",
    },
    "i_1.2": {
        "name": "Khay-burger",
        "res_id": "1",
    },
    "i_2": {
        "name": "Apple",
        "res_id": "2",
    },
    "i_3": {
        "name": "Pie",
        "res_id": "3",
    },
    "i_4": {
        "name": "Burger",
        "res_id": "4",
    },
    "i_5": {
        "name": "HazBurger",
        "res_id": "5",
    },
    "i_6": {
        "name": "Subway sandwich", 
        "res_id": "6"},
    "i_7": {
        "name": "Meatballs",
        "res_id": "7",
    },
    "i_8": {
        "name": "Glenfres_iddich",
        "res_id": "8",
    },
}
