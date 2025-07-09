#nodeType

Represents an in game crafting recipe. A recipe is an operation on a set and amount of ingredients (input items) and which outputs a single item and amount.
# Name
The namespace / resource location of the in game recipe, with the recipe type prepended

`type-modid:recipe`
# Value
The sum of the input ingredient values $X_i$ multiplied by each ingredient amount $c_i$.

$\underset{i} \sum c_{i} \, X_{i}$
# Input 
- ==Ingredient== Node
	- **Edge** : Holds $c_i$
- ==Cycle== Node
	- **Edge** : Holds $c_i$
# Output :
- [[Item]] Node
	- **Edge** : Holds $c_k$
- [[Cycle]] Node
	- **Edge** : Holds $c_k$
