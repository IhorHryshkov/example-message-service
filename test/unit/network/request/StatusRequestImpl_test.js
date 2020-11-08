/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-02T22:03
 */
//--------StatusRequestImpl_test.js--------

import StatusRequest from "../../../../src/network/request/StatusRequestImpl";
import sinon         from "sinon";

describe(
	`"StatusRequest" test`,
	() => {
		let statusRequest;

		const noOp          = () => {
		};
		const netConfig     = {
			getAxios: () => {
				return {
					get: noOp,
				};
			},
		};
		const defaultParams = {
			constants: {
				status: {
					network: {
						all: {
							path  : "/test/test",
							method: "get",
						},
					},
				},
			},
		};

		beforeEach(() => {
			statusRequest = new StatusRequest({
				netConfig,
				defaultParams,
			});
		});

		describe(
			`"all" function test`,
			() => {
				it(
					"Return reject",
					async () => {
						const expected    = "Test error";
						const requestStub = sinon.stub(
							statusRequest._net,
							"get",
						);
						requestStub.withArgs(
							"/test/test",
							{
								params: {
									id  : 1,
									name: "testName",
								},
							},
						)
							.rejects(Error("Test error"));
						let result;
						try {
							await statusRequest.all({
								id  : 1,
								name: "testName",
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
										id       : 1,
										name     : "testName",
										createdAt: 1599838469129,
										updatedAt: 1599838469129,
									},
								],
							},
						};
						const requestStub = sinon.stub(
							statusRequest._net,
							"get",
						);
						requestStub.withArgs(
							"/test/test",
							{
								params: {
									id  : 1,
									name: "testName",
								},
							},
						)
							.resolves({
								status: 200,
								data  : {
									data: [
										{
											id       : 1,
											name     : "testName",
											createdAt: 1599838469129,
											updatedAt: 1599838469129,
										},
									],
								},
							});

						const result = await statusRequest.all({
							id  : 1,
							name: "testName",
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