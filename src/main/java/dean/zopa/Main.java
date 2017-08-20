package dean.zopa;

import dean.zopa.csv.InputParser;
import dean.zopa.csv.InputValidator;
import dean.zopa.lender.Lender;
import dean.zopa.lender.LenderPool;
import dean.zopa.logic.LoanAlgorithm;
import dean.zopa.logic.LoanCalculator;
import org.javamoney.moneta.Money;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {


	/**
	 * TODO:
	 * 1. Make sure no rounding issues
	 * 2. Run against larger/more csv files
	 * 3. Double check no input edge case validations have been missed (e.g. csv file)
	 * 4. See if alogorithm can be simplified
	 * @param args
	 * @throws IOException
	 */

	//gradle run -PappArgs="['marketTest.csv', '1000']"
	public static void main(String... args) throws IOException {
		System.out.println(run(args[0], args[1]));
	}

	public static Quote run(String arg0, String arg1) throws IOException {
		String filePath = arg0;
		Stream<String> stream = Files.lines(Paths.get(filePath));
		List<String> fileLines = stream.collect(Collectors.toList());
		InputParser inputParser = new InputParser(new InputValidator());
		List<Lender> lenders = inputParser.parseLenders(fileLines);
		LenderPool lenderPool = new LenderPool(lenders);
		BigDecimal amountRequested = inputParser.parseAmount(arg1, lenderPool);
		LoanCalculator loanCalculator = new LoanCalculator(new LoanAlgorithm(lenderPool));
		return new Quote(Money.of(amountRequested, Config.CURRENCY), loanCalculator);
	}

}
