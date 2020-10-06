/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-06T17:14
 */

//--------StatusRequest.js--------

class StatusRequestImpl {
	constructor({netConfig, defaultParams}) {
		this._net           = netConfig.getAxios();
		this._defaultParams = defaultParams;
	}

	async all(params) {
		const {network} = this._defaultParams.constants.status;
		return await this._net[network.all.method](
			network.all.path,
			{params}
		);
	}
}

export default StatusRequestImpl;
