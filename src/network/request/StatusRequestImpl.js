/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-06T17:14
 */
// --------StatusRequest.js--------

/**
 * Requests to the server for status service
 */
export default class StatusRequestImpl {
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
	 * Load all statuses or load statuses by query fields "name" and "id".
	 * @param params - Params for query {id: number, name: string}
	 * @returns {
	 * Promise<{status:number, data:{resId: string, data:[{id: number, name: string, createdAt: number, updatedAt: number}], timestamp: number}}>
	 * |Promise<{status:number, data:{timestamp: number, resId: string, error:{code: string, message: string, method: string, endpoint: string}}}>
	 * }
	 */
	async all(params) {
		const { network } = this._defaultParams.constants.status;
		const result = await this._net[network.all.method](network.all.path, { params });
		return result;
	}
}
