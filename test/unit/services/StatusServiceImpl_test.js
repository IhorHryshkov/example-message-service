/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-06T06:59
 */
//--------StatusServiceImpl_test.js--------

import StatusService from "../../../src/services/StatusServiceImpl";
import sinon         from "sinon";

describe(
	`"StatusService" test`,
	() => {
		let statusService;

		const noOp          = () => {
		};
		const netConfig     = {
			getAxios: noOp,
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
				global: {
					database: {
						version: 1,
					},
				},
			},
		};

		beforeEach(() => {
			statusService = new StatusService({
				netConfig,
				dbConfig,
				defaultParams,
			});
		});

		describe(
			`"all" function test`,
			() => {
				it(
					"Return reject request",
					async () => {
						const expected    = {
							code   : "ERROR",
							message: "Error",
						};
						const requestStub = sinon.stub(
							statusService._statusRequest,
							"all",
						);
						requestStub.withArgs({
							id  : 1,
							name: "testName",
						})
						.rejects({
							code   : "ERROR",
							message: "Error",
						});

						let result;
						try {
							result = await statusService.all({
								id  : 1,
								name: "testName",
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id  : 1,
								name: "testName",
							},
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);

						expect(result)
						.toEqual(expected);
						requestStub.restore();
					},
				);
				it(
					"Return error status of request",
					async () => {
						const expected    = 400;
						const requestStub = sinon.stub(
							statusService._statusRequest,
							"all",
						);
						requestStub.withArgs({
							id  : 1,
							name: "testName",
						})
						.resolves({
							status: 400,
							data  : {
								error: {
									code   : 400,
									message: "Error",
								},
							},
						});

						let result;
						try {
							result = await statusService.all({
								id  : 1,
								name: "testName",
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id  : 1,
								name: "testName",
							},
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);

						expect(result)
						.toEqual(expected);
						requestStub.restore();
					},
				);
				it(
					"Return reject DAO",
					async () => {
						const expected    = {
							code   : "ERROR",
							message: "Error",
						};
						const requestStub = sinon.stub(
							statusService._statusRequest,
							"all",
						);
						requestStub.withArgs({
							id  : 1,
							name: "testName",
						})
						.resolves({
							status: 200,
							data  : {
								data: [
									{
										id  : 1,
										name: "testName",
									},
								],
							},
						});
						const daoStub = sinon.stub(
							statusService._statusDao,
							"add",
						);
						daoStub.withArgs({
							data: [
								{
									id  : 1,
									name: "testName",
								},
							],
						})
						.rejects({
							code   : "ERROR",
							message: "Error",
						});

						let result;
						try {
							result = await statusService.all({
								id  : 1,
								name: "testName",
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id  : 1,
								name: "testName",
							},
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{
								data: [
									{
										id  : 1,
										name: "testName",
									},
								],
							},
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
						requestStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected    = 200;
						const requestStub = sinon.stub(
							statusService._statusRequest,
							"all",
						);
						requestStub.withArgs({
							id  : 1,
							name: "testName",
						})
						.resolves({
							status: 200,
							data  : {
								data: [
									{
										id  : 1,
										name: "testName",
									},
								],
							},
						});
						const daoStub = sinon.stub(
							statusService._statusDao,
							"add",
						);
						daoStub.withArgs({
							data: [
								{
									id  : 1,
									name: "testName",
								},
							],
						})
						.resolves({
							code   : "SUCCESS",
							message: "Success",
						});

						let result;
						try {
							result = await statusService.all({
								id  : 1,
								name: "testName",
							});
						} catch (e) {
							result = e;
						}

						const requestParams         = requestStub.getCall(0).args;
						const expectedRequestParams = [
							{
								id  : 1,
								name: "testName",
							},
						];
						expect(requestParams)
						.toMatchObject(expectedRequestParams);
						const daoParams         = daoStub.getCall(0).args;
						const expectedDaoParams = [
							{
								data: [
									{
										id  : 1,
										name: "testName",
									},
								],
							},
						];
						expect(daoParams)
						.toMatchObject(expectedDaoParams);

						expect(result)
						.toEqual(expected);
						daoStub.restore();
						requestStub.restore();
					},
				);
			},
		);
	},
);
