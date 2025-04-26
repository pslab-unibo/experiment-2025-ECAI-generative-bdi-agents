package it.unibo.jakta.playground

// gpt 4o
val text1 = """
    ```yaml
    EVENT: achieve reach(X)
    CONDITIONS:
      - object(X)
      - not(there_is(X, here))
    OPERATIONS:
      - achieve find(X)
      - achieve go_to(X)
      - add there_is(X, here)
    ---
    EVENT: achieve find(X)
    CONDITIONS:
      - object(X)
    OPERATIONS:
      - execute print("Searching for object", X)
      - <none>
    ---
    EVENT: achieve go_to(X)
    CONDITIONS:
      - object(X)
      - not(there_is(X, here))
    OPERATIONS:
      - achieve get_direction_towards(X, D)
      - execute move(D)
    ---
    EVENT: achieve get_direction_towards(X, D)
    CONDITIONS:
      - object(X)
      - direction(D)
      - not(obstacle(D))
    OPERATIONS:
      - execute getDirectionToMove(D)
    ---
    EVENT: test there_is(X, here)
    CONDITIONS:
      - object(X)
    OPERATIONS:
      - execute print("Reached object", X)
    ```
    ```yaml
    - goal: `find(X)`
      purpose: search for object X in the environment
    - goal: `go_to(X)`
      purpose: move toward the location of object X
    - goal: `get_direction_towards(X, D)`
      purpose: determine a direction D to move toward object X avoiding obstacles
    ```
""".trimIndent()

// O4-mini
val text2 = """
```yaml
EVENT: test there_is(home, D)
CONDITIONS:
  - <none>
OPERATIONS:
  - achieve reach(home)
---
EVENT: achieve reach(home)
CONDITIONS:
  - there_is(home, here)
OPERATIONS:
  - <none>
---
EVENT: achieve reach(home)
CONDITIONS:
  - there_is(home, D)
  - not obstacle(D)
OPERATIONS:
  - execute move(D)
---
EVENT: achieve reach(home)
CONDITIONS:
  - there_is(home, D)
  - obstacle(D)
OPERATIONS:
  - execute getDirectionToMove(D2)
  - execute move(D2)
  - achieve reach(home)
```
```yaml
<none>
```
""".trimIndent()

// claude 3.7 sonnet
val text3 = """
    I'll create a set of generalized BDI plans to help the agent achieve the goal of reaching home. 
    Each plan will follow the specified format and use the appropriate prefixes for events, conditions, and operations.
    
    ```yaml
    EVENT: achieve reach(home)
    CONDITIONS:
      - there_is(home, Direction) & not(obstacle(Direction))
    OPERATIONS:
      - execute move(Direction)
    ---
    EVENT: achieve reach(home)
    CONDITIONS:
      - there_is(home, Direction) & obstacle(Direction)
    OPERATIONS:
      - execute getDirectionToMove(NewDirection)
      - execute move(NewDirection)
    ---
    EVENT: achieve reach(home)
    CONDITIONS:
      - not(there_is(home, Direction)) & direction(Direction) & not(obstacle(Direction))
    OPERATIONS:
      - execute move(Direction)
      - achieve reach(home)
    ---
    EVENT: test obstacle(Direction)
    CONDITIONS:
      - <none>
    OPERATIONS:
      - execute getDirectionToMove(NewDirection)
      - execute move(NewDirection)
    ---
    EVENT: achieve reach(Object)
    CONDITIONS:
      - there_is(Object, here)
    OPERATIONS:
      - <none>
    ```
    Admissible goals and beliefs I invented:
    ```yaml
    - belief: `there_is(Object, here)`
      purpose: indicates that the Object is at the agent's current location
    ```
""".trimIndent()

// deepseek v3
val text4 = """
    ```yaml
    EVENT: achieve reach(home)
    CONDITIONS:
      - there_is(home, here)
    OPERATIONS:
      - execute print("Goal achieved", home)
    ---

    EVENT: achieve reach(home)
    CONDITIONS:
      - not(there_is(home, here))
      - there_is(home, Direction)
      - not(obstacle(Direction))
    OPERATIONS:
      - execute move(Direction)
      - achieve reach(home)
    ---

    EVENT: achieve reach(home)
    CONDITIONS:
      - not(there_is(home, here))
      - not(there_is(home, _))
      - direction(Direction)
      - not(obstacle(Direction))
    OPERATIONS:
      - execute move(Direction)
      - achieve reach(home)
    ---

    EVENT: achieve reach(home)
    CONDITIONS:
      - not(there_is(home, here))
      - not(there_is(home, _))
      - forall(Direction, obstacle(Direction))
    OPERATIONS:
      - execute print("Cannot reach home", "All directions blocked")
      - execute fail()
    ```
""".trimIndent()

