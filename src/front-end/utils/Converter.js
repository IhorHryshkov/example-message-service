/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:20
 */
//--------Converter.js--------

class ConverterImpl {
	changeNumberToTextCounter(numb) {
		if (numb.length <= 3) {
			return numb;
		} else if (numb.length > 3 && numb.length <= 6) {
			return `${numb.substring(0, 1)}K+`;
		} else if (numb.length > 6 && numb.length <= 9) {
			return `${numb.substring(0, 1)}M+`;
		} else if (numb.length > 9) {
			return `${numb.substring(0, 1)}B+`;
		}
	}

	changeNumberToTextRating(numb) {
		return numb ? (numb / 10).toPrecision(2) : "5.0"
	}
}

class Converter {
	constructor() {
		const converterImpl = new ConverterImpl();

		this.changeNumberToTextCounter = (numb) => converterImpl.changeNumberToTextCounter(numb);
		this.changeNumberToTextRating  = (numb) => converterImpl.changeNumberToTextRating(numb);
	}

}

export default Converter;
