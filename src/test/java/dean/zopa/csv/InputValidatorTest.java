package dean.zopa.csv;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class InputValidatorTest {

	@Test(expected = IllegalArgumentException.class)
	public void Given_EmptyContent_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.generalValidate("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_ContentWithEmptySpace_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.generalValidate(" ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_NullContent_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.generalValidate(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_LenderWithZeroAmount_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		Lender lender = new Lender("bob", new BigDecimal("0.1"), Money.of(0, Config.CURRENCY));
		inputValidator.validateLender(lender);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_LenderWithNegativeAmount_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		Lender lender = new Lender("bob", new BigDecimal("0.1"), Money.of(-1, Config.CURRENCY));
		inputValidator.validateLender(lender);
	}

	@Test
	public void Given_LenderWithValidAmount_Then_DoNotThrowException() {
		InputValidator inputValidator = new InputValidator();
		Lender lender = new Lender("bob", new BigDecimal("0.1"), Money.of(1, Config.CURRENCY));
		inputValidator.validateLender(lender);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_LenderWithEmptyName_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		Lender lender = new Lender(" ", new BigDecimal("0.1"), Money.of(1, Config.CURRENCY));
		inputValidator.validateLender(lender);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_LenderWithNullName_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		Lender lender = new Lender(null, new BigDecimal("0.1"), Money.of(1, Config.CURRENCY));
		inputValidator.validateLender(lender);
	}

	@Test
	public void Given_LenderWithValidName_Then_DoNotThrowException() {
		InputValidator inputValidator = new InputValidator();
		Lender lender = new Lender("x", new BigDecimal("0.1"), Money.of(1, Config.CURRENCY));
		inputValidator.validateLender(lender);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_LenderWithRateOf100Percent_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		Lender lender = new Lender("x", new BigDecimal("1"), Money.of(1, Config.CURRENCY));
		inputValidator.validateLender(lender);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_LenderWithRateOfLessThanZero_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		Lender lender = new Lender("x", new BigDecimal("-0.01"), Money.of(1, Config.CURRENCY));
		inputValidator.validateLender(lender);
	}

	@Test
	public void Given_LenderWithValidRate_Then_DoNotThrowException() {
		InputValidator inputValidator = new InputValidator();
		Lender lender = new Lender("x", new BigDecimal("0.01"), Money.of(1, Config.CURRENCY));
		inputValidator.validateLender(lender);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_LendersWithNonUniqueNames_Then_ThrowExceptions() {
		InputValidator inputValidator = new InputValidator();
		List<Lender> lenders = Arrays.asList(
				new Lender("l1", null, null),
				new Lender("l1", null, null),
				new Lender("l3", null, null));

		inputValidator.validateUniqueLenders(lenders);
	}

	@Test
	public void Given_LendersWithUniqueNames_Then_DoNotThrowExceptions() {
		InputValidator inputValidator = new InputValidator();
		List<Lender> lenders = Arrays.asList(
				new Lender("l1", null, null),
				new Lender("l2", null, null),
				new Lender("l3", null, null));
		inputValidator.validateUniqueLenders(lenders);
	}


}