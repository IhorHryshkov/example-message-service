/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-11-01T18:23
 */
//--------TypeDaoImpl_test.js--------

import TypeDao from "../../../../src/database/dao/TypeDaoImpl";
import sinon   from "sinon";

describe(
	`"TypeDao" test`,
	() => {
		let typeDao;

		const noOp          = () => {
		};
		const dbConfig      = {
			getLocal    : noOp,
			getIndexDB  : () => {
				return {
					type: {
						put  : noOp,
						where: () => {
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

		beforeEach(() => {
			typeDao = new TypeDao(
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
							typeDao._validator,
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
							await typeDao.add({
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
							typeDao._validator,
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
							typeDao._indexDB.type,
							"put",
						);
						putStub.withArgs({
							id  : 1,
							name: "Test",
						})
						.rejects(Error("Test error"));

						let result;
						try {
							await typeDao.add({
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
							typeDao._validator,
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
							typeDao._indexDB.type,
							"put",
						);
						putStub.withArgs({
							id  : 1,
							name: "Test",
						})
						.resolves(1);
						const result = await typeDao.add({
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
						putStub.restore();
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
							typeDao._indexDB.type,
							"where",
						);
						whereStub.withArgs("name")
						.returns({ equalsIgnoreCase: noOp });
						const equalsIgnoreCaseStub = sinon.stub(
							typeDao._indexDB.type.where("name"),
							"equalsIgnoreCase",
						);
						equalsIgnoreCaseStub.withArgs("testName").returns({ first: noOp });
						const firstStub = sinon.stub(
							typeDao._indexDB.type.where("name").equalsIgnoreCase("testName"),
							"first",
						);
						firstStub.rejects(Error("Test error"));
						let result;
						try {
							await typeDao.getByName({ name: "testName" });
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
							typeDao._indexDB.type,
							"where",
						);
						whereStub.withArgs("name")
						.returns({ equalsIgnoreCase: noOp });
						const equalsIgnoreCaseStub = sinon.stub(
							typeDao._indexDB.type.where("name"),
							"equalsIgnoreCase",
						);
						equalsIgnoreCaseStub.withArgs("testName").returns({ first: noOp });
						const firstStub = sinon.stub(
							typeDao._indexDB.type.where("name").equalsIgnoreCase("testName"),
							"first",
						);
						firstStub.resolves({
							id  : 1,
							name: "testName",
						});
						const validateStub = sinon.stub(
							typeDao._validator,
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
							result = await typeDao.getByName({ name: "testName" });
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
							typeDao._indexDB.type,
							"where",
						);
						whereStub.withArgs("name")
						.returns({ equalsIgnoreCase: noOp });
						const equalsIgnoreCaseStub = sinon.stub(
							typeDao._indexDB.type.where("name"),
							"equalsIgnoreCase",
						);
						equalsIgnoreCaseStub.withArgs("testName").returns({ first: noOp });
						const firstStub = sinon.stub(
							typeDao._indexDB.type.where("name").equalsIgnoreCase("testName"),
							"first",
						);
						firstStub.resolves({
							id  : 1,
							name: "testName",
						});
						const validateStub = sinon.stub(
							typeDao._validator,
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
						const result = await typeDao.getByName({ name: "testName" });
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
							typeDao._indexDB.type,
							"where",
						);
						whereStub.withArgs({ id: 1 })
						.returns({ first: noOp });
						const firstStub = sinon.stub(
							typeDao._indexDB.type.where({ id: 1 }),
							"first",
						);
						firstStub.rejects(Error("Test error"));
						let result;
						try {
							await typeDao.getById({ id: 1 });
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
							typeDao._indexDB.type,
							"where",
						);
						whereStub.withArgs({ id: 1 })
						.returns({ first: noOp });
						const firstStub = sinon.stub(
							typeDao._indexDB.type.where({ id: 1 }),
							"first",
						);
						firstStub.resolves({
							id  : 1,
							name: "testName",
						});
						const validateStub = sinon.stub(
							typeDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"getById",
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
							await typeDao.getById({ id: 1 });
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
							id  : 1,
							name: "testName",
						};
						const whereStub = sinon.stub(
							typeDao._indexDB.type,
							"where",
						);
						whereStub.withArgs({ id: 1 })
						.returns({ first: noOp });
						const firstStub = sinon.stub(
							typeDao._indexDB.type.where({ id: 1 }),
							"first",
						);
						firstStub.resolves({
							id  : 1,
							name: "testName",
						});
						const validateStub = sinon.stub(
							typeDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"getById",
							{
								id  : 1,
								name: "testName",
							},
						)
						.returns(null);
						const result = await typeDao.getById({ id: 1 });
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						firstStub.restore();
						validateStub.restore();
					},
				);
			},
		);
	},
);
