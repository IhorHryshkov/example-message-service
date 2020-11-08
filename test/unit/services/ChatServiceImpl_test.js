/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-05T14:53
 */
//--------ChatServiceImpl_test.js--------

import ChatService from "../../../src/services/ChatServiceImpl";
import sinon       from "sinon";
import uuid        from "uuid";
import crypto      from "crypto";

describe(
	`"ChatService" test`,
	() => {
		let chatService;

		const noOp          = () => {
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
			},
		};

		beforeEach(() => {
			chatService = new ChatService({
				dbConfig,
				defaultParams,
			});
		});

		describe(
			`"add" function test`,
			() => {
				jest.spyOn(
					uuid,
					"v4",
				)
				.mockReturnValue("test-uuid");
				jest.spyOn(
					crypto,
					"createHash",
				)
				.mockReturnValue({
					update: () => {
						return {
							digest: () => {
								return {
									toLowerCase: () => {
										return "test-hash";
									},
								};
							},
						};
					},
				});
				it(
					"Return reject DAO",
					async () => {
						const expected = {
							code   : "ERROR",
							message: "Error",
						};
						const daoStub  = sinon.stub(
							chatService._chatDao,
							"add",
						);
						daoStub.withArgs({
							user_own_id: "test-user-own-id",
							chat_id    : "test-hash",
							user_id    : "test-user-own-id",
							id         : "test-uuid",
						})
						.rejects({
							code   : "ERROR",
							message: "Error",
						});

						let result;
						try {
							result = await chatService.add({
								user_own_id: "test-user-own-id",
								user_id    : "test-user-id",
							});
						} catch (e) {
							result = e;
						}

						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{
								user_own_id: "test-user-own-id",
								chat_id    : "test-hash",
								user_id    : "test-user-own-id",
								id         : "test-uuid",
							},
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected = {
							user_own_id: "test-user-own-id",
							chat_id    : "test-hash",
							user_id    : "test-user-own-id",
							id         : "test-uuid",
						};
						const daoStub  = sinon.stub(
							chatService._chatDao,
							"add",
						);
						daoStub.withArgs({
							user_own_id: "test-user-own-id",
							chat_id    : "test-hash",
							user_id    : "test-user-own-id",
							id         : "test-uuid",
						})
						.resolves({
							user_own_id: "test-user-own-id",
							chat_id    : "test-hash",
							user_id    : "test-user-own-id",
							id         : "test-uuid",
						});

						let result;
						try {
							result = await chatService.add({
								user_own_id: "test-user-own-id",
								user_id    : "test-user-id",
							});
						} catch (e) {
							result = e;
						}

						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{
								user_own_id: "test-user-own-id",
								chat_id    : "test-hash",
								user_id    : "test-user-own-id",
								id         : "test-uuid",
							},
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);


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
				jest.spyOn(
					crypto,
					"createHash",
				)
				.mockReturnValue({
					update: () => {
						return {
							digest: () => {
								return {
									toLowerCase: () => {
										return "test-hash";
									},
								};
							},
						};
					},
				});
				it(
					"Return reject DAO",
					async () => {
						const expected = {
							code   : "ERROR",
							message: "Error",
						};
						const daoStub  = sinon.stub(
							chatService._chatDao,
							"getById",
						);
						daoStub.withArgs({ chat_id: "test-hash" })
						.rejects({
							code   : "ERROR",
							message: "Error",
						});

						let result;
						try {
							result = await chatService.getById({ user_id: "test-user-id" });
						} catch (e) {
							result = e;
						}

						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{ chat_id: "test-hash" },
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected = {
							timestamp: 1111111111,
							body     : "test-body",
							chat_id  : "test-hash",
							user_id  : "test-user-id",
							id       : "test-uuid",
						};
						const daoStub  = sinon.stub(
							chatService._chatDao,
							"getById",
						);
						daoStub.withArgs({ chat_id: "test-hash" })
						.resolves({
							timestamp: 1111111111,
							body     : "test-body",
							chat_id  : "test-hash",
							user_id  : "test-user-id",
							id       : "test-uuid",
						});

						let result;
						try {
							result = await chatService.getById({ user_id: "test-user-id" });
						} catch (e) {
							result = e;
						}

						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{ chat_id: "test-hash" },
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
					},
				);
			},
		);
	},
);
