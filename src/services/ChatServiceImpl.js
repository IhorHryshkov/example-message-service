/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-09T18:17
 */
// --------ChatServiceImpl.js--------
import crypto from "crypto";
import { v4 } from "uuid";
import RootService from "./iface/RootService";
import ChatDao from "../database/dao/ChatDaoImpl";

/**
 * Service for chat processing
 */
export default class ChatServiceImpl extends RootService {
	/**
	 * Initialization of DAO
	 * @param dbConfig - Database config object {@link DatabaseConfigImpl}
	 * @param defaultParams - Default params and constants
	 */
	constructor({ dbConfig, defaultParams }) {
		super({ defaultParams });
		this._chatDao = new ChatDao(dbConfig, defaultParams);
	}

	/**
	 * Generation params and add new message on chat
	 * @param obj - Data of message {user_own_id: string, user_id: string, body: string, timestamp: number}
	 * @returns {Promise.resolve<{id: string, user_id: string, chat_id: string, body: string, timestamp: number}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async add(obj) {
		const { user_id, user_own_id } = obj;

		const hash = crypto.createHash("sha512").update(user_id);
		const result = {
			...obj,
			chat_id: hash.digest("hex").toLowerCase(),
			user_id: user_own_id || user_id,
			id: v4(),
		};
		return this._chatDao.add(result);
	}

	/**
	 * Load all messages by chat user ID
	 * @param user_id - User ID of the chat user
	 * @returns {Promise.resolve<[{id: string, user_id: string, chat_id: string, body: string, timestamp: number}]>|Promise.reject<{message: string, code: string}>}
	 */
	async getById({ user_id }) {
		const hash = crypto.createHash("sha512").update(user_id);
		const chat_id = hash.digest("hex").toLowerCase();
		return this._chatDao.getById({ chat_id });
	}
}
