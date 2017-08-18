package dean.zopa.csv;

import dean.zopa.Config;
import dean.zopa.lender.Lender;
import org.javamoney.moneta.Money;
import org.junit.Test;

import java.math.BigDecimal;

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
	public void Given_EmptyRow_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.validateRow(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_NullRow_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.validateRow(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_OneColumnInRow_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.validateRow("x");
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_TwoColumnsInRow_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.validateRow("x,y");
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_FourColumnsInRow_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.validateRow("x,y,z,w");
	}

	@Test
	public void Given_ThreeColumnsInRow_Then_DoNotThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.validateRow("x,y,z");
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_FirstColumnIsEmptyInRow_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.validateRow(",x,z");
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_SecondColumnIsEmptyInRow_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.validateRow("x,,z");
	}

	@Test(expected = IllegalArgumentException.class)
	public void Given_ThirdColumnIsEmptyInRow_Then_ThrowException() {
		InputValidator inputValidator = new InputValidator();
		inputValidator.validateRow("x,y,");
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

}