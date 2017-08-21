# Notes

Application can be run with `gradle run -PappArgs="['marketTest.input', '1000']` from command line, or just invoke the java main method in `Main`.

Everything done in TDD. Decided to give Junit a shot as I've been using Spock the past 2 years. Definitely did not like the usage of Junit vs Spock, as tests feel less readable and its not as easy to test the same method with mulitple data sets.

# Design Decisions

Different LoanCalculators can easily be switched around as `LoanCalculator` is an interface `Quote` uses. The current LoanAlgorithm could be much better, so future re-building of the the loan algorithm is easy to do. You might also want to test which loan algorithms are more successful, so its useful to have them easily switchable.

`WeightedLoanAlgorithm` was designed to ensure lenders had partial contributions, but borrowers still got low competitive rates. One glaring hole with my implementation is that lenders with low amounts of money and low rates can easily have all their money given to one borrower if they are requesting high amounts. Due to time constraints and the complexity of designing such an algorithm I just left it as is.

Decided to just use my own CSV parsing instead of the other frameworks (jackson, opencsv) as it was surprisingly simple to do without it. I can imagine when datasets get huge though you may want something that is more well tested and performs better, but for this excercise it felt like the simplest approach. Made the `InputParser` an interface so its again easily replaceable/switchable.

`WeightedLoanAlgorithm` has small rounding issues of less than 0.0001p, which I purposely chose to ignore due to time constraints and the fact that there is often some small rounding issues when dividing/multipling money with percentages/ratios.

Decided to give `MonetaryAmount` a shot for the first time as it apparently has a good chance to make into future java versions. Think it may have been simpler to handle all money as BigDecimals though, but maybe I just need to get more used to the API.

Currency, Locale and Repayment periods are easily changeable in the `Config` class. Given more time I would have put this into a settings file (e.g. xml/groovy) so its more easily changeable. Could have also made the algorithms, input parsers etc. all configurable via a settings file given more time.

Put lenders in a `LenderPool` instead of just passing them around as a list to have it work it in a more Object Orientated design. Common logic needed to be done on them (summing their rates/amounts/removing lenders with no available money from the pool as the algorithm is used) can all be left to that class.

