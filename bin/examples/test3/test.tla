--------------------------- MODULE test ---------------------------
EXTENDS Naturals, Reals, TLC

VARIABLE varA, varB

toggleA ==
		CASE 
			varA = TRUE 
			-> 	/\  varA' = FALSE 
				/\  varB' = varB 
		[] 
			varA # TRUE 
			-> 	/\  varA' = TRUE   
				/\  varB' = varB
    
    
toggleB ==
		CASE 
			varB = TRUE 
			->  /\  varA' = varA 
			    /\  varB' = FALSE 
		[] 
			varB = FALSE 
			->  /\  varA' = varA  
			    /\  varB' = TRUE
   
   
Init ==
    /\  varA = FALSE
    /\  varB = FALSE
    
Next ==   
       \/ toggleA
       \/ toggleB

        
=============================================================================
