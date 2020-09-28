/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:02
 */
//--------PreferencesDaoImpl.js--------

import schema  from './schemas/preferences';
import RootDao from './iface/RootDao';

class PreferencesDaoImpl extends RootDao {
	constructor(dbConfig, defaultParams) {
		super({
			dbConfig,
			defaultParams
		});
		this._validator.initSchemas(schema);
	}

	add(obj) {
		return new Promise((resolve, reject) => {
			const validateError = this._validator.validateSchema(this.add.name, obj);
			if (validateError) {
				return reject(validateError);
			}
			const {key, data} = obj;
			this._db.setItem(key, JSON.stringify(data));
			return resolve(this._defaultParams.messages.info.success);
		});
	}

	remove(obj) {
		return new Promise((resolve, reject) => {
			try {
				const validateError = this._validator.validateSchema(this.remove.name, obj);
				if (validateError) {
					return reject(validateError);
				}
				const {key} = obj;
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

	getByKey(obj) {
		return new Promise((resolve, reject) => {
			try {
				let validateError = this._validator.validateSchema(this.remove.name, obj);
				if (validateError) {
					return reject(validateError);
				}
				const {key}   = obj;
				let result    = this._db.getItem(key);
				result        = JSON.parse(result);
				validateError = this._validator.validateSchema(this.getByKey.name, {
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

export default PreferencesDaoImpl;
