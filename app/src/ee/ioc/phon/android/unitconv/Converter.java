package ee.ioc.phon.android.unitconv;

import javax.measure.unit.Unit;

/**
 * <p>Converts an expression (String) into a number
 * and returns it as a string. If an error occurs then returns the
 * error message.</p>
 * 
 * <p>Different kinds of input expressions are supported:</p>
 * 
 * <ul>
 * <li>1 2 m IN ft</li>
 * <li>( 1 2 + 3 4 ) * 1 2 3</li>
 * </ul>
 * 
 * TODO: make this code not ugly
 */

public class Converter {

	public enum ExprType {
		MAP,
		UNITCONV,
		EXPR
	};

	private final ExprType mExprType;
	private final String mPrettyIn;

	private double mNumber;
	private String mIn;
	private String mOut;

	public Converter(String expr) {
		if (expr.contains(" IN ")) {
			mExprType = ExprType.UNITCONV;
			String[] splits = expr.split(" IN ");
			String numberAsStr = splits[0].replaceFirst("[^0-9\\. ].*", "").replaceAll("[^0-9\\.]", "");
			mNumber = Double.parseDouble(numberAsStr);
			mIn  = splits[0].replaceFirst("^[0-9\\. ]+", "").replaceAll("\\s+", "");
			mOut = splits[1].replaceAll("\\s+", "");
			mPrettyIn =  mNumber + " " + mIn + " IN " + mOut;
		} else if (expr.contains(",")) {
			mExprType = ExprType.MAP;
			// Remove space between digits
			mPrettyIn = expr.replaceAll("(\\d)\\s+", "$1");
		} else {
			mExprType = ExprType.EXPR;
			mPrettyIn = expr.replaceAll("\\s+", "");
		}
	}


	/**
	 * @return pretty-printed version of the expression that was given to the constructor
	 */
	public String getIn() {
		return mPrettyIn;
	}


	public String getView() {
		switch (mExprType) {
		case MAP:
			String query = mPrettyIn;
			if (query.contains("FROM")) {
				query = query.replaceFirst("FROM", "saddr=");
				query = query.replaceFirst("TO", "&daddr=");
				return "http://maps.google.com/maps?" + query;
			} else {
				return "http://maps.google.com/maps?daddr=" + query;
			}
		}
		return null;
	}


	/**
	 * @return evaluation of the expression that was given to the constructor
	 */
	public String getOut() {
		switch (mExprType) {
		case MAP:
			return "Click for map";
		case UNITCONV:
			return "" + Unit.valueOf(mIn).getConverterTo(Unit.valueOf(mOut)).convert(mNumber);
		case EXPR:
			MathEval math = new MathEval();
			return "" + math.evaluate(mPrettyIn);
		}
		return null;
	}
}