package it.unibo.jakta.playground

val text1 = """
    ```yaml
    EVENT: reach(home)
    CONDITIONS:
    - there_is(home, D)
    - direction(D)
    - ¬obstacle(D)
    OPERATIONS:
    - execute move(D)
    ---
    
    EVENT: reach(home)
    CONDITIONS:
    - there_is(home, D)
    - direction(D)
    - obstacle(D)
    OPERATIONS:
    - achieve find_alternative_path(D)
    - achieve reach(home)
    ---
    
    EVENT: find_alternative_path(D)
    CONDITIONS:
    - direction(D)
    OPERATIONS:
    - achieve find_accessible_direction(D1)
    - execute move(D1)
    ---
    
    EVENT: find_accessible_direction(D)
    CONDITIONS:
    <none>
    OPERATIONS:
    - achieve check_accessible(D)
    ---
    
    EVENT: check_accessible(D)
    CONDITIONS:
    - direction(D)
    - ¬obstacle(D)
    OPERATIONS:
    <none>
    ```

    Invented admissible goals and beliefs
    
    - goal: `find_alternative_path(D)`
      purpose: find an alternative path when direction D is blocked
    - goal: `find_accessible_direction(D)`
      purpose: find a direction different from D that is accessible (i.e., not blocked)
    - goal: `check_accessible(D)`
      purpose: verify that direction D is accessible (i.e., free of obstacles)
""".trimIndent()

val text2 = """
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
