/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:17
 */
// --------CallbackServiceImpl.js--------

import RootService from "./iface/RootService";
import CallbackRequestImpl from "../network/request/CallbackRequestImpl";
import CallbackDao from "../database/dao/CallbackDaoImpl";

/**
 * Service for callback processing
 */
export default class CallbackServiceImpl extends RootService {
	/**
	 * Initialization of configuration data, network and DAO
	 * @param netConfig - Network config object {@link NetworkConfigImpl}
	 * @param dbConfig - Database config object {@link DatabaseConfigImpl}
	 * @param defaultParams - Default params and constants
	 */
	constructor({ netConfig, dbConfig, defaultParams }) {
		super({ defaultParams });
		this._callbackRequest = new CallbackRequestImpl({
			netConfig,
			defaultParams,
		});
		this._callbackDao = new CallbackDao(dbConfig, defaultParams);
	}

	/**
	 * Send approve message to server and remove data from callback state machine
	 * @param username - Username of the logged user
	 * @param resId - Response ID from the server when start some action on server
	 * @returns {Promise<{resId: string}>|Promise<{state: string}>|undefined}
	 */
	async add({ username, resId }) {
		const { prefixStorageKey, postfixStorageKey } = this._defaultParams.constants.callback;

		const result = await this._callbackRequest.approve({ resId });
		if (result.status === 200) {
			await this._callbackDao.remove({ key: `${prefixStorageKey}${username}_${resId}${postfixStorageKey}` });
		}
		return result.data.data;
	}

	/**
	 * Get data from callback state machine by key
	 * @param username - Username of the logged user
	 * @param resId - Response ID from the server when start some action on server
	 * @returns {Promise.resolve<{key: string, data:{callback: string}}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async getById({ username, resId }) {
		const { prefixStorageKey, postfixStorageKey } = this._defaultParams.constants.callback;
		return this._callbackDao.getByKey({ key: `${prefixStorageKey}${username}_${resId}${postfixStorageKey}` });
	}
}
