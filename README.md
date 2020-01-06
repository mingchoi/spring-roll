# spring-roll
![CoverImage](https://github.com/mingchoi/spring-roll/blob/master/doc/images/spring-roll.png?raw=true)

This is a experimental project of code generate on Spring Cloud. It can generate these component from your Domain Model:
#### Model
  - Data Transfer Object
    - auto conversion between Entity and Dto
    - sanitize sensitive data
#### Presentation Layer
  - Rest Controller
    - CRUD API mapping
  
#### Business Layer
  - Service
    - Generated CRUD operation template
    - Entity access control
    - Owner identity mapping
  
#### Persisten Layer
  - Repository
    - generated query method
    - JPA template
  
#### Security
  - WebSecurity Config
    - BCrypt password protection
    - Flexible access rule
  - JWT Authorization
    - Stateless authorization
  - Login API
    - Generate JWT token
  
# Getting Started

## Prepare your Domain Model
First, ready your domain model in `domain/YourEntity.kt`.
For example:
```@Entity
@Table(name = "user")
class UserEntity(
        @Id
        var id: String? = null,

        @FindBy
        @Column(nullable = false, length = 50, unique = true)
        var username: String? = null,

        @Santized
        @Column(nullable = false, length = 100)
        var password: String? = null,

        @FindBy
        @Column(nullable = false, length = 120, unique = true)
        @Santized
        var email: String? = null,

        @Column(nullable = false)
        var age: Int? = null,

        @Column(nullable = false)
        var vip: Boolean? = null
)
```
Note that @FindBy and @Santized annotations are provided by spring-roll to help generating the code in the way you want.

Currently there are some annotations provided:

|Annotations           |Feature                                                   |
|----------------------|----------------------------------------------------------|
|@FindBy               |Generate a search query and API with this column          |
|@Santized             |Erase sensitive(e.g. password) from the presentation layer|
|@Write(rule = "owner")|Restrict WRITE permission to role (coming soon)           |
|@Read(rule = "all")   |Restrict READ permission to role (coming soon)            |

## Generate the codes
Just run the code below, your Dto, RestContoller, Service and Repository will be all generated!
```
Generator().apply {
    createAll(UserEntity::class)
    createAll(NoteEntity::class)
}
```

## Test the API

Get user list by `GET /user`
```
curl http://localhost:8080/user
>>> [] # Currently have no user in DB
```

Create user by `POST /user`
```
curl -H "Content-Type:application/json" -X POST -d '{
	"username": "dumpling",
	"password": "123456",
	"email": "dimsum@mail.com",
	"age": 24,
	"vip": false
}' http://127.0.0.1:8080/user
>>>{
  "id": "bf3c7072-97eb-4c35-a89b-648ea706c27d",
	"username": "dumpling",
	"age": 24,
	"vip": false
}
```

Get single user with ID by `GET /user/{id}`
```
curl http://localhost:8080/user/bf3c7072-97eb-4c35-a89b-648ea706c27d
>>>{
  "id": "bf3c7072-97eb-4c35-a89b-648ea706c27d",
	"username": "dumpling",
	"age": 24,
	"vip": false
}
```
Update user by `PUT /user` (Format is similar to POST)

Delete user by `DELETE /user` (Format is similar to POST)

## Adding Security
Run the code below to generate security config and filters
```
Generator().apply {
    createSecurity(analyze(UserEntity::class), true)
}
```

Login by `POST /login`
```
curl -H "Content-Type:application/json" -X POST -d '{
	"username": "dumpling",
	"password": "123456",
}' http://127.0.0.1:8080/login
>>>{
  "user": {
    "id": "bf3c7072-97eb-4c35-a89b-648ea706c27d",
	  "username": "dumpling",
	  "age": 24,
	  "vip": false
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjoiYmYzYzcwNzItOTdlYi00YzM1LWE4OWItNjQ4ZWE3MDZjMjdkIn0.pKwqRMKA_pDRlD4_wGKd3WhFZZp8Y4wl6ItZiBh1qr0"
}
```

With default security config, only register and login API are open to public. Regular CRUD endpoint must 


## Future
There are few feature planned to implenment in future
- Entity access control
- Subscription for Real-time update
- CLI interactive Setup 
- Admin UI
- Spring Cloud support
