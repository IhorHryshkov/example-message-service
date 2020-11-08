/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-02T22:54
 */
//--------UserRequestImpl_test.js--------

import UserRequest from "../../../../src/network/request/UserRequestImpl";
import sinon       from "sinon";

describe(
	`"UserRequest" test`,
	() => {
		let userRequest;

		const noOp          = () => {
		};
		const netConfig     = {
			getAxios: () => {
				return {
					get : noOp,
					post: noOp,
					put : noOp,
				};
			},
		};
		const defaultParams = {
			constants: {
				user: {
					network: {
						all   : {
							path  : "/test/test",
							method: "get",
						},
						add   : {
							path  : "/test/test",
							method: "post",
						},
						update: {
							path  : "/test/test",
							method: "put",
						},
					},
				},
			},
		};

		beforeEach(() => {
			userRequest = new UserRequest({
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
							userRequest._net,
							"post",
						);
						requestStub.withArgs(
							"/test/test",
							{
								username: "Tester",
							},
						)
							.rejects(Error("Test error"));
						let result;
						try {
							await userRequest.add({
								username: "Tester",
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
									username: "Tester",
								},
							},
						};
						const requestStub = sinon.stub(
							userRequest._net,
							"post",
						);
						requestStub.withArgs(
							"/test/test",
							{
								username: "Tester",
							},
						)
							.resolves({
								status: 200,
								data  : {
									data: {
										username: "Tester",
									},
								},
							});

						const result = await userRequest.add({
							username: "Tester",
						});
						expect(result)
							.toEqual(expected);
						requestStub.restore();
					},
				);
			},
		);
		describe(
			`"all" function test`,
			() => {
				it(
					"Return reject",
					async () => {
						const expected    = "Test error";
						const requestStub = sinon.stub(
							userRequest._net,
							"get",
						);
						requestStub.withArgs(
							"/test/test",
							{
								params: {
									id      : "Test",
									username: "Tester",
								},
							},
						)
							.rejects(Error("Test error"));
						let result;
						try {
							await userRequest.all({
								id      : "Test",
								username: "Tester",
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
								data: [
									{
										id       : "Test",
										username : "Tester",
										meta     : null,
										createdAt: 1599840451118,
										updatedAt: 1599840451118,
										status   : {
											id       : 1,
											name     : "testName",
											createdAt: 1599840451103,
											updatedAt: 1599840451103,
										},
									},
								],
							},
						};
						const requestStub = sinon.stub(
							userRequest._net,
							"get",
						);
						requestStub.withArgs(
							"/test/test",
							{
								params: {
									id      : "Test",
									username: "Tester",
								},
							},
						)
							.resolves({
								status: 200,
								data  : {
									data: [
										{
											id       : "Test",
											username : "Tester",
											meta     : null,
											createdAt: 1599840451118,
											updatedAt: 1599840451118,
											status   : {
												id       : 1,
												name     : "testName",
												createdAt: 1599840451103,
												updatedAt: 1599840451103,
											},
										},
									],
								},
							});

						const result = await userRequest.all({
							id      : "Test",
							username: "Tester",
						});
						expect(result)
							.toEqual(expected);
						requestStub.restore();
					},
				);
			},
		);
		describe(
			`"update" function test`,
			() => {
				it(
					"Return reject",
					async () => {
						const expected    = "Test error";
						const requestStub = sinon.stub(
							userRequest._net,
							"put",
						);
						requestStub.withArgs(
							"/test/test/Test",
							{ statusId: 1 },
						)
							.rejects(Error("Test error"));
						let result;
						try {
							await userRequest.update({
								id      : "Test",
								statusId: 1,
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
									userId  : "Test",
									statusId: 1,
								},
							},
						};
						const requestStub = sinon.stub(
							userRequest._net,
							"put",
						);
						requestStub.withArgs(
							"/test/test/Test",
							{ statusId: 1 },
						)
							.resolves({
								status: 200,
								data  : {
									data: {
										userId  : "Test",
										statusId: 1,
									},
								},
							});

						const result = await userRequest.update({
							id      : "Test",
							statusId: 1,
						});
						expect(result)
							.toEqual(expected);
						requestStub.restore();
					},
				);
			},
		);
	},
);