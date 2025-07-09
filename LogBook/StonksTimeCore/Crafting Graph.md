#mods #graph

# Structure 

##  Basic Premise :

$$
G = \left(I \cup R, \; (I\times R) \cup (R \times I)\right)
$$
With $I$ the set of every item and $R$ The set of every recipe.

An edge $(x_i, r_i)$ with a weight $c_i$ means that $c_i$ amount of $x_i$ is used in recipe $r_i$.
An edge $(r_i,x_i)$ with a weight $c_i$ means that $c_i$ amount of $x_i$ is produced in recipe $r_i$

Including $R$ garanties one way paths between nodes.

## Ingredients managing :

For some recipes, multiple items act as the same ingredient. After manual verification, every item within a same ingredient is used the same amount.

We will represent such ingredient with an arbitrary node named after the original Ingredient object. Luckily, if an ingredient has the same object name as another, it always contains the same items, meaning we don't have to rename them. They don't however share the same amount of items. We thus apply the weight property to the edge going from the ingredient to the recipe

![[BasicCraftSetup|3000]]

# Propagation :

Using this model, value propagation is as follows :

> 1) $x_{ij} \to X_i$  : $\underset{j} \min \{x_{ij}\}$
> 2) $X_{ik} \to r_k$  :  $\underset{i} \sum c_{ik} \, X_{ik}$
> 3) $r_k \to x_k$  :  $\frac{r_k}{c_k}$

For instance, assuming $x_1 = 1$, $x_2 = 2$, $x_3 = 3$, $c_i = 4$, and $c_k = 2$

> 1) $X_i = \min \{1,2,3\} = 1$
> 2) $r_j = 4 \times 1 = 4$
> 3) $x_k = \frac{4}{2} = 2$  

The values are assigned to nodes.

Each node has a set of plausible values called candidates. They represent the degeneracies in values for each node. The easiest way to collapse those candidates is by taking the minimum.
## Testing :

![[simple_graph|1000]]

For a simple (no cycles) subgraph, the propagation happens like expected.
# Strategy :

Before being able to propagate the values, we need to back propagate the graph in order to determine the roots of the graph, the "atoms" from which every other node descends. These can easily be identified by looking at the in degree, the number of edges coming in a node.

Assuming $x_i \in I$ a node 
- $x_i$ is **atomic** when $\text{deg}_{in} (x_i) = 0$ 
- $x_i$ is **dead end** when $\text{deg}_{out}(x_i) = 0$
- $x_i$ is **isolated** when $x_i$ is atomic and a dead end

Naive back propagation is not feasible, as the $\min$ function is not invertible. Inspired by the way Aequivaleo does it, we can use a list of candidate values that are kept until the values flood back.

## Cycle Handling : 

Cycles are difficult to handle, as they aren't easily treated by simple recursive call of ancestors (we get stuck in an infinite loop).

We do not simply want cycles, as they do not take into account embedded cycles, or cycles sharing a node / edge or more. We must thus obtain **Strongly Connected Components** instead.


> [!NOTE] Strongly Connected Components
> Subgraph in which every node is reachable from every other node.
> They form a partition of the graph

This property means that, if one node of the SCC has a value, every other node value within the SCC can be determined. Each SCC thus needs to, given a representative item node (usually the most important one, i.e. the one which has the highest in degree / out degree). This however can lead to degeneracies, as a node being part of a cycle doesn't mean that the node is unsolvable.

The other property means that, if a node is not within a non trivial SCC (cycle), it will be in a trivial SCC (single node). We thus filter for length 1 SCC.

### Cycle Node :
We collapse every SCC into a Cycle Node that serves as a link between the SCC itself (as a subgraph) and the rest of the graph. This is almost equivalent to the condensation of the graph, as we still want to keep our original logic.

This then turns the whole graph into a **Directed Acyclic Graph**, allowing for the propagation algorithm to operate.

A cycle node's subgraph contains all 3 other node types.
- A *Recipe node* outputs to only one Item node, which is necessarily in the cycle but could have an Ingredient input that is outside the cycle.
- An *Ingredient node* can output to multiple recipe nodes so it can output to outside the cycle, and can have multiple item node input, also possibly outside the cycle.
- An *Item node* can output to multiple ingredient and have multiple crafting inputs

=> Because of all this, it is possible that a cycle node can be connected to another cycle node (if and only if it is a one way connection, as a 2-way connection would mean that every node in cycle A has a path to cycle B and vice versa, meaning that they form a larger SCC)


> [!NOTE] Input / Output
> - In : *Recipe*, *Ingredient*, *Item*, *Cycle*
> - Out : *Recipe*, *Ingredient*, *Cycle*

Inside the subgraph, a custom logic needs to be applied, so we pass the incoming nodes as global attributes of the subgraph, by storing the edges as the node attribute "inEdges". Same thing for outgoing edges as "outEdges"
### Cycle Algorithm :

To propagate values through a cycle, we assume the following : 

- Every input node has already been determined.

The algorithm is :
- Get the pseudo edges from the main graph pointing to the subnodes
- Ignoring every inner edge, initialize the values of the input subnodes (which we can do since we assume every input node is determined) into 2 fields, the basic **SCT** candidate value and a new **SCTInit** field, which allows us to keep track of this first value
- Using only the inner graph, compute the candidate values for every node in the graph
- Recompute the input subnodes using **SCTInit**
	- *Ingredient* Node : minimum between **SCTInit** and the incoming inner values.
	- *Recipe* Node : Sum of **SCTInit** and the incoming inner values.
	- *Item* Node : Append the incoming inner values.
- Using those new candidates, recompute the entire subgraph
- If the set of candidates stays the same for every node, or a certain amount of iterations passed, consider the cycle solved

Once the cycle is solved, the outside nodes can call upon the cycle to continue the propagation.


### Sub-Cycle Propagation :

For a simple cycle, the behavior is stable.
![[cycle_propagating|800]]
