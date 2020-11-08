/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:20
 */
// --------RootDao.js--------

/**
 * Root database access object
 */
export default class RootDao {
	constructor({ dbConfig, defaultParams, schema }) {
		this._defaultParams = defaultParams;
		this._db = dbConfig.getLocal();
		this._indexDB = dbConfig.getIndexDB(this._defaultParams.constants.global.database.version);
		this._validator = dbConfig.getValidator();
		this._validator.initSchemas(schema);
	}

	getById() {
		return Promise.reject(Error("No init getById"));
	}

	add() {
		return Promise.reject(Error("No init add"));
	}

	remove() {
		return Promise.reject(Error("No init remove"));
	}

	update() {
		return Promise.reject(Error("No init update"));
	}

	all() {
		return Promise.reject(Error("No init all"));
	}
}
