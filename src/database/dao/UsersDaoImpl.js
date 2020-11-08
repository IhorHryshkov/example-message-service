/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-08T16:01
 */
// --------UsersDaoImpl.js--------
import schema from "./schemas/users.json";
import RootDao from "./iface/RootDao";

/**
 * DAO for data of users
 */
export default class UsersDaoImpl extends RootDao {
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
	 * Add new users to user table
	 * @param data - Array of users [{id: string, username: string, status:{id: number, name: string}}]
	 * @param excludeId - Exclude current user ID before add to table
	 * @returns {Promise.resolve<{
			message: string,
			code   : string
		}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async add({ data, excludeId }) {
		const validateError = this._validator.validateSchema("add", data);
		if (validateError) {
			return Promise.reject(validateError);
		}
		const promises = [];
		for (let i = 0, { length } = data; i < length; i += 1) {
			if (excludeId !== data[i].id) {
				promises.push(
					this._indexDB.users
						.add(
							{
								...data[i],
								updateUserAt: Date.now(),
							},
							data[i].id
						)
						.catch(e => e)
				);
			}
		}
		await Promise.all(promises);
		return this._defaultParams.messages.info.success;
	}

	/**
	 * Get user by user ID
	 * @param id - ID of user
	 * @returns {Promise.resolve<{updateUserAt: number, id: string, username: string, status:{id: number, name: string}, updateSystemAt: number}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async getById({ id }) {
		try {
			const result = await this._indexDB.users.where({ id }).first();
			const validateError = this._validator.validateSchema("getById", result);
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

	/**
	 * Get all users and reverse sort
	 * @param sort - Sort by column name
	 * @returns {Promise.resolve<[{updateUserAt: number, id: string, username: string, status:{id: number, name: string}, updateSystemAt: number}]>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async all({ sort }) {
		try {
			const result = await this._indexDB.users.reverse().sortBy(sort);
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

	/**
	 * Update user data by user ID
	 * @param id - ID of the user
	 * @param data - Data for update, all filed is optional {updateUserAt: number, id: string, username: string, status:{id: number, name: string}, updateSystemAt: number}
	 * @returns {Promise<{code: string, message: string}|*>}
	 */
	async update({ id, data }) {
		try {
			return await this._indexDB.users.update(id, data);
		} catch (e) {
			return Promise.reject({
				code: "ERROR",
				message: e.message,
			});
		}
	}
}
