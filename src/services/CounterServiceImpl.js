/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-04T21:15
 */
// --------CounterServiceImpl.js--------
import RootService from "./iface/RootService";
import CounterRequest from "../network/request/CounterRequestImpl";
import CallbackDao from "../database/dao/CallbackDaoImpl";
import TypeDao from "../database/dao/TypeDaoImpl";
import CounterDao from "../database/dao/CounterDaoImpl";

/**
 * Service for counters processing
 */
export default class CounterServiceImpl extends RootService {
	/**
	 * Initialization of configuration data, network and DAO
	 * @param netConfig - Network config object {@link NetworkConfigImpl}
	 * @param dbConfig - Database config object {@link DatabaseConfigImpl}
	 * @param defaultParams - Default params and constants
	 */
	constructor({ netConfig, dbConfig, defaultParams }) {
		super({ defaultParams });
		this._counterRequest = new CounterRequest({
			netConfig,
			defaultParams,
		});
		this._callbackDao = new CallbackDao(dbConfig, defaultParams);
		this._typeDao = new TypeDao(dbConfig, defaultParams);
		this._counterDao = new CounterDao(dbConfig, defaultParams);
		this.addMessageCounter = this.addMessageCounter.bind(this);
		this.updateMessageCounter = this.updateMessageCounter.bind(this);
	}

	/**
	 * Load all counters from server by logged user ID, update DB and processing counters for the showing
	 * @param id - User ID of the logged user
	 * @returns {Promise.resolve<[{id: number, counts: number, name: string}]>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async getById(id) {
		const promises = [];

		let result = await this._counterRequest.getById(id);
		const data = result.status !== 200 ? [] : [...result.data.data];
		await this._counterDao.add({ data });
		result = await this._counterDao.all();
		for (let i = 0, { length } = result; i < length; i += 1) {
			promises.push(this._addTypeNameToCounters(result[i]));
		}
		result = await Promise.all(promises);
		return result;
	}

	/**
	 * Add new count for message counter in the server
	 * @param user_id - User ID of the logged user
	 * @param username - Username of the logged user
	 * @returns {
	 * Promise<{timestamp: number, resId: string, data:{userId: string, typeId: number, count: number}}>
	 * |Promise<{timestamp: number, resId: string, data:{state: string}}>
	 * |Promise<{timestamp: number, resId: string, error:{code: string, message: string, method: string, endpoint: string}}>
	 * }
	 */
	async addMessageCounter({ user_id, username }) {
		const { callback, user } = this._defaultParams.constants;
		const type = await this._typeDao.getByName({ name: user.default.type });
		const result = await this._counterRequest.add({
			userId: user_id,
			typeId: type.id,
		});
		if (result.status === 201) {
			await this._callbackDao.add({
				key: `${callback.prefixStorageKey}${username}_${result.data.resId}${callback.postfixStorageKey}`,
				data: { callback: callback.enums.UPDATE_COUNTER },
			});
		}

		return result.data;
	}

	/**
	 * Update counts for message counter and return all counters
	 * @returns {Promise.resolve<[{id: number, counts: number, name: string}]>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async updateMessageCounter() {
		const promises = [];

		const { user } = this._defaultParams.constants;
		const type = await this._typeDao.getByName({ name: user.default.type });
		const counterById = await this._counterDao.getById({ id: type.id });
		await this._counterDao.add({
			data: [
				{
					keys: { typeId: type.id },
					counts: counterById ? counterById.counts + 1 : 1,
				},
			],
		});
		let result = await this._counterDao.all();
		for (let i = 0, { length } = result; i < length; i += 1) {
			promises.push(this._addTypeNameToCounters(result[i]));
		}
		result = await Promise.all(promises);
		return result;
	}

	async _addTypeNameToCounters(result) {
		const type = await this._typeDao.getById({ id: result.id });
		return {
			...result,
			name: type.name,
		};
	}
}
