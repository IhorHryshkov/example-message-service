/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-03T16:52
 */
//--------WebSocketImpl_test.js--------

import WebSocket from "../../../../src/network/websocket/WebSocketImpl";
import sinon     from "sinon";

describe(
	`"UserRequest" test`,
	() => {
		let webSocket;
		let timeStub;

		const noOp          = () => {
		};
		const netConfig     = {
			addSockSubscribe: noOp,
			getSockConnect  : () => {
				return {
					configure: noOp,
					subscribe: noOp,
					send     : noOp,
				};
			},
		};
		const defaultParams = {};

		beforeAll(() => {
			timeStub = sinon.stub(
				Date,
				"now",
			)
				.returns(1604261344258);
		});

		beforeEach(() => {
			webSocket = new WebSocket({
				netConfig,
				defaultParams,
				serviceQueue: "/topicOrQueue/test/",
				clientName  : "test",
			});
		});

		afterAll(() => {
			timeStub.restore();
		});

		describe(
			`"receiveQueue" function test`,
			() => {
				it(
					"Exception received queue",
					async () => {
						const expected      = "Test error";
						const configureStub = sinon.stub(
							webSocket._client,
							"configure",
						);
						configureStub.throws(Error("Test error"));
						let result;
						try {
							await webSocket.receiveQueue(
								"test",
								"test",
								noOp,
								noOp,
							);
						} catch (e) {
							result = e.message;
						}
						expect(result)
							.toEqual(expected);
						configureStub.restore();
					},
				);
				it(
					"Successful received queue",
					async () => {
						const expected      = undefined;
						const subscribeStub = sinon.stub(
							webSocket._client,
							"subscribe",
						);
						subscribeStub.returns({ test: "test" });
						const addSubscribeStub = sinon.stub(
							webSocket._net,
							"addSockSubscribe",
						);

						let result;
						try {
							await webSocket.receiveQueue(
								"test",
								"test",
								noOp,
								noOp,
							);
						} catch (e) {
							result = e.message;
						}
						const subscribeParams         = subscribeStub.getCall(0).args;
						const expectedSubscribeParams = [
							"/topicOrQueue/test/test",
							expect.any(Function),
							{
								durable      : true,
								"auto-delete": false,
								ack          : "client-individual",
							},
						];
						expect(subscribeParams)
							.toMatchObject(expectedSubscribeParams);
						const addSubscribeParams         = addSubscribeStub.getCall(0).args;
						const expectedAddSubscribeParams = [
							{ test: "test" },
							"test",
						];
						expect(addSubscribeParams)
							.toMatchObject(expectedAddSubscribeParams);

						expect(result)
							.toEqual(expected);
						subscribeStub.restore();
						addSubscribeStub.restore();
					},
				);
			},
		);
		describe(
			`"receiveTopic" function test`,
			() => {
				it(
					"Exception received queue",
					async () => {
						const expected      = "Test error";
						const configureStub = sinon.stub(
							webSocket._client,
							"configure",
						);
						configureStub.throws(Error("Test error"));
						let result;
						try {
							await webSocket.receiveTopic(
								"testTopic",
								"test",
								"test",
								noOp,
								noOp,
							);
						} catch (e) {
							result = e.message;
						}
						expect(result)
							.toEqual(expected);
						configureStub.restore();
					},
				);
				it(
					"Successful received queue",
					async () => {
						const expected      = undefined;
						const subscribeStub = sinon.stub(
							webSocket._client,
							"subscribe",
						);
						subscribeStub.returns({ test: "test" });
						const addSubscribeStub = sinon.stub(
							webSocket._net,
							"addSockSubscribe",
						);

						let result;
						try {
							await webSocket.receiveTopic(
								"testTopic",
								"test",
								"test",
								noOp,
								noOp,
							);
						} catch (e) {
							result = e.message;
						}
						const subscribeParams         = subscribeStub.getCall(0).args;
						const expectedSubscribeParams = [
							"/topicOrQueue/test/testTopic",
							expect.any(Function),
							{
								"x-queue-name": "test",
								id            : "test-1604261344258",
								durable       : true,
								"auto-delete" : false,
								ack           : "client",
							},
						];
						expect(subscribeParams)
							.toMatchObject(expectedSubscribeParams);
						const addSubscribeParams         = addSubscribeStub.getCall(0).args;
						const expectedAddSubscribeParams = [
							{ test: "test" },
							"test",
						];
						expect(addSubscribeParams)
							.toMatchObject(expectedAddSubscribeParams);

						expect(result)
							.toEqual(expected);
						subscribeStub.restore();
						addSubscribeStub.restore();
					},
				);
			},
		);
		describe(
			`"send" function test`,
			() => {
				it(
					"Exception send queue",
					async () => {
						const expected = "Test error";
						const sendStub = sinon.stub(
							webSocket._client,
							"send",
						);
						sendStub.withArgs(
							"/topicOrQueue/test/test",
							{},
							{ test: "test" },
						)
							.throws(Error("Test error"));
						let result;
						try {
							await webSocket.send(
								"test",
								{ test: "test" },
							);
						} catch (e) {
							result = e.message;
						}
						const subscribeParams         = sendStub.getCall(0).args;
						const expectedSubscribeParams = [
							"/topicOrQueue/test/test",
							{},
							{ test: "test" },
						];
						expect(subscribeParams)
							.toMatchObject(expectedSubscribeParams);

						expect(result)
							.toEqual(expected);
						sendStub.restore();
					},
				);
				it(
					"Successful send queue",
					async () => {
						const expected = undefined;
						const sendStub = sinon.stub(
							webSocket._client,
							"send",
						);
						sendStub.withArgs(
							"/topicOrQueue/test/test",
							{},
							{ test: "test" },
						);
						let result;
						try {
							await webSocket.send(
								"test",
								{ test: "test" },
							);
						} catch (e) {
							result = e.message;
						}
						const subscribeParams         = sendStub.getCall(0).args;
						const expectedSubscribeParams = [
							"/topicOrQueue/test/test",
							{},
							{ test: "test" },
						];
						expect(subscribeParams)
							.toMatchObject(expectedSubscribeParams);

						expect(result)
							.toEqual(expected);
						sendStub.restore();
					},
				);
			},
		);
	},
);