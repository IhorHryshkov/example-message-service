/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:20
 */
// --------RootService.js--------

export default class RootService {
	constructor({ defaultParams }) {
		this._defaultParams = defaultParams;
		this.init = this.init.bind(this);
		this.add = this.add.bind(this);
		this.remove = this.remove.bind(this);
		this.all = this.all.bind(this);
		this.update = this.update.bind(this);
		this.getById = this.getById.bind(this);
	}

	init() {
		return Promise.reject(Error("No init all"));
	}

	all() {
		return Promise.reject(Error("No init all"));
	}

	getById() {
		return Promise.reject(Error("No init getById"));
	}

	add() {
		return Promise.reject(Error("No init add"));
	}

	update() {
		return Promise.reject(Error("No init update"));
	}

	remove() {
		return Promise.reject(Error("No init remove"));
	}
}
