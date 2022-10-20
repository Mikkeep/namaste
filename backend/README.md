## Backend documentation

### Environment setup

Run these commands from the directory root folder.

**Export Flask environment variable**
Linux
```shell
$ export FLASK_APP=backend
```
Windows
```shell
  set FLASK_APP=backend
```
**Initialize database**

```shell
$ flask --app backend init-db
```

**Fill database with test data(NOTE: if you have done this once it will result in UNIQUE constraint failed error)**

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

***

### FAQ

<br>

**Flask --app produces error**

Flask command `flask --app <option>` produces following error:

```shell
Error: No such option: --app
```

> Make sure you have activated virtual environment as stated in the Installation guide.

<br>

**Flask run does not work**

Starting Flask application does not work with command:
`flask run` producing error:

```shell
Error: Could not import 'backend.backend'
```

> Navigate to git root folder and try the command again. The command does not work from the backend folder as it can not import itself from inside the folder.
