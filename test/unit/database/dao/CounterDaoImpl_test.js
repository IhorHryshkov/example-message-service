/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-29T20:59
 */
//--------CounterDaoImpl_test.js--------

import CounterDao from "../../../../src/database/dao/CounterDaoImpl";
import sinon      from "sinon";

describe(
	`"CounterDao" test`,
	() => {
		let counterDao;

		const noOp          = () => {
		};
		const dbConfig      = {
			getLocal    : noOp,
			getIndexDB  : () => {
				return {
					counter: {
						put    : noOp,
						toArray: noOp,
						where  : () => {
							return {
								first: noOp,
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
			counterDao = new CounterDao(
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
							counterDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							[
								{
									keys  : { typeId: 1 },
									counts: 10,
								},
							],
						)
						.returns({
							code   : "ERROR",
							message: "Error",
						});
						let result;
						try {
							await counterDao.add({
								data: [
									{
										keys  : { typeId: 1 },
										counts: 10,
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
							counterDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							[
								{
									keys  : { typeId: 1 },
									counts: 10,
								},
							],
						)
						.returns(null);
						const putStub = sinon.stub(
							counterDao._indexDB.counter,
							"put",
						);
						putStub.withArgs({
							id    : 1,
							counts: 10,
						})
						.rejects(Error("Test error"));

						let result;
						try {
							await counterDao.add({
								data: [
									{
										keys  : { typeId: 1 },
										counts: 10,
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
							counterDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"add",
							[
								{
									keys  : { typeId: 1 },
									counts: 10,
								},
							],
						)
						.returns(null);
						const putStub = sinon.stub(
							counterDao._indexDB.counter,
							"put",
						);
						putStub.withArgs({
							id    : 1,
							counts: 10,
						})
						.resolves(1);
						const result = await counterDao.add({
							data: [
								{
									keys  : { typeId: 1 },
									counts: 10,
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
							counterDao._indexDB.counter,
							"where",
						);
						whereStub.withArgs({ id: 1 })
						.returns({ first: noOp });
						const firstStub = sinon.stub(
							counterDao._indexDB.counter.where({ id: 1 }),
							"first",
						);
						firstStub.rejects(Error("Test error"));
						let result;
						try {
							await counterDao.getById({ id: 1 });
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
					"Return null if validation incorrect",
					async () => {
						const expected  = null;
						const whereStub = sinon.stub(
							counterDao._indexDB.counter,
							"where",
						);
						whereStub.withArgs({ id: 1 })
						.returns({ first: noOp });
						const firstStub = sinon.stub(
							counterDao._indexDB.counter.where({ id: 1 }),
							"first",
						);
						firstStub.resolves({
							id    : 1,
							counts: 10,
						});
						const validateStub = sinon.stub(
							counterDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"getById",
							{
								id    : 1,
								counts: 10,
							},
						)
						.returns({
							code   : "ERROR",
							message: "Error",
						});
						const result = await counterDao.getById({ id: 1 });
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
							id    : 1,
							counts: 10,
						};
						const whereStub = sinon.stub(
							counterDao._indexDB.counter,
							"where",
						);
						whereStub.withArgs({ id: 1 })
						.returns({ first: noOp });
						const firstStub = sinon.stub(
							counterDao._indexDB.counter.where({ id: 1 }),
							"first",
						);
						firstStub.resolves({
							id    : 1,
							counts: 10,
						});
						const validateStub = sinon.stub(
							counterDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"getById",
							{
								id    : 1,
								counts: 10,
							},
						)
						.returns(null);
						const result = await counterDao.getById({ id: 1 });
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						firstStub.restore();
						validateStub.restore();
					},
				);
			},
		);
		describe(
			`"all" function test`,
			() => {
				it(
					"Return reject any error",
					async () => {
						const expected  = {
							code   : "ERROR",
							message: "Test error",
						};
						const whereStub = sinon.stub(
							counterDao._indexDB.counter,
							"toArray",
						);
						whereStub.rejects(Error("Test error"));
						let result;
						try {
							await counterDao.all();
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						whereStub.restore();
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
							counterDao._indexDB.counter,
							"toArray",
						);
						whereStub.resolves([
							{
								id    : 1,
								counts: 10,
							},
						]);
						const validateStub = sinon.stub(
							counterDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"all",
							[
								{
									id    : 1,
									counts: 10,
								},
							],
						)
						.returns({
							code   : "ERROR",
							message: "Error",
						});
						let result;
						try {
							await counterDao.all();
						} catch (e) {
							result = e;
						}
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						validateStub.restore();
					},
				);
				it(
					"Return success",
					async () => {
						const expected  = [
							{
								id    : 1,
								counts: 10,
							},
						];
						const whereStub = sinon.stub(
							counterDao._indexDB.counter,
							"toArray",
						);
						whereStub.resolves([
							{
								id    : 1,
								counts: 10,
							},
						]);
						const validateStub = sinon.stub(
							counterDao._validator,
							"validateSchema",
						);
						validateStub.withArgs(
							"all",
							[
								{
									id    : 1,
									counts: 10,
								},
							],
						)
						.returns(null);
						const result = await counterDao.all();
						expect(result)
						.toEqual(expected);
						whereStub.restore();
						validateStub.restore();
					},
				);
			},
		);
	},
);
