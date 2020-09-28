/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-24T01:01
 */
//--------index.js--------

import {takeEvery} from 'redux-saga/effects';
import {constants} from '../../../../config/front-end/constants.json';

const {SEND_MESSAGE} = constants.chat.actions;

export default function* chatWatcher() {
	yield takeEvery(SEND_MESSAGE, workerSendChat);
}

function* workerSendChat(action) {

}
