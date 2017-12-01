------------------------------- MODULE 2state -------------------------------
EXTENDS Naturals, TLC
CONSTANTS UNKNOWN, OK, FAILED, ATTACHED, DETACHED, MAX_TIME

VARIABLES state, t

report_received ==
        /\  state # DETACHED
        /\  state' = OK
        /\  t' = t
        
timeout ==
        /\  t > 40
        /\  state # DETACHED
        /\  state' = FAILED
        /\  t' = t

attach ==
        /\  state = DETACHED
        /\  state' = UNKNOWN
        /\  t' = t
        
detach ==
        /\  state' = DETACHED
        /\  t' = t
        
init ==
        /\  state = UNKNOWN
        /\  t = 0
tick ==
        /\  t < MAX_TIME
        /\  t' = t + 1
        /\  state' = state
Next ==
        \/  tick
        \/  report_received
        \/  timeout
        \/  attach
        \/  detach
                                 
=============================================================================
\* Modification History
\* Last modified Fri Nov 07 11:28:39 EST 2014 by malmorsy
\* Created Sat Oct 25 13:33:43 EST 2014 by malmorsy
