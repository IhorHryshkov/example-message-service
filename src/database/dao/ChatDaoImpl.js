/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-09T18:17
 */
// --------ChatDaoImpl.js--------
import RootDao from "./iface/RootDao";
import schema from "./schemas/chat.json";

/**
 * DAO for data of chat messages
 */
export default class ChatDaoImpl extends RootDao {
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
	 * Add message data to chat table
	 * @param obj - Data of message {id: string, user_id: string, chat_id: string, body: string, timestamp: number}
	 * @returns {Promise.resolve<{id: string, user_id: string, chat_id: string, body: string, timestamp: number}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async add(obj) {
		const validateError = this._validator.validateSchema("add", obj);
		if (validateError) {
			return Promise.reject(validateError);
		}
		await this._indexDB.chat.put(obj, obj.id);
		return obj;
	}

	/**
	 * Get last 25 chat messages by chat ID and sort by "timestamp"
	 * @param chat_id - ID of chat
	 * @returns {Promise.resolve<[{id: string, user_id: string, chat_id: string, body: string, timestamp: number}]>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async getById({ chat_id }) {
		try {
			const result = await this._indexDB.chat.where({ chat_id }).limit(25).sortBy("timestamp");
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
}
