# spring-demo

* Application will be launched at localhost:8080
* Access h2 console by visiting localhost:8080/h2/
  * h2 properties are defined in the application.properties file.
* Basic JWT token Generation implemented.
  * Provide correct username, password to /login to generate JWT token.
  * the given user must be present in the database to generate token.
* Implemented Role based authorization to endpoints.
  * Refer to BookController class to see which roles are authorized for a particular endpoint.
  * All endpoints require authentication except /login endpoint.
* A single user can have multiple roles. Users table is auto initialized with 3 usernames upon running this application.
  * Roles: admin, user
  * check UserDatabaseInitializer class for usernames and passwords for generating valid token from /login endpoint.
* In case the SECRET is not provided, refer to SecretKeyProvider class and its implementation
  * The key generated will be same throughout application's lifetime. So that the JWT tokens can be consistent.
  * Permanent SECRET key is still better because the generated key will generate a new SECRET everytime the application is re-launched.