// llama 3.3.-70
val text5 = """
    ```yml
    EVENT: achieve reach(home)
    CONDITIONS:
      - not(obstacle(here))
      - there_is(home, here)
    OPERATIONS:
      - execute print("Agent has reached home", "Success")
    
    ---
    EVENT: achieve reach(home)
    CONDITIONS:
      - there_is(home, Direction)
      - not(obstacle(Direction))
    OPERATIONS:
      - execute move(Direction)
      - achieve reach(home)
    
    ---
    EVENT: test obstacle(Direction)
    CONDITIONS:
      - direction(Direction)
    OPERATIONS:
      - achieve getDirectionToMove(Direction)
      - execute move(Direction)
      - achieve reach(home)
    
    ---
    EVENT: achieve reach(home)
    CONDITIONS:
      - <none>
    OPERATIONS:
      - test obstacle(north)
      - test obstacle(east)
      - test obstacle(west)
    
    ---
    EVENT: achieve getDirectionToMove(Direction)
    CONDITIONS:
      - direction(Direction)
      - obstacle(Direction)
    OPERATIONS:
      - remove obstacle(Direction)
      - execute move(Direction)
    
    ---
    EVENT: achieve getDirectionToMove(Direction)
    CONDITIONS:
      - direction(Direction)
      - not(obstacle(Direction))
    OPERATIONS:
      - execute move(Direction)
    ```
""".trimIndent()

// gemini 2.0 flash experimental
val text6 = """
    ```yaml
    EVENT: achieve reach(Object)
    CONDITIONS:
      - there_is(Object, here)
    OPERATIONS:
      - execute print(reached, Object)
    ---
    EVENT: achieve reach(Object)
    CONDITIONS:
      - not(there_is(Object, here))
    OPERATIONS:
      - execute getDirectionToMove(Direction)
      - achieve move_towards(Direction, Object)
    ---
    EVENT: achieve move_towards(Direction, Object)
    CONDITIONS:
      - direction(Direction)
      - ~obstacle(Direction)
    OPERATIONS:
      - execute move(Direction)
      - achieve reach(Object)
    ---
    EVENT: achieve move_towards(Direction, Object)
    CONDITIONS:
      - direction(Direction)
      - obstacle(Direction)
    OPERATIONS:
      - execute getDirectionToMove(NewDirection)
      - achieve move_towards(NewDirection, Object)
    ---
    EVENT: test obstacle(Direction)
    CONDITIONS:
      - direction(Direction)
    OPERATIONS:
      - execute print(obstacle_detected, Direction)
      - execute getDirectionToMove(NewDirection)
      - achieve move_towards(NewDirection, home)
    ```
    ```yaml
    - goal: `move_towards(Direction, Object)`
      purpose: move in the given `Direction` with the intention of reaching the `Object`
    ```
""".trimIndent()

// Phi4 Q4
val text7 = """
    ```yaml
    EVENT: achieve reach(home)
    CONDITIONS:
      - not(obstacle(north))
      - not(obstacle(east))
      - not(obstacle(west))
      - not(obstacle(north_east))
      - not(obstacle(north_west))
      - obstacle(south)
      - obstacle(south_east)
      - obstacle(south_west)
    OPERATIONS:
      - achieve reach(north)
      - execute move(north)
      - test there_is(home, here)
      - if not(test there_is(home, here)):
        - achieve reach(east)
        - execute move(east)
        - test there_is(home, here)
        - if not(test there_is(home, here)):
          - achieve reach(west)
          - execute move(west)
          - test there_is(home, here)
    
    ---
    EVENT: achieve reach(north)
    CONDITIONS:
      - not(obstacle(north))
    OPERATIONS:
      - execute move(north)
      - test there_is(home, here)
      - if not(test there_is(home, here)):
        - achieve reach(east)
        - execute move(east)
        - test there_is(home, here)
        - if not(test there_is(home, here)):
          - achieve reach(west)
          - execute move(west)
          - test there_is(home, here)
    
    ---
    EVENT: achieve reach(east)
    CONDITIONS:
      - not(obstacle(east))
    OPERATIONS:
      - execute move(east)
      - test there_is(home, here)
      - if not(test there_is(home, here)):
        - achieve reach(west)
        - execute move(west)
        - test there_is(home, here)
    
    ---
    EVENT: achieve reach(west)
    CONDITIONS:
      - not(obstacle(west))
    OPERATIONS:
      - execute move(west)
      - test there_is(home, here)
    ```
    
    ### Invented Beliefs and Goals
    
    - belief: `direction_free_of_obstacles(Direction)`
      purpose: `Direction` is a direction with no obstacles, suitable for moving.
    
    - goal: `achieve move_in_direction_free_of_obstacles(Direction)`
      purpose: Move in a `Direction` that is confirmed to be free of obstacles.
""".trimIndent()
