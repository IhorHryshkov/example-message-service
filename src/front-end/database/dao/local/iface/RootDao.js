/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:20
 */
//--------RootDao.js--------

class RootDao {
	constructor({dbConfig, defaultParams}) {
		this._defaultParams = defaultParams;
		this._db            = dbConfig.getLocal();
		this._validator = dbConfig.getValidator();
	}

	getById(obj) {
		return Promise.reject('No init getById');
	};

	add(obj) {
		return Promise.reject('No init add');
	};

	remove(obj) {
		return Promise.reject('No init remove');
	};
}

export default RootDao;
