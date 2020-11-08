/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:02
 */
// --------PreferencesDaoImpl.js--------
import schema from "./schemas/preferences.json";
import RootDao from "./iface/RootDao";

/**
 * DAO for data of preferences
 */
export default class PreferencesDaoImpl extends RootDao {
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
	 * Add preferences data to local storage
	 * @param key - Key for preferences data
	 * @param data - Data of preferences
	 	{
			mode: string,
			darkLightChecked: boolean,
			leftRightChecked: boolean,
			nav_side:{
				left: string,
				right: string
			},
			lastChat: string,
			user:{
				id: string,
				username: string,
				timestamp: number
			}
		}
	 * @returns {Promise.resolve<{
			message: string,
			code   : string
		}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	add({ key, data }) {
		return new Promise((resolve, reject) => {
			const validateError = this._validator.validateSchema("add", {
				key,
				data,
			});
			if (validateError) {
				return reject(validateError);
			}
			this._db.setItem(key, JSON.stringify(data));
			return resolve(this._defaultParams.messages.info.success);
		});
	}

	/**
	 * Remove preferences data of local storage
	 * @param key - Key for remove data
	 * @returns {Promise.resolve<{
			message: string,
			code   : string
		}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	remove({ key }) {
		return new Promise((resolve, reject) => {
			try {
				const validateError = this._validator.validateSchema("remove", { key });
				if (validateError) {
					return reject(validateError);
				}
				this._db.removeItem(key);
				return resolve(this._defaultParams.messages.info.success);
			} catch (e) {
				return reject({
					code: "ERROR",
					message: e.message,
				});
			}
		});
	}

	/**
	 * Get preferences data by key
	 * @param key - Key for get preferences data
	 * @returns {Promise.resolve<
	 	{
			mode: string,
			darkLightChecked: boolean,
			leftRightChecked: boolean,
			nav_side:{
				left: string,
				right: string
			},
			lastChat: string,
			user:{
				id: string,
				username: string,
				timestamp: number
			}
	 	}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	getByKey({ key }) {
		return new Promise((resolve, reject) => {
			try {
				let validateError = this._validator.validateSchema("remove", { key });
				if (validateError) {
					return reject(validateError);
				}
				let result = this._db.getItem(key);
				if (result) {
					result = JSON.parse(result);
					validateError = this._validator.validateSchema("getByKey", {
						key,
						data: result,
					});
					if (validateError) {
						return reject(validateError);
					}
				}
				return resolve(result);
			} catch (e) {
				return reject({
					code: "ERROR",
					message: e.message,
				});
			}
		});
	}
}
