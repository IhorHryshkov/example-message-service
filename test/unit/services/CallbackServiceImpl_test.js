/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-04T18:04
 */
//--------CallbackServiceImpl_test.js--------

import CallbackService from "../../../src/services/CallbackServiceImpl";
import sinon           from "sinon";

describe(
	`"CallbackService" test`,
	() => {
		let callbackService;

		const noOp          = () => {
		};
		const netConfig     = {
			getAxios: noOp,
		};
		const dbConfig      = {
			getLocal    : noOp,
			getIndexDB  : noOp,
			getValidator: () => {
				return {
					initSchemas: noOp,
				};
			},
		};
		const defaultParams = {
			constants: {
				global  : {
					database: {
						version: 1,
					},
				},
				callback: {
					prefixStorageKey : "test_",
					postfixStorageKey: "_test",
				},
			}
		};

		beforeEach(() => {
			callbackService = new CallbackService({
				netConfig,
				dbConfig,
				defaultParams,
			});
		});

		describe(
			`"add" function test`,
			() => {
				it(
					"Return reject request",
					async () => {
						const expected    = "Test error";
						const requestStub = sinon.stub(
							callbackService._callbackRequest,
							"approve",
						);
						requestStub.withArgs({ resId: "Test" })
							.rejects(Error("Test error"));
						let result;
						try {
							await callbackService.add({
								username: "Tester",
								resId   : "Test",
							});
						} catch (e) {
							result = e.message;
						}
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [{ resId: "Test" }];
						expect(requestParams)
							.toMatchObject(expectedRequestParams);
						expect(result)
							.toEqual(expected);
						requestStub.restore();
					},
				);
				it(
					"Return undefined if request has error",
					async () => {
						const expected    = undefined;
						const requestStub = sinon.stub(
							callbackService._callbackRequest,
							"approve",
						);
						requestStub.withArgs({ resId: "Test" })
							.resolves({
								status: 400,
								data  : {
									error: {
										code   : 400,
										message: "Test error",
									},
								},
							});
						let result;
						try {
							result = await callbackService.add({
								username: "Tester",
								resId   : "Test",
							});
						} catch (e) {
							result = e.message;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [{ resId: "Test" }];
						expect(requestParams)
							.toMatchObject(expectedRequestParams);
						expect(result)
							.toEqual(expected);
						requestStub.restore();
					},
				);
				it(
					"Return reject DAO",
					async () => {
						const expected    = {
							code   : "ERROR",
							message: "Error",
						};
						const requestStub = sinon.stub(
							callbackService._callbackRequest,
							"approve",
						);
						requestStub.withArgs({ resId: "Test" })
							.resolves({
								status: 200,
								data  : {
									data: {
										resId: "Test",
									},
								},
							});
						const daoStub = sinon.stub(
							callbackService._callbackDao,
							"remove",
						);
						daoStub.withArgs({ key: "test_Tester_Test_test" })
							.rejects({
								code   : "ERROR",
								message: "Error",
							});

						let result;
						try {
							result = await callbackService.add({
								username: "Tester",
								resId   : "Test",
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [{ resId: "Test" }];
						expect(requestParams)
							.toMatchObject(expectedRequestParams);
						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [{ key: "test_Tester_Test_test" }];
						expect(daoParams)
							.toMatchObject(daoRequestParams);

						expect(result)
							.toEqual(expected);
						requestStub.restore();
						daoStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected    = {
							resId: "Test",
						};
						const requestStub = sinon.stub(
							callbackService._callbackRequest,
							"approve",
						);
						requestStub.withArgs({ resId: "Test" })
							.resolves({
								status: 200,
								data  : {
									data: {
										resId: "Test",
									},
								},
							});
						const daoStub = sinon.stub(
							callbackService._callbackDao,
							"remove",
						);
						daoStub.withArgs({ key: "test_Tester_Test_test" })
							.resolves({
								code   : "SUCCESS",
								message: "Success",
							});

						let result;
						try {
							result = await callbackService.add({
								username: "Tester",
								resId   : "Test",
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [{ resId: "Test" }];
						expect(requestParams)
							.toMatchObject(expectedRequestParams);
						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [{ key: "test_Tester_Test_test" }];
						expect(daoParams)
							.toMatchObject(daoRequestParams);

						expect(result)
							.toEqual(expected);
						requestStub.restore();
						daoStub.restore();
					},
				);
			},
		);
		describe(
			`"getById" function test`,
			() => {
				it(
					"Return reject DAO",
					async () => {
						const expected = {
							code   : "ERROR",
							message: "Error",
						};
						const daoStub  = sinon.stub(
							callbackService._callbackDao,
							"getByKey",
						);
						daoStub.withArgs({ key: "test_Tester_Test_test" })
							.rejects({
								code   : "ERROR",
								message: "Error",
							});

						let result;
						try {
							result = await callbackService.getById({
								username: "Tester",
								resId   : "Test",
							});
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [{ key: "test_Tester_Test_test" }];
						expect(daoParams)
							.toMatchObject(daoRequestParams);

						expect(result)
							.toEqual(expected);
						daoStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected = {
							resId: "Test",
						};
						const daoStub  = sinon.stub(
							callbackService._callbackDao,
							"getByKey",
						);
						daoStub.withArgs({ key: "test_Tester_Test_test" })
							.resolves({
								resId: "Test",
							});

						let result;
						try {
							result = await callbackService.getById({
								username: "Tester",
								resId   : "Test",
							});
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [{ key: "test_Tester_Test_test" }];
						expect(daoParams)
							.toMatchObject(daoRequestParams);

						expect(result)
							.toEqual(expected);
						daoStub.restore();
					},
				);
			},
		);
	},
);