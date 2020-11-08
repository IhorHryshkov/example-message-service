/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-25T07:25
 */
// --------index.jsx--------
import "../../assets/css/components/chat/Chat.css";
import React, { Component } from "react";
import { Container, Row } from "react-bootstrap";
import { connect } from "react-redux";
import CustomScroll from "../Common/Scrolls/Custom";
import Message from "./Common/Message";
import InputMessage from "./Common/InputMessage";
import LoadData from "../Common/Spinner";
import { initChat, loadChat, sendMessage } from "../../mq/actions/chat";

const mapStateToProps = state => {
	return state;
};
const mapDispatchToProps = dispatch => {
	return {
		loadChat: obj => dispatch(loadChat(obj)),
		sendMessage: obj => dispatch(sendMessage(obj)),
		initChat: obj => dispatch(initChat(obj)),
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	class Chat extends Component {
		// Load selected chat from DB
		componentDidUpdate(prevProps) {
			const { selectedChat, user, loadChat } = this.props;
			if (selectedChat && selectedChat !== prevProps.selectedChat) {
				loadChat({
					user_id: selectedChat,
					user_own_id: user.id,
				});
			}
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
						mode: mode === "light" ? "dark" : "light",
						text: strings.load,
						size: "xl",
						classNames: "absolute",
					}}
				/>
			);
		}

		/**
		 * Creating "chat messages" and "input message" components for render it
		 * @returns {JSX.Element}
		 * @private
		 */
		_renderChat() {
			const { mode, strings, exceptionMessage, schema, selectedChat, user, sendMessage } = this.props;
			return (
				<>
					<Row className="chat-row">
						<CustomScroll
							removeTracksWhenNotUsed
							values={{ execScrollToBottom: true }}
							{...{
								mode,
							}}
						>
							<Container fluid>{this._renderMessages()}</Container>
						</CustomScroll>
					</Row>
					<Row className="send-message-row">
						<InputMessage
							{...{
								strings,
								exceptionMessage,
								schema,
								submit: data =>
									sendMessage({
										data,
										user,
										user_id: selectedChat,
									}),
							}}
						/>
					</Row>
				</>
			);
		}

		/**
		 * Creating "Messages" components for render it
		 * @returns {*|JSX.Element}
		 * @private
		 */
		_renderMessages() {
			const { messages, mode, user } = this.props;
			return messages && messages.length > 0 ? (
				messages.map(message => (
					<Message
						key={message.id}
						{...{
							message,
							mode,
							user_id: user.id,
						}}
					/>
				))
			) : (
				<></>
			);
		}

		render() {
			const { selectedChat, progress } = this.props;
			return (
				<Container fluid className="main-col">
					{!selectedChat ? "" : progress ? this._renderInProgress() : this._renderChat()}
				</Container>
			);
		}
	}
);
