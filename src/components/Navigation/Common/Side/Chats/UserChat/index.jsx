/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-08T11:40
 */
// --------index.jsx--------
import "../../../../../../assets/css/components/common/UserChat.css";
import React, { Component } from "react";
import { Badge, Button } from "react-bootstrap";
import { connect } from "react-redux";
import { initChat } from "../../../../../../mq/actions/chat";
import selectUser from "../../../../../../mq/actions/navigation/Common/Side/Chats/UserChat";

const mapDispatchToProps = dispatch => {
	return {
		selectUser: payload => dispatch(selectUser(payload)),
	};
};
const mapStateToProps = state => {
	return state;
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	class UserChat extends Component {
		// Set active user if current user was last chat
		componentDidMount() {
			const { lastChat, selectUser, store, user } = this.props;
			if (lastChat && lastChat === user.id) {
				selectUser({
					store,
					user_id: user.id,
				});
			}
		}

		// Sending data for chat's init if user was select
		componentDidUpdate(prevProps) {
			const { store, selectedUser } = this.props;
			if (selectedUser && selectedUser !== prevProps.selectedUser) {
				store.chat.dispatch(initChat(selectedUser));
			}
		}

		render() {
			const { user, mode, selectUser, side, strings, selectedUser, newMessageCount, newMessageCountString, store } = this.props;
			return (
				<div className="root-user-chat">
					{newMessageCount > 0 ? (
						<>
							<Badge pill variant={mode} className={`user-chat-badge ${side === "left" ? "right" : "left"}`}>
								{strings.messageNew}
							</Badge>
							<Badge id="side-users-chat" pill variant={mode} className={`user-chat-badge ${side}`}>
								{newMessageCountString}
							</Badge>
						</>
					) : (
						" "
					)}
					<Button
						className="user-chat-button"
						variant={`outline-${mode}`}
						active={selectedUser}
						onClick={() =>
							selectUser({
								store,
								user_id: user.id,
							})
						}
					>
						{user.username}
					</Button>
				</div>
			);
		}
	}
);
