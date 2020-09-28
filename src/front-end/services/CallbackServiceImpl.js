/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:17
 */
//--------CallbackServiceImpl.js--------

import RootService         from './iface/RootService';
import CallbackRequestImpl from '../network/request/CallbackRequestImpl';
import CallbackDao         from '../database/dao/local/CallbackDaoImpl';

class CallbackServiceImpl extends RootService {
	constructor({netConfig, dbConfig, defaultParams}) {
		super({defaultParams});
		this._callbackRequest = new CallbackRequestImpl({
			netConfig,
			defaultParams
		});
		this._callbackDao = new CallbackDao(dbConfig, defaultParams);
	}

	async add(obj) {
		const {username, resId}                     = obj;
		const {prefixStorageKey, postfixStorageKey} = this._defaultParams.constants.callback;

		const result = await this._callbackRequest.approve({resId});
		if (result.status === 200) {
			await this._callbackDao.remove({key: `${prefixStorageKey}${username}_${resId}${postfixStorageKey}`});
		}
		return result;
	}

	async getById(obj) {
		const {username, resId} = obj;

		const {prefixStorageKey, postfixStorageKey} = this._defaultParams.constants.callback;
		return await this._callbackDao.getByKey({key: `${prefixStorageKey}${username}_${resId}${postfixStorageKey}`});
	}
}

export default CallbackServiceImpl;
