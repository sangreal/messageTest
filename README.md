# message broker

## Architecture Thought
1. Keep a map in the memory for storing the message and another one for offsets for each consumer per topic. The data will be persisted to mysql eventually
2. Now it is only one instance, then map will be fine but if there are many instances behind a loadbalancer, then
we should consider setting up a leader replica(Per topic) and every write will be routed to that.
3. Every topic will have its own leader which should be distributed evenly across the instances
4. check if there is error while persistence, rollback will be trigger.
Obviously if rollback fails, some kind of retry mechanism should be implemented
5. After starting up, it will load data from mysql to memory in case of crash or restart 

---
##idempotent:
### producer
1. Producer can use pid and sequence number for identify the message. In the future we should check with the max sn and 
producer id to make sure if this is a dup message other than iterating through the whole message list

### consumer
1. use offset for dedup, but consumer should also use external datastores to remove dup since 
it could crash or network issue so that the offset could not commit.
2. for future we could define the partitions for topic to increase concurrency

---
##scalability
1. if having multiple instances, message broker should have a leading replica which talks with producer only.
Other instance just sync with the leading replica for data consistence.
2. need leader election since there is possibility that one instance goes down,
Other follow replicas should elect one new leader which may seek the help from zookeeper

###producer
every producer has its own pid and sn, so producer could have multiple instances

### consumer
the consumer should have same instance number with the number of partition of one topic,
otherwise the extra instances will be in idle state

---
##