# messageTest

## Error handling
### sending message
1. let producer generate a uuid for one message for identification
2. check if there is error while persistence, rollback will be trigger.
Obviously if rollback fails, some kind of retry mechanism should be implemented


###idempotent:
#### producer
1. I use sequence number for identify the message. In the future we should check with the max sn 
to make sure if this is a dup message

#### consumer
1. use offset for dedup, but consumer should also use external datastores to remove dup since 
it could crash or network issue so that the offset could not commit.

###scalability
1. if having multiple instances, message broker should have a leading replica which talks with producer only.
Other instance just sync with the leading replica for data consistence.
