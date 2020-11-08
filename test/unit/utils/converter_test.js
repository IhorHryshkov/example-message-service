/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-23T22:24
 */
//--------converter_test.js--------

import changeNumberToTextCounter from "../../../src/utils/Converter";

describe(`"Converter" test`, () => {
	it("changeNumberToTextCounter return success 1K+", () => {
		const num = 1000;
		const result = changeNumberToTextCounter(num);
		expect(result).toEqual("1K+");
	});
	it("changeNumberToTextCounter return success 1M+", () => {
		const num = 1000000;
		const result = changeNumberToTextCounter(num);
		expect(result).toEqual("1M+");
	});
	it("changeNumberToTextCounter return success 1B+", () => {
		const num = 1000000000;
		const result = changeNumberToTextCounter(num);
		expect(result).toEqual("1B+");
	});
});
