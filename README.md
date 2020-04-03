# Blockchain
A Java implementation of a blockchain clone.

## Features
#### Implementation of blockchain in Java
* Simple proof-of-work algorithm
* Accounts (wallets) that can easily create transactions between each other
#### Persisted Data
* Data is persisted into a MongoDB database at each node
    * Why MongoDB? A variety of reasons, me wanting to learn MongoDB being the primary reason. 
    The workload is largely transactional (i.e. don't need joins), and mapping 
    each block to a JSON document logically makes sense.
## Plans for Future Work
#### Peer-to-peer network
Right now, this isn't much of a "blockchain", since it isn't distributed.
#### Webserver
Webserver to handle all the necessary stuff using Spring. Authentication, users, etc.
#### Front end
