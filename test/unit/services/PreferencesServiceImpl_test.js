/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-06T04:05
 */
//--------PreferencesServiceImpl_test.js--------

import PreferencesService from "../../../src/services/PreferencesServiceImpl";
import sinon              from "sinon";

describe(
	`"PreferencesService" test`,
	() => {
		let preferencesService;
		let timeStub;

		const noOp          = () => {
		};
		const dbConfig      = {
			getLocal    : noOp,
			getIndexDB  : noOp,
			getValidator: () => {
				return {
					initSchemas: noOp
				};
			}
		};
		const defaultParams = {
			constants: {
				global  : {
					preferences: {
						maxOnlineTTL: 1
					},
					database   : {
						version: 1
					}
				},
				callback: {
					prefixStorageKey : "test_",
					postfixStorageKey: "_test"
				}
			}
		};

		beforeAll(() => {
			timeStub = sinon.stub(Date, "now").returns(1604261344258);
		});

		beforeEach(() => {
			preferencesService = new PreferencesService({
				dbConfig,
				defaultParams
			});
		});

		afterAll(() => {
			timeStub.restore();
		});

		describe(
			`"add" function test`,
			() => {
				it(
					"Return reject DAO",
					async () => {
						const expected = {
							code   : "ERROR",
							message: "Error"
						};
						const daoStub  = sinon.stub(
							preferencesService._preferencesDao,
							"add"
						);
						daoStub.withArgs({
							key : "test-key",
							data: "test-data-object"
						})
						.rejects({
							code   : "ERROR",
							message: "Error"
						});

						let result;
						try {
							result = await preferencesService.add({
								key : "test-key",
								data: "test-data-object"
							});
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [
							{
								key : "test-key",
								data: "test-data-object"
							}
						];
						expect(daoParams)
						.toMatchObject(daoRequestParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
					}
				);
				it(
					"Return success",
					async () => {
						const expected = {
							code   : "SUCCESS",
							message: "Success"
						};
						const daoStub  = sinon.stub(
							preferencesService._preferencesDao,
							"add"
						);
						daoStub.withArgs({
							key : "test-key",
							data: "test-data-object"
						})
						.resolves({
							code   : "SUCCESS",
							message: "Success"
						});

						let result;
						try {
							result = await preferencesService.add({
								key : "test-key",
								data: "test-data-object"
							});
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [
							{
								key : "test-key",
								data: "test-data-object"
							}
						];
						expect(daoParams)
						.toMatchObject(daoRequestParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
					}
				);
			}
		);
		describe(
			`"all" function test`,
			() => {
				it(
					"Return reject DAO",
					async () => {
						const expected = {
							code   : "ERROR",
							message: "Error"
						};
						const daoStub  = sinon.stub(
							preferencesService._preferencesDao,
							"getByKey"
						);
						daoStub.withArgs({key: "test-key"})
						.rejects({
							code   : "ERROR",
							message: "Error"
						});

						let result;
						try {
							result = await preferencesService.all({key: "test-key"});
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [
							{key: "test-key"}
						];
						expect(daoParams)
						.toMatchObject(daoRequestParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
					}
				);
				it(
					"Return success with end ttl",
					async () => {
						const expected = {
							user: {
								timestamp: 1604261344256,
								endTTL   : true
							}
						};
						const daoStub  = sinon.stub(
							preferencesService._preferencesDao,
							"getByKey"
						);
						daoStub.withArgs({key: "test-key"})
						.resolves({
							user: {
								timestamp: 1604261344256
							}
						});

						let result;
						try {
							result = await preferencesService.all({key: "test-key"});
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [
							{key: "test-key"}
						];
						expect(daoParams)
						.toMatchObject(daoRequestParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
					}
				);
				it(
					"Return success",
					async () => {
						const expected = {
							user: {
								timestamp: 1604261344258
							}
						};
						const daoStub  = sinon.stub(
							preferencesService._preferencesDao,
							"getByKey"
						);
						daoStub.withArgs({key: "test-key"})
						.resolves({
							user: {
								timestamp: 1604261344258
							}
						});

						let result;
						try {
							result = await preferencesService.all({key: "test-key"});
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [
							{key: "test-key"}
						];
						expect(daoParams)
						.toMatchObject(daoRequestParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
					}
				);
			}
		);
	}
);
