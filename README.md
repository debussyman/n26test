Description
---------

Run using gradle - `./gradlew build && java -jar build/libs/gs-spring-boot-0.1.0.jar`

I've never used Spring Boot before, but I remember it was mentioned on our call so I thought I'd try it out. I don't know much about the runtime aspects of Boot such as threading, concurrent requests, non-blocking behavior, etc.

I wrote a few tests to emulate sample transactions but they are single-threaded. However synchronization of each bucket update should be sufficient for limited concurrent requests. At some point the number of threads (requests) blocking to update the same "second" bucket could impact request times. At that frequency of requests I believe that a single server in-memory solution wouldn't be sufficient anyway.

Assumptions
---------
 * Statistics are inclusive of the current second
