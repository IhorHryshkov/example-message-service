/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:21
 */
//--------Validator.js--------

import Ajv from 'ajv';

class Validator {
	constructor({defaultParams}) {
		this._ajv           = new Ajv({
			allErrors       : true,
			removeAdditional: 'all'
		});
		this._defaultParams = defaultParams;
	}

	initSchemas(schemas) {
		let userSchema;
		const keys = Object.keys(schemas);
		for (let key, i = 0, length = keys.length; i < length; i++) {
			key        = keys[i];
			userSchema = require(`../database/models${schemas[key].path}`);
			this._ajv.addSchema(
				userSchema,
				key
			);
		}
	}

	validateSchema(schemaName, data) {
		const valid = this._ajv.validate(
			schemaName,
			data
		);
		if (!valid) {
			const {code} = this._defaultParams.messages.error.dataIncorrect;
			return {
				code,
				message: this._errorResponse(this._ajv.errors)
			};
		}
		return null;
	}

	_errorResponse(schemaErrors) {
		return schemaErrors.map(({message, dataPath}) => {
			const result = {message};
			if (dataPath) {
				result.path = dataPath;
			}

			return result;
		});
	}
}

export default Validator;
