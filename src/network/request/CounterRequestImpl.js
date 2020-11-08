/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-04T21:13
 */
// --------CounterRequest.js--------

/**
 * Requests to the server for counter service
 */
export default class CounterRequestImpl {
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
	 * Adding counts for specifics type and user.
	 * @param obj - Data for add counts to user {userId: string, typeId: number, count: number}
	 * @returns {
	 * Promise<{status:number, data:{timestamp: number, resId: string, data:{userId: string, typeId: number, count: number}}}>
	 * |Promise<{status:number, data:{timestamp: number, resId: string, data:{state: string}}}>
	 * |Promise<{status:number, data:{timestamp: number, resId: string, error:{code: string, message: string, method: string, endpoint: string}}}>
	 * }
	 */
	async add(obj) {
		const { network } = this._defaultParams.constants.counter;
		const result = await this._net[network.add.method](network.add.path, obj);
		return result;
	}

	/**
	 * Load all counters and details for specific user.
	 * @param id - User ID of the counter
	 * @returns {
	 * Promise<{status:number, data:{resId: string, data:[{keys:{userId: string, typeId: number}, counts: number, createdAt: number, updatedAt: number}], timestamp: number}}>
	 * |Promise<{status:number, data:{timestamp: number, resId: string, error:{code: string, message: string, method: string, endpoint: string}}}>
	 * }
	 */
	async getById(id) {
		const { network } = this._defaultParams.constants.counter;
		const result = await this._net[network.getById.method](`${network.getById.path}/${id}`);
		return result;
	}
}
