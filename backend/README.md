## Backend documentation

### Environment setup

Run these commands from the directory root folder.

**Export Flask environment variable**

```shell
$ export FLASK_APP=backend
```

**Initialize database**

```shell
$ flask --app backend init-db
```

**Fill database with test data**

```shell
$ flask --app backend testgen
```

**Run the Flask server**

```shell
$ flask run
```

### Installation

**For Linux/MacOS**

```shell
$ source backend/env/bin/activate
```

```shell
$ pip install -r backend/requirements.txt
```

---

**For Windows**

```shell
C:\> backend\env\Scripts\activate.bat
```

```shell
pip install -r backend\requirements.txt
```
