/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:11
 */

//--------UserRequest.js--------

class UserRequestImpl {
	constructor({netConfig, defaultParams}) {
		this._net = netConfig.getAxios();
		this._defaultParams = defaultParams;
	}

	async add(obj) {
		const {network} = this._defaultParams.constants.user;
		return await this._net[network.add.method](
			network.add.path,
			obj
		);
	}

	async all(params) {
		const {network} = this._defaultParams.constants.user;
		return await this._net[network.all.method](
			network.all.path,
			{params}
		);
	}
}

export default UserRequestImpl;
