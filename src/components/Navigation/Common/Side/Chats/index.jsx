/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-26T14:58
 */
// --------index.jsx--------

import "../../../../../assets/css/components/navigation/SideChats.css";
import React, { Component } from "react";
import { ButtonGroup } from "react-bootstrap";
import { connect, Provider } from "react-redux";

import allUsers from "../../../../../mq/actions/navigation/Common/Side/Chats";
import LoadData from "../../../../Common/Spinner";
import UserChat from "./UserChat";

const mapDispatchToProps = dispatch => {
	return {
		allUsers: payload => dispatch(allUsers(payload)),
	};
};
const mapStateToProps = state => {
	return state;
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	class Chats extends Component {
		// Load all users for chats
		componentDidMount() {
			const { store, user, allUsers } = this.props;
			allUsers({
				store,
				user_id: user.id,
			});
		}

		/**
		 * Creating "InProgress" component for render it
		 * @returns {JSX.Element}
		 * @private
		 */
		_renderInProgress() {
			const { strings, mode } = this.props;
			return (
				<LoadData
					{...{
						mode,
						text: strings.load,
						size: "xl",
						classNames: "absolute",
					}}
				/>
			);
		}

		/**
		 * Creating "Chats" component for render it
		 * @returns {JSX.Element}
		 * @private
		 */
		_renderUsers() {
			const { users, mode, side, store, lastChat } = this.props;
			return (
				<ButtonGroup id="side-users-chat" vertical className="users-button-group">
					{users && users.length > 0 ? (
						users.map(user => (
							<Provider key={user.id} store={store.side.chats[user.id]}>
								<UserChat
									id="side-users-chat"
									{...{
										lastChat,
										store,
										side,
										user,
										mode,
									}}
								/>
							</Provider>
						))
					) : (
						<></>
					)}
				</ButtonGroup>
			);
		}

		render() {
			const { progress } = this.props;
			return progress ? this._renderInProgress() : this._renderUsers();
		}
	}
);
