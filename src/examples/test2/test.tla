--------------------------- MODULE test ---------------------------
EXTENDS Naturals, Reals, TLC

VARIABLE varA, varB

toggleA ==
		IF  
			/\ varA = TRUE 
		THEN
    		/\  varA' = FALSE
    		/\  varB' = varB
    	ELSE
    		/\  varA' = TRUE
    		/\  varB' = varB
    
    
toggleB ==
    	IF  
			/\ varB = TRUE 
		THEN
    		/\  varB' = FALSE
    		/\  varA' = varA
    	ELSE
    		/\  varB' = TRUE
    		/\  varA' = varA
   
Init ==
    /\  varA = FALSE
    /\  varB = FALSE
    
Next ==   
       \/ toggleA
       \/ toggleB

        
=============================================================================
