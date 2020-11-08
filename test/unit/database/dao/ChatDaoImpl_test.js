/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-26T20:06
 */
//--------ChatDaoImpl_test.js--------

import ChatDao from "../../../../src/database/dao/ChatDaoImpl";
import sinon   from "sinon";

describe(
	`"ChatDao" test`,
	() => {
		let chatDao;

		const noOp          = () => {
		};
		const dbConfig      = {
			getLocal    : noOp,
			getIndexDB  : () => {
				return {
					chat: {
						put  : noOp,
						where: () => {
							return {
								limit: () => {
									return {
										sortBy: noOp,
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
		};

		beforeEach(() => {
			chatDao = new ChatDao(
				dbConfig,
				defaultParams,
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
							message: "Error",
						};
						const validateStub = sinon.stub(
							chatDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							{
								id  : 1,
								name: "Test",
							},
						)
							.returns({
								code   : "ERROR",
								message: "Error",
							});
						let result;
						try {
							await chatDao.add({
								id  : 1,
								name: "Test",
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
					"Return success",
					async () => {
						const expected     = {
							id  : 1,
							name: "Test",
						};
						const validateStub = sinon.stub(
							chatDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							{
								id  : 1,
								name: "Test",
							},
						)
							.returns(null);
						let result = await chatDao.add({
							id  : 1,
							name: "Test",
						});
						expect(result)
							.toEqual(expected);
						validateStub.restore();
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
							chatDao._indexDB.chat,
							"where",
						);
						whereStub.withArgs({ chat_id: "Test" })
						.returns({ limit: noOp });
						const limitsStub = sinon.stub(
							chatDao._indexDB.chat.where({ chat_id: "Test" }),
							"limit",
						);
						limitsStub.withArgs(25)
							.returns({ sortBy: noOp });
						const sortByStub = sinon.stub(
							chatDao._indexDB.chat.where({ chat_id: "Test" })
								.limit(25),
							"sortBy",
						);
						sortByStub.withArgs("timestamp")
							.rejects(Error("Test error"));
						let result;
						try {
							await chatDao.getById({ chat_id: "Test" });
						} catch (e) {
							result = e;
						}
						expect(result)
							.toEqual(expected);
						whereStub.restore();
						limitsStub.restore();
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
							chatDao._indexDB.chat,
							"where",
						);
						whereStub.withArgs({ chat_id: "Test" })
							.returns({ limit: noOp });
						const limitsStub = sinon.stub(
							chatDao._indexDB.chat.where({ chat_id: "Test" }),
							"limit",
						);
						limitsStub.withArgs(25)
							.returns({ sortBy: noOp });
						const sortByStub = sinon.stub(
							chatDao._indexDB.chat.where({ chat_id: "Test" })
								.limit(25),
							"sortBy",
						);
						sortByStub.withArgs("timestamp")
							.resolves([]);
						const validateStub = sinon.stub(
							chatDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"getById",
							[],
						)
							.returns({
								code   : "ERROR",
								message: "Error",
							});

						let result;
						try {
							await chatDao.getById({ chat_id: "Test" });
						} catch (e) {
							result = e;
						}
						expect(result)
							.toEqual(expected);
						whereStub.restore();
						limitsStub.restore();
						sortByStub.restore();
						validateStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected  = [
							{
								chat_id  : "Test",
								message  : "Test",
								timestamp: 1111111111,
							},
						];
						const whereStub = sinon.stub(
							chatDao._indexDB.chat,
							"where",
						);
						whereStub.withArgs({ chat_id: "Test" })
							.returns({ limit: noOp });
						const limitsStub = sinon.stub(
							chatDao._indexDB.chat.where({ chat_id: "Test" }),
							"limit",
						);
						limitsStub.withArgs(25)
							.returns({ sortBy: noOp });
						const sortByStub = sinon.stub(
							chatDao._indexDB.chat.where({ chat_id: "Test" })
								.limit(25),
							"sortBy",
						);
						sortByStub.withArgs("timestamp")
							.resolves([
								{
									chat_id  : "Test",
									message  : "Test",
									timestamp: 1111111111,
								},
							]);
						const validateStub = sinon.stub(
							chatDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"getById",
							[
								{
									chat_id  : "Test",
									message  : "Test",
									timestamp: 1111111111,
								},
							],
						)
							.returns(null);

						const result = await chatDao.getById({ chat_id: "Test" });
						expect(result)
							.toEqual(expected);
						whereStub.restore();
						limitsStub.restore();
						sortByStub.restore();
						validateStub.restore();
					},
				);
			},
		);
	},
);
