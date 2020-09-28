/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:21
 */
//--------Converter.js--------

export const searchFirstIdInNode = (path) => {
	let mutPath = path;
	let id      = '';
	while (!id || id.length <= 0) {
		id = mutPath.id;
		if (id && id.length > 0) {
			break;
		} else {
			mutPath = mutPath && mutPath.parentNode ? mutPath.parentNode : undefined;
			if (!mutPath) {
				break;
			}
		}
	}
	return id;
}
