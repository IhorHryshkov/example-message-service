/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-04T21:13
 */

//--------CounterRequest.js--------

class CounterRequestImpl {
	constructor({netConfig, defaultParams}) {
		this._net           = netConfig.getAxios();
		this._defaultParams = defaultParams;
	}

	async add(obj) {
		const {network} = this._defaultParams.constants.counter;
		return await this._net[network.add.method](
			network.add.path,
			obj
		);
	}

	async getById(id) {
		const {network} = this._defaultParams.constants.counter;
		return await this._net[network.getById.method](`${network.getById.path}/${id}`);
	}
}

export default CounterRequestImpl;
