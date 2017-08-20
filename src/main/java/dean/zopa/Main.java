package dean.zopa;

import dean.zopa.csv.InputParser;
import dean.zopa.csv.InputValidator;
import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;
import dean.zopa.logic.LoanAlgorithm;
import dean.zopa.logic.LoanCalculator;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

	public static void main(String... args) throws IOException {
		String filePath = args[0];
		String amountRequested = args[1];
		Stream<String> stream = Files.lines(Paths.get(filePath));
		List<String> fileLines = stream.collect(Collectors.toList());
		InputParser inputParser = new InputParser(fileLines, new InputValidator());
		List<Lender> lenders = inputParser.parseLenders();
		LenderPool lenderPool = new LenderPool(lenders);
		LoanCalculator loanCalculator = new LoanCalculator(new LoanAlgorithm(lenderPool));
		Quote quote = new Quote(Money.of(new BigDecimal(amountRequested), Config.CURRENCY), loanCalculator);
		System.out.println(quote.toString());
	}

}
