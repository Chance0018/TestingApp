# The REST Listicator

- Web Server based app which responds to REST HTTP Requests
- Features
   - List management
   - User management
   - Feature-Toggles to configure the app

## Install and Running

[download the jar](http://compendiumdev.co.uk/downloads/apps/restlisticator/v1/rest-list-system.jar)


- run by typing:
    - `java -jar <insertnameofjarfilehere>.jar`
    - e.g. if you downloaded `restlisticator.jar` then it would be `java -jar restlisticator.jar`

If you double click it then it will be running in the background on port 4567 - you might have to use task manager to kill the Java VM that it is running on to exit.

Three users are created by default with different permissions: `superadmin`, `admin`, `user` - all have the default password set to `password`

Command Line Arguments:

- `-port=1234` set the port to supplied integer (defaults to 4567)
- `-bugfixes=false` (defaults to true)

## End Points Summary


- `/heartbeat` - is the server alive?
- `/lists` - manage the List entities - create, amend lists
- `/lists/{guid}` - create, amend, delete a List
- `/users` - user management - create, delete
- `/users/{username}/password` - amend a User's password
- `/users/{username}/apikey` - amend a User's api key
- `/feature-toggles` - `superadmin` can toggle app features on and off

The end points may be nested in a sub path e.g. `/listicator/heartbeat`

Check with your system admin to find out how the application has been configured.


---

## End Points

### Heartbeat

- `/heartbeat`
    - `GET` - return 200 to indicate server is running

~~~~~~~~
curl -i -X GET http://localhost:4567/heartbeat
~~~~~~~~

~~~~~~~~
curl -v -X GET http://localhost:4567/heartbeat
~~~~~~~~

---

### Lists

- `/lists`
    - `GET`
        - return all the lists in the system
    - `POST`
        - requires authorized and authenticated user
        - create a list with partial payload e.g. `{title:'my title'}`
    - `PUT`
        - requires authorized and authenticated user
        - create a list with full payload i.e. `title`, `description`, `guid`, `createdDate`, `amendedDate`

---

#### Lists Examples

_Note: continuation character on mac is `\` and on Windows it is `^`_

~~~~~~~~
curl -X GET ^
  http://localhost:4567/lists ^
  -H "accept: application/json"
~~~~~~~~

'GET /lists'

`Accept: application/json`

~~~~~~~~
{
    "lists": [
        {
            "guid": "d4625287-989a-4454-b01a-cb99545a87a6",
            "title": "title",
            "description": "",
            "createdDate": "2017-07-19-15-53-14",
            "amendedDate": "2017-07-19-15-53-14"
        }
    ]
}
~~~~~~~~


~~~~~~~~
curl -X GET ^
  http://localhost:4567/lists ^
  -H "accept: application/xml"
~~~~~~~~

`Accept: application/xml`

~~~~~~~~
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<lists>
    <list>
        <guid>d4625287-989a-4454-b01a-cb99545a87a6</guid>
        <title>title</title>
        <description></description>
        <createdDate>2017-07-19-15-53-14</createdDate>
        <amendedDate>2017-07-19-15-53-14</amendedDate>
    </list>
</lists>
~~~~~~~~

---

### List

- /lists/{guid}
    - `GET` - return the details of a particular list
        - can filter list with `?title=exactTitle`
        - can filter list with `?title="partialTitle"`
    - `PUT` - amend a list or create a new list when supplied with full details of the list
    - `POST` - amend details of a list
        - user must be authenticated
        - only user with appropriate permission can amend a list
            - admin can amend any, user can ammend unowned, and lists they own
        - resource will be created if it does not exist
    - `PATCH` - partial amend of a list
        - user must be authenticated
        - only user with appropriate permission can delete a list
        - resource must exist
    - `DELETE` - delete a list
        - user must be authenticated
        - only user with appropriate permission can delete a list
            - admin can delete any list
            - users can delete their own, or unowned, or admin created lists
- /lists/{guid}?without={fieldlist}
    - `GET` - return the details of a particular list but without the fields in the comma separated `fieldlist` e.g. `guid,title`

---

#### List Examples


~~~~~~~~
curl -X GET \
  http://localhost:4567/lists/d4625287-989a-4454-b01a-cb99545a87a6 \
  -H 'accept: application/json'
~~~~~~~~

GET /lists/{guid}

`Accept: application/json`

~~~~~~~~
{
    "guid": "d4625287-989a-4454-b01a-cb99545a87a6",
    "title": "title",
    "description": "",
    "createdDate": "2017-07-19-15-53-14",
    "amendedDate": "2017-07-19-15-53-14"
}
~~~~~~~~


~~~~~~~~
curl -X GET ^
  http://localhost:4567/lists/d4625287-989a-4454-b01a-cb99545a87a6 ^
  -H 'accept: application/xml'
~~~~~~~~

`Accept: application/xml`

~~~~~~~~
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<list>
    <guid>d4625287-989a-4454-b01a-cb99545a87a6</guid>
    <title>title</title>
    <description></description>
    <createdDate>2017-07-19-15-53-14</createdDate>
    <amendedDate>2017-07-19-15-53-14</amendedDate>
</list>
~~~~~~~~

---

PATCH /lists/{guid}

{
    "guid": "d4625287-989a-4454-b01a-cb99545a87a6",
    "title": "title2",
}

_currently does not comply with https://tools.ietf.org/html/rfc7396 _

---

### Users

- `/users`
    - `GET` - return usernames of all users
        - can be filtered with `?username=exactmatch` e.g. `?username=admin`
        - can be filtered with `?username="partialmatch"` e.g. `?username="adm"`

~~~~~~~~
curl -X GET http://localhost:4567/users
~~~~~~~~

- `/users`
     - `POST` - creat a user with `username` and `password`
         - calling user must be authenticated
         - calling user must be admin or have CREATE_USER permissions
         - username must be minimum of 6 chars
         - password must be minimum of 6 chars
         - 201 returned on success
         - 409 returned if user already exists

~~~~~~~~
curl -X POST \
  http://localhost:4567/users \
  -H 'accept: application/json' \
  -H 'authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
  -H 'content-type: application/json' \
  -d '{username:"username", password:"password"}'
~~~~~~~~

---

### User

- `/users/{username}`
    - `GET` - return details of a user
        - user must be authenticated
        - user can only get their own details, not those of anyone else
        - admin users can get details of any user

~~~~~~~~
curl -X GET http://localhost:4567/users/superadmin ^
 -H "authorization: Basic YWRtaW46cGFzc3dvcmQ="
~~~~~~~~

- `/users/{username}/password`
    - `PUT` - set the password
        - user must be authenticated
        - user can only set their own details, not those of anyone else
        - admin users can set details of any user
        - password must be 6 characters minimum
        - payload is a partial user object containing the password
        - payload type must be set in the content-type header or assumes JSON
        - returns 204 No Content if successful



`PUT /users/{username}/password`

XML:

~~~~~~~~
<user><password>newPassword</password></user>
~~~~~~~~

JSON:

~~~~~~~~
{password:'newPassword'}
~~~~~~~~


- `/users/{username}/apikey`
    - `PUT` - set the API AuthKey
        - user must be authenticated
        - user can only set their own details, not those of anyone else
        - admin users can set details of any user
        - apikey must be 10 characters minimum
        - payload is a partial user object containing the password
        - payload type must be set in the content-type header or assumes JSON
        - returns 204 No Content if successful


`PUT /users/{username}/apikey`

XML:

~~~~~~~~
<user><apikey>newApiKeyIsThisYes</apikey></user>
~~~~~~~~

JSON:

~~~~~~~~
{apikey:'newApiKeyIsThisYes'}
~~~~~~~~

- `/feature-toggles`
    - `GET` - return status of all feature toggles
        - user must be authenticated
    - `POST` - amend a list of feature toggles
        - user must be authenticated
        - only user with appropriate permission can set feature toggles

---

## Payload representation

- `Content-Type`
    - Use the `Content-Type` header to specify the format of the payload as either XML or JSON
    - A value of `application/json` means the payload is represented as JSON
    - A value of `application/xml` means the payload is represented as XML
    - A `400` error will be returned if the payload can not be converted into the appropriate representation
    - If no `Content-Type` is supplied then the  application assumes a JSON representation.

- `Accept`
    - Use the `Accept` header to specify the format of the desired response
    - A value of `application/json` means you want to receive JSON
    - A value of `application/xml` means you want to receive XML


##  Authentication

- Some requests require an authenticated user.
- The system accepts Simple Basic Authentication
    - an `Authentication` header with the value `Basic` followed by the `username:password` base 64 encoded
- The system also accepts a custom header `X-API-AUTH` with a value of the `api key` defined for each user. the `api key` is visible to the user when they make a `GET` request for their user details


## Verbs

- The system accepts the `X-HTTP-Method-Override` header to allow clients that don't support verbs such as `PATCH`

## General

- `OPTIONS` can be used on all endpoints to receive the `Allow` header with the allowed verbs.
- `404` is returned if the end point is not recognised
- `405` is returned if a method which is not mentioned in `Allow` is used on an an endpoint
- `500` is returned in the event of an unhandled condition in the server - this should never occur



## Known Bugs

The system has been coded with some known bugs - these are all fixed by default. If you would like to test your testing skills then start with `-bugfixes=false` to have the known bugs present in the application.

See if you can find them.

`java -jar rest-list-system.jar -bugfixes=false`