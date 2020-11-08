/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-26T20:07
 */
// --------index.js--------

import { constants } from "../../../../../../config/constants.json";

const { USER_ALL } = constants.user.actions;

export default payload => {
	return {
		payload,
		type: USER_ALL,
	};
};
