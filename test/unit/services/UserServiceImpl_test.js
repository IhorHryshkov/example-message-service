/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-06T18:09
 */
//--------UserServiceImpl_test.js--------

import UserService from "../../../src/services/UserServiceImpl";
import sinon       from "sinon";

describe(
	`"UserService" test`,
	() => {
		let userService;
		let timeStub;

		const noOp          = () => {
		};
		const netConfig     = {
			getAxios: noOp
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
					database: {
						version: 1
					}
				},
				callback: {
					prefixStorageKey : "test_",
					postfixStorageKey: "_test",
					enums            : {
						ADD_USER      : "ADD_USER",
						UPDATE_USER   : "UPDATE_USER",
						UPDATE_COUNTER: "UPDATE_COUNTER"
					}
				},
				user    : {
					default: {
						status: "testName"
					}
				}
			}
		};

		beforeAll(() => {
			timeStub = sinon.stub(
				Date,
				"now"
			)
			.returns(1604261344258);
		});

		beforeEach(() => {
			userService = new UserService({
				netConfig,
				dbConfig,
				defaultParams
			});
		});

		afterAll(() => {
			timeStub.restore();
		});

		describe(
			`"all" function test`,
			() => {
				it(
					"Return reject request",
					async () => {
						const expected    = {
							code   : "ERROR",
							message: "Error"
						};
						const requestStub = sinon.stub(
							userService._userRequest,
							"all"
						);
						requestStub.withArgs({
							id      : "test-user-id",
							username: "test-username"
						})
						.rejects({
							code   : "ERROR",
							message: "Error"
						});

						let result;
						try {
							result = await userService.all({
								params : {
									id      : "test-user-id",
									username: "test-username"
								},
								user_id: "test-user-id-exclude"
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id      : "test-user-id",
								username: "test-username"
							}
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);

						expect(result)
						.toEqual(expected);
						requestStub.restore();
					}
				);
				it(
					"Return reject DAO",
					async () => {
						const expected    = {
							code   : "ERROR",
							message: "Error"
						};
						const requestStub = sinon.stub(
							userService._userRequest,
							"all"
						);
						requestStub.withArgs({
							id      : "test-user-id",
							username: "test-username"
						})
						.resolves({
							status: 200,
							data  : {
								data: [
									{
										id      : "test-user-id",
										username: "test-username",
										status  : {
											id  : 1,
											name: "testName"
										}
									}
								]
							}
						});
						const daoAddStub = sinon.stub(
							userService._usersDao,
							"add"
						);
						daoAddStub.withArgs({
							excludeId: "test-user-id-exclude",
							data     : [
								{
									id      : "test-user-id",
									username: "test-username",
									status  : {
										id  : 1,
										name: "testName"
									}
								}
							]
						})
						.rejects({
							code   : "ERROR",
							message: "Error"
						});

						let result;
						try {
							result = await userService.all({
								params : {
									id      : "test-user-id",
									username: "test-username"
								},
								user_id: "test-user-id-exclude"
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id      : "test-user-id",
								username: "test-username"
							}
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						const daoAddParams         = daoAddStub.getCall(0).args;
						const expectedDaoAddParams = [
							{
								excludeId: "test-user-id-exclude",
								data     : [
									{
										id      : "test-user-id",
										username: "test-username",
										status  : {
											id  : 1,
											name: "testName"
										}
									}
								]
							}
						];
						expect(daoAddParams)
						.toMatchObject(expectedDaoAddParams);

						expect(result)
						.toEqual(expected);
						daoAddStub.restore();
						requestStub.restore();
					}
				);
				it(
					"Return undefined if request return error",
					async () => {
						const expected    = undefined;
						const requestStub = sinon.stub(
							userService._userRequest,
							"all"
						);
						requestStub.withArgs({
							id      : "test-user-id",
							username: "test-username"
						})
						.resolves({
							status: 400,
							data  : {
								error: {
									code   : 400,
									message: "Error"
								}
							}
						});
						const daoStub = sinon.stub(
							userService._usersDao,
							"all"
						);
						daoStub.withArgs({sort: "updateUserAt"})
						.resolves(undefined);

						let result;
						try {
							result = await userService.all({
								params : {
									id      : "test-user-id",
									username: "test-username"
								},
								user_id: "test-user-id-exclude"
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id      : "test-user-id",
								username: "test-username"
							}
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{sort: "updateUserAt"}
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);

						expect(result)
						.toEqual(expected);
						requestStub.restore();
						daoStub.restore();
					}
				);
				it(
					"Return old saved data if request return error",
					async () => {
						const expected    = [
							{
								id      : "test-user-id",
								username: "test-username",
								status  : {
									id  : 1,
									name: "testName"
								}
							}
						];
						const requestStub = sinon.stub(
							userService._userRequest,
							"all"
						);
						requestStub.withArgs({
							id      : "test-user-id",
							username: "test-username"
						})
						.resolves({
							status: 400,
							data  : {
								error: {
									code   : 400,
									message: "Error"
								}
							}
						});
						const daoStub = sinon.stub(
							userService._usersDao,
							"all"
						);
						daoStub.withArgs({sort: "updateUserAt"})
						.resolves([
							{
								id      : "test-user-id",
								username: "test-username",
								status  : {
									id  : 1,
									name: "testName"
								}
							}
						]);

						let result;
						try {
							result = await userService.all({
								params : {
									id      : "test-user-id",
									username: "test-username"
								},
								user_id: "test-user-id-exclude"
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id      : "test-user-id",
								username: "test-username"
							}
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{sort: "updateUserAt"}
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);

						expect(result)
						.toEqual(expected);
						requestStub.restore();
						daoStub.restore();
					}
				);
				it(
					"Return success",
					async () => {
						const expected    = [
							{
								id      : "test-user-id",
								username: "test-username",
								status  : {
									id  : 1,
									name: "testName"
								}
							}
						];
						const requestStub = sinon.stub(
							userService._userRequest,
							"all"
						);
						requestStub.withArgs({
							id      : "test-user-id",
							username: "test-username"
						})
						.resolves({
							status: 200,
							data  : {
								data: [
									{
										id      : "test-user-id",
										username: "test-username",
										status  : {
											id  : 1,
											name: "testName"
										}
									}
								]
							}
						});
						const daoAddStub = sinon.stub(
							userService._usersDao,
							"add"
						);
						daoAddStub.withArgs({
							excludeId: "test-user-id-exclude",
							data     : [
								{
									id      : "test-user-id",
									username: "test-username",
									status  : {
										id  : 1,
										name: "testName"
									}
								}
							]
						})
						.resolves({
							code   : "SUCCESS",
							message: "Success"
						});
						const daoStub = sinon.stub(
							userService._usersDao,
							"all"
						);
						daoStub.withArgs({sort: "updateUserAt"})
						.resolves([
							{
								id      : "test-user-id",
								username: "test-username",
								status  : {
									id  : 1,
									name: "testName"
								}
							}
						]);

						let result;
						try {
							result = await userService.all({
								params : {
									id      : "test-user-id",
									username: "test-username"
								},
								user_id: "test-user-id-exclude"
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id      : "test-user-id",
								username: "test-username"
							}
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						const daoAddParams         = daoAddStub.getCall(0).args;
						const expectedDaoAddParams = [
							{
								excludeId: "test-user-id-exclude",
								data     : [
									{
										id      : "test-user-id",
										username: "test-username",
										status  : {
											id  : 1,
											name: "testName"
										}
									}
								]
							}
						];
						expect(daoAddParams)
						.toMatchObject(expectedDaoAddParams);
						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{sort: "updateUserAt"}
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);

						expect(result)
						.toEqual(expected);
						requestStub.restore();
						daoStub.restore();
						daoAddStub.restore();
					}
				);
			}
		);
		describe(
			`"select" function test`,
			() => {
				it(
					"Return reject DAO",
					async () => {
						const expected = {
							code   : "ERROR",
							message: "Error"
						};
						const daoStub  = sinon.stub(
							userService._usersDao,
							"update"
						);
						daoStub.withArgs({
							id  : "test-user-id",
							data: {updateUserAt: 1604261344258}
						})
						.rejects({
							code   : "ERROR",
							message: "Error"
						});

						let result;
						try {
							result = await userService.select("test-user-id");
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [
							{
								id  : "test-user-id",
								data: {updateUserAt: 1604261344258}
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
						const expected = 1;
						const daoStub  = sinon.stub(
							userService._usersDao,
							"update"
						);
						daoStub.withArgs({
							id  : "test-user-id",
							data: {updateUserAt: 1604261344258}
						})
						.resolves(1);

						let result;
						try {
							result = await userService.select("test-user-id");
						} catch (e) {
							result = e;
						}

						const daoParams        = daoStub.getCall(0).args;
						const daoRequestParams = [
							{
								id  : "test-user-id",
								data: {updateUserAt: 1604261344258}
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
			`"add" function test`,
			() => {
				it(
					"Return reject request",
					async () => {
						const expected    = "Test error";
						const requestStub = sinon.stub(
							userService._userRequest,
							"add"
						);
						requestStub.withArgs({username: "test-username"})
						.rejects(Error("Test error"));
						let result;
						try {
							result = await userService.add({username: "test-username"});
						} catch (e) {
							result = e.message;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [{username: "test-username"}];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						expect(result)
						.toEqual(expected);
						requestStub.restore();
					}
				);
				it(
					"Return reject DAO",
					async () => {
						const expected    = {
							code   : "ERROR",
							message: "Error"
						};
						const requestStub = sinon.stub(
							userService._userRequest,
							"add"
						);
						requestStub.withArgs({username: "test-username"})
						.resolves({
							status: 201,
							data  : {
								resId: "test-res-id",
								data : {username: "test-username"}
							}
						});
						const daoStub = sinon.stub(
							userService._callbackDao,
							"add"
						);
						daoStub.withArgs({
							key : `test_test-username_test-res-id_test`,
							data: {callback: "ADD_USER"}
						})
						.rejects({
							code   : "ERROR",
							message: "Error"
						});

						let result;
						try {
							result = await userService.add({username: "test-username"});
						} catch (e) {
							result = e;
						}
						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{
								key : `test_test-username_test-res-id_test`,
								data: {callback: "ADD_USER"}
							}
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [{username: "test-username"}];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						expect(result)
						.toMatchObject(expected);
						requestStub.restore();
						daoStub.restore();
					}
				);
				it(
					"Return error data if request return error",
					async () => {
						const expected    = {
							status: 400,
							data  : {
								resId    : "test-res-id",
								error    : {
									code   : 400,
									message: "Test error"
								},
								timestamp: 1111111111111
							}
						};
						const requestStub = sinon.stub(
							userService._userRequest,
							"add"
						);
						requestStub.withArgs({username: "test-username"})
						.resolves({
							status: 400,
							data  : {
								resId    : "test-res-id",
								error    : {
									code   : 400,
									message: "Test error"
								},
								timestamp: 1111111111111
							}
						});
						let result;
						try {
							result = await userService.add({username: "test-username"});
						} catch (e) {
							result = e.message;
						}
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [{username: "test-username"}];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						expect(result)
						.toMatchObject(expected);
						requestStub.restore();
					}
				);
				it(
					"Return success",
					async () => {
						const expected    = {
							status: 201,
							data  : {
								resId    : "test-res-id",
								data     : {username: "test-username"},
								timestamp: 1111111111111
							}
						};
						const requestStub = sinon.stub(
							userService._userRequest,
							"add"
						);
						requestStub.withArgs({username: "test-username"})
						.resolves({
							status: 201,
							data  : {
								resId    : "test-res-id",
								data     : {username: "test-username"},
								timestamp: 1111111111111
							}
						});
						const daoStub = sinon.stub(
							userService._callbackDao,
							"add"
						);
						daoStub.withArgs({
							key : `test_test-username_test-res-id_test`,
							data: {callback: "ADD_USER"}
						})
						.resolves({
							code   : "SUCCESS",
							message: "Success"
						});

						let result;
						try {
							result = await userService.add({username: "test-username"});
						} catch (e) {
							result = e;
						}
						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{
								key : `test_test-username_test-res-id_test`,
								data: {callback: "ADD_USER"}
							}
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [{username: "test-username"}];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						expect(result)
						.toMatchObject(expected);
						requestStub.restore();
						daoStub.restore();
					}
				);
			}
		);
		describe(
			`"update" function test`,
			() => {
				it(
					"Return reject DAO",
					async () => {
						const expected      = {
							code   : "ERROR",
							message: "Error"
						};
						const daoStatusStub = sinon.stub(
							userService._statusDao,
							"getByName"
						);
						daoStatusStub.withArgs({name: "testName"})
						.rejects({
							code   : "ERROR",
							message: "Error"
						});
						let result;
						try {
							result = await userService.update({
								id      : "test-user-id",
								username: "test-username"
							});
						} catch (e) {
							result = e;
						}

						const daoStatusParams         = daoStatusStub.getCall(0).args;
						const expectedDaoStatusParams = [{name: "testName"}];
						expect(daoStatusParams)
						.toMatchObject(expectedDaoStatusParams);
						expect(result)
						.toEqual(expected);
						daoStatusStub.restore();
					}
				);
				it(
					"Return reject request",
					async () => {
						const expected      = "Test error";
						const daoStatusStub = sinon.stub(
							userService._statusDao,
							"getByName"
						);
						daoStatusStub.withArgs({name: "testName"})
						.resolves({
							id  : 1,
							name: "testName"
						});
						const requestStub = sinon.stub(
							userService._userRequest,
							"update"
						);
						requestStub.withArgs({
							id      : "test-user-id",
							statusId: 1
						})
						.rejects(Error("Test error"));
						let result;
						try {
							result = await userService.update({
								id      : "test-user-id",
								username: "test-username"
							});
						} catch (e) {
							result = e.message;
						}
						const daoStatusParams         = daoStatusStub.getCall(0).args;
						const expectedDaoStatusParams = [{name: "testName"}];
						expect(daoStatusParams)
						.toMatchObject(expectedDaoStatusParams);
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id      : "test-user-id",
								statusId: 1
							}
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						expect(result)
						.toEqual(expected);
						requestStub.restore();
						daoStatusStub.restore();
					}
				);
				it(
					"Return error data if request return error",
					async () => {
						const expected      = {
							status: 400,
							data  : {
								resId    : "test-res-id",
								error    : {
									code   : 400,
									message: "Test error"
								},
								timestamp: 1111111111111
							}
						};
						const daoStatusStub = sinon.stub(
							userService._statusDao,
							"getByName"
						);
						daoStatusStub.withArgs({name: "testName"})
						.resolves({
							id  : 1,
							name: "testName"
						});
						const requestStub = sinon.stub(
							userService._userRequest,
							"update"
						);
						requestStub.withArgs({
							id      : "test-user-id",
							statusId: 1
						})
						.resolves({
							status: 400,
							data  : {
								resId    : "test-res-id",
								error    : {
									code   : 400,
									message: "Test error"
								},
								timestamp: 1111111111111
							}
						});
						let result;
						try {
							result = await userService.update({
								id      : "test-user-id",
								username: "test-username"
							});
						} catch (e) {
							result = e.message;
						}
						const daoStatusParams         = daoStatusStub.getCall(0).args;
						const expectedDaoStatusParams = [{name: "testName"}];
						expect(daoStatusParams)
						.toMatchObject(expectedDaoStatusParams);
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id      : "test-user-id",
								statusId: 1
							}
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						expect(result)
						.toMatchObject(expected);
						requestStub.restore();
						daoStatusStub.restore();
					}
				);
				it(
					"Return success",
					async () => {
						const expected      = {
							status: 200,
							data  : {
								resId    : "test-res-id",
								data     : {
									statusId: 1,
									userId  : "test-user-id"
								},
								timestamp: 1111111111111
							}
						};
						const daoStatusStub = sinon.stub(
							userService._statusDao,
							"getByName"
						);
						daoStatusStub.withArgs({name: "testName"})
						.resolves({
							id  : 1,
							name: "testName"
						});
						const requestStub = sinon.stub(
							userService._userRequest,
							"update"
						);
						requestStub.withArgs({
							id      : "test-user-id",
							statusId: 1
						})
						.resolves({
							status: 200,
							data  : {
								resId    : "test-res-id",
								data     : {
									statusId: 1,
									userId  : "test-user-id"
								},
								timestamp: 1111111111111
							}
						});
						const daoCallbackStub = sinon.stub(
							userService._callbackDao,
							"add"
						);
						daoCallbackStub.withArgs({
							key : `test_test-username_test-res-id_test`,
							data: {callback: "UPDATE_USER"}
						})
						.resolves({
							code   : "SUCCESS",
							message: "Success"
						});
						let result;
						try {
							result = await userService.update({
								id      : "test-user-id",
								username: "test-username"
							});
						} catch (e) {
							result = e.message;
						}
						const daoStatusParams         = daoStatusStub.getCall(0).args;
						const expectedDaoStatusParams = [{name: "testName"}];
						expect(daoStatusParams)
						.toMatchObject(expectedDaoStatusParams);
						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id      : "test-user-id",
								statusId: 1
							}
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						const daoCallbackParams         = daoCallbackStub.getCall(0).args;
						const expectedDaoCallbackParams = [
							{
								key : `test_test-username_test-res-id_test`,
								data: {callback: "UPDATE_USER"}
							}
						];
						expect(daoCallbackParams)
						.toMatchObject(expectedDaoCallbackParams);
						expect(result)
						.toEqual(expected);
						requestStub.restore();
						daoStatusStub.restore();
						daoCallbackStub.restore();
					}
				);
			}
		);
	}
);
