![](https://img.shields.io/travis/thoughtlogix/sparkjava-starter-advanced.svg) 
![](https://img.shields.io/github/license/thoughtlogix/sparkjava-starter-advanced.svg)

# Sparkjava Advanced Starter

The sparkjava advanced starter provides a seed / boilerplate / example project using Spark, Kotlin, Gradle, Hibernate, Jackson and more.  If you are looking for something lighter, try the [SparkJava Simple Starter](https://github.com/thoughtlogix/sparkjava-starter-simple) project.

__Note: This project is a bit of a Frankenstein merged from a couple other projects.  Until the code is reviewed and more tests are written, use with caution.__

## Features and Limitations

All the features of the [SparkJava Simple Starter](https://github.com/thoughtlogix/sparkjava-starter-simple) project and...

* Hibernate & Envers for JPA persistence
* Jackson and content negotiation for marshaling data through common routes
* Simple authentication example.  Need to switch to [Spark-pac4j](https://github.com/pac4j/spark-pac4j)
* Example Crud - Currently just Todo.
* Basic Internationalization

## Installation

Make sure you have jdk 1.8+ installed.

* Download or clone this repo: `git clone https://github.com/thoughtlogix/sparkjava-starter-advanced`.

## Running

From the project dir, run:

* If you don't have Gradle installed, run `./gradlew` for nix or `gradlew` for win
* run `gradle runServer`
* open `http://localhost:7011`
* use `ctr + c` to stop the server

## See also

* [SparkJava Simple Starter Kit](https://github.com/thoughtlogix/sparkjava-starter-simple)

## Todo

* Add more examples
* Add Contact form
* SMTP mail integration
* Read settings from json file.
* Hook up envers to GUI
* Added a sample js client to demonstrate REST API.

##Faq

* How should I use this?

At the moment, just use it for ideas.  Soon, you can hack it to fit your needs.  Eventually, simply extending
a few classes and hook in your own functionality.

* Why use Hibernate instead of a lighter alternative (i.e. Ebean or JOOQ)?

We already use Hibernate (with Envers) for other projects so we just went with that. 
A lighter database framework might be more inline with Spark.  

* Why use ports 7010/7011 instead of default 4567 from SparK?

For the same reason we dont use 3000, 8080, or 9000.  We may have other tools 
already running on those ports so we rolled the dice and picked these.

## Notes

* Not everything is idiomatic Kotlin but we'll get there.

## License

MIT - Go nuts
