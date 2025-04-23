package it.unibo.jakta.playground

val text = """
        Here is a general plan to pursue the goal reach(home), 
        based on the BDI framework and the agent's current internal state.
        ---
        ```yaml
        EVENT: reach(X)
        CONDITIONS:
        - object(X)
        - ~(there_is(X, here))
        OPERATIONS:
        - achieve find(X)
        - achieve go_to(X)
        ```
        ---
        ```yaml
        EVENT: find(X)
        CONDITIONS:
        - object(X)
        OPERATIONS:
        - <none>
        ```
        ---
        ```yaml
        EVENT: go_to(X)
        CONDITIONS:
        - object(X)
        - ~(there_is(X, here))
        OPERATIONS:
        - achieve go_towards(X, D)
        ```
        ---
        ```yaml
        EVENT: go_towards(X, D)
        CONDITIONS:
        - direction(D)
        - ~obstacle(D)
        - there_is(X, D)
        OPERATIONS:
        - execute move(D)
        ```
        ---
        ```yaml
        EVENT: go_towards(X, D)
        CONDITIONS:
        - direction(D)
        - ~obstacle(D)
        - ~there_is(X, D)
        OPERATIONS:
        - execute move(D)
        - achieve go_to(X)
        ```
        ---
        # Invented Goals and Beliefs
        ```yaml
        - goal: find(X)
          purpose: Identify the direction of object X if it exists.
        - goal: go_to(X)
          purpose: Move towards object X until it is reached.
        - goal: go_towards(X, D)
          purpose: Move one step in direction D towards object X.
        ```
        These generalized plans enable the agent to reach any known object in the environment,
        adapting to current knowledge of direction and obstacles.
""".trimIndent()
