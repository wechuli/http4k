title: Hexagonal http4k: Building a walking skeleton
description: A step-by-step guide to TDDing a simple http4k application in Hexagonal style

## What will we do here?
This extended tutorial is designed to demonstrate how to evolve a suite of test-driven applications in a [Hexagonal Architecture] and [outside-in TDD] style using [http4k]. Starting with the development of a [Walking Skeleton], we will write test cases defining the expected interactions with the system and then drill down further into the core, maximising reuse of test code and allowing us to test the same functionality at several different levels of the [Testing Pyramid]:

1. Pure domain model - ie. does our model work?
2. In-memory testing of HTTP-level interactions - ie. do our apps talk the same language?
3. Local port-bound testing of HTTP-level interactions - ie. Can our apps talk to each other?
4. Remote port-bound testing of HTTP-level interactions- ie. Does this deployed system work?

## What problems are we trying to solve?


## Introduction to the problem
We are not overly interested in introducing a domain which may be hard for readers to understand, so we have chosen to implement an API for a simple stateful `Calculator` - ie. one which tracks the result of the last calculation and which can be manipulated. This example is simple enough that everyone will be familiar with it, yet meaty enough that we can evolve the solution to a satisfactory level of complexity so as to demonstrate how the concepts here can be applied to a real problem.

The steps we will follow are as follows:

1. We develop a [Walking Skeleton] to prove that our system basically works. We will use the "RESET" functionality to do this - ie. the calculator clears it's state to 0. 
2. Implement "ADDITION" functionality in the app. 
3. Implement "SQUARE" functionality in the app.
4. Implement 


[outside-in TDD]: https://softwareengineering.stackexchange.com/questions/166409/tdd-outside-in-vs-inside-out
