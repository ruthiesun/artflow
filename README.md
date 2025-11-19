# Artflow

A full-stack web application for documenting art projects.

See a demo [here](https://artflow-demo.onrender.com/demo/projects/). Login credentials for demo account are the following:

Email: ruthieismakinganapp@gmail.com

Password: N6MUh56qwUNmFjR?

View my personal page [here](https://artflow-demo.onrender.com/ruthie/projects/).

Note that the backend will likely take a minute or two to spin up (the page will look pretty empty until then).

---

## Features

- User authentication with Firebase
- Account verification and password reset with JWTs and Spring Security
- Tagging system for projects
- Public/private visibility options
- Responsive UI

## Project Structure

### Backend

- Spring Boot, Hibernate
- MVC architecture
- Developed with a TDD approach, with JUnit tests for the model and controller layers
- Controller: Input validation with DTO objects and invokes the service layer
- Service: Business logic involving the model layer POJOs
- Repository: Queries the database, returning model layer POJOs
- Model: POJOs used by Hibernate to map to the database
- Implements a REST API

### Frontend

- React, TypeScript, Tailwind CSS, Axios, Vite
- Routing with React Router
- Authentication with the Firebase SDK and a React context
- Styling delegated to Tailwind theme configurations and base React components (e.g. styled buttons, plain text, headers...)

---

## REST API endpoints

### Authentication

`/api/auth/register (POST)`

`/api/auth/login (POST)`

`/api/auth/logout (POST)`

`/api/auth/verify (GET)`

`/api/auth/resetRequest (POST)`

`/api/auth/reset (POST)`

### PROJECTS

`/api/<username>/projects (POST)`

`/api/<username>/projects/?tags=<tag_name>&visibility=<public_or_private> (GET)`

`/api/<username>/projects (PUT)`

`/api/<username>/projects/<project_name> (GET)`

`/api/<username>/projects/<project_name> (DELETE)`

### IMAGES

`/api/<username>/projects/<project_name>/images (POST)`

`/api/<username>/projects/<project_name>/images (GET)`

`/api/<username>/projects/<project_name>/images (PUT)`

`/api/<username>/projects/<project_name>/images/<image_id> (GET)`

`/api/<username>/projects/<project_name>/images/<image_id> (DELETE)`

### TAGS

`/api/<username>/tags (GET)`

`/api/<username>/projects/<project_name>/tags (POST)`

`/api/<username>/projects/<project_name>/tags (GET)`

`/api/<username>/projects/<project_name>/tags/<tag_name> (GET)`

`/api/<username>/projects/<project_name>/tags/<tag_name> (DELETE)`







