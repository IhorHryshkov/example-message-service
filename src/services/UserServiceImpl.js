/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:17
 */
// --------UserServiceImpl.js--------
import RootService from "./iface/RootService";
import UserRequest from "../network/request/UserRequestImpl";
import CallbackDao from "../database/dao/CallbackDaoImpl";
import StatusDao from "../database/dao/StatusDaoImpl";
import UsersDao from "../database/dao/UsersDaoImpl";

/**
 * Service for users processing
 */
export default class UserServiceImpl extends RootService {
	/**
	 * Initialization of configuration data, network and DAO
	 * @param netConfig - Network config object {@link NetworkConfigImpl}
	 * @param dbConfig - Database config object {@link DatabaseConfigImpl}
	 * @param defaultParams - Default params and constants
	 */
	constructor({ netConfig, dbConfig, defaultParams }) {
		super({ defaultParams });
		this._userRequest = new UserRequest({
			netConfig,
			defaultParams,
		});
		this._callbackDao = new CallbackDao(dbConfig, defaultParams);
		this._statusDao = new StatusDao(dbConfig, defaultParams);
		this._usersDao = new UsersDao(dbConfig, defaultParams);
		this.select = this.select.bind(this);
	}

	/**
	 * Load all users for a chat by params and add to DB
	 * @param params - Some query params like a username or ID {id: string, username: string}
	 * @param user_id - Logged user ID for exclude it before add users list to DB
	 * @returns {Promise.resolve<{updateUserAt: number, id: string, username: string, status: {id: number, name: string}, updateSystemAt: number}>|Promise.reject<{message: string, code: string}>}
	 */
	async all({ params, user_id }) {
		const result = await this._userRequest.all(params);
		if (result.status === 200) {
			await this._usersDao.add({
				data: result.data.data,
				excludeId: user_id,
			});
		}
		return this._usersDao.all({ sort: "updateUserAt" });
	}

	/**
	 * Update timestamp for column updateUserAt for selected user
	 * @param id - User ID of the selected user
	 * @returns {Promise<{code: string, message: string}>|Promise<number>}
	 */
	select(id) {
		return this._usersDao.update({
			id,
			data: { updateUserAt: Date.now() },
		});
	}

	/**
	 * Register new user in the server side and create record in state machine
	 * @param username - Username of the new user
	 * @returns {
	 * Promise<{status: number, data:{timestamp: number, resId: string, data:{username: string}}}>
	 * |Promise<{status: number, data:{timestamp: number, resId: string, data:{state: string}}}>
	 * |Promise<{status: number, data:{timestamp: number, resId: string, error:{code: string, message: string, method: string, endpoint: string}}}>
	 * }
	 */
	async add({ username }) {
		const { prefixStorageKey, postfixStorageKey, enums } = this._defaultParams.constants.callback;

		const result = await this._userRequest.add({ username });
		if (result.status === 201) {
			await this._callbackDao.add({
				key: `${prefixStorageKey}${username}_${result.data.resId}${postfixStorageKey}`,
				data: { callback: enums.ADD_USER },
			});
		}

		return result;
	}

	/**
	 * Update user status to online when TTL logged user is end
	 * @param id - User ID of the old logged user
	 * @param username - Username of the old logged user
	 * @returns {
	 * Promise<{status: number, data:{timestamp: number, resId: string, data:{username: string}}}>
	 * |Promise<{status: number, data:{timestamp: number, resId: string, data:{state: string}}}>
	 * |Promise<{status: number, data:{timestamp: number, resId: string, error:{code: string, message: string, method: string, endpoint: string}}}>
	 * }
	 */
	async update({ id, username }) {
		const { callback, user } = this._defaultParams.constants;
		const status = await this._statusDao.getByName({ name: user.default.status });
		const result = await this._userRequest.update({
			id,
			statusId: status.id,
		});
		if (result.status === 200) {
			await this._callbackDao.add({
				key: `${callback.prefixStorageKey}${username}_${result.data.resId}${callback.postfixStorageKey}`,
				data: { callback: callback.enums.UPDATE_USER },
			});
		}

		return result;
	}
}
