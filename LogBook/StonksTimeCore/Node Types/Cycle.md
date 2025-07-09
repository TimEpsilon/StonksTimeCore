#nodeType

Proxy for a Strongly Connected Component. Links the SCC subgraph to the main graph. The subgraph is then related to the original cycle-containing graph in order to get the input and output edges / nodes.
# Name
A simple iteration obtained by determining the SCC

`cycle-id`
# Value
Holds no value but is marked as computed once the subgraph has stabilized
# Input 
- ==Ingredient== Node
	- **Edge** : Holds nothing
- ==Item== Node
	- **Edge** : Holds nothing
- ==Recipe== node
	- **Edge** : Holds nothing
- ==Cycle== node
	- **Edge** : Holds nothing
# Output :
- [[Recipe]] Node :
	- **Edge** : Holds nothing
- [[Ingredient]] Node :
	- **Edge** : Holds nothing
- [[Cycle]] node
	- **Edge** : Holds nothing
