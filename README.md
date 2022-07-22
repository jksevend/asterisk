# ![Logo](./documentation/asterisk_logo.png) Asterisk

A sample full stack web application and mobile app. Utilising Json Web Tokens (JWT) to authorized requests. Made with the (as I call it) SPARF stack. Which means
**S**pring-Boot, **P**ostgresql, **A**ngular, **R**edis and **F**lutter.

- [General](#general)
- [Requirements](#requirements)
- [Structure](#structure)
- [Docker](#docker)

## General
This project is for demonstration purpose only. Feel free to clone this project and built
on top of it the application you would like!

It is recommended to use IntelliJ IDEA for development. It should automatically
detect and import needed settings for you to get started in no time.

## Requirements
* Java 17
* Maven
* Redis
* Flutter 3.0.1
* Docker
* NodeJS and npm

## Structure
This project is structured in the following way:
* ``/asterisk-app`` contains a Flutter application
* ``/asterisk-backend`` contains the Spring Boot backend
* ``/asterisk/frontend`` contains a nx workspace with an angular website and admin app
* ``/documentation`` contains images used in README

## Docker
The whole stack is dockerized. Run ``docker compose up --build`` to execute the postgresql, redis, angular website, angular admin and spring boot backend containers.
To verify everything is working visit http://localhost:8080/healthcheck (backend), http://localhost:80 (frontend-website) and http://localhost:81 (frontend-admin)