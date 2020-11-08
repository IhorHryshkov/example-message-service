/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-31T21:44
 */
//--------StatusDaoImpl_test.js--------

import StatusDao from "../../../../src/database/dao/StatusDaoImpl";
import sinon     from "sinon";

describe(
	`"StatusDao" test`,
	() => {
		let statusDao;

		const noOp          = () => {
		};
		const dbConfig      = {
			getLocal    : noOp,
			getIndexDB  : () => {
				return {
					status: {
						put  : noOp,
						where: () => {
							return {
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

		beforeEach(() => {
			statusDao = new StatusDao(
				dbConfig,
				defaultParams,
			);
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
							statusDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							[
								{
									id  : 1,
									name: "Test",
								},
							],
						)
						.returns({
							code   : "ERROR",
							message: "Error",
						});
						let result;
						try {
							await statusDao.add({
								data: [
									{
										id  : 1,
										name: "Test",
									},
								],
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
					"Return reject any error",
					async () => {
						const expected     = {
							code   : "ERROR",
							message: "Test error",
						};
						const validateStub = sinon.stub(
							statusDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							[
								{
									id  : 1,
									name: "Test",
								},
							],
						)
						.returns(null);
						const putStub = sinon.stub(
							statusDao._indexDB.status,
							"put",
						);
						putStub.withArgs({
							id  : 1,
							name: "Test",
						})
						.rejects(Error("Test error"));

						let result;
						try {
							await statusDao.add({
								data: [
									{
										id  : 1,
										name: "Test",
									},
								],
							});
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						validateStub.restore();
						putStub.restore();
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
							statusDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							[
								{
									id  : 1,
									name: "Test",
								},
							],
						)
						.returns(null);
						const putStub = sinon.stub(
							statusDao._indexDB.status,
							"put",
						);
						putStub.withArgs({
							id  : 1,
							name: "Test",
						})
						.resolves(1);
						const result = await statusDao.add({
							data: [
								{
									id  : 1,
									name: "Test",
								},
							],
						});
						expect(result)
						.toEqual(expected);
						validateStub.restore();
					},
				);
			},
		);
		describe(
			`"getByName" function test`,
			() => {
				it(
					"Return reject any error",
					async () => {
						const expected  = {
							code   : "ERROR",
							message: "Test error",
						};
						const whereStub = sinon.stub(
							statusDao._indexDB.status,
							"where",
						);
						whereStub.withArgs("name")
						.returns({ equalsIgnoreCase: noOp });
						const equalsIgnoreCaseStub = sinon.stub(
							statusDao._indexDB.status.where("name"),
							"equalsIgnoreCase",
						);
						equalsIgnoreCaseStub.withArgs("testName").returns({ first: noOp });
						const firstStub = sinon.stub(
							statusDao._indexDB.status.where("name").equalsIgnoreCase("testName"),
							"first",
						);
						firstStub.rejects(Error("Test error"));
						let result;
						try {
							await statusDao.getByName({ name: "testName" });
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						firstStub.restore();
						equalsIgnoreCaseStub.restore();
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
							statusDao._indexDB.status,
							"where",
						);
						whereStub.withArgs("name")
						.returns({ equalsIgnoreCase: noOp });
						const equalsIgnoreCaseStub = sinon.stub(
							statusDao._indexDB.status.where("name"),
							"equalsIgnoreCase",
						);
						equalsIgnoreCaseStub.withArgs("testName").returns({ first: noOp });
						const firstStub = sinon.stub(
							statusDao._indexDB.status.where("name").equalsIgnoreCase("testName"),
							"first",
						);
						firstStub.resolves({
							id  : 1,
							name: "testName",
						});
						const validateStub = sinon.stub(
							statusDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"getByName",
							{
								id  : 1,
								name: "testName",
							},
						)
						.returns({
							code   : "ERROR",
							message: "Error",
						});
						let result;
						try {
							result = await statusDao.getByName({ name: "testName" });
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						firstStub.restore();
						equalsIgnoreCaseStub.restore();
						validateStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected  = {
							id  : 1,
							name: "testName",
						};
						const whereStub = sinon.stub(
							statusDao._indexDB.status,
							"where",
						);
						whereStub.withArgs("name")
						.returns({ equalsIgnoreCase: noOp });
						const equalsIgnoreCaseStub = sinon.stub(
							statusDao._indexDB.status.where("name"),
							"equalsIgnoreCase",
						);
						equalsIgnoreCaseStub.withArgs("testName").returns({ first: noOp });
						const firstStub = sinon.stub(
							statusDao._indexDB.status.where("name").equalsIgnoreCase("testName"),
							"first",
						);
						firstStub.resolves({
							id  : 1,
							name: "testName",
						});
						const validateStub = sinon.stub(
							statusDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"getByName",
							{
								id  : 1,
								name: "testName",
							},
						)
						.returns(null);
						const result = await statusDao.getByName({ name: "testName" });
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						firstStub.restore();
						validateStub.restore();
						equalsIgnoreCaseStub.restore();
					},
				);
			},
		);
	},
);
