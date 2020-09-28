import {constants} from '../../../../../../../config/front-end/constants.json';

/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-26T19:20
 */
//--------index.js--------

const {USER_GET_SUCCESS, USER_SELECT_SUCCESS} = constants.user.actions;

const initialState = {
	selectedUser: null,
	users       : [
		{
			"id"       : "d512a983-6d41-4910-9d3e-7ddc1983df2f",
			"username" : "testUser",
			"meta"     : null,
			"createdAt": 1599840451118,
			"updatedAt": 1599840451118,
			"status"   : {
				"id"       : 1,
				"name"     : "Online",
				"createdAt": 1599840451103,
				"updatedAt": 1599840451103
			}
		},
		{
			"id"       : "d512a983-6d41-4910-9d3e-7ddc1983df22",
			"username" : "testUser2",
			"meta"     : null,
			"createdAt": 1599840451118,
			"updatedAt": 1599840451118,
			"status"   : {
				"id"       : 2,
				"name"     : "Offline",
				"createdAt": 1599840451103,
				"updatedAt": 1599840451103
			}
		},
		{
			"id"       : "d512a983-6d41-4910-9d3e-7ddc1983df23",
			"username" : "testUser3",
			"meta"     : null,
			"createdAt": 1599840451118,
			"updatedAt": 1599840451118,
			"status"   : {
				"id"       : 1,
				"name"     : "Online",
				"createdAt": 1599840451103,
				"updatedAt": 1599840451103
			}
		},
		{
			"id"       : "d512a983-6d41-4910-9d3e-7ddc1983df24",
			"username" : "testUser4",
			"meta"     : null,
			"createdAt": 1599840451118,
			"updatedAt": 1599840451118,
			"status"   : {
				"id"       : 1,
				"name"     : "Online",
				"createdAt": 1599840451103,
				"updatedAt": 1599840451103
			}
		},
		{
			"id"       : "d512a983-6d41-4910-9d3e-7ddc1983df25",
			"username" : "testUser5",
			"meta"     : null,
			"createdAt": 1599840451118,
			"updatedAt": 1599840451118,
			"status"   : {
				"id"       : 2,
				"name"     : "Offline",
				"createdAt": 1599840451103,
				"updatedAt": 1599840451103
			}
		},
		{
			"id"       : "d512a983-6d41-4910-9d3e-7ddc1983df26",
			"username" : "testUser6",
			"meta"     : null,
			"createdAt": 1599840451118,
			"updatedAt": 1599840451118,
			"status"   : {
				"id"       : 1,
				"name"     : "Online",
				"createdAt": 1599840451103,
				"updatedAt": 1599840451103
			}
		},
		{
			"id"       : "d512a983-6d41-4910-9d3e-7ddc1983df27",
			"username" : "testUser7",
			"meta"     : null,
			"createdAt": 1599840451118,
			"updatedAt": 1599840451118,
			"status"   : {
				"id"       : 1,
				"name"     : "Online",
				"createdAt": 1599840451103,
				"updatedAt": 1599840451103
			}
		},
		{
			"id"       : "d512a983-6d41-4910-9d3e-7ddc1983df28",
			"username" : "testUser8",
			"meta"     : null,
			"createdAt": 1599840451118,
			"updatedAt": 1599840451118,
			"status"   : {
				"id"       : 1,
				"name"     : "Online",
				"createdAt": 1599840451103,
				"updatedAt": 1599840451103
			}
		}
	]
};

export default (state = initialState, action) => {
	switch (action.type) {
		case USER_GET_SUCCESS: {
			return {
				...state,
				users: action.payload
			}
		}
		case USER_SELECT_SUCCESS: {
			const indexOld = state.users.findIndex(user => user.id === state.selectedUser);
			if (indexOld >= 0) {
				state.users[indexOld].isChat = false;
			}
			const indexNew            = state.users.findIndex(user => user.id === action.payload);
			state.users[indexNew].isChat = true;
			return {
				...state,
				users       : state.users,
				selectedUser: action.payload
			}
		}
		default:
			return state;
	}
};
