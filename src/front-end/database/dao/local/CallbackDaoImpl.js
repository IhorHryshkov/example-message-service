/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:02
 */
//--------PreferencesDaoImpl.js--------

import schema  from './schemas/callback.json';
import RootDao from './iface/RootDao';

class CallbackDaoImpl extends RootDao {
	constructor(dbConfig, defaultParams) {
		super({
			dbConfig,
			defaultParams
		});
		this._validator.initSchemas(schema);
	}

	add({key, data}) {
		return new Promise((resolve, reject) => {
			const validateError = this._validator.validateSchema(this.add.name, {
				key,
				data
			});
			if (validateError) {
				return reject(validateError);
			}
			this._db.setItem(key, JSON.stringify(data));
			resolve(this._defaultParams.messages.info.success);
		});
	}

	remove({key}) {
		return new Promise((resolve, reject) => {
			try {
				const validateError = this._validator.validateSchema(this.remove.name, {key});
				if (validateError) {
					return reject(validateError);
				}
				this._db.removeItem(key);
				return resolve(this._defaultParams.messages.info.success);
			} catch (e) {
				return reject({
					code   : "ERROR",
					message: e.message
				})
			}
		});
	}

	getByKey({key}) {
		return new Promise((resolve, reject) => {
			try {
				let result          = this._db.getItem(key);
				result              = JSON.parse(result);
				const validateError = this._validator.validateSchema(this.getByKey.name, {
					key,
					data: result
				});
				if (validateError) {
					return reject(validateError);
				}
				return resolve(result);
			} catch (e) {
				return reject({
					code   : "ERROR",
					message: e.message
				})
			}
		});
	}
}

export default CallbackDaoImpl;
