/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-16T10:04
 */
// --------CounterDaoImpl.js--------
import schema from "./schemas/counter.json";
import RootDao from "./iface/RootDao";

/**
 * DAO for data of user counters
 */
export default class CounterDaoImpl extends RootDao {
	/**
	 * Initialization of schema validation and configuration data
	 * @param dbConfig - Database config object {@link DatabaseConfigImpl}
	 * @param defaultParams - Default params and constants
	 */
	constructor(dbConfig, defaultParams) {
		super({
			dbConfig,
			defaultParams,
			schema,
		});
	}

	/**
	 * Add new counters to counter table
	 * @param data - Array of counters [{id: number, counts: number}]
	 * @returns {Promise.resolve<{
			message: string,
			code   : string
		}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async add({ data }) {
		try {
			const validateError = this._validator.validateSchema("add", data);
			if (validateError) {
				return Promise.reject(validateError);
			}
			const promises = [];
			for (let i = 0, { length } = data; i < length; i += 1) {
				promises.push(
					this._indexDB.counter.put({
						id: data[i].keys.typeId,
						counts: data[i].counts,
					})
				);
			}
			await Promise.all(promises);
			return this._defaultParams.messages.info.success;
		} catch (e) {
			return Promise.reject({
				code: "ERROR",
				message: e.message,
			});
		}
	}

	/**
	 * Get first counter by counter ID
	 * @param id - ID of counter
	 * @returns {Promise.resolve<{id: number, counts: number}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async getById({ id }) {
		try {
			const result = await this._indexDB.counter.where({ id }).first();
			const validateError = this._validator.validateSchema("getById", result);
			if (validateError) {
				return null;
			}
			return result;
		} catch (e) {
			return Promise.reject({
				code: "ERROR",
				message: e.message,
			});
		}
	}

	/**
	 * Get all counters
	 * @returns {Promise.resolve<[{id: number, counts: number}]>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async all() {
		try {
			const result = await this._indexDB.counter.toArray();
			const validateError = this._validator.validateSchema("all", result);
			if (validateError) {
				return Promise.reject(validateError);
			}
			return result;
		} catch (e) {
			return Promise.reject({
				code: "ERROR",
				message: e.message,
			});
		}
	}
}
