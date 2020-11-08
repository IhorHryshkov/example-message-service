/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:11
 */
// --------CallbackRequest.js--------

/**
 * Requests to the server for callback service
 */
export default class CallbackRequestImpl {
	/**
	 * Initialization of configuration data and network
	 * @param netConfig - Network config object {@link NetworkConfigImpl}
	 * @param defaultParams - Default params and constants
	 */
	constructor({ netConfig, defaultParams }) {
		this._net = netConfig.getAxios();
		this._defaultParams = defaultParams;
		this.approve = this.approve.bind(this);
	}

	/**
	 * Resolve web socket message if client successful receive it
	 * @param obj - Response ID from web socket message {resId: string}
	 * @returns {
	 * Promise<{status:number, data:{timestamp: number, resId: string, data:{resId: string}}}>
	 * |Promise<{status:number, data:{timestamp: number, resId: string, error:{code: string, message: string, method: string, endpoint: string}}}>
	 * }
	 */
	async approve(obj) {
		const { network } = this._defaultParams.constants.callback;
		const result = await this._net[network.approve.method](network.approve.path, obj);
		return result;
	}
}
