/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:17
 */
//--------UserServiceImpl.js--------

import RootService from './iface/RootService';
import UserRequest from '../network/request/UserRequestImpl';
import CallbackDao from '../database/dao/local/CallbackDaoImpl';
import StatusDao   from '../database/dao/local/StatusDaoImpl';

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
		this._statusDao   = new StatusDao(
			dbConfig,
			defaultParams
		);
	}

	async all(obj) {
		const result = await this._userRequest.all(obj);
		return result.data.data;
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

	async update({id, username}) {
		const {status, callback, user} = this._defaultParams.constants;
		const localStatus              = await this._statusDao.getByKey({
			key : `${status.prefixStorageKey}Status${status.postfixStorageKey}`,
			name: user.default.status
		});
		const result                   = await this._userRequest.update({
			id,
			statusId: localStatus.id
		});
		if (result.status === 200) {
			await this._callbackDao.add({
				key : `${callback.prefixStorageKey}${username}_${result.data.resId}${callback.postfixStorageKey}`,
				data: {callback: callback.enums.UPDATE_USER}
			})
		}

		return result;
	}

	getById(obj) {
		return super.getById(obj);
	}
}

export default UserServiceImpl;
