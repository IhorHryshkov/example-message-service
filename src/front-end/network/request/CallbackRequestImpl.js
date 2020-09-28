/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:11
 */

//--------LoginRequest.js--------

class CallbackRequestImpl {
	constructor({netConfig, defaultParams}) {
		this._net           = netConfig.getAxios();
		this._defaultParams = defaultParams;
		this.approve        = this.approve.bind(this);
	}

	async approve(obj) {
		const {network} = this._defaultParams.constants.callback;
		return await this._net[network.approve.method](network.approve.path, obj);
	}
}

export default CallbackRequestImpl;
