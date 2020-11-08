/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-26T17:17
 */
//--------CallbackDaoImpl_test.js--------

import CallbackDao from "../../../../src/database/dao/CallbackDaoImpl";
import sinon       from "sinon";

describe(
	`"CallbackDao" test`,
	() => {
		let callbackDao;

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
			callbackDao = new CallbackDao(
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
							callbackDao._validator,
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
							await callbackDao.add({
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
							callbackDao._validator,
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
							callbackDao._db,
							"setItem"
						);
						dbStub.withArgs(
							{
								key : "Test",
								data: dataStringify
							}
						);

						const result = await callbackDao.add({
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
					"Return reject",
					async () => {
						const expected     = {
							code   : "ERROR",
							message: "Error"
						};
						const validateStub = sinon.stub(
							callbackDao._validator,
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
							await callbackDao.remove({
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
					"Return resolve",
					async () => {
						const expected     = {
							code   : "SUCCESS",
							message: "Success"
						};
						const validateStub = sinon.stub(
							callbackDao._validator,
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
							result = await callbackDao.remove({
								key: "Test"
							});
						} catch (e) {
						}
						expect(result)
						.toEqual(expected);
						validateStub.restore();
					}
				);
			}
		);
		describe(
			`"getByKey" function test`,
			() => {
				it(
					"Return reject",
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
							callbackDao._db,
							"getItem"
						);
						dbStub.withArgs("Test")
						.returns(dataStringify);

						const validateStub = sinon.stub(
							callbackDao._validator,
							"validateSchema"
						);
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
						.returns({
							code   : "ERROR",
							message: "Error"
						});
						let result;
						try {
							await callbackDao.getByKey({
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
					"Return resolve",
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
							callbackDao._db,
							"getItem"
						);
						dbStub.withArgs("Test")
						.returns(dataStringify);
						const validateStub = sinon.stub(
							callbackDao._validator,
							"validateSchema"
						);
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

						let result;
						try {
							result = await callbackDao.getByKey({
								key: "Test"
							});
						} catch (e) {
						}
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
