Feature: Creates a new parking lot

    Scenario: should create a parking lot with valid parameters

        Given user wants to create a parking lot with the following attributes
            | name       | capacity |
            | Basement   | 5        |
            | Ground     | 3        |
            | FirstFloor | 4        |
        When user try to save the new parking lot
        Then the save is 'SUCCESSFUL'