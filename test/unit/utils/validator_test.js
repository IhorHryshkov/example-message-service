/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-26T02:05
 */
//--------validator_test.js--------

import Validator from "../../../src/utils/Validator";
import sinon     from "sinon";

describe(`"Validator" test`, () => {
	describe(
		`"_errorResponse" function test`,
		() => {
			it("Return empty array", () => {
				const defaultParams = {};
				const expected      = [];
				const validator     = new Validator({ defaultParams });
				const result        = validator.visibleForTests.errorResponse([]);
				expect(result).toEqual(expected);
			});
			it("Return success one format error message in array", () => {
				const defaultParams = {};
				const expected      = [
					{
						message: "test",
						path   : ".test",
					},
				];
				const validator     = new Validator({ defaultParams });
				const result        = validator.visibleForTests.errorResponse([
					{
						message : "test",
						dataPath: ".test",
					},
				]);
				expect(result).toEqual(expected);
			});
			it("Return success two format error message in array", () => {
				const defaultParams = {};
				const expected      = [
					{
						message: "test",
						path   : ".test",
					},
					{
						message: "test2",
						path   : ".test2",
					},
				];
				const validator     = new Validator({ defaultParams });
				const result        = validator.visibleForTests.errorResponse([
					{
						message : "test",
						dataPath: ".test",
					},
					{
						message : "test2",
						dataPath: ".test2",
					},
				]);
				expect(result).toEqual(expected);
			});
		},
	);
	describe(
		`"validateSchema" function test`,
		() => {
			it("Return null if data is valid", () => {
				const defaultParams = {};
				const schemaName    = "test";
				const data          = {
					id  : 1,
					test: "test",
				};

				const validator       = new Validator({ defaultParams });
				const ajvValidateStub = sinon.stub(
					validator.visibleForTests.ajv,
					"validate",
				);
				ajvValidateStub.withArgs(
					"test",
					{
						id  : 1,
						test: "test",
					},
				).returns(true);

				const result = validator.validateSchema(schemaName, data);
				expect(result).toBeNull();

				ajvValidateStub.restore();
			});
			it("Return successful error response data", () => {
				const defaultParams = {
					messages: {
						error: {
							dataIncorrect: {
								code   : 400,
								message: "test",
							},
						},
					},
				};
				const schemaName    = "test";
				const data          = {
					id  : 1,
					test: "test",
				};
				const expected      = {
					code   : 400,
					message: [
						{
							message: "test",
							path   : ".test",
						},
					],
				};

				const validator                      = new Validator({ defaultParams });
				validator.visibleForTests.ajv.errors = [
					{
						message : "test",
						dataPath: ".test",
					},
				];
				const errorResponseStub              = sinon.stub(
					validator.visibleForTests,
					"errorResponse",
				);
				errorResponseStub.withArgs([
					{
						message : "test",
						dataPath: ".test",
					},
				]).returns([
					{
						message: "test",
						path   : ".test",
					},
				]);
				const ajvValidateStub = sinon.stub(
					validator.visibleForTests.ajv,
					"validate",
				);
				ajvValidateStub.withArgs(
					"test",
					{
						id  : 1,
						test: "test",
					},
				).returns(false);

				const result = validator.validateSchema(schemaName, data);
				expect(result).toEqual(expected);

				ajvValidateStub.restore();
				errorResponseStub.restore();
			});
		},
	);
});
