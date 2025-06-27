Format each plan as follows:

```yaml
EVENT: achieve event to be pursued
CONDITIONS:
  - condition to be satisfied
  - other conditions to be satisfied
  - ...
OPERATIONS:
  - [execute|achieve|add|remove|update] operation to be performed
  - [execute|achieve|add|remove|update] other operations to be performed
  - ...
```
Separate plans with a bar such as `---`.
Represent the lack of conditions or operations with the word `<none>`.

Events can be drawn from the admissible goals.
Conditions can be logic formulas over the beliefs, possibly involving negation and logical connectives.
Operations can be drawn from the admissible actions or from the admissible goals.
You can invent new admissible goals or beliefs, but you cannot invent admissible actions.
You cannot reference admissible goals or beliefs that already exist.
You must use all the admissible goals and beliefs that you invent.
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

Be as general and minimal as possible: use variables instead of constants where appropriate, reuse patterns across plans, and avoid over-specification.
Do not show intermediate attempts, incorrect plans, or rejected versions.
The output must be a block containing the admissible goals and beliefs and a block containing the final, clean, and minimal plan set only.