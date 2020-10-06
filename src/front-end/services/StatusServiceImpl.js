/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:17
 */
//--------StatusServiceImpl.js--------

import RootService   from './iface/RootService';
import StatusRequest from '../network/request/StatusRequestImpl';
import StatusDao     from '../database/dao/local/StatusDaoImpl';

class UserServiceImpl extends RootService {
	constructor({netConfig, dbConfig, defaultParams}) {
		super({defaultParams});
		this._statusRequest = new StatusRequest({
			netConfig,
			defaultParams
		});
		this._statusDao     = new StatusDao(
			dbConfig,
			defaultParams
		);
	}

	async all(obj) {
		const {prefixStorageKey, postfixStorageKey} = this._defaultParams.constants.status;

		const {status, data} = await this._statusRequest.all(obj);
		if (status === 200) {
			await this._statusDao.add({
				key : `${prefixStorageKey}Status${postfixStorageKey}`,
				data: data.data
			})
		}
		return status;
	}
}

export default UserServiceImpl;
