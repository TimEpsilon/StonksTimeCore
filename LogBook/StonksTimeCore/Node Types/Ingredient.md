#nodeType

Represents an ingredient, input to recipes.
# Name
The Ingredient id, not unique between runs.

`Ingredient@id`
# Value
The minimum between the values of each incoming item node.

$\underset{j} \min \{x_{j}\}$
# Input 
- ==Item== Node
	- **Edge** : Holds no value
- ==Cycle== Node
	- **Edge** : Holds no value
# Output :
- [[Recipe]] Node
	- **Edge** : Holds $c_i$
- [[Cycle]] Node
	- **Edge** : Holds $c_i$