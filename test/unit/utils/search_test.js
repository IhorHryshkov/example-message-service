/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-24T14:45
 */
//--------search_test.js--------

import searchFirstIdInNode from "../../../src/utils/Search";

describe(`"Search" test`, () => {
	it("searchFirstIdInNode return undefined ID", () => {
		const path = {};
		const result = searchFirstIdInNode(path);
		expect(result).toBeUndefined();
	});
	it("searchFirstIdInNode return ID if ID in root node", () => {
		const path = { id: "test" };
		const expected = "test";
		const result = searchFirstIdInNode(path);
		expect(result).toEqual(expected);
	});
	it("searchFirstIdInNode return ID if ID in parent node", () => {
		const path = { parentNode: { id: "test" } };
		const expected = "test";
		const result = searchFirstIdInNode(path);
		expect(result).toEqual(expected);
	});
});
