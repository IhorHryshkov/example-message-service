/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-04T21:15
 */
//--------CounterServiceImpl.js--------

import RootService    from './iface/RootService';
import CounterRequest from '../network/request/CounterRequestImpl';
import CallbackDao    from '../database/dao/local/CallbackDaoImpl';

class CounterServiceImpl extends RootService {
	constructor({netConfig, dbConfig, defaultParams}) {
		super({defaultParams});
		this._counterRequest = new CounterRequest({
			netConfig,
			defaultParams
		});
		this._callbackDao    = new CallbackDao(
			dbConfig,
			defaultParams
		);
	}

	async getById(id) {
		const result = await this._counterRequest.getById(id);
		return result.status === 204 ? [] : result.data.data;
	}

	async add(obj) {
		return super.add(obj);
	}

	update(obj) {
		return super.update(obj);
	}

	all(obj) {
		return super.all(obj);
	}
}

export default CounterServiceImpl;
