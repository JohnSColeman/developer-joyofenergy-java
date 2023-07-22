Feature: As an electricity consumer, I want to be able to view my usage cost of the last week so that I can monitor my spending

  Scenario: Given I have a smart meter ID with price plan attached to it and usage data stored, when I request the usage cost then I am shown the correct cost of last week's usage
    Given a smart meter with ID "smart-meter-0" and time zone "GMT"
    And the following readings for the smart meter:
      | Date            | Reading |
      | 2021-10-19 8:00 | 1       |
      | 2021-10-20 8:00 | 2       |
      | 2021-10-21 8:00 | 1       |
      | 2021-10-22 8:00 | 1       |
      | 2021-10-23 8:00 | 3       |
      | 2021-10-24 8:00 | 1       |
      | 2021-10-25 8:00 | 4       |
      | 2021-10-26 8:00 | 1       |
    Then the weekly usage cost is 2940.00

#  Scenario:  Given I have a smart meter ID without a price plan attached to it and usage data stored, when I request the usage cost then an error message is displayed


