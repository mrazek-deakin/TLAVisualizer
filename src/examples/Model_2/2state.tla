
---------------------------- MODULE 2state ----------------------------
EXTENDS TLC, Naturals

CONSTANTS NUM_OF_SENSORS 
CONSTANTS STARTED, 
    RUNNING,
    FAILED,
    STOPPED, 
    SENSOR_TIMEOUT, 
    SYS_TIMEOUT,
    MAX_TIME,
    MODEL_ERROR


VARIABLE sys_timeout
VARIABLE sensors_timeout
VARIABLE system_is_up
VARIABLE rec_msg
VARIABLE now
VARIABLE sys_status

reset_timeouts ==
    /\ sys_timeout' = now
    /\ sensors_timeout' = [ i \in (1..NUM_OF_SENSORS) |-> now]
    /\ UNCHANGED<<now>>
  

system_status ==
 /\ sys_status' =
        CASE 
        /\ system_is_up
        /\ rec_msg
        /\ now - sys_timeout <= SYS_TIMEOUT
        /\ \E i \in (1..NUM_OF_SENSORS) : 
            /\ now - sensors_timeout[i] <= SENSOR_TIMEOUT
        -> RUNNING
    []
        /\ ~system_is_up
        ->  STOPPED
    []    
        /\ system_is_up
        /\  
            \/ now - sys_timeout > SYS_TIMEOUT
            \/ \A i \in (1..NUM_OF_SENSORS) : 
                /\ now - sensors_timeout[i] > SENSOR_TIMEOUT
        ->  FAILED
    []
        /\ system_is_up
        /\ ~rec_msg
        /\ now - sys_timeout <= SYS_TIMEOUT
        /\ \E i \in (1..NUM_OF_SENSORS) : 
            /\ now - sensors_timeout[i] <= SENSOR_TIMEOUT
        ->  STARTED
    
    []
        OTHER 
        ->  MODEL_ERROR
 
detach ==
    /\ system_is_up' = FALSE
    /\ UNCHANGED <<sys_timeout, sensors_timeout, rec_msg>>
 
attach ==
    IF  
        /\ ~system_is_up
    THEN  
        /\ system_is_up' = TRUE
        /\ rec_msg' = FALSE
        /\ reset_timeouts
    ELSE
        /\  UNCHANGED <<sys_timeout, sensors_timeout, system_is_up, rec_msg>>

restart ==
    /\ system_is_up' = TRUE
    /\ rec_msg' = FALSE
    /\ reset_timeouts

invalid_report  ==
    /\ UNCHANGED << sys_timeout, sensors_timeout, system_is_up, rec_msg>>

valid_report ==
    IF 
        /\ system_is_up
    THEN 
        /\ sys_timeout' = now
        /\ rec_msg' = TRUE
        /\ UNCHANGED <<sensors_timeout, system_is_up>>
        /\ PrintT(<<sys_timeout, sys_timeout', now>>)
    ELSE
        /\ UNCHANGED <<sys_timeout, sensors_timeout, system_is_up, rec_msg>>

verified_report == 
    IF
        /\ system_is_up 
    THEN 
        /\ \E s \in 1..NUM_OF_SENSORS:
            /\ sensors_timeout' = [sensors_timeout EXCEPT ![s] = now]
        /\ sys_timeout' = now     
        /\ rec_msg' = TRUE
        /\ UNCHANGED <<system_is_up>>
    ELSE
        /\ UNCHANGED <<sys_timeout, sensors_timeout, system_is_up, rec_msg>>
        

unverified_report == 
    IF
        /\ system_is_up 
    THEN 
        /\ sys_timeout' = now
        /\ rec_msg' = TRUE
        /\ UNCHANGED <<system_is_up,sensors_timeout>>
    ELSE
        /\ UNCHANGED <<sys_timeout, sensors_timeout, system_is_up, rec_msg>>
 
tick == 
        /\ now < MAX_TIME
        /\ now' = now + 1 
       

Next == 
            
    \/  /\  \/ restart
            \/ attach
            \/ detach
            \/ invalid_report
            \/ valid_report
            \/ verified_report
            \/ unverified_report        
        /\ system_status
        /\ PrintT(<<sys_status, sys_status',  sys_timeout, sensors_timeout, system_is_up , rec_msg', now>>)
        /\ tick
    \/  /\ tick 
        /\ system_status
        /\ PrintT(<<sys_status, sys_timeout, sensors_timeout, system_is_up , rec_msg, now>>)
        /\ UNCHANGED <<sys_timeout, sensors_timeout, rec_msg, system_is_up>>
    \/ UNCHANGED <<sys_status, sys_timeout, sensors_timeout, system_is_up , rec_msg, now>>
        
Init == 
    /\ sys_status = STARTED
    /\ system_is_up = TRUE
    /\ now = 0
    /\ sys_timeout = 0
    /\ sensors_timeout = [ i \in (1..NUM_OF_SENSORS) |-> 0]
    /\ rec_msg = FALSE

=============================================================================
