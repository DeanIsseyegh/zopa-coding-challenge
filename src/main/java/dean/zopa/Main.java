package dean.zopa;

import dean.zopa.input.CustomInputParser;
import dean.zopa.input.InputParser;
import dean.zopa.input.InputValidator;
import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;
import dean.zopa.logic.LoanCalculator;
import dean.zopa.logic.WeightedLoanAlgorithm;
import dean.zopa.logic.WeightedLoanCalculator;

import javax.money.MonetaryAmount;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

	//gradle run -PappArgs="['marketTest.input', '1000']"
	public static void main(String... args) throws IOException {
		System.out.println(run(args[0], args[1]));
	}

	public static Quote run(String filePath, String amount) throws IOException {
		Stream<String> stream = Files.lines(Paths.get(filePath));
		List<String> fileLines = stream.collect(Collectors.toList());
		InputParser inputParser = new CustomInputParser(new InputValidator());
		List<Lender> lenders = inputParser.parseLenders(fileLines);
		LenderPool lenderPool = new LenderPool(lenders);
		MonetaryAmount amountRequested = inputParser.parseAmount(amount, lenderPool);
		LoanCalculator loanCalculator = new WeightedLoanCalculator(new WeightedLoanAlgorithm(lenderPool));
		return new Quote(amountRequested, loanCalculator);
	}

}
