/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-02T13:19
 */
//--------CallbackRequestImpl_test.js--------

import CallbackRequest from "../../../../src/network/request/CallbackRequestImpl";
import sinon           from "sinon";

describe(
	`"CallbackRequest" test`,
	() => {
		let callbackRequest;

		const noOp          = () => {
		};
		const netConfig     = {
			getAxios: () => {
				return {
					post: noOp,
				};
			},
		};
		const defaultParams = {
			constants: {
				callback: {
					network: {
						approve: {
							method: "post",
							path  : "/test/test",
						},
					},
				},
			},
		};

		beforeEach(() => {
			callbackRequest = new CallbackRequest({
				netConfig,
				defaultParams,
			});
		});

		describe(
			`"approve" function test`,
			() => {
				it(
					"Return reject",
					async () => {
						const expected  = "Test error";
						const requestStub = sinon.stub(
							callbackRequest._net,
							"post",
						);
						requestStub.withArgs(
							"/test/test",
							{
								resId: "Test",
							},
						)
							.rejects(Error("Test error"));
						let result;
						try {
							await callbackRequest.approve({
								resId: "Test",
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
						const expected = {
							status: 200,
							data  : {
								data: {
									resId: "Test",
								},
							},
						};
						const requestStub = sinon.stub(
							callbackRequest._net,
							"post",
						);
						requestStub.withArgs(
							"/test/test",
							{
								resId: "Test",
							},
						)
							.resolves({
								status: 200,
								data  : {
									data: {
										resId: "Test",
									},
								},
							});

						const result = await callbackRequest.approve({
							resId: "Test",
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