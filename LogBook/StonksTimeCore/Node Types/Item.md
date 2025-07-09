#nodeType

Represents an in game item.
# Name
The namespace / resource location of the in game item

`modid:item`
# Value
The value of this item, either calculated from incoming edges or user defined if the node is *atomic*.

Each candidate value is computed by dividing the incoming recipe value $r_i$ by the amount produced $c_i$ 

 $\dfrac{r_i}{c_i}$
# Input 
- ==Recipe== Node
	- **Edge** : Holds $c_k$
# Output :
- [[Ingredient]] Node
	- **Edge** : Holds no value
- [[Cycle]] Node
	- **Edge** : Holds no value
