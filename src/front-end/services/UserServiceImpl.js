/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:17
 */
//--------UserServiceImpl.js--------

import RootService from './iface/RootService';
import UserRequest from '../network/request/UserRequestImpl';
import CallbackDao from '../database/dao/local/CallbackDaoImpl';

class UserServiceImpl extends RootService {
	constructor({netConfig, dbConfig, defaultParams}) {
		super({defaultParams});
		this._userRequest = new UserRequest({
			netConfig,
			defaultParams
		});
		this._callbackDao = new CallbackDao(
			dbConfig,
			defaultParams
		);
	}

	async all(obj) {
		const result = await this._userRequest.all(obj);
		return result.data;
	}

	async add({username}) {
		const {prefixStorageKey, postfixStorageKey, enums} = this._defaultParams.constants.callback;

		const result = await this._userRequest.add({username});
		if (result.status === 201) {
			await this._callbackDao.add({
				key : `${prefixStorageKey}${username}_${result.data.resId}${postfixStorageKey}`,
				data: {callback: enums.ADD_USER}
			})
		}

		return result;
	}

	update(obj) {
		return super.update(obj);
	}

	getById(obj) {
		return super.getById(obj);
	}
}

export default UserServiceImpl;
