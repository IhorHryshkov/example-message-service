/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:21
 */
// --------Search.js--------

/**
 * Utils for search node by Id in path
 * @param path
 * @returns {string}
 */
export default path => {
	let localPath = path;
	let id;
	while (!id || id.length <= 0) {
		id = localPath.id;
		if (id && id.length > 0) {
			break;
		} else {
			localPath = localPath && localPath.parentNode ? localPath.parentNode : undefined;
			if (!localPath) {
				break;
			}
		}
	}
	return id;
};
