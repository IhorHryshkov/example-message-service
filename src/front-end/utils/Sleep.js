/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-03T12:45
 */
//--------Sleep.js--------

export default class Sleep {
	static sleep = (ms) => {
		return new Promise(resolve => setTimeout(
			resolve,
			ms
		));
	}
}