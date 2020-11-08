/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:21
 */
// --------Validator.js--------
import Ajv from "ajv";

/**
 * Utils for data validation
 */
export default class Validator {
	/**
	 * Initialization of configuration data
	 * @param defaultParams - Default params and constants
	 */
	constructor({ defaultParams }) {
		this._ajv = new Ajv({
			allErrors: true,
			removeAdditional: "all",
		});
		this._defaultParams = defaultParams;
		this.visibleForTests = {
			ajv: this._ajv,
			errorResponse: this._errorResponse,
		};
	}

	/**
	 * Init validation schema by path
	 * @param schemas - JSON path data of schema
	 */

	/* eslint global-require: "off" */
	initSchemas(schemas) {
		const keys = Object.keys(schemas);
		let schemaPath;
		for (let key, i = 0, { length } = keys; i < length; i += 1) {
			key = keys[i];
			schemaPath = require(`../database/models${schemas[key].path}`); // eslint-disable-line import/no-dynamic-require
			this._ajv.addSchema(schemaPath, key);
		}
	}

	/**
	 * Validation data by schema name
	 * @param schemaName -Name of schema
	 * @param data
	 * @returns {null|{code: string, message: string}}
	 */
	validateSchema(schemaName, data) {
		const valid = this._ajv.validate(schemaName, data);
		if (!valid) {
			const { code } = this._defaultParams.messages.error.dataIncorrect;
			return {
				code,
				message: this._errorResponse(this._ajv.errors),
			};
		}
		return null;
	}

	/**
	 * Create error response from error messages
	 * @param schemaErrors - Array of error messages
	 * @returns {string}
	 * @private
	 */
	_errorResponse = schemaErrors => {
		return schemaErrors.map(({ message, dataPath }) => {
			const result = { message };
			if (dataPath) {
				result.path = dataPath;
			}

			return result;
		});
	};
}
