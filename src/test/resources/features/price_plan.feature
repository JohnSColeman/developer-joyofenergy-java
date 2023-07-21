Feature: Calculate consumption cost of electricity readings for different price plans

  Scenario Outline: Calculate consumption cost for a smart meter with readings
    Given a smart meter with ID "<smartMeterId>" and time zone "<time zone>"
    And the following electricity readings for the smart meter:
      | Date                      | Reading |
      | Saturday 1 Jul 2023 23:59 | 500     |
      | Sunday 2 Jul 2023 23:59   | 1000    |
      | Monday 3 Jul 2023 23:59   | 1500    |
    And the following price plans:
      | Plan Name | Energy Supplier | Unit Rate | Monday Multiplier | Tuesday Multiplier | Wednesday Multiplier | Thursday Multiplier | Friday Multiplier | Saturday Multiplier | Sunday Multiplier |
      | Plan A    | Supplier X      | 0.05      | 1.0               | 1.0                | 1.0                  | 1.0                 | 1.0               | 1.0                 | 1.0               |
      | Plan B    | Supplier Y      | 0.06      | 1.2               | 1.2                | 1.2                  | 1.0                 | 1.0               | 1.0                 | 1.0               |
      | Plan C    | Supplier Z      | 0.07      | 1.5               | 1.5                | 1.5                  | 1.5                 | 1.5               | 1.0                 | 1.0               |
    When I calculate the consumption cost for each price plan
    Then the consumption cost for each price plan should be:
      | Energy Supplier | Price Plan | Cost    |
      | Supplier X      | Plan A     | <costA> |
      | Supplier Y      | Plan B     | <costB> |
      | Supplier Z      | Plan C     | <costC> |

    Examples:
      | smartMeterId | time zone     | costA | costB | costC  |
      | 12345678      | Europe/Berlin | 1.05 | 1.26 | 1.47 |
#      | 12345678      | Europe/Berlin | 75.00 | 96.00 | 112.00 |
