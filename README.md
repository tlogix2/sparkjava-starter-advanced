![](https://img.shields.io/travis/thoughtlogix/sparkjava-starter-advanced.svg) 
![](https://img.shields.io/github/license/thoughtlogix/sparkjava-starter-advanced.svg)

# Sparkjava Advanced Starter

The sparkjava advanced starter provides a seed / boilerplate / example project using Spark, Kotlin, Gradle, Hibernate, Jackson and more.  If you are looking for something lighter, try the [SparkJava Simple Starter](https://github.com/thoughtlogix/sparkjava-starter-simple) project.

## Features and Limitations

All the features of the [SparkJava Simple Starter](https://github.com/thoughtlogix/sparkjava-starter-simple) project and...

* Hibernate for JPA persistence
* Jackson and content negotiation for marshaling data through routes
* Reads settings from json file.

## Installation

Make sure you have jdk 1.8+ installed.

* Download or clone this repo: `git clone https://github.com/thoughtlogix/sparkjava-starter-advanced`.

## Running

From the project dir, run:

* run `gradle runServer`
* open `http://localhost:7011`

## See also

* [SparkJava Simple Starter Kit](https://github.com/thoughtlogix/sparkjava-starter-simple)

## Todo

* Basic authentication.
* Contact form
* SMTP mail integration
* Internationalization

##Faq

* Why use hibernate instead of a lighter alternative (i.e. EBean or JOOQ)?
We already use Hibernate (with Envers) for other projects so we just went with that.

* Why use 7010/7011 instead of default 4567 from SparK?
For the same reason we dont use 3000, 4000, 8080, or 9000.  We may have other tools already running on those ports.


## Notes

* Not everything is idiomatic Kotlin but we'll get there.

## License

MIT - Go nuts
