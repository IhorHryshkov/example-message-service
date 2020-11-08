/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-05T19:05
 */
//--------CounterServiceImpl_test.js--------

import CounterService from "../../../src/services/CounterServiceImpl";
import sinon          from "sinon";

describe(
	`"CounterService" test`,
	() => {
		let counterService;

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
					enums            : {
						ADD_USER      : "ADD_USER",
						UPDATE_USER   : "UPDATE_USER",
						UPDATE_COUNTER: "UPDATE_COUNTER",
					},
				},
				user    : {
					default: {
						type: "message",
					},
				},
			},
		};

		beforeEach(() => {
			counterService = new CounterService({
				netConfig,
				dbConfig,
				defaultParams,
			});
		});

		describe(
			`"_addTypeNameToCounters" function test`,
			() => {
				it(
					"Return reject DAO",
					async () => {
						const expected = {
							code   : "ERROR",
							message: "Error",
						};
						const daoStub  = sinon.stub(
							counterService._typeDao,
							"getById",
						);
						daoStub.withArgs({ id: 1 })
						.rejects({
							code   : "ERROR",
							message: "Error",
						});

						let result;
						try {
							result = await counterService._addTypeNameToCounters({
								id    : 1,
								counts: 10,
							});
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [{ id: 1 }];
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
							id    : 1,
							counts: 10,
							name  : "testName",
						};
						const daoStub  = sinon.stub(
							counterService._typeDao,
							"getById",
						);
						daoStub.withArgs({ id: 1 })
						.resolves({
							id  : 1,
							name: "testName",
						});

						let result;
						try {
							result = await counterService._addTypeNameToCounters({
								id    : 1,
								counts: 10,
							});
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [{ id: 1 }];
						expect(daoParams)
						.toMatchObject(daoRequestParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
					},
				);
			},
		);
		describe(
			`"getById" function test`,
			() => {
				it(
					"Return reject request",
					async () => {
						const expected    = "Test error";
						const requestStub = sinon.stub(
							counterService._counterRequest,
							"getById",
						);
						requestStub.withArgs("test-user-id")
						.rejects(Error("Test error"));
						let result;
						try {
							await counterService.getById("test-user-id");
						} catch (e) {
							result = e.message;
						}
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = ["test-user-id"];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						expect(result)
						.toEqual(expected);
						requestStub.restore();
					},
				);
				it(
					"Return error if request has error or reject DAO",
					async () => {
						const expected    = {
							code   : "ERROR",
							message: "Error",
						};
						const requestStub = sinon.stub(
							counterService._counterRequest,
							"getById",
						);
						requestStub.withArgs("test-user-id")
						.resolves({
							status: 400,
							data  : {
								error: {
									code   : 400,
									message: "Test error",
								},
							},
						});
						const daoAddStub = sinon.stub(
							counterService._counterDao,
							"add",
						);
						daoAddStub.withArgs({ data: [] })
						.resolves({
							code   : "SUCCESS",
							message: "Success",
						});
						const daoAllStub = sinon.stub(
							counterService._counterDao,
							"all",
						);
						daoAllStub.rejects({
							code   : "ERROR",
							message: "Error",
						});
						let result;
						try {
							result = await counterService.getById("test-user-id");
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = ["test-user-id"];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						const daoAddParams         = daoAddStub.getCall(0).args;
						const expectedDaoAddParams = [{ data: [] }];
						expect(daoAddParams)
						.toMatchObject(expectedDaoAddParams);
						expect(result)
						.toEqual(expected);
						requestStub.restore();
						daoAddStub.restore();
						daoAllStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected    = [
							{
								id    : 1,
								counts: 10,
								name  : "testName",
							},
						];
						const requestStub = sinon.stub(
							counterService._counterRequest,
							"getById",
						);
						requestStub.withArgs("test-user-id")
						.resolves({
							status: 200,
							data  : {
								data: [
									{
										id    : 1,
										counts: 10,
									},
								],
							},
						});
						const daoAddStub = sinon.stub(
							counterService._counterDao,
							"add",
						);
						daoAddStub.withArgs({
							data: [
								{
									id    : 1,
									counts: 10,
								},
							],
						})
						.resolves({
							code   : "SUCCESS",
							message: "Success",
						});
						const daoAllStub = sinon.stub(
							counterService._counterDao,
							"all",
						);
						daoAllStub.resolves([
							{
								id    : 1,
								counts: 10,
							},
						]);
						const localMethodStub = sinon.stub(
							counterService,
							"_addTypeNameToCounters",
						);
						localMethodStub.withArgs({
							id    : 1,
							counts: 10,
						}).resolves(
							{
								id    : 1,
								counts: 10,
								name  : "testName",
							},
						);
						let result;
						try {
							result = await counterService.getById("test-user-id");
						} catch (e) {
							result = e;
						}
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = ["test-user-id"];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						const daoAddParams         = daoAddStub.getCall(0).args;
						const expectedDaoAddParams = [
							{
								data: [
									{
										id    : 1,
										counts: 10,
									},
								],
							},
						];
						expect(daoAddParams)
						.toMatchObject(expectedDaoAddParams);
						const localMethodParams         = localMethodStub.getCall(0).args;
						const expectedLocalMethodParams = [
							{
								id    : 1,
								counts: 10,
							},
						];
						expect(localMethodParams)
						.toMatchObject(expectedLocalMethodParams);

						expect(result)
						.toMatchObject(expected);
						requestStub.restore();
						daoAddStub.restore();
						daoAllStub.restore();
						localMethodStub.restore();
					},
				);
			},
		);
		describe(
			`"addMessageCounter" function test`,
			() => {
				it(
					"Return reject DAO",
					async () => {
						const expected    = {
							code   : "ERROR",
							message: "Error",
						};
						const daoTypeStub = sinon.stub(
							counterService._typeDao,
							"getByName",
						);
						daoTypeStub.withArgs({ name: "message" })
						.rejects({
							code   : "ERROR",
							message: "Error",
						});
						let result;
						try {
							result = await counterService.addMessageCounter({
								user_id : "test-user-id",
								username: "test-username",
							});
						} catch (e) {
							result = e;
						}

						const daoAddParams         = daoTypeStub.getCall(0).args;
						const expectedDaoAddParams = [{ name: "message" }];
						expect(daoAddParams)
						.toMatchObject(expectedDaoAddParams);
						expect(result)
						.toEqual(expected);
						daoTypeStub.restore();
					},
				);
				it(
					"Return reject request",
					async () => {
						const expected    = "Test error";
						const daoTypeStub = sinon.stub(
							counterService._typeDao,
							"getByName",
						);
						daoTypeStub.withArgs({ name: "message" })
						.resolves({
							id  : 1,
							name: "testName",
						});
						const requestStub = sinon.stub(
							counterService._counterRequest,
							"add",
						);
						requestStub.withArgs({
							userId: "test-user-id",
							typeId: 1,
						})
						.rejects(Error("Test error"));
						let result;
						try {
							result = await counterService.addMessageCounter({
								user_id : "test-user-id",
								username: "test-username",
							});
						} catch (e) {
							result = e.message;
						}
						const daoAddParams         = daoTypeStub.getCall(0).args;
						const expectedDaoAddParams = [{ name: "message" }];
						expect(daoAddParams)
						.toMatchObject(expectedDaoAddParams);
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								userId: "test-user-id",
								typeId: 1,
							},
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						expect(result)
						.toEqual(expected);
						requestStub.restore();
						daoTypeStub.restore();
					},
				);
				it(
					"Return error data if request return error",
					async () => {
						const expected    = {
							resId    : "test-res-id",
							error    : {
								code   : 400,
								message: "Test error",
							},
							timestamp: 1111111111111,
						};
						const daoTypeStub = sinon.stub(
							counterService._typeDao,
							"getByName",
						);
						daoTypeStub.withArgs({ name: "message" })
						.resolves({
							id  : 1,
							name: "testName",
						});
						const requestStub = sinon.stub(
							counterService._counterRequest,
							"add",
						);
						requestStub.withArgs({
							userId: "test-user-id",
							typeId: 1,
						})
						.resolves({
							status: 400,
							data  : {
								resId    : "test-res-id",
								error    : {
									code   : 400,
									message: "Test error",
								},
								timestamp: 1111111111111,
							},
						});
						let result;
						try {
							result = await counterService.addMessageCounter({
								user_id : "test-user-id",
								username: "test-username",
							});
						} catch (e) {
							result = e.message;
						}
						const daoAddParams         = daoTypeStub.getCall(0).args;
						const expectedDaoAddParams = [{ name: "message" }];
						expect(daoAddParams)
						.toMatchObject(expectedDaoAddParams);
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								userId: "test-user-id",
								typeId: 1,
							},
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						expect(result)
						.toEqual(expected);
						requestStub.restore();
						daoTypeStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected    = {
							resId    : "test-res-id",
							data     : {
								typeId: 1,
								userId: "test-user-id",
								count : 1,
							},
							timestamp: 1111111111111,
						};
						const daoTypeStub = sinon.stub(
							counterService._typeDao,
							"getByName",
						);
						daoTypeStub.withArgs({ name: "message" })
						.resolves({
							id  : 1,
							name: "testName",
						});
						const daoCallbackStub = sinon.stub(
							counterService._callbackDao,
							"add",
						);
						daoCallbackStub.withArgs({
							key : `test_test-username_test-res-id_test`,
							data: { callback: "UPDATE_COUNTER" },
						})
						.resolves({
							code   : "SUCCESS",
							message: "Success",
						});
						const requestStub = sinon.stub(
							counterService._counterRequest,
							"add",
						);
						requestStub.withArgs({
							userId: "test-user-id",
							typeId: 1,
						})
						.resolves({
							status: 201,
							data  : {
								resId    : "test-res-id",
								data     : {
									typeId: 1,
									userId: "test-user-id",
									count : 1,
								},
								timestamp: 1111111111111,
							},
						});
						let result;
						try {
							result = await counterService.addMessageCounter({
								user_id : "test-user-id",
								username: "test-username",
							});
						} catch (e) {
							result = e.message;
						}
						const daoAddParams         = daoTypeStub.getCall(0).args;
						const expectedDaoAddParams = [{ name: "message" }];
						expect(daoAddParams)
						.toMatchObject(expectedDaoAddParams);
						const daoCallbackParams         = daoCallbackStub.getCall(0).args;
						const expectedDaoCallbackParams = [
							{
								key : `test_test-username_test-res-id_test`,
								data: { callback: "UPDATE_COUNTER" },
							},
						];
						expect(daoCallbackParams)
						.toMatchObject(expectedDaoCallbackParams);
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								userId: "test-user-id",
								typeId: 1,
							},
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						expect(result)
						.toEqual(expected);
						requestStub.restore();
						daoTypeStub.restore();
						daoCallbackStub.restore();
					},
				);
			},
		);
		describe(
			`"updateMessageCounter" function test`,
			() => {
				it(
					"Return reject DAO",
					async () => {
						const expected    = {
							code   : "ERROR",
							message: "Error",
						};
						const daoTypeStub = sinon.stub(
							counterService._typeDao,
							"getByName",
						);
						daoTypeStub.withArgs({ name: "message" })
						.rejects({
							code   : "ERROR",
							message: "Error",
						});
						let result;
						try {
							result = await counterService.updateMessageCounter();
						} catch (e) {
							result = e;
						}

						const daoAddParams         = daoTypeStub.getCall(0).args;
						const expectedDaoAddParams = [{ name: "message" }];
						expect(daoAddParams)
						.toMatchObject(expectedDaoAddParams);
						expect(result)
						.toEqual(expected);
						daoTypeStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected    = [
							{
								id    : 1,
								counts: 10,
								name  : "testName",
							},
						];
						const daoTypeStub = sinon.stub(
							counterService._typeDao,
							"getByName",
						);
						daoTypeStub.withArgs({ name: "message" })
						.resolves({
							id  : 1,
							name: "testName",
						});
						const daoCounterByIdStub = sinon.stub(
							counterService._counterDao,
							"getById",
						);
						daoCounterByIdStub.withArgs({ id: 1 })
						.resolves({
							id    : 1,
							counts: 10,
							name  : "testName",
						});
						const daoCounterAddStub = sinon.stub(
							counterService._counterDao,
							"add",
						);
						daoCounterAddStub.withArgs({
							data: [
								{
									keys  : { typeId: 1 },
									counts: 11,
								},
							],
						})
						.resolves({
							code   : "SUCCESS",
							message: "Success",
						});
						const daoCounterAllStub = sinon.stub(
							counterService._counterDao,
							"all",
						);
						daoCounterAllStub
						.resolves([
							{
								id    : 1,
								counts: 11,
							},
						]);
						const localMethodStub = sinon.stub(
							counterService,
							"_addTypeNameToCounters",
						);
						localMethodStub.withArgs({
							id    : 1,
							counts: 11,
						}).resolves(
							{
								id    : 1,
								counts: 10,
								name  : "testName",
							},
						);
						let result;
						try {
							result = await counterService.updateMessageCounter();
						} catch (e) {
							result = e.message;
						}
						const daoAddParams         = daoTypeStub.getCall(0).args;
						const expectedDaoAddParams = [{ name: "message" }];
						expect(daoAddParams)
						.toMatchObject(expectedDaoAddParams);
						const daoCounterByIdParams         = daoCounterByIdStub.getCall(0).args;
						const expectedDaoCounterByIdParams = [{ id: 1 }];
						expect(daoCounterByIdParams)
						.toMatchObject(expectedDaoCounterByIdParams);
						const daoCounterAddParams         = daoCounterAddStub.getCall(0).args;
						const expectedDaoCounterAddParams = [
							{
								data: [
									{
										keys  : { typeId: 1 },
										counts: 11,
									},
								],
							},
						];
						expect(daoCounterAddParams)
						.toMatchObject(expectedDaoCounterAddParams);
						const localMethodParams         = localMethodStub.getCall(0).args;
						const expectedLocalMethodParams = [
							{
								id    : 1,
								counts: 11,
							},
						];
						expect(localMethodParams)
						.toMatchObject(expectedLocalMethodParams);
						expect(result)
						.toEqual(expected);
						daoCounterAddStub.restore();
						localMethodStub.restore();
						daoCounterByIdStub.restore();
						daoTypeStub.restore();
						daoCounterAllStub.restore();
					},
				);
			},
		);
	},
);
