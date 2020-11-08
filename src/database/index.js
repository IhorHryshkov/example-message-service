/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:05
 */
// --------index.js--------

import Validator from "../utils/Validator";

/**
 * Initialization instances of database
 */
export default class DatabaseConfigImpl {
	constructor({ config, defaultParams }) {
		this._defaultParams = defaultParams;
		this._config = config;
	}

	/**
	 * Initialization validator for I/O data
	 * @returns {Validator}
	 */
	getValidator() {
		return new Validator({ defaultParams: this._defaultParams });
	}

	/**
	 * Initialization local storage
	 * @returns {Storage}
	 */
	getLocal() {
		return this._config.local;
	}

	/**
	 * Initialization session storage
	 * @returns {Storage}
	 */
	getSession() {
		return this._config.session;
	}

	/**
	 * Initialization ORM Dexie for IndexDB
	 * @param dbVersion - Version of DB
	 * @returns {Dexie | Dexie}
	 */
	getIndexDB(dbVersion) {
		if (!this._config.indexDB.isOpen()) {
			this._config.indexDB.version(dbVersion).stores({
				users: "&id,username,updateUserAt,updateSystemAt",
				status: "&id,name",
				counter: "&id",
				type: "&id,name",
				chat: "&id,timestamp,chat_id",
			});
		}
		return this._config.indexDB;
	}
}
