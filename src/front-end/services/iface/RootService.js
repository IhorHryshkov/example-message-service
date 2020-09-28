/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:20
 */
//--------RootService.js--------

class RootService {

	constructor({defaultParams}) {
		this._defaultParams = defaultParams;
		this.add            = this.add.bind(this);
		this.remove         = this.remove.bind(this);
		this.all            = this.all.bind(this);
		this.update         = this.update.bind(this);
		this.getById        = this.getById.bind(this);
	}

	all(obj) {
		return Promise.reject('No init all');
	};

	getById(obj) {
		return Promise.reject('No init getById');
	};

	add(obj) {
		return Promise.reject('No init add');
	};

	update(obj) {
		return Promise.reject('No init update');
	};

	remove(obj) {
		return Promise.reject('No init remove');
	};
}

export default RootService;
