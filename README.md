# How to Play at work

## What is Play Framework
Play is an open-source modern web framework for writing scalable web applications in Java and Scala. Play is based on a lightweight, stateless, web-friendly architecture. Built on Akka, Play provides predictable and minimal resource consumption (CPU, memory, threads) for highly-scalable applications. It is developer friendly, write code, hit refresh key and see changes. Has powerful console and build tools, has great support for Eclipse and Intellij IDEA. Play was built for needs of modern web and mobile apps, provides RESTful by default, has a built-in JSON module and extensive NoSQL & Big Data Support. It is solid, fast and the code is compiled and runs on JVM. The ecosystem around Java is huge. There are libraries for everything - most of which can be used in Play. "Play Framework is the best".

## Play Framework System Requirements
Below are the minimum system specification to develop an application in Play Framework for Windows, Mac and Linux.

**Note:** A list with more tools to **play** with you will find [here]().

1. Install [JDK 1.6 or later](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
  * If you are using MacOS, Java is built-in.
  * If you are using Linux, make sure to use either the Sun JDK or OpenJDK.
  * If you are using Windows, just download and install the latest JDK package.
2. Install [Scala](http://www.scala-lang.org/download/install.html) is a general-purpose language designed to support clear, concise and type-safe programming as well as to scale from the smallest applications to the biggest. (Optional but good to have, you may also use [Java](https://java.com/en/download/)).
3. Install [SBT](http://www.scala-sbt.org/release/tutorial/) is an open source build tool for Scala and Java projects, similar to Java's Maven or Ant. (Optional but good to have).
4. Install [Play Framework](https://www.playframework.com/documentation/2.3.x/Installing) through a tool called called [Typesafe Activator](http://typesafe.com/community/core-tools/activator-and-sbt ).
5. Install [PostgreSQL](https://wiki.postgresql.org/wiki/Detailed_installation_guides).
6. Install [pgAdmin](http://www.pgadmin.org/download/) is a comprehensive PostgreSQL database design and management system (Windows and Mac, pgAdmin is included by default in most Linux distributions, such as RedHat, Fedora, Debian and Ubuntu).

## Game Play
Here is a [Github project source]() where you can find all the work that will be explained next and some aditional examples.

Let's create a new vanilla Play Scala application and name it **scala-game**. Here is a the command you need to run in command-line:
```ch
activator new scala-game play-scala
```

To start your game from command-line run from your project root folder:
```ch
activator run
```
or (recommanded)
```ch
sbt run
```
To view your work go to:
```ch
http://localhost:9000/
```
Hit refresh button in your browser page every time you made changes and want to see them.

## Game Configuration

To add more experience to your **Play** skils add the following dependencies to your **build.sbt** file:
```sbt
"com.typesafe.play" %% "play-slick" % "0.8.1",
"org.postgresql" % "postgresql" % "9.2-1002-jdbc4"
```

* [Slick](http://slick.typesafe.com/) is a modern database query and access library for Scala. It allows you to work with stored data almost as if you were using Scala collections while at the same time giving you full control over when a database access happens and which data is transferred. You can write your database queries in Scala instead of SQL, thus profiting from the static checking, compile-time safety and compositionality of Scala. 
* [PostgreSQL JDBC](https://jdbc.postgresql.org/) to connect to PostgreSQL database

Now if you have this dependencies. Create a database and add it's credidentials to **application.config** file. Add a package named **dao** to your app folder, where we will define database tables and set it in configuration file to tell Slick where the table mappings are, also uncomment **evolutionplugin** and set it enabled so that [Play Framework's Evolutions](https://www.playframework.com/documentation/2.0/Evolutions) will create tables and relations between them (primary keys, indexes, sequences etc...) for you.

```conf
db.default.driver=org.postgresql.Driver
db.default.url="jdbc:postgresql://localhost:5432/scalagame"
db.default.user=postgres
db.default.password="password"

# Evolutions
# ~~~~~
# You can disable evolutions if needed
slick.default="dao.*"
evolutionplugin=enabled
```

To make the application to suits our needs we have to specify to whici database to connect (a Play application can define muliple databases, we have just one named **default**, but we can define other databases as well ex: db.history where we can store player's history, etc ...), to do that we need to create a trait named **PostgresSupport** and define an implicit database session so won't need to set it out for every request to database:

```scala
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._

trait PostgresSupport {
  trait PostgresSupport {
    implicit lazy val session: Session = play.api.db.slick.DB("default").createSession()
  }
}
```

## Loading Textures

Create a package models and in it a case class named Player. [complete models](https://github.com/marius-carp/scala-game/tree/master/app/models)

```scala
case class Player (
  idPlayer: Option[Int],
  name: String,
  level: Int
)
```

Having our modeles we just need to map them to tabels. As you see in the model `idPlayer` is optional because we will let `PostgreSQL` to generate a `PrimaryKey` for the player using `AutoInc`. [complete tables](https://github.com/marius-carp/scala-game/tree/master/app/dao)

```scala
class PlayerTable(tag: Tag) extends Table[Player](tag, "players") {
  def idPlayer = column[Int]("id_player", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def level = column[Int]("level", O.NotNull)

  def * = (idPlayer.?, name, level) <> (Player.tupled, Player.unapply _)
}
```

Play tracks your database evolutions using several evolutions script. These scripts are written in plain old SQL and should be located in the db/evolutions directory of your application.

The first script is named [1.sql](https://github.com/marius-carp/scala-game/blob/master/conf/evolutions/default/1.sql)

If evolutions are activated, Play will check your database schema state before each request in DEV mode, or before starting the application in PROD mode. In DEV mode, if your database schema is not up to date, an error page will suggest that you to synchronize your database schema by running the appropriate SQL script.


When a user first time accessing our appliction on localhost `"localhost/"` a GET HTTP request will be made to this rote `"/"`. The entire list of routes can be found in the file `conf/routes`, is the configuration file used by the router. This file lists all of the routes needed by the application. Each route consists of an HTTP method and URI pattern, both associated with a call to an Action generator.

```
# Home page
GET         /                      controllers.Application.index
# Insert single player
POST        /player/single         controllers.Application.addSinglePlayer
# Get Players Json Format
GET         /player/multi          controllers.Application.findAll
# Insert multi player Json format
POST        /player/multi          controllers.Application.addMultiPlayer
# Map static resources from the /public folder to the /assets URL path
GET         /assets/*file          controllers.Assets.at(path="/public", file)
```

Most of the requests received by a Play application are handled by an Action. An Action is basically a request => result function that handles a request and generates a result to be sent to the client. As you can see every route in our `routes` file is accessing a method of an object `Application` aka `Controller` in `controllers` package. A `Controller` is nothing more than a singleton object that generates `Action` values.

## Game modes
You can chose to Play in two modes: **Single player** using **Play Scala template** or **Multi player** using one of the follwing JavaScript frameworks: [AngularJS](https://angularjs.org/), [BackboneJS](http://backbonejs.org/), [EmberJS](http://emberjs.com/), [ExtJS](http://www.sencha.com/products/extjs/), [DustJS](http://akdubya.github.io/dustjs/) or other JavaScript frameworks used to develop web applications.

Adding a new player and get players list using [Slick](http://slick.typesafe.com/).

* players list (`players` is the mapped table)
```scala
  def findAll = players.list
```
* add new player
```scala
  def savePlayer(player: Player): Player = {
    player.idPlayer match {
      case None => {
        val id = (players returning players.map(_.idPlayer)) += player
        player.copy(idPlayer = Some(id))
      }
      case Some(id) => {
        val query = for {
          c <- players if c.idPlayer === id
        } yield c
        query.update(player)
        player
      }
    }
  }
```

### Single Player
A **Play Scala template** is a simple text file, that contains small blocks of Scala code. They can generate any text-based format, such as HTML, XML or CSV. The template system has been designed to feel comfortable to those used to dealing with HTML, allowing web designers to easily work with the templates. Templates are compiled as standard Scala functions.

If you create a `views/Application/index.scala.html` template file, it will generate a `views.html.Application.index` function. By accessing `/` route with a `GET` request will be sent to the server, the router will call `controllers.Application.index` which will return as a response a complete list of players (HTML format).

```scala
 object Application extends Controller {
  def index = Action {
    Ok(views.html.index(Players.findAll))
  }
 }
```

In [index.scala.html]() having the player list, we just need to iterate it and display every player in a table. Play Framework will render the page server-side.
```html
@(players: List[Player])

@main("Player List") {
    <h2>Previously inserted players:</h2>

    <table>
        <tr><th>idPlayer</th><th>Name</th><th>Level</th></tr>
        @for(p <- players){
            <tr><td>@p.idPlayer</td><td>@p.name</td><td>@p.level</td></tr>
        }
    </table>
}
```

Adding players to the game is as simple as that: submit a form using `POST` to this route `/player/single`
```html
 <h2>Insert a player here:</h2>

 <form action="/player/single" method="POST">
     <input name="name" type="text" placeholder="player name"/>
     <input name="level" type="text" placeholder="player level"/>
     <input type="submit"/>
 </form>
```

will call this method `controllers.Application.addSinglePlayer`.
```scala
 def addSinglePlayer = DBAction { implicit rs =>
  val player = playerForm.bindFromRequest.get
  Players.savePlayer(player)
  
  Redirect(routes.Application.index)
 }
```

### Multi Player

Building a modern web application, with a client-side JavaScript application, served from the Play Framework, they will be two separete applications in one project: **API Backend** (presistent data) and **Frontend side** (making AJAX calls to the server). The JavaScript application will be in `public` folder, aiming to server static content: HTML, CSS, images, etc ...

To test our calls use we gonna use [Postman](http://assist-software.net/downloads/postman-http-client-testing-web-services).

By making a GET call to `/player/multi` this method will be executed `controllers.Application.findAll` and it will return a list of players in `JSON` format.

```scala
def findAll = DBAction{ implicit rs =>
  Ok(Json.toJson(Players.findAll))
}
```

ex:
```json
 [
    {
        "idPlayer": 1,
        "name": "Frunza",
        "level": 21
    },
    {
        "idPlayer": 2,
        "name": "Gamer",
        "level": 41
    }
]
```

To add a player use `POST` method to this route `/player/multi` with the following `JSON` body:
```json
{
  "name": "Newbie",
  "level": 1
}
```

## Game Features
To gain more experience in Playing at work you can try this modules or create one by yourself and support the comunity.
* [Amazon S3 module (Scala)](https://github.com/Rhinofly/play-s3). A minimal S3 API wrapper that allows you to list, get, add and remove items from a S3 bucket.
* [Authentication and Authorization module (Scala)](https://github.com/t2v/play2-auth). This module offers Authentication and Authorization features to Play2.x applications.
* [Deadbolt 2 Plugin](https://github.com/schaloner/deadbolt-2). Deadbolt is a powerful authorisation mechanism for defining access rights to certain controller methods or parts of a view.
* [Dust Plugin](https://github.com/jmparsons/play-dustjs). Provides support for the dust client side template language (DustJS).
* [Memcached Plugin](https://github.com/mumoshu/play2-memcached). Provides a memcached based cache implementation.
* [MongoDB Salat, Casbah Plugin (Scala)](https://github.com/leon/play-salat). Provides managed MongoDB access and object mapping using Salat and Casbah
* [Redis Plugin (Java and Scala)](https://github.com/typesafehub/play-plugins). Provides a redis based cache implementation, also lets you use Redis specific APIs
* [SecureSocial (Java and Scala)](http://www.securesocial.ws/). An authentication module supporting OAuth, OAuth2, OpenID, Username/Password and custom authentication schemes.

## Most popular Players
* [LinkedIn](http://engineering.linkedin.com/frontend/new-technologies-new-linkedin-home-page) 
* [The Guardian](http://www.guardian.co.uk/)
* [Twitter](twitter.com)
* [Foursquare](https://foursquare.com/)
* [Coursera](http://www.coursera.org/)
* [Klout](https://klout.com/home)
* [Walmart](http://www.walmart.com/)
