Format each plan as follows:

```yaml
EVENT: event to be pursued
CONDITIONS:
- condition to be satisfied
- other conditions to be satisfied
- ...
OPERATIONS:
- operation to be performed
- other operations to be performed
- ...
```

Separate plans with a bar such as `---`.
Represent the lack of conditions or operations with the word `<none>`.
Try to be as general as possible, using the minimal number of plans, possibly involving variables.

Events can be drawn from the admissible goals.
Conditions can be logic formulas over the beliefs, possibly involving negation and logical connectives.
Operations can be drawn from the admissible actions or from the admissible goals.
You can invent new admissible goals or beliefs, but you cannot invent admissible actions.
Only use FOL syntax, with no quantifiers.

In the end, provide a list of admissible goals and beliefs that you invented, along with their natural language interpretation.
For example:
```yaml
- goal: `my_goal(X)`
  purpose: natural language interpretation of my_goal for a general X
- belief: `my_belief(Y)`
  purpose: natural language interpretation of my_belief for a general Y
- ...
```
