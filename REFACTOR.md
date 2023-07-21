# Refactoring for code hygiene
Methods for assessing code and working out where to focus for improvement to keep technical debt to a minimal of
non-urgent priority 4 (P4) issues.

## Triage priorities
1. immediate - behaviour not as expected; crashes, deadlocks, high latency, output bad data
2. urgent - any other non-critical bad ux, that which is developing or may develop architectural erosion
3. semi-urgent - code to improve that does not present critical risk but will constitute a significant benefit
4. non-urgent - remaining issues that are minor and can wait

## Hit list for improving code
1. comprehension: does the code clearly express what it is doing in domain context
2. modularity: vertical slice, critical for sustainable software architecture see https://sustainable-software-architecture.com/
3. domain: are the key business elements well modelled see https://www.oreilly.com/library/view/domain-driven-design-tackling/0321125215/
4. patterns and frameworks: important guardrails for sustainable software architecture
5. code smells and vulnerabilities, static code analysis and other metrics
6. documentation, self-documenting code
7. standards and conventions
8. BDD > TDD

## Shit list
1. narcissistic design see https://www.youtube.com/watch?v=LEZv-kQUSi4
2. handling null values (common Java issue, why not Kotlin or Go?)
3. abuse of abstract classes and inheritance
4. tight coupling
5. beware-of-beans/data-driven development/anemic domain model as an anti-pattern see https://martinfowler.com/bliki/AnemicDomainModel.html
6. making a mess with threads and concurrency (another big Java issue)
7. exception/error handling: swallowing exceptions (try-catch malarkey), using exceptions for error reporting, idiomatic or functional
8. god classes or methods
9. insufficient functional or unit tests, flaky/unstable tests
10. unit tests as a crutch!? see The Tyranny of Metrics see https://press.princeton.edu/books/hardcover/9780691174952/the-tyranny-of-metrics and https://blog.stevensanderson.com/2009/11/04/selective-unit-testing-costs-and-benefits/
