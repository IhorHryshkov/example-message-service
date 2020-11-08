/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-06T16:01
 */
// --------StatusDaoImpl.js--------
import schema from "./schemas/status.json";
import RootDao from "./iface/RootDao";

/**
 * DAO for data of user statuses
 */
export default class StatusDaoImpl extends RootDao {
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
	 * Add new statuses to status table
	 * @param data - Array of statuses [{id: number, name: string}]
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
				promises.push(this._indexDB.status.put(data[i], data[i].id));
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
	 * Get first status by status name
	 * @param name - Name of status
	 * @returns {Promise.resolve<{id: number, name: string}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async getByName({ name }) {
		try {
			const result = await this._indexDB.status.where("name").equalsIgnoreCase(name).first();
			const validateError = this._validator.validateSchema("getByName", result);
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
