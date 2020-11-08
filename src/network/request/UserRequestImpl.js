/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:11
 */
// --------UserRequest.js--------

/**
 * Requests to the server for user service
 */
export default class UserRequestImpl {
	/**
	 * Initialization of configuration data and network
	 * @param netConfig - Network config object {@link NetworkConfigImpl}
	 * @param defaultParams - Default params and constants
	 */
	constructor({ netConfig, defaultParams }) {
		this._net = netConfig.getAxios();
		this._defaultParams = defaultParams;
	}

	/**
	 * Adding new user data.
	 * @param obj - Data for add new user {username: string}
	 * @returns {
	 * Promise<{status:number, data:{timestamp: number, resId: string, data:{username: string}}}>
	 * |Promise<{status:number, data:{timestamp: number, resId: string, data:{state: string}}}>
	 * |Promise<{status:number, data:{timestamp: number, resId: string, error:{code: string, message: string, method: string, endpoint: string}}}>
	 * }
	 */
	async add(obj) {
		const { network } = this._defaultParams.constants.user;
		const result = await this._net[network.add.method](network.add.path, obj);
		return result;
	}

	/**
	 * Load all users or load users by query fields "username" and "userId".
	 * @param params - Params for query {username: string, userId: string}
	 * Promise<{status:number, data:{resId: string, data:[{id: string, username: string, meta: object, createdAt: number, updatedAt: number, status:{id: number, name: string, createdAt: number, updatedAt: number}}], timestamp: number}}>
	 * |Promise<{status:number, data:{timestamp: number, resId: string, error:{code: string, message: string, method: string, endpoint: string}}}>
	 */
	async all(params) {
		const { network } = this._defaultParams.constants.user;
		const result = await this._net[network.all.method](network.all.path, { params });
		return result;
	}

	/**
	 * Update status for specific user.
	 * @param id - User Id of the user;
	 * @param statusId - Status ID of the status;
	 * Promise<{status:number, data:{timestamp: number, resId: string, data:{userId: string, statusId: number}}}>
	 * |Promise<{status:number, data:{timestamp: number, resId: string, data:{state: string}}}>
	 * |Promise<{status:number, data:{timestamp: number, resId: string, error:{code: string, message: string, method: string, endpoint: string}}}>
	 * }
	 */
	async update({ id, statusId }) {
		const { network } = this._defaultParams.constants.user;
		const result = await this._net[network.update.method](`${network.update.path}/${id}`, { statusId });
		return result;
	}
}
