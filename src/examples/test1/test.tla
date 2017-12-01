--------------------------- MODULE test ---------------------------
EXTENDS Naturals, Reals, TLC

VARIABLE varA, varB

doA_true ==
    /\  varA' = TRUE
    
doA_false ==
    /\  varA' = FALSE
    
doB_true ==
    /\  varB' = TRUE

 
doB_false ==
    /\  varB' = FALSE
   
Init ==
    /\  varA = FALSE
    /\  varB = FALSE
    
Next ==   
       /\   
            \/ doA_true
            \/ doA_false
       /\   
            \/ doB_true
            \/ doB_false
        
=============================================================================
