/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-02T18:38
 */
//--------CounterRequestImpl_test.js--------

import CounterRequest from "../../../../src/network/request/CounterRequestImpl";
import sinon          from "sinon";

describe(
	`"CounterRequest" test`,
	() => {
		let counterRequest;

		const noOp          = () => {
		};
		const netConfig     = {
			getAxios: () => {
				return {
					post: noOp,
					get : noOp,
				};
			},
		};
		const defaultParams = {
			constants: {
				counter: {
					network: {
						getById: {
							path  : "/test/test",
							method: "get",
						},
						add    : {
							path  : "/test/test",
							method: "post",
						},
					},
				},
			},
		};

		beforeEach(() => {
			counterRequest = new CounterRequest({
				netConfig,
				defaultParams,
			});
		});

		describe(
			`"add" function test`,
			() => {
				it(
					"Return reject",
					async () => {
						const expected    = "Test error";
						const requestStub = sinon.stub(
							counterRequest._net,
							"post",
						);
						requestStub.withArgs(
							"/test/test",
							{
								userId: "Test",
								typeId: 1,
								count : 10,
							},
						)
							.rejects(Error("Test error"));
						let result;
						try {
							await counterRequest.add({
								userId: "Test",
								typeId: 1,
								count : 10,
							});
						} catch (e) {
							result = e.message;
						}
						expect(result)
							.toEqual(expected);
						requestStub.restore();
					},
				);
				it(
					"Return resolve",
					async () => {
						const expected    = {
							status: 200,
							data  : {
								data: {
									userId: "Test",
									typeId: 1,
									count : 10,
								},
							},
						};
						const requestStub = sinon.stub(
							counterRequest._net,
							"post",
						);
						requestStub.withArgs(
							"/test/test",
							{
								userId: "Test",
								typeId: 1,
								count : 10,
							},
						)
							.resolves({
								status: 200,
								data  : {
									data: {
										userId: "Test",
										typeId: 1,
										count : 10,
									},
								},
							});

						const result = await counterRequest.add({
							userId: "Test",
							typeId: 1,
							count : 10,
						});
						expect(result)
							.toEqual(expected);
						requestStub.restore();
					},
				);
			},
		);
		describe(
			`"getById" function test`,
			() => {
				it(
					"Return reject",
					async () => {
						const expected    = "Test error";
						const requestStub = sinon.stub(
							counterRequest._net,
							"get",
						);
						requestStub.withArgs("/test/test/Test")
							.rejects(Error("Test error"));
						let result;
						try {
							await counterRequest.getById("Test");
						} catch (e) {
							result = e.message;
						}
						expect(result)
							.toEqual(expected);
						requestStub.restore();
					},
				);
				it(
					"Return resolve",
					async () => {
						const expected    = {
							status: 200,
							data  : {
								data: [
									{
										keys     : {
											userId: "Test",
											typeId: 1,
										},
										counts   : 10,
										createdAt: 1601974201791,
										updatedAt: 1602773160779,
									},
								],
							},
						};
						const requestStub = sinon.stub(
							counterRequest._net,
							"get",
						);
						requestStub.withArgs("/test/test/Test")
							.resolves({
								status: 200,
								data  : {
									data: [
										{
											keys     : {
												userId: "Test",
												typeId: 1,
											},
											counts   : 10,
											createdAt: 1601974201791,
											updatedAt: 1602773160779,
										},
									],
								},
							});

						const result = await counterRequest.getById("Test");
						expect(result)
							.toEqual(expected);
						requestStub.restore();
					},
				);
			},
		);
	},
);