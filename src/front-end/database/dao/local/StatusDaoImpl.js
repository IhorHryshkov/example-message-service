/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-06T16:01
 */
//--------StatusDaoImpl.js--------

import schema  from './schemas/status.json';
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
			const validateError = this._validator.validateSchema(
				this.add.name,
				{
					key,
					data
				}
			);
			if (validateError) {
				return reject(validateError);
			}
			this._db.setItem(
				key,
				JSON.stringify(data)
			);
			resolve(this._defaultParams.messages.info.success);
		});
	}

	getByKey({key, name}) {
		return new Promise((resolve, reject) => {
			try {
				let result          = this._db.getItem(key);
				result              = JSON.parse(result);
				result              = result.find(item => item.name.toLowerCase() === name.toLowerCase());
				const validateError = this._validator.validateSchema(
					this.getByKey.name,
					result
				);
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
