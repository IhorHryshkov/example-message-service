/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-31T20:48
 */
//--------PreferencesDaoImpl_test.js--------

import PreferencesDao from "../../../../src/database/dao/PreferencesDaoImpl";
import sinon          from 'sinon';

describe(
	`"PreferencesDao" test`,
	() => {
		let preferencesDao;

		const noOp          = () => {
		};
		const dbConfig      = {
			getLocal    : () => {
				return {
					setItem   : noOp,
					removeItem: noOp,
					getItem   : noOp
				};
			},
			getIndexDB  : noOp,
			getValidator: () => {
				return {
					initSchemas   : noOp,
					validateSchema: noOp
				};
			}
		};
		const defaultParams = {
			constants: {
				global: {
					database: {
						version: 1
					}
				}
			},
			messages : {
				"info": {
					"success": {
						"message": "Success",
						"code"   : "SUCCESS"
					}
				}
			}
		};

		beforeEach(() => {
			preferencesDao = new PreferencesDao(
				dbConfig,
				defaultParams
			);
		});
		describe(
			`"add" function test`,
			() => {
				it(
					"Return reject",
					async () => {
						const expected     = {
							code   : "ERROR",
							message: "Error"
						};
						const validateStub = sinon.stub(
							preferencesDao._validator,
							"validateSchema"
						);
						validateStub.withArgs(
							"add",
							{
								key : "Test",
								data: {
									id  : 1,
									name: "Test"
								}
							}
						)
						.returns({
							code   : "ERROR",
							message: "Error"
						});
						let result;
						try {
							await preferencesDao.add({
								key : "Test",
								data: {
									id  : 1,
									name: "Test"
								}
							});
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						validateStub.restore();
					}
				);
				it(
					"Return resolve",
					async () => {
						const expected      = {
							code   : "SUCCESS",
							message: "Success"
						};
						const dataStringify = JSON.stringify({
							id  : 1,
							name: "Test"
						});
						const validateStub  = sinon.stub(
							preferencesDao._validator,
							"validateSchema"
						);
						validateStub.withArgs(
							"add",
							{
								key : "Test",
								data: {
									id  : 1,
									name: "Test"
								}
							}
						)
						.returns(null);

						const dbStub = sinon.stub(
							preferencesDao._db,
							"setItem"
						);
						dbStub.withArgs(
							{
								key : "Test",
								data: dataStringify
							}
						);

						const result = await preferencesDao.add({
							key : "Test",
							data: {
								id  : 1,
								name: "Test"
							}
						});
						expect(result)
						.toEqual(expected);
						validateStub.restore();
					}
				);
			}
		);
		describe(
			`"remove" function test`,
			() => {
				it(
					"Return reject validation",
					async () => {
						const expected     = {
							code   : "ERROR",
							message: "Error"
						};
						const validateStub = sinon.stub(
							preferencesDao._validator,
							"validateSchema"
						);
						validateStub.withArgs(
							"remove",
							{
								key: "Test"
							}
						)
						.returns({
							code   : "ERROR",
							message: "Error"
						});
						let result;
						try {
							await preferencesDao.remove({
								key: "Test"
							});
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						validateStub.restore();
					}
				);
				it(
					"Return reject any error",
					async () => {
						const expected     = {
							code   : "ERROR",
							message: "Error any"
						};
						const validateStub = sinon.stub(
							preferencesDao._validator,
							"validateSchema"
						);
						validateStub.withArgs(
							"remove",
							{
								key: "Test"
							}
						)
						.returns(null);
						const removeStub = sinon.stub(
							preferencesDao._db,
							"removeItem"
						);
						removeStub.withArgs("Test").throws(Error("Error any"));
						let result;
						try {
							await preferencesDao.remove({
								key: "Test"
							});
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						validateStub.restore();
						removeStub.restore();
					}
				);
				it(
					"Return resolve",
					async () => {
						const expected     = {
							code   : "SUCCESS",
							message: "Success"
						};
						const validateStub = sinon.stub(
							preferencesDao._validator,
							"validateSchema"
						);
						validateStub.withArgs(
							"remove",
							{
								key: "Test"
							}
						)
						.returns(null);
						const removeStub = sinon.stub(
							preferencesDao._db,
							"removeItem"
						);
						removeStub.withArgs("Test").returns(1);
						const result = await preferencesDao.remove({
							key: "Test"
						});
						expect(result)
						.toEqual(expected);
						validateStub.restore();
						removeStub.restore();
					}
				);
			}
		);
		describe(
			`"getByKey" function test`,
			() => {
				it(
					"Return reject validation",
					async () => {
						const expected      = {
							code   : "ERROR",
							message: "Error"
						};
						const dataStringify = JSON.stringify({
							id  : 1,
							name: "Test"
						});
						const dbStub        = sinon.stub(
							preferencesDao._db,
							"getItem"
						);
						dbStub.withArgs("Test")
						.returns(dataStringify);

						const validateStub = sinon.stub(
							preferencesDao._validator,
							"validateSchema"
						);
						validateStub.withArgs(
							"remove",
							{
								key: "Test"
							}
						)
						.returns({
							code   : "ERROR",
							message: "Error"
						});
						let result;
						try {
							await preferencesDao.getByKey({
								key: "Test"
							});
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						validateStub.restore();
						dbStub.restore();
					}
				);
				it(
					"Return reject any error",
					async () => {
						const expected      = {
							code   : "ERROR",
							message: "Error any"
						};
						const dbStub        = sinon.stub(
							preferencesDao._db,
							"getItem"
						);
						dbStub.withArgs("Test").throws(Error("Error any"));

						const validateStub = sinon.stub(
							preferencesDao._validator,
							"validateSchema"
						);
						validateStub.withArgs(
							"remove",
							{
								key: "Test"
							}
						)
						.returns(null);
						let result;
						try {
							await preferencesDao.getByKey({
								key: "Test"
							});
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						validateStub.restore();
						dbStub.restore();
					}
				);
				it(
					"Return resolve undefined if not found",
					async () => {
						const expected = undefined;

						const dbStub = sinon.stub(
							preferencesDao._db,
							"getItem"
						);
						dbStub.withArgs("Test")
						.returns(undefined);
						const validateStub = sinon.stub(
							preferencesDao._validator,
							"validateSchema"
						);
						validateStub.withArgs(
							"remove",
							{
								key: "Test"
							}
						)
						.returns(null);

						let result = await preferencesDao.getByKey({
							key: "Test"
						});
						expect(result)
						.toEqual(expected);
						validateStub.restore();
					}
				);
				it(
					"Return resolve success",
					async () => {
						const expected      = {
							id  : 1,
							name: "Test"
						};
						const dataStringify = JSON.stringify({
							id  : 1,
							name: "Test"
						});

						const dbStub = sinon.stub(
							preferencesDao._db,
							"getItem"
						);
						dbStub.withArgs("Test")
						.returns(dataStringify);
						const validateStub = sinon.stub(
							preferencesDao._validator,
							"validateSchema"
						);
						validateStub.withArgs(
							"remove",
							{
								key: "Test"
							}
						)
						.returns(null);
						validateStub.withArgs(
							"getByKey",
							{
								key : "Test",
								data: {
									id  : 1,
									name: "Test"
								}
							}
						)
						.returns(null);

						const result = await preferencesDao.getByKey({
							key: "Test"
						});
						expect(result)
						.toEqual(expected);
						validateStub.restore();
						dbStub.restore();
					}
				);
			}
		);
	}
);
