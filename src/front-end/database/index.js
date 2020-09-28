/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:05
 */
//--------index.js--------

import Validator from '../utils/Validator';

class DatabaseConfigImpl {

	constructor({config, defaultParams}) {
		this._defaultParams = defaultParams;
		this._config        = config;
	}

	getValidator() {
		return new Validator({defaultParams: this._defaultParams});
	}

	getLocal() {
		return this._config.local;
	}

	getSession() {
		return this._config.session;
	}
}

export default DatabaseConfigImpl;
