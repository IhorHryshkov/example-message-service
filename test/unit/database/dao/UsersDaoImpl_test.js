/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-01T19:27
 */
//--------UsersDaoImpl_test.js--------

import UsersDao from "../../../../src/database/dao/UsersDaoImpl";
import sinon    from "sinon";

describe(
	`"UsersDao" test`,
	() => {
		let usersDao;
		let timeStub;

		const noOp          = () => {
		};
		const dbConfig      = {
			getLocal    : noOp,
			getIndexDB  : () => {
				return {
					users: {
						add    : noOp,
						update : noOp,
						reverse: () => {
							return {
								sortBy: noOp,
							};
						},
						where  : () => {
							return {
								first           : noOp,
								equalsIgnoreCase: () => {
									return {
										first: noOp,
									};
								},
							};
						},
					},
				};
			},
			getValidator: () => {
				return {
					initSchemas   : noOp,
					validateSchema: noOp,
				};
			},
		};
		const defaultParams = {
			constants: {
				global: {
					database: {
						version: 1,
					},
				},
			},
			messages : {
				"info": {
					"success": {
						"message": "Success",
						"code"   : "SUCCESS",
					},
				},
			},
		};

		beforeAll(() => {
			timeStub = sinon.stub(Date, "now").returns(1604261344258);
		});

		beforeEach(() => {
			usersDao = new UsersDao(
				dbConfig,
				defaultParams,
			);
		});

		afterAll(() => {
			timeStub.restore();
		});

		describe(
			`"add" function test`,
			() => {
				it(
					"Return reject validation",
					async () => {
						const expected     = {
							code   : "ERROR",
							message: "Error",
						};
						const validateStub = sinon.stub(
							usersDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							[
								{
									id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
									username: "Tester",
									status  : {
										id  : 1,
										name: "testName",
									},
								},
							],
						)
						.returns({
							code   : "ERROR",
							message: "Error",
						});
						let result;
						try {
							await usersDao.add({
								data     : [
									{
										id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
										username: "Tester",
										status  : {
											id  : 1,
											name: "testName",
										},
									},
								],
								excludeId: "",
							});
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						validateStub.restore();
					},
				);
				it(
					"Return resolve when skip any error in dexie add method",
					async () => {
						const expected     = {
							code   : "SUCCESS",
							message: "Success",
						};
						const validateStub = sinon.stub(
							usersDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							[
								{
									id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
									username: "Tester",
									status  : {
										id  : 1,
										name: "testName",
									},
								},
							],
						)
						.returns(null);
						const addStub = sinon.stub(
							usersDao._indexDB.users,
							"add",
						);
						addStub.withArgs({
							id          : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
							username    : "Tester",
							status      : {
								id  : 1,
								name: "testName",
							},
							updateUserAt: 1604261344258,
						}, "1a0db4d9-886d-4be3-8e79-08e16af599d6").rejects(Error("Test error"));

						const result = await usersDao.add({
							data     : [
								{
									id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
									username: "Tester",
									status  : {
										id  : 1,
										name: "testName",
									},
								},
							],
							excludeId: "",
						});
						expect(result)
						.toEqual(expected);
						validateStub.restore();
						addStub.restore();
					},
				);
				it(
					"Return success if one user in array and user is equal excluded user ID",
					async () => {
						const expected     = {
							code   : "SUCCESS",
							message: "Success",
						};
						const validateStub = sinon.stub(
							usersDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							[
								{
									id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
									username: "Tester",
									status  : {
										id  : 1,
										name: "testName",
									},
								},
							],
						)
						.returns(null);
						const result = await usersDao.add({
							data     : [
								{
									id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
									username: "Tester",
									status  : {
										id  : 1,
										name: "testName",
									},
								},
							],
							excludeId: "1a0db4d9-886d-4be3-8e79-08e16af599d6",
						});
						expect(result)
						.toEqual(expected);
						validateStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected     = {
							code   : "SUCCESS",
							message: "Success",
						};
						const validateStub = sinon.stub(
							usersDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							[
								{
									id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
									username: "Tester",
									status  : {
										id  : 1,
										name: "testName",
									},
								},
							],
						)
						.returns(null);
						const addStub = sinon.stub(
							usersDao._indexDB.users,
							"add",
						);
						addStub.withArgs({
							id          : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
							username    : "Tester",
							status      : {
								id  : 1,
								name: "testName",
							},
							updateUserAt: 1604261344258,
						}, "1a0db4d9-886d-4be3-8e79-08e16af599d6").resolves(1);
						const result = await usersDao.add({
							data     : [
								{
									id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
									username: "Tester",
									status  : {
										id  : 1,
										name: "testName",
									},
								},
							],
							excludeId: "1a0db4d9-886d-4be3-8e79-08e16af599d8",
						});
						expect(result)
						.toEqual(expected);
						validateStub.restore();
						addStub.restore();
					},
				);
			},
		);
		describe(
			`"getById" function test`,
			() => {
				it(
					"Return reject any error",
					async () => {
						const expected  = {
							code   : "ERROR",
							message: "Test error",
						};
						const whereStub = sinon.stub(
							usersDao._indexDB.users,
							"where",
						);
						whereStub.withArgs({ id: "1a0db4d9-886d-4be3-8e79-08e16af599d6" })
						.returns({ first: noOp });
						const firstStub = sinon.stub(
							usersDao._indexDB.users.where(
								{ id: "1a0db4d9-886d-4be3-8e79-08e16af599d6" }),
							"first",
						);
						firstStub.rejects(Error("Test error"));
						let result;
						try {
							await usersDao.getById({ id: "1a0db4d9-886d-4be3-8e79-08e16af599d6" });
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						firstStub.restore();
					},
				);
				it(
					"Return reject validation",
					async () => {
						const expected  = {
							code   : "ERROR",
							message: "Error",
						};
						const whereStub = sinon.stub(
							usersDao._indexDB.users,
							"where",
						);
						whereStub.withArgs({ id: "1a0db4d9-886d-4be3-8e79-08e16af599d6" })
						.returns({ first: noOp });
						const firstStub = sinon.stub(
							usersDao._indexDB.users.where(
								{ id: "1a0db4d9-886d-4be3-8e79-08e16af599d6" }),
							"first",
						);
						firstStub.resolves({
							id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
							username: "Tester",
							status  : {
								id  : 1,
								name: "testName",
							},
						});
						const validateStub = sinon.stub(
							usersDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"getById",
							{
								id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
								username: "Tester",
								status  : {
									id  : 1,
									name: "testName",
								},
							},
						)
						.returns({
							code   : "ERROR",
							message: "Error",
						});
						let result;
						try {
							await usersDao.getById({ id: "1a0db4d9-886d-4be3-8e79-08e16af599d6" });
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						firstStub.restore();
						validateStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected  = {
							id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
							username: "Tester",
							status  : {
								id  : 1,
								name: "testName",
							},
						};
						const whereStub = sinon.stub(
							usersDao._indexDB.users,
							"where",
						);
						whereStub.withArgs({ id: "1a0db4d9-886d-4be3-8e79-08e16af599d6" })
						.returns({ first: noOp });
						const firstStub = sinon.stub(
							usersDao._indexDB.users.where(
								{ id: "1a0db4d9-886d-4be3-8e79-08e16af599d6" }),
							"first",
						);
						firstStub.resolves({
							id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
							username: "Tester",
							status  : {
								id  : 1,
								name: "testName",
							},
						});
						const validateStub = sinon.stub(
							usersDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"getById",
							{
								id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
								username: "Tester",
								status  : {
									id  : 1,
									name: "testName",
								},
							},
						)
						.returns(null);
						const result = await usersDao.getById({ id: "1a0db4d9-886d-4be3-8e79-08e16af599d6" });
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						firstStub.restore();
						validateStub.restore();
					},
				);
			},
		);
		describe(
			`"all" function test`,
			() => {
				it(
					"Return reject any error",
					async () => {
						const expected  = {
							code   : "ERROR",
							message: "Test error",
						};
						const whereStub = sinon.stub(
							usersDao._indexDB.users,
							"reverse",
						);
						whereStub.returns({ sortBy: noOp });
						const sortByStub = sinon.stub(
							usersDao._indexDB.users.reverse(),
							"sortBy",
						);
						sortByStub.withArgs("timestamp").rejects(Error("Test error"));
						let result;
						try {
							await usersDao.all({ sort: "timestamp" });
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						sortByStub.restore();
					},
				);
				it(
					"Return reject validation",
					async () => {
						const expected  = {
							code   : "ERROR",
							message: "Error",
						};
						const whereStub = sinon.stub(
							usersDao._indexDB.users,
							"reverse",
						);
						whereStub.returns({ sortBy: noOp });
						const sortByStub = sinon.stub(
							usersDao._indexDB.users.reverse(),
							"sortBy",
						);
						sortByStub.withArgs("timestamp").resolves([
							{
								id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
								username: "Tester",
								status  : {
									id  : 1,
									name: "testName",
								},
							},
						]);
						const validateStub = sinon.stub(
							usersDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"all",
							[
								{
									id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
									username: "Tester",
									status  : {
										id  : 1,
										name: "testName",
									},
								},
							],
						)
						.returns({
							code   : "ERROR",
							message: "Error",
						});
						let result;
						try {
							await usersDao.all({ sort: "timestamp" });
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						sortByStub.restore();
						validateStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected  = [
							{
								id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
								username: "Tester",
								status  : {
									id  : 1,
									name: "testName",
								},
							},
						];
						const whereStub = sinon.stub(
							usersDao._indexDB.users,
							"reverse",
						);
						whereStub.returns({ sortBy: noOp });
						const sortByStub = sinon.stub(
							usersDao._indexDB.users.reverse(),
							"sortBy",
						);
						sortByStub.withArgs("timestamp").resolves([
							{
								id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
								username: "Tester",
								status  : {
									id  : 1,
									name: "testName",
								},
							},
						]);
						const validateStub = sinon.stub(
							usersDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"all",
							[
								{
									id      : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
									username: "Tester",
									status  : {
										id  : 1,
										name: "testName",
									},
								},
							],
						)
						.returns(null);
						const result = await usersDao.all({ sort: "timestamp" });
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						validateStub.restore();
						sortByStub.restore();
					},
				);
			},
		);
		describe(
			`"update" function test`,
			() => {
				it(
					"Return reject any error",
					async () => {
						const expected  = {
							code   : "ERROR",
							message: "Test error",
						};
						const whereStub = sinon.stub(
							usersDao._indexDB.users,
							"update",
						);
						whereStub.withArgs("1a0db4d9-886d-4be3-8e79-08e16af599d6", {
							updateUserAt: 1604265237205,
						}).rejects(Error("Test error"));
						let result;
						try {
							await usersDao.update({
								id  : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
								data: {
									updateUserAt: 1604265237205,
								},
							});
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						whereStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected  = 1;
						const whereStub = sinon.stub(
							usersDao._indexDB.users,
							"update",
						);
						whereStub.withArgs("1a0db4d9-886d-4be3-8e79-08e16af599d6", {
							updateUserAt: 1604265237205,
						}).resolves(1);
						const result = await usersDao.update({
							id  : "1a0db4d9-886d-4be3-8e79-08e16af599d6",
							data: {
								updateUserAt: 1604265237205,
							},
						});
						expect(result)
						.toEqual(expected);
						whereStub.restore();
					},
				);
			},
		);
	},
);
