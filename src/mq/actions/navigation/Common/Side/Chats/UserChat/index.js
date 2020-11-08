/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-12T15:27
 */
// --------index.js--------

import { constants } from "../../../../../../../config/constants.json";

const { USER_SELECT } = constants.user.actions;

export default payload => {
	return {
		payload,
		type: USER_SELECT,
	};
};